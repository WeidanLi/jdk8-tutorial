

之前写了JDK8集合流的入门以及筛选，也就是集合流使用的打开和中间操作。这次带来的是不同的收集数据的方式。

本节代码GitHub地址：[jdk8 收集器](https://github.com/WeidanLi/jdk8-tutorial/tree/master/jdk8-collect)

### 一、准备：
还是老规矩，使用菜单进行示例。（代码的话建议拷贝这部分）


### 二、收集器简介
收集器即收集东西的容器，它用于使用集合流的时候的终端操作，即我们在日常的业务逻辑中把流进行过滤也好，进行筛选也好，然后我们总该要有一个容器可以存放这些过滤后的元素。这时候的收集器就派上用场了。如代码所示一个最简单的收集器的使用实例（当然我感觉平时应该没人这么无聊）

<!--more-->

```java
 /**
 * 收集器是可以收集一个流中的数据的一个容器
 * cn.liweidan.collect.Demo01#demo01
 */
@Test
public void demo01(){
    List<Dish> collect = dishList.stream().collect(Collectors.toList());
    System.out.println(collect);
}
```

### 三、JDK8提供的预定义收集器
官方为我们提供了预定义的收集器的一些常用的必要功能，分别有：
- 元素规约与汇总
- 元素分组
- 元素分区

#### 1. 计算元素的个数counting()
counting()用于总结流中元素的个数，有两种写法，分别如代码所示。counting()使用起来还是比较简单的。
```java
/**
 * cn.liweidan.collect.Demo01#demo02()
 * counting()使用
 */
@Test
public void demo02(){
    /* 查询卡路里大于400的菜单的个数 */
    Long count = dishList.stream().filter(dish -> dish.getColories() > 400).collect(Collectors.counting());
    System.out.println("卡路里大于400的菜单个数：" + count);
    
    /* 第二种写法 */
    count = dishList.stream().filter(dish -> dish.getColories() > 400).count();
    System.out.println("卡路里大于400的菜单个数：" + count);
}
```

#### 2.查找流中的元素某个属性的最大值或者最小值
我们通常需要去拿到一个对象集合中对象某个属性进行查询最大和最小值，按照JDK8以前的写法是需要先去遍历集合中所有的对象，去读取某个属性的值，然后去记录最大值或者最小值进行记录，全部遍历完成以后把该值进行返回。这种写法，描述起来也麻烦，写起来也麻烦。

JDK8的流中就可以比较方便的拿到上面的需求。只需要在流中定义需要什么东西，当流完成以后就可以取到所需要的值了。不过我们需要先定义一个比较器，来告诉JVM我们需要个什么值。

```java
/**
 * cn.liweidan.collect.Demo01#demo03()
 * 取出最大值以及最小值
 */
@Test
public void demo03(){
    /* 定义一个卡路里比较器 */
    Comparator<Dish> comparator = Comparator.comparingInt(Dish::getColories);
    /* Collectors.maxBy(comparator)即取出流中的最大值 */
    Optional<Dish> collect = dishList.stream().collect(Collectors.maxBy(comparator));
    System.out.println(collect.get());
    /* Collectors.minBy(comparator)即取出流中的最小值 */
    Optional<Dish> collect1 = dishList.stream().collect(Collectors.minBy(comparator));
    System.out.println(collect1.get());
}
```
不过有时候我们需要最大值以及最小值在一个流中取出，这时候我们可以使用后面的分组进行实现。

#### 4. 汇总
汇总即对集合中元素的某个属性进行统计，如菜单中的所有卡路里的汇总。
```java
/**
 * cn.liweidan.collect.Demo01#demo04()
 * 汇总：对集合中所有菜单的卡路里进行统计计算
 */
@Test
public void demo04(){
    int collect = dishList.stream().collect(Collectors.summingInt(Dish::getColories));
    System.out.println(collect);
}
```
示例中我们使用了summingInt方法，当然Collectors还提供了Long和Double方法对Long和Double进行统计。

汇总不仅仅包括sum，还包括了平均数、最大值最小值等等。Collectors同时还定义了averagingInt以及IntSummaryStatistics来分别拿出元素属性的均值和所有统计数据（包括最大值、最小值、均值等）

```java
/**
 * 查询菜单集合中卡路里的平均值以及所有统计数据
 */
@Test
public void demo05(){
    /* 查询所有菜单卡路里的平均值 */
    Double collect = dishList.stream().collect(Collectors.averagingInt(Dish::getColories));
    System.out.println("卡路里均值：" + collect);
    /* 查询菜单中所有的汇总数据 */
    IntSummaryStatistics collect1 = dishList.stream().collect(Collectors.summarizingInt(Dish::getColories));
    System.out.println(collect1);// IntSummaryStatistics{count=9, sum=4200, min=120, average=466.666667, max=800}
}
```

#### 5. joining连接字符串
joining方法可以把自动调用对象的toString方法，然后把字符串连接在一起，如果需要使用分隔符，只要把分隔符传递给该方法就可以了。
```java
/**
 * joining连接字符串
 */
@Test
public void demo06(){
    String collect = dishList.stream().map(Dish::getName).collect(Collectors.joining(", "));
    System.out.println(collect);
    // pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon
}
```

#### 6. 实现自定义归约--reduce使用
像蚊帐之前讲的均值、最值这些操作，其实都是官方对reduce常用方法的封装，如果官方提供的这些方法不能够满足要求的话，那么就需要我们自己来自定义reduce的实现了。

reduce需要传入三个参数：
- 第一个参数是规约操作的起始值，即如果要统计总值的时候，那么起始值是0
- 第二个参数就是要调用的对象的方法了，即菜单的卡路里值
- 第三个参数就是一个BinaryOperator操作了，在这里定义我们拿到的值的操作方式。即相加。

- 也可以直接只传需要的操作，去除前两个参数。

```java
/**
 * cn.liweidan.collect.Demo01#demo07()
 * Collectors.reducing的使用
 */
@Test
public void demo07(){
    /**
     * 取出卡路里最大的菜单
     */
    Optional<Dish> collect = dishList.stream().collect(Collectors.reducing((d1, d2) -> d1.getColories() > d2.getColories() ? d1 : d2));
    System.out.println(collect.get());

    /**
     * 计算菜单总卡路里值
     */
    Integer integer1 = dishList.stream().collect(Collectors.reducing(0,// 初始值
            Dish::getColories,// 转换函数
            Integer::sum));// 累积函数
    System.out.println(integer1);

    Integer integer2 = dishList.stream().map(Dish::getColories).reduce(Integer::sum).get();
    System.out.println(integer2);

    int sum = dishList.stream().mapToInt(Dish::getColories).sum();// 推荐
    System.out.println(sum);

}
```
在计算总和的时候，推荐使用mapToInt，因为可以免去自动装箱拆箱的性能消耗。

### 四、分组
#### 1. 简单分组
我们经常需要对数据进行分组，特别是在数据库操作的时候。当我们需要从一个集合中进行分组，代码会变得十分复杂，分组功能刚好能够解决这个问题。我们可以对菜单中的类型进行分组，也可以根据卡路里的大小对菜单进行自定义的分组。
```java
/**
 * 简单分组
 */
@Test
public void test01(){
    /** 按照属性类型进行分组 */
    Map<Dish.Type, List<Dish>> collect = dishList.stream().collect(Collectors.groupingBy(Dish::getType));
    System.out.println(collect);
    // {FISH=[Dish(name=prawns, vegetarain=false, colories=300, type=FISH),
    // Dish(name=salmon, vegetarain=false, colories=450, type=FISH)],
    // OTHER=[Dish(name=french fries, vegetarain=true, colories=530, type=OTHER),
    // Dish(name=rice, vegetarain=true, colories=350, type=OTHER),
    // Dish(name=season fruit, vegetarain=true, colories=120, type=OTHER),
    // Dish(name=pizza, vegetarain=true, colories=550, type=OTHER)],
    // MEAT=[Dish(name=pork, vegetarain=false, colories=800, type=MEAT),
    // Dish(name=beef, vegetarain=false, colories=700, type=MEAT), Dish(name=chicken, vegetarain=false, colories=400, type=MEAT)]}

    /** 自定义简单的分组方式 */
    Map<CaloricLevel, List<Dish>> map = dishList.stream().collect(Collectors.groupingBy(d -> {
        /** 此处写if的时候注意要顾及到所有的情况 */
        if(d.getColories() <= 400){
            return CaloricLevel.DIET;
        }else if (d.getColories() <= 700){
            return CaloricLevel.NORMAL;
        } else {
            return CaloricLevel.FAT;
        }
    }));
    System.out.println(map);
    // {FAT=[Dish(name=pork, vegetarain=false, colories=800, type=MEAT)],
    // NORMAL=[Dish(name=beef, vegetarain=false, colories=700, type=MEAT), Dish(name=french fries, vegetarain=true, colories=530, type=OTHER), Dish(name=pizza, vegetarain=true, colories=550, type=OTHER), Dish(name=salmon, vegetarain=false, colories=450, type=FISH)],
    // DIET=[Dish(name=chicken, vegetarain=false, colories=400, type=MEAT), Dish(name=rice, vegetarain=true, colories=350, type=OTHER), Dish(name=season fruit, vegetarain=true, colories=120, type=OTHER), Dish(name=prawns, vegetarain=false, colories=300, type=FISH)]}
}
```

#### 2. 多级分组
如果我们需要进行多级分组，比如根据菜单的类型分组的情况下又要根据卡路里大小进行分组。那么我们可以在groupingBy中再传入第二个groupingBy。
```java
/**
 * 多级分组
 */
@Test
public void test02(){
    Map<Dish.Type, Map<CaloricLevel, List<Dish>>> collect = 
            dishList.stream().collect(Collectors.groupingBy(Dish::getType, 
                    Collectors.groupingBy(d -> {
        if (d.getColories() <= 400) {
            return CaloricLevel.DIET;
        } else if (d.getColories() <= 700) {
            return CaloricLevel.NORMAL;
        } else {
            return CaloricLevel.FAT;
        }
    })));
    System.out.println(collect);
}
```

#### 4. 按子组收集数据
在上一节的第二个参数传递的是一个groupingBy，但是收集器的第二个参数可以传入其他的收集器，以便可以达到手机子组数据的目的。比如我们可以计算每种菜单分类的个数，传入一个counting
```java
/**
 * 多级分组收集数据
 */
@Test
public void test03(){
    /** 计算每一种品类的菜单个数 */
    Map<Dish.Type, Long> typeLongMap = dishList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.counting()));
    System.out.println(typeLongMap);
}
```

#### 5. 按照谓词分区
partitioningBy：通过传递一个条件只有true和false的结果的表达式，返回的结果中包括true（满足条件）的集合以及false（不满足条件）的集合。
比如，筛选出来质数以及非质数，那么我们可以传递一个表达式或者一个方法，返回的是true和false，true表示是质数，false表示非质数的集合。和按子组收集数据的区别就是，这里还可以收集到不满足条件的所有元素集合。
```java
/**
 * 将数字按质数以及非质数分区
 */
@Test
public void test08(){
    int demoInt = 100;
    Map<Boolean, List<Integer>> collect = IntStream.rangeClosed(2, demoInt).boxed()
            .collect(Collectors.partitioningBy(candidate -> isPrime(candidate)));
    System.out.println(collect);
}

public boolean isPrime(int candidate){
    /** 通过传递的数字进行开方，我们只需要对传递的数字与开方的数字进行比对即可，计算次数会减少 */
    int candidateRoot = (int) Math.sqrt((double) candidate);
    /** 产生一个从2开始到开方跟的数字的数据流，与该数据流的每一个元素进行求余 */
    return IntStream.rangeClosed(2, candidateRoot)
            .noneMatch(i -> candidate % i == 0);// 表示没有一个元素与开方根的数字求余等于0的
}
```

### 五、Collect静态工厂方法表
|工厂方法|返回类型|用途|示例|
|---------|---------|-----|----|
|toList|List<T>|把流中所有的项目收集到List|`dishList.collect(Collectors.toList())`|
|toSet|Set<T>|把流中所有的项目收集到Set|`dishList.collect(Collectors.toSet())`|
|toCollection|Collection<T>|把流中所有项目收集到给定的供应源创建的集合|`dishList.collect(Collectors. toCollection(), ArrayList::new)`|
|counting|Long|计算出来流中元素的个数|`dishList.collect(Collectors.counting)`|
|summingInt|Integer|计算出来集合中元素的某个属性的和|`int collect = dishList.stream().collect(Collectors.summingInt(Dish::getColories));`
|averagingInt|Double|计算出集合中元素某个属性的均值|`Double collect = dishList.stream().collect(Collectors.averagingInt(Dish::getColories));`
|summarizingInt|IntSummaryStatistics|计算出集合中元素某个属性的统计值，包括最值、均值、总和等|`IntSummaryStatistics collect1 = dishList.stream().collect(Collectors.summarizingInt(Dish::getColories));`
|joinging|String|连接流中每个元素调用toString进行拼接，使用传递的分隔符进行分割|`String collect = dishList.stream().map(Dish::getName).collect(Collectors.joining(", "));`
|maxBy|Optional<T>|通过传递的比较器收集元素中属性最大的值，如果流为空则返回Optional.empty()|`Optional<Dish> collect = dishList.stream().collect(Collectors.reducing((d1, d2) -> d1.getColories() > d2.getColories() ? d1 : d2));`
|minBy|Optional<T>|通过传递的比较器收集元素中属性最小的值，如果流为空则返回Optional.empty()|略|
|reducing|归约操作产生的类型|从一个作为累加器的起始值开始，利用BinaryOperator与流中的元素逐个结合，从而将流规约为单个值|`Integer integer1 = dishList.stream().collect(Collectors.reducing(0,Dish::getColories,Integer::sum));`
|collectingAndThen|转换函数返回的类型|包裹另外一个收集器，对其结果进行转换|
|groupingBy|Map<K, List<T>>|对流中元素的每个值进行分组|略
|partitioningBy|Map<boolean, List<T>>|对流中元素的每个值进行分区|略

### 六、开发自定义收集器
#### 方式一
如果我们需要开发自己的自定义收集器的时候，需要让我们自己的收集器去实现Collector接口。
Collector接口一共有五个方法去自己实现，现在我们用开发我们自己的ToList收集器为例，写一个我们自己的收集器。
```java
package cn.liweidan.custom.collector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * <p>Desciption:自定义ToList收集器</p>
 * CreateTime : 2017/7/10 下午6:37
 * Author : Weidan
 * Version : V1.0
 */
public class MyListCollector<T> implements Collector<T, List<T>, List<T>> {

    /*
        第一个泛型指的是需要收集的流的泛型
        第二个泛型指的是累加器在收集时候的类型
        第三个泛型指的是返回的类型（可能不是集合，比如counting()）
     */

    /**
     * 建立一个新的结果容器
     * @return
     */
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    /**
     * 将元素累加到容器中去
     * @return
     */
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return (list, item) -> list.add(item);
    }

    /**
     * 对结果容器进行最终转换（如需要转换成Long返回，则在这一步体现）
     * @return
     */
    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity();// 此处无需进行转换，直接返回此函数即可
    }

    /**
     * 对每个子流中的数据进行规约操作
     * 即在集合流中，处理器会将集合流进行不停地分割，分割到一定的很多的小子流的时候，再进行操作
     * 在这一步就是将每一个小流中的元素合并到一起
     * @return
     */
    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list1, list2) ->{
            list1.addAll(list2);
            return list1;
        };
    }

    /**
     * 这个方法是定义流返回的情况，一共有三种情况，存放于Characteristics枚举中
     * UNORDERED：规约结果不受项目的遍历和累计顺序的影响
     * CONCURRENT：accumulator函数可以从多个线程去调用。如果收集器没有标记UNORDERED那他仅用在无需数据源才可以规约
     * INDENTITY_FINISH：表明完成器方法返回的是一个恒等函数，可以跳过。标记这种情况则表示累加器A可以不加检查的转换为累加器B
     * @return
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT, Characteristics.IDENTITY_FINISH));
    }
}

```

现在对我们自己的收集器进行测试，这里与自带的收集器的区别就是我没有定义工厂模式去拿到toList收集器的实例，而是需要自己手动new出来。
```java
@Test
public void test(){
    List<Dish> collect = dishList.stream().collect(new MyListCollector<Dish>());
    System.out.println(collect);
}
```

#### 方式二
方式二比较简单，但是功能也稍微差一点。就是通过使用collect方法的重载方法进行自定义收集器，并不需要去实现Collector接口。
```java
/**
 * 使用方式二进行自定义收集
 */
@Test
public void test02(){
    ArrayList<Object> collect = dishList.stream().collect(
            ArrayList::new, // 相当于方式一的supplier()方法，用于创建一个容器
            List::add,// 相当于方式一的accumulator方法，用于迭代遍历每个元素进行加入容器
            List::addAll// 规约并行中所有的容器
    );
    System.out.println(collect);
}
```

另外值得注意的是，这个方法并不能传递任何关于characteristics的信息，也就是说，默认已经给我们设定为INDENTITY_FINISH以及CONCURRENT了。


### 七、开发自己的质数收集器
在前面我们已经试验过一个质数收集器了，在这里使用自定义收集器再收集一次一定范围内的质数。在之前，我们是使用小于被测数的平方根的数字进行对比，到了这里我们再做进一步的优化，就是只拿小于被测数的平方根的质数作为除数。

PrimeNumberCollector：
```java
package cn.liweidan.custom.collector2;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * <p>Desciption:质数收集器</p>
 * CreateTime : 2017/7/11 上午10:43
 * Author : Weidan
 * Version : V1.0
 */
public class PrimeNumberCollector implements Collector<Integer,
                                            Map<Boolean, List<Integer>>,
                                        Map<Boolean, List<Integer>>> {

    public static <A> List<A> takeWhile(List<A> list, Predicate<A> p){
        int i = 0;
        for (A a : list) {
            if(!p.test(a)){
                return list.subList(0, i);
            }
            i++;
        }
        return list;
    }

    /**
     * 拿到所有的质数，以及被测数字。取出小于被测数的平方根与所有质数比较，只拿被测数与小于平方根的质数做计算
     * @param primes
     * @param candidate
     * @return
     */
    public static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return takeWhile(primes, i -> i <= candidateRoot)
                .stream()
                .noneMatch(p -> candidate % p == 0);
    }

    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<Boolean, List<Integer>>(){{
            put(true, new ArrayList<>());
            put(false, new ArrayList<>());
        }};
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            acc.get(isPrime(acc.get(true), candidate))
                    .add(candidate);
        };
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return null;
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }
}

```

测试：
```java
@Test
public void test01(){
    Map<Boolean, List<Integer>> collect = IntStream.rangeClosed(2, 100).boxed().collect(new PrimeNumberCollector());
    System.out.println(collect);
}
```