# jdk8 并行流使用


### 一、简述
JDK8为了让处理大数据量集合更快速，使用了并行的形式来进行处理。在上面的例子中我们也看到了，如果我们需要一个并行流的话，只要对一个集合打开parallelStream即可。在JDK7以前，想要对一个集合进行并行处理似乎是一件困难的事情。所以这一篇文章我们可以看看JDK8是怎么实现并行处理的。


### 二、将顺序流转换为并行流
有一个需求是，求从1开始到n的所有数字的和。
JDK7的做法是：
```java
/**
 * JDK7的做法
 */
@Test
public void test01(){
    long l = iterativeSum(100);
    System.out.println(l);
}
public long iterativeSum(long n){
    long res = 0;
    for(long i = 0; i <= n; i++){
        res += i;
    }
    return res;
}
```

JDK8有两种方式，一种是顺序流，另外一种就是并行流了。
顺序流的方式：
```java
/**
 * JDK8的顺序流做法
 */
@Test
public void test02(){
    long l = sequentialSum(100);
    System.out.println(l);
}
/** 顺序流的方式 */
public long sequentialSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
            .limit(n)
            .reduce(0L, Long::sum);
}
```

然后我们只要对以上的代码加以修改就可以变成并行流的方式了：
```java
/**
 * JDK8的并行流做法
 */
@Test
public void test03(){
    long l = parallelSum(100);
    System.out.println(l);
}
/** 并行流的方式 */
public long parallelSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
            .limit(n)
            .parallel()// 只需要对一个顺序流调用此方法，就可以实现顺序流到并行流的转换
            .reduce(0L, Long::sum);
}
```
#### 注意
这里有个需要注意的地方就是，可能有人会以为我们可以通过调用不同的转换（parallel转换为并行，sequential转换为顺序流）来控制到每一步操作是否通过顺序流还是并行流，但是其实并不是这样的，最后一次调用会影响到整个流的操作，例如：
```Stream.parallel().filter(...). sequential().map(...).parallel().reduce(..)```
这个语句其实整个操作都是并行操作的，因为在这段代码中，**最后一次调用的方法是parallel**

#### 使用的核心数
并行流使用的核心数是默认的处理器的数量，如果需要更改，我们可以通过java.util.concurrent.ForkJoinPool.common.paramllelism来修改默认的核心数量。比如通过调用的System.setProperty来更改。但是默认核心数量是一个很好的设置，如果没有特别的需求的话并不建议修改。
```java 
/**
 * 查看电脑核心数量
 */
@Test
public void test04(){
    System.out.println(Runtime.getRuntime().availableProcessors());
}
```

#### 正确使用并行
在使用并行的时候我们应该尽量去**避免对一个值进行修改**，因为在多线程情况下，这个值的修改会被竞争，这样就会出现计算结果错误的后果，但是当我们去给这个值加上锁的时候，这个操作又失去了并行的意义。
```java
/**
 * 并行流错误使用示范
 */
@Test
public void test05() {
    /** 顺序流的方式 */
    Accmulator accmulator = new Accmulator();
    LongStream.rangeClosed(1, 100).forEach(accmulator::add);
    System.out.println(accmulator.total);// 5050

    /** 并行流的方式 */
    for (int i = 0; i < 10; i++) {
        accmulator = new Accmulator();
        LongStream.rangeClosed(1, 100).parallel().forEach(accmulator::add);
        System.out.println(accmulator.total);
    }
    /*
    4004
    4872
    4952
    5050
    4874
    4719
    4935
    4798
    5050
    4392
     */
}
/**
 * 创建一个类用于修改这个类里的变量
 */
class Accmulator {
    long total = 0;

    public void add(long v) {
        total += v;
    }
}
```
**当输出来的值只有一次是正确的时候，性能似乎就变得不太那么重要了。**

### 三、使用分支/合并框架进行求和
JDK8并行流的使用我们需要考虑一定的因素，并不是所有的操作都是用并行流就是好的。如果数据量较小，使用并行流并不比使用顺序流的速度要快，因为创建线程是需要耗费资源的。

接下来就来体验一下JDK7的ForkJoin框架。（说真的，我写代码这么久以来还没用过这个框架...）

如果使用过线程池的同学就知道，ExecutorService是用来配置系统中线程池信息的，而ForkJoin就是对该接口的一个实现，称为ForkJoinPool，顾名思义就是一个分割线程的池。而这些任务就在这个池里面获取线程资源进行执行。

需求：定义一个函数，对Long类型的前n个数进行求和。
```java
package cn.liweidan.forkandjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * <p>Desciption:并行求和Long数组</p>
 * CreateTime : 2017/7/17 上午11:25
 * Author : Weidan
 * Version : V1.0
 */
public class ForkJoinCaculator extends RecursiveTask<Long> {// 需要继承RecursiveTask，传递并行处理后返回值的类型；如果执行的没有返回值，则继承RecursiveAction

    /** 需要进行计算的long数组 */
    private final long[] numbers;
    /** 子任务处理数组的其实和终止位置 */
    private final int start;
    private final int end;

    /** 进行数组分割的阈值 */
    public static final long THRESHOLD = 10_000;

    public ForkJoinCaculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    private ForkJoinCaculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    /**
     * JDK会通过线程池中的一个线程调用该方法，这里需要我们确定一个条件判断，判断这个任务是否需要继续进行切割
     * 在这里我们运行的时候，定义一个阈值，如果数组小于这个阈值，则直接进行计算，如果大于这个阈值，则对这个数组进行分割
     * @return
     */
    @Override
    protected Long compute() {
        /** 计算长度 */
        int length = end - start;
        /** 如果长度小于阈值则直接进行计算 */
        if(length <= THRESHOLD){
            return computeSequentially();
        }

        /** 创建一个从开始到开始+长度的一半的数组计算线程 */
        ForkJoinCaculator leftTask =
                new ForkJoinCaculator(numbers, start, start + length / 2);
        /** 加入线程池 */
        leftTask.fork();

        /** 创建一个任务为后面剩余部分求和 */
        ForkJoinCaculator rightTask =
                new ForkJoinCaculator(numbers, start + length / 2, end);
        /** 同步执行 */
        Long rightRes = rightTask.compute();

        /** 读取刚刚创建的第一个任务的结果，join方法会等待该线程返回结果，未完成线程会阻塞 */
        Long leftRes = leftTask.join();

        /** 返回左右两个任务计算出来的结果 */
        return leftRes + rightRes;
    }

    /**
     * 用于顺序计算
     * @return
     */
    private Long computeSequentially() {
        long sum = 0;
        for (long number : numbers) {
            sum += number;
        }
        return sum;
    }

    public static long forkjoinSum(long n) {
        long numbers[] = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinCaculator(numbers);
        return new ForkJoinPool().invoke(task);
    }
}

```
**注意代码中的join方法是会进行线程阻塞的，在那里需要等待每个任务的返回结果再进行下去。**

接下来就是运行了。
```java
public class ForkJoinCaculatorTest {
    @Test
    public void forkjoinSum() throws Exception {
        long startTime = System.nanoTime();
        long l = ForkJoinCaculator.forkjoinSum(10_000_000);
        long endTime = System.nanoTime();
        System.out.println("共耗时：" + ((endTime -startTime)/1000000) + "毫秒, 结果是：" + l);
    }

}
```

运行完成以后，输出`共耗时：3400毫秒, 结果是：51200005120000000`

运行以上程序的时候，Java会把长度为10_000_000的long数组传递给了`ForkJoinCaculator`，期间通过`compute`进行分割，将数组分割到足够小的长度的时候进行计算，最后，程序把每个线程计算出来的结果进行合并，得出以上的结果。
