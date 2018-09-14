
### 一、准备
代码：[jdk8-stream2](https://github.com/WeidanLi/jdk8-tutorial/tree/master/jdk8-stream2)

在上一次的JDK8集合流中，讲了集合流的基本使用。这次，讲解集合流对集合的高级功能：
- 筛选、切片和匹配
- 查找、匹配和归约
- 使用数值范围等数值流
- 从多个源创建流
- 无限流。

<!--more-->

我们还是使用上次的示例：
准备Dish（菜单）：
```java
package pojo;

import lombok.*;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午2:51
 * Author : Weidan
 * Version : V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Dish {

    /** 名字 */
    private String name;
    /** 是否素食 */
    private boolean vegetarain;
    /** 卡路里 */
    private int colories;
    /** 类型 */
    private Type type;

    public enum Type {MEAT, FISH, OTHER};

}

```

以及获取很多菜单的工具类。
```java
package cn.liweidan.common.utils;

import cn.liweidan.pojo.Dish;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午2:55
 * Author : Weidan
 * Version : V1.0
 */
public class DishUtils {

    public static List<Dish> getDishes(){
        return Arrays.asList(new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH));
    }

}

```

### 二、筛选和切片
#### 1. 使用谓词进行筛选（filter）
这个方法在上一节中大部分的进行讲解。这里不再多说一次。
```java
/**
 * 使用Filter进行筛选
 * @param
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testFilter(){
    /** 筛选素食菜单 */
    List<String> collect = dishList.stream()
            .filter(Dish::isVegetarain)
            .map(Dish::getName)
            .collect(Collectors.toList());
    System.out.println(collect);// [french fries, rice, season fruit, pizza]

}
```

#### 2. 筛选各异的元素（distinct）
在流中，如果我们进行筛选以后的元素是存在相同的情况下，但是我们不需要列出相同的元素只是需要不同的元素的时候即可使用distinct进行筛选出来各异的元素，相当于MySQL中的GroupBy操作。
```java
/**
 * 筛选出来各异的元素, 从数组中提取出来大于1的元素，不要重复
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testDistinct(){
    List<Integer> integerList = Arrays.asList(1, 2, 3, 2);
    List<Integer> collect = integerList.stream()
            .filter(i -> i > 1)
            .distinct()
            .collect(Collectors.toList());
    System.out.println(collect);// [2, 3]
}
```
调用Distinct方法的时候，相当于Java8在遍历integerList并且进行过滤的时候，会把相同的元素只是通过第一个，其他的拦截在中间环节，然后进行收集，即可得到去重的元素集合。


#### 3. 截断流limit()
这个方法是可以取得筛选后的元素的前几个元素，把需要的个数传给该方法。有点类似于MySQL的分页查询，但是又不能进行分页的方法，因为在第一次讲过，流用过一次即消失，并不能重复使用。
例如我们需要取得素食菜单的前三个元素的时候，即可在中间操作的时候是用Limit()方法。
```java
/**
 * 提取菜单中前三个素食菜单的名字
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testLimit(){
    List<String> collect = dishList.stream()
            .filter(Dish::isVegetarain)// [french fries, rice, season fruit, pizza]
            .limit(3)// [french fries, rice, season fruit]
            .map(Dish::getName)
            .collect(Collectors.toList());
    System.out.println(collect);
}
```

#### 4. 跳过元素skip()
在流的使用中，如果我们确切的第n个元素不要，即可通过调用skip方法并且把n传递给这个方法即可跳过第n个元素。
```java
/**
 * 提取菜单中素食菜单的名字 并且跳过第2个元素
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testSkip(){
    List<String> collect = dishList.stream()
            .filter(Dish::isVegetarain)// [french fries, rice, season fruit, pizza]
            .skip(1)// [rice, season fruit, pizza]
            .map(Dish::getName)
            .collect(Collectors.toList());
    System.out.println(collect);
}
```

### 三、映射（map和flatMap）
#### 1. map映射
map映射是可以提取一个集合中，某个元素的某个属性的映射。比如提取所有菜单的名字的长度，即可调用两次map进行提取，第一次提取到了所有菜单的名字，返回了Stream<String>流，然后再通过调用String中的length（）方法进行获取所有字符串的长度。
```java
/**
 * 获取所有菜单名字的长度
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testMap(){
    List<Integer> collect = dishList.stream()
            .map(Dish::getName)// 首先获取所有菜单名字
            .map(String::length)// 再通过菜单名字的流获取长度并且收集起来
            .collect(Collectors.toList());
    System.out.println(collect);// [4, 4, 7, 12, 4, 12, 5, 6, 6]
}
```

#### 2. flatMap映射
flatMap和Map的区别就是可以把多个流合并在一起进行一次操作，比如在一个单词数组中，通过调用String的split方法获取到了多个字符串数组的流，这时候我们需要对这些流进行整体的筛选，即可通过flatMap把所有流中的内容进行合并，然后集中处理，犹如物流链中的流水线进行合并，这时候在合并后的总流中只需要一个工人即可把其他所有流水线中的快递进行筛选。
```java
/**
 * 将多个流合并成一个流 flatMap
 * 需求：将["Hello", "World"]分成各不相同的字符
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testFlatMap(){
    // Method 1
    List<String> strings = Arrays.asList("Hello", "World");
    List<String[]> collect = strings.stream()
            .map(w -> w.split(""))
            .distinct()
            .collect(Collectors.toList()); // 来到这一步的时候发现拿出来的是两个数组集合
    /*
        这是因为在流的过程中，调用String的切管方法，得到的时候两个单词的数组的流，也就是Stream<String[]>的形式，但这个并不是我们想要的
    */
    System.out.println(collect);// [[Ljava.lang.String;@2f333739, [Ljava.lang.String;@77468bd9]

    // Method 2
    List<Stream<String>> collect1 = strings.stream()
            .map(w -> w.split(""))
            .map(Arrays::stream)
            .collect(Collectors.toList());// 拿到的是String流的集合。这也不是我们想要的

    // Method 3 flatMap
    List<String> collect2 = strings.stream()
            .map(w -> w.split(""))
            .flatMap(Arrays::stream)
            .distinct()
            .collect(Collectors.toList());
    System.out.println(collect2);// [H, e, l, o, W, r, d]
    /*
    flatMap 可以把多个流合并成一个流进行操作。
     */
}
```

### 四、匹配查找
这部分比较简单，分为anyMatch、allMatch、noneMatch以及一个查找的方法findAny。分别代表的意思就是部分符合、全部符合、没有一个符合以及查找符合条件中的一个，findAny和findFirst的区别就是，findAny会返回一个符合条件的，可能并不是第一个，而findFirst则会返回第一个。如果对顺序没有太大要求的话可以通过调用findAny进行处理。
```java
package cn.liweidan.demo;

import cn.liweidan.common.utils.DishUtils;
import cn.liweidan.pojo.Dish;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Desciption:查找和匹配</p>
 * CreateTime : 2017/6/12 上午10:29
 * Author : Weidan
 * Version : V1.0
 */
public class Test03 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 部分匹配：检查菜单中是否含有素食菜
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testAnyMatch(){
        boolean anyMatch = dishList.stream()
                .anyMatch(Dish::isVegetarain);
        if(anyMatch){
            System.out.println("这个菜单中含有素食品");
        }
    }

    /**
     * 所有匹配：检查菜单中所有菜单是否卡路里都小于1000
     */
    @Test
    public void testAllMatch(){
        if (dishList.stream().allMatch(d -> d.getColories() < 1000)) {
            System.out.println("所有菜单的卡路里都小于1000");
        }
    }

    /**
     * 所有不匹配：是否所有菜单都没有大于1000卡路里的
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testNoneMatch(){
        if (dishList.stream().noneMatch(d -> d.getColories() >= 1000)) {
            System.out.println("所有菜单卡路里都小于1000");
        }
    }

    /**
     * 查找任意一个符合条件元素
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testFindAny(){
        dishList.stream()
                .filter(Dish::isVegetarain)
                .findAny()
                .ifPresent(d -> System.out.println(d.getName()));
        /*
        Optional: isPresent() 是否存在
                ifPresent(Consumer) 如果存在执行参数中的方法
                get()获取确切的值
                orElse(T other) 如果不存的时候 返回传入的值
          加入这个接口是为了解决长久以来的NULL异常
         */
    }
}

```
而这个部分有些代码是拿到一个Optional返回值的，Optional是孙公司为了解决一些存在空值的问题而设置的一个类，我们可以通过orElse来解决返回值不存在的情况的返回，这样就不会拿到一个null值，从而可以解决空指针异常。


### 五、归约：求和、求最大、求最小
这部分看起来也不太难，建议使用的时候提供一个默认值以及通过调用Integer的静态方法来处理，一来呢可以防止空指针，二来呢阅读起来也非常方便。直接上代码吧：
```java
package cn.liweidan.demo;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <p>Desciption:归约：求和、求最大、求最小</p>
 * CreateTime : 2017/6/12 上午11:28
 * Author : Weidan
 * Version : V1.0
 */
public class Test04 {

    @Test
    public void testReduce01(){
        List<Integer> numbers = Arrays.asList(4, 5, 3, 9);
        /** 求和 */
        Integer sum1 = numbers.stream().reduce(0, (a, b) -> a + b);// 如果不存在值则返回默认值0
        Integer sum2 = numbers.stream().reduce(0, Integer::sum);// 如果不存在值则返回默认值0
        // 相当于numbers.stream().reduce((a, b) -> a + b).orElse(0)
        Optional<Integer> sumOptional = numbers.stream().reduce((a, b) -> a + b);// 返回Optional进行Null的判断
        /** 最大值 */
        Optional<Integer> max1 = numbers.stream().reduce((a, b) -> a > b ? a : b);
        Optional<Integer> max2 = numbers.stream().reduce(Integer::max);
        /** 最小值 */
        Optional<Integer> min1 = numbers.stream().reduce(Integer::min);
        System.out.println(sum2);
        System.out.println(max2.get());
        System.out.println(min1.get());
    }

}

```

### 六、流的转换
也许我们在流中设计计算的时候需要把Integer的值转成int值进行计算，然后又要转会Integer类进行赋值，这其中就涉及装箱和拆箱操作，如果没有权衡好的话，装箱和拆箱这个动作会消耗掉很多的资源。所以JDK8涉及了对应的流。分别有IntStream、DoubleStream以及LongStream。

所以当我们需要计算所有菜单的卡路里总和的时候需要把每个菜单的卡路里转换成int类型的属性，然后再进行计算。
```java
/**
 * 计算所有菜单的卡路里总和
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testNumberStream(){
    int sum = dishList.stream()
            .mapToInt(Dish::getColories)
            .sum();
    System.out.println(sum);
}

/**
 * 默认值OptionalInt，同样的求和还是返回提供判空的Optional对象
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testOptionalInt(){
    OptionalInt max = dishList.stream()
            .mapToInt(Dish::getColories)
            .max();
    System.out.println(max.orElse(1));
}
```

当我们需要进行装箱的时候，三个流又提供了boxed()方法进行装箱。
```java
/**
 * 转换为对象流
 * @author Weidan
 * @version 1.0
 * @return
 */
@Test
public void testToObj(){
    IntStream intStream = dishList.stream()
            .mapToInt(Dish::getColories);
    Stream<Integer> boxed = intStream.boxed();
}
```

### 七、IntStream、DoubleStream、LongStream的生成范围数值。
数值流提供了两个方法，range()以及rangeClosed()，第一个是不包含边缘值的，也就是说，不包含传递的两个数值，而第二个方法是包含传递的数值的。
下面就以生成1-100之间的数生成勾股数来体验一下这个方法。

```java
/**
 * 生成勾股数组
 */
@Test
public void testRange(){
    Stream<int[]> stream = IntStream.rangeClosed(1, 100).boxed()// 生成1-100的int数并且进行装箱
            .flatMap(a ->// 把每个a进行流的扁平化，即所有流放在一起
                    IntStream.rangeClosed(a, 100)// 继续生成第二个数字
                            .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)// 如果第三个数也是整数的话
                            .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)})// 返回数组对象
            );
    stream.forEach(t -> System.out.println(t[0] + "," + t[1] + "," + t[2]));

    /*
    当然以上的操作进行了两次开方以及平方计算，这时候我们进行优化，只要一次性生成所有，然后通过filter去筛选出来。
     */
    
    Stream<double[]> stream1 = IntStream.rangeClosed(1, 100).boxed()
            .flatMap(a ->
                    IntStream.rangeClosed(a, 100)
                            .mapToObj(b -> new double[]{a, b, (double) Math.sqrt(a * a + b * b)})
                            .filter(t -> t[2] % 1 == 0)
            );
    stream1.forEach(t -> System.out.println(t[0] + "," + t[1] + "," + t[2]));

}
```

### 八、流的生成
以上我们生成流的方法都是通过集合来生成一个集合流，但是不仅仅是集合可以生成流的。我们可以使用值、数组、以及文件来生成一个流。
```java
/**
 * 由值创建流
 */
@Test
public void test01(){
    Stream<String> stringStream = Stream.of("Java 8", "Lambdas", "In", "Action");
}

/**
 * 由数组创建
 */
@Test
public void test02(){
    int[] numbers = {1, 2, 3, 4, 5, 6};
    Arrays.stream(numbers);
}

/**
 * 由文件中产生
 */
@Test
public void test03(){
    long uniqueWords = 0;
    try (Stream<String> lines = Files.lines(Paths.get("/Users/liweidan/Java/workspace/java-jdk8-stream2/src/test/resources/Test08.java"), Charset.defaultCharset())){
        uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
                            .distinct()
                            .count();
        System.out.println(uniqueWords);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

以及我们可以通过Stream的静态方法iterate和generate来生成一个流。
```java
/**
 * iterate生成无限流
 */
@Test
public void test04(){
    // 代表从0开始，每隔2就生成一次数值，这个流是个无限流。
    Stream.iterate(0, n -> n+2)
            .limit(10)
            .forEach(System.out::println);
}

/**
 * 斐波那契数列
 * 0，1，1，2，3，5，8...
 */
@Test
public void test05(){
    Stream.iterate(new int[]{0, 1}, n -> new int[]{n[0] + n[1], n[0] + n[1] + n[1]})
            .limit(10)
            .forEach(t -> System.out.println(t[0] + "," + t[1]));
}

/**
 * generate生成
 */
@Test
public void test06(){
    Stream.generate(Math::random)
            .limit(5)
            .forEach(System.out::println);
}

/**
 * 利用generate生成斐波那契数列
 * 并行的时候不安全
 */
@Test
public void test07(){
    IntSupplier fib = new IntSupplier() {
        int previous = 0;
        int current = 1;
        @Override
        public int getAsInt() {
            int oldPrev = this.previous;
            int nextValue = this.previous + this.current;
            this.previous = this.current;
            this.current = nextValue;
            return oldPrev;
        }
    };

    IntStream.generate(fib).limit(10).forEach(System.out::println);
}
```