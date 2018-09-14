# jdk8 集合流中间操作


示例代码：[jdk8-stream](https://github.com/WeidanLi/jdk8-tutorial/tree/master/jdk8-stream)
### 零、准备

这一个部分，我们准备了菜单。包含菜单名字、是否是素食、卡路里数量以及菜单类型四个属性，使用了Lombok自动加入GETTER&SETTER这些元素。
```java
package cn.liweidan.pojo;

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

并且准备了一些菜单。
```java
package cn.liweidan.utils;

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

### 一、集合的处理
在jdk7以及以前，如果我们需要从一个集合中取出一个符合我们所需要条件的变量的时候，就需要去遍历这个元素中的每一个元素，然后使用`if`语句进行判断，如果符合我们所要的条件，就把这个元素放入另外一个集合中去。

<!--more-->

#### 1. 串行流
例如：
```java
public class Demo01 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 取出卡路里大于300的菜单。
     */
    @Test
    public void test01(){
        List<Dish> result = new ArrayList<>();
        for (Dish dish : dishList) {
            if(dish.getColories() > 300){
                result.add(dish);
            }
        }
        System.out.println(result);
    }

}
```
当然如果有多个条件的话我们都需要加到 `if`语句中去，这样子看起来`if`语句会很长，也不会阅读。
所以到了JDK8引入流的模式和Lambda表达式，一切就变得简单了。
例如：
```java
public class Demo01 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 取出卡路里大于300并且类型为FISH的菜单
     */
    @Test
    public void test01(){
        List<Dish> collect = dishList.stream() // 获取该集合的流对象
                .filter(dish -> dish.getColories() > 300) // 大于300卡路里
                .filter(dish -> dish.getType().equals(Dish.Type.FISH)) // 类型是鱼的
                .collect(Collectors.toList()); // 我要封装成一个集合进行返回
        System.out.println(collect);// [Dish(name=salmon, vegetarain=false, colories=450, type=FISH)]
    }
}
```
一切就变得十分简单了。
当然想象起来也不会太难，你就当拿到这个流的时候，把集合中所有元素都放在了流水线上面去，第一个filter相当于一个工人，他筛选出来了擦鲁丽大于300的菜单，第二个filter就相当于第二个工人，他把类型是鱼的元素再取出来，其他的不要了。然后collect相当于到了流水线的末端了，把他包装成一箱（集合）拿出来，你就可以得到你想要的结果了。

#### 2. 并行流
当然如果你的集合很大，一条流水线貌似太慢了，你可以使用多条流水线：
```java
/**
 * 取出卡路里大于300并且类型为FISH的菜单(多线程)
 */
@Test
public void test02(){
    List<Dish> collect = dishList.parallelStream() // 获取该集合的流对象(多条流水线并行处理)
            .filter(dish -> dish.getColories() > 300) // 大于300卡路里
            .filter(dish -> dish.getType().equals(Dish.Type.FISH)) // 类型是鱼的
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回
    System.out.println(collect);// [Dish(name=salmon, vegetarain=false, colories=450, type=FISH)]
}
```
注意到这次取出流的方式是不同的，是通过`parallelStream()`来取出并行流。

#### 3. 时间对比
我对上面刚刚两个流进行了时间上消耗的测试。
```java
/**
 * 取出卡路里大于300并且类型为FISH的菜单
 */
@Test
public void test01(){
    long l = System.nanoTime();
    List<Dish> collect = dishList.stream() // 获取该集合的流对象
            .filter(dish -> dish.getColories() > 300) // 大于300卡路里
            .filter(dish -> dish.getType().equals(Dish.Type.FISH)) // 类型是鱼的
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回
    System.out.println(collect);// [Dish(name=salmon, vegetarain=false, colories=450, type=FISH)]
    System.out.println((System.nanoTime() - l) / 1000000);// 118ms
}

/**
 * 取出卡路里大于300并且类型为FISH的菜单(多线程)
 */
@Test
public void test02(){
    long l = System.nanoTime();
    List<Dish> collect = dishList.parallelStream() // 获取该集合的流对象(多条流水线并行处理)
            .filter(dish -> dish.getColories() > 300) // 大于300卡路里
            .filter(dish -> dish.getType().equals(Dish.Type.FISH)) // 类型是鱼的
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回
    System.out.println(collect);// [Dish(name=salmon, vegetarain=false, colories=450, type=FISH)]
    System.out.println((System.nanoTime() - l) / 1000000);// 4ms
}
```
使用串行流的时候，消耗的时间是118毫秒，而使用并行流的时候，使用的时间是4毫秒。这个时间上的差距还是很明显的。

但是是不是意味着我们可以一味的使用并行流呢？答案是**不行的**。因为如果我们的集合都是简单的数据的时候，我们我们开启并行流的开销可能要比节省的时间要来得多，这时候就导致我们要的效果反而更差了。

比如我只要从一个存放Integer的集合中拿出大于一个数的集合，并且这个集合只有3个元素。那么并行流就显得不那么矫健了。
```java
package cn.liweidan.demo1;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午3:46
 * Author : Weidan
 * Version : V1.0
 */
public class Demo02 {

    List<Integer> integerList = new ArrayList<>();

    @Before
    public void before(){
        integerList.add(10);
        integerList.add(20);
        integerList.add(30);
    }

    @Test
    public void test01(){
        long start = System.nanoTime();
        integerList.parallelStream().filter(integer -> integer > 25).collect(toList());
        System.out.println((System.nanoTime() - start));// 133276458
    }

    @Test
    public void test02(){
        long start = System.nanoTime();
        integerList.stream().filter(integer -> integer > 25).collect(toList());
        System.out.println((System.nanoTime() - start));// 338833
    }
}

```

#### 4. map方法进行提取集合中元素的某个属性
在遍历集合的时候，有时候你只要提取出来符合条件的元素中的某个属性，比如，我只要提取出来菜单集合中卡路里大于300的所有菜单的名字，这个时候就可以使用map这个方法了。
```java
/**
 * 取出卡路里大于300并且类型为FISH的菜单的名字
 */
@Test
public void test01(){
    List<String> collect = dishList.stream() // 获取该集合的流对象
            .filter(dish -> dish.getColories() > 300) // 大于300卡路里
            .filter(dish -> dish.getType().equals(Dish.Type.FISH)) // 类型是鱼的
            .map(dish -> dish.getName())// 获得符合菜单的元素的名字
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回
    System.out.println(collect);// [salmon]
}
```

#### 5. 流只能迭代一次
流在使用的时候，只能迭代一次，迭代完成以后，这个流就被关闭了，如果再进行另一次迭代，那么程序将会抛出异常`IllegalStateException: stream has already been operated upon or closed`
```java
@Test
public void test01(){
    Stream<Dish> stream =
            dishList.stream();
    stream.forEach(dish -> System.out.println(dish));
    stream.forEach(dish -> System.out.println(dish));// stream has already been operated upon or closed
}
```
这是为什么呢，这是因为流在迭代的过程中是以延迟的方式进行的。换句话说，就是流在迭代到其中的一个元素的时候，后面的元素流对象是不知道的，这样有什么好处呢，比如你说取出**卡路里大于300的前三个元素**的时候，流迭代到符合条件的第三个即可进行关闭，节省了资源的开销。
当然**不是任何时候流使用过一次的时候都会消耗掉**，是在使用collect或forEach这样的终端操作的时候才会把流关闭，因为在中间操作（也就是筛选过程）中，流会把你想要的条件一个一个串起来，然后在终端操作的时候一次性返回。

### 二、集的操作（中间操作以及终端操作）
#### 1. 中间操作
##### 1）filter
筛选操作，上面例子用得很多了。

##### 2）sorted
对流中的元素根据指定的属性进行排序
```java
/**
 * 取出卡路里大于100并且根据卡路里数量进行排序
 */
@Test
public void test01(){
    List<Dish> collect = dishList.stream() // 获取该集合的流对象
            .filter(dish -> dish.getColories() > 300) // 大于100卡路里
            .sorted(Comparator.comparing(dish -> dish.getColories()))// 根据卡路里从小到大进行排序
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回

    System.out.println(collect);// [Dish(name=rice, vegetarain=true, colories=350, type=OTHER), 
    // Dish(name=chicken, vegetarain=false, colories=400, type=MEAT), 
    // Dish(name=salmon, vegetarain=false, colories=450, type=FISH), 
    // Dish(name=french fries, vegetarain=true, colories=530, type=OTHER), 
    // Dish(name=pizza, vegetarain=true, colories=550, type=OTHER), 
    // Dish(name=beef, vegetarain=false, colories=700, type=MEAT), 
    // Dish(name=pork, vegetarain=false, colories=800, type=MEAT)]]
}
```

##### 3）limit
对以上执行出来的结果取出前几个的值，比如对上面的需求出去前三个的值。则可以这么写：
```java
/**
 * 取出卡路里大于100并且根据卡路里数量进行排序
 */
@Test
public void test02(){
    List<Dish> collect = dishList.stream() // 获取该集合的流对象
            .filter(dish -> dish.getColories() > 300) // 大于100卡路里
            .sorted(Comparator.comparing(dish -> dish.getColories()))// 根据卡路里从小到大进行排序
            .limit(3)// 取出前三个的值
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回

    System.out.println(collect);// [Dish(name=rice, vegetarain=true, colories=350, type=OTHER),
    // Dish(name=chicken, vegetarain=false, colories=400, type=MEAT),
    // Dish(name=salmon, vegetarain=false, colories=450, type=FISH)
}
```

##### 4）map
map操作相当于告诉流要取出的元素的属性值。比如可以将以上的需求包装成，取出卡路里大于100的前三个菜单的名字。
```java
/**
 * 取出卡路里大于100并且根据卡路里数量进行排序 取出菜单的名字
 */
@Test
public void test03(){
    List<String> collect = dishList.stream() // 获取该集合的流对象
            .filter(dish -> dish.getColories() > 300) // 大于100卡路里
            .sorted(Comparator.comparing(dish -> dish.getColories()))// 根据卡路里从小到大进行排序
            .limit(3)// 取出前三个的值
            .map(Dish::getName)// 取出名字
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回

    System.out.println(collect);// [rice, chicken, salmon]
}
```
##### 5）distinct
对流中的元素的属性进行去重，比如我现在有两个菜单的名字是相同的，但是我只要进行归类，取出一个值就够了，即可调用distinct进行中间操作。
```java
@Test
public void test04(){
    List<Dish> dishes = Arrays.asList(new Dish("pork", false, 800, Dish.Type.MEAT),
            new Dish("beef", false, 700, Dish.Type.MEAT),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("season fruit", true, 120, Dish.Type.OTHER),
            new Dish("pizza", true, 550, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("salmon", false, 450, Dish.Type.FISH),
            new Dish("pork", false, 400, Dish.Type.MEAT));// 与第一个元素名字相同
    List<String> collect = dishes.stream() // 获取该集合的流对象
            .filter(dish -> dish.getColories() > 300) // 大于100卡路里
            .map(Dish::getName)
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回

    System.out.println(collect);// [pork, beef, chicken, french fries, rice, pizza, salmon, pork] 两个pork

    collect = dishes.stream() // 获取该集合的流对象
            .filter(dish -> dish.getColories() > 300) // 大于100卡路里
            .map(Dish::getName)
            .distinct()
            .collect(Collectors.toList()); // 我要封装成一个集合进行返回
    System.out.println(collect);// [pork, beef, chicken, french fries, rice, pizza, salmon] 去除了重复
}
```

#### 2. 终端操作
终端操作分为三类：forEach()、count()、collect()
##### 1）forEach
对每个元素进行操作，不做返回。
打印每个元素：
```java
/**
 * 打印集合中卡路里大于500的菜单
 */
@Test
public void test02(){
    /** 串行 */
    dishList.stream()
            .filter(dish -> dish.getColories() > 500)
            .forEach(dish -> System.out.println(dish));
    /*
    Dish(name=pork, vegetarain=false, colories=800, type=MEAT)
    Dish(name=beef, vegetarain=false, colories=700, type=MEAT)
    Dish(name=french fries, vegetarain=true, colories=530, type=OTHER)
    Dish(name=pizza, vegetarain=true, colories=550, type=OTHER)
     */

    /** 并行 */
    dishList.parallelStream()
            .filter(dish -> dish.getColories() > 500)
            .forEach(dish -> System.out.println(dish));
    /** 每次打印都不同 */
}
```

##### 2）count
统计最后流中的元素
```java 
/**
 * 统计集合中卡路里大于500的菜单数
 */
@Test
public void test01(){
    long count = dishList.stream()
            .filter(dish -> dish.getColories() > 500)
            .count();// 统计 返回long类型
    System.out.println(count);// 4
}
```

##### 3）collect
返回指定的格式，以上所有实例都是使用这个方法，不再重复。