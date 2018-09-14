
### 简介
jdk8其实是个优秀的版本，在集合、线程、时间日期等API进行了增强，以及加入Lambda表达式的开发，jdk一直被误解为慢、卡，但是我相信随着开发者的进步，jdk将会变得越来越好。

jdk8学习的初衷：
- 提高系统效率
- 提高开发效率
- 函数式编程的应用
- 增强多线程编程

### JDK8入门
JDK8默认最大的特性应该就是Lambda表达式了吧。先上线几个Lambda表达式进行体验一下。
代码我托管于GitHub社区：[jdk8-function](https://github.com/WeidanLi/jdk8-tutorial/tree/master/jdk8-function)
### 准备
为了测试，我们新建一个苹果，围绕着苹果开来展开需求的实现。苹果具有两个属性，一个颜色和一个重量。我们可以通过集合+我们自己的POJO类来实现。
实例是JDK8实战中的例子，如有侵犯，请告知删除。


```java
package cn.liweidan.jdk8.pojo;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/2 下午2:55
 * Author : Weidan
 * Version : V1.0
 */
public class Apple {
    private String color;
    private int wight;

    public Apple(String color, int wight) {
        this.color = color;
        this.wight = wight;
    }

    public Apple() {
    }

    public Apple(int wight) {
        this.wight = wight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getWight() {
        return wight;
    }

    public void setWight(int wight) {
        this.wight = wight;
    }

    @Override
    public String toString() {
        return "Apple{" +
                "color='" + color + '\'' +
                ", wight=" + wight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Apple apple = (Apple) o;

        if (wight != apple.wight) return false;
        return color != null ? color.equals(apple.color) : apple.color == null;
    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + wight;
        return result;
    }
}

```
### 一、Predicate接口
Predicate接口中定义了一个需要实现的方法test(T t)方法，还有JDK8特有的几个已经实现了的默认方法，打开Predicate里有一个注解，@FunctionalInterface，代表这个接口只能拥有一个未实现的方法，是一个函数接口。
现在，我们需要在我们的一个苹果集合中，拿出重量大于100的苹果。
如果是以前，那么我们就需要通过遍历一个集合，拿出来符合条件的苹果，然后进行返回。
```java
public static List<Apple> filterApple(List<Apple> appleList){
    List<Apple> apples = new ArrayList<>();
    for (Apple apple : appleList) {
        if(apple.getWight() > 100){
            apples.add(apple);
        }
    }
    return apples;
}
```
这种方式，可以实现现在的需求，但是当用户的需求改了以后，比如找出红色的苹果，那我们就需要重写里面的if方法的条件。不够灵活。
JDK8引入Predicate接口以后，我们就可以通过把这个条件进行封装，把经常改变需求的if里面的判断语句进行封装，变成Predicate接口，在调用filterApple的时候将条件传入，即可实现需求的变更。
JDK8的方法可以这么写:
```java
/**
 Predicate<T>接口：默认调用test方法，返回boolean值，传递的参数只有一个。
 在需要调用Lambeda表达式返回boolean的时候使用。
 */
public static List<Apple> filterApple(List<Apple> appleList, Predicate<Apple> predicate){
    List<Apple> apples = new ArrayList<>();
    for (Apple apple : appleList) {
        if(predicate.test(apple)){
            apples.add(apple);
        }
    }
    return apples;
}
```
Predicate即可通过Lambda表达式传入需要的条件，即可实现可变的过滤需求。
```java
public static void main(String[] args) {
    List<Apple> appleList = Arrays.asList(new Apple("RED", 80),
            new Apple("GREEN", 100),
            new Apple("BLACK", 150));
    // 重量大于100
    List<Apple> filterApple = filterApple(appleList, apple -> apple.getWight() > 100);
    // 红色的苹果
    filterApple = filterApple(appleList, apple -> apple.getColor().equals("RED"));
}
```
可以看到，我们已经可以把我们需要的苹果筛选出来了。
这个方法的调用，就是通过传入一个Predicate<Apple>实现，然后把apple.getWight() > 100返回给test(Apple apple)方法，test方法再进行返回给if语句，从而拿到我们需要的苹果，是不是比以前的方法简单。
所以当我们有个需求是需要可变的返回值boolean得需求的时候，即可使用Predicate接口。后面的JDK8的集合流中的filter()也是通过传入Predicate拿到boolean值进行筛选的。
**代码位置：cn.liweidan.jdk8.PredicateInterface中**

### 二、Consumer<T>接口
Consumer接口也是个函数式接口，只有该接口只含有一个未实现的accept(T t)方法。返回值是void，用于封装在日常生活中没有返回值情况的代码，比如遍历一个集合并且打印每个元素，我们即可通过Consumer接口实现一个forEach方法。接收一个集合并且使用Lambda书写需要对集合的操作，在Consumer参数中即可拿到每一个集合中的元素进行操作。
```java
/**
 * Consumer<T>提供了一个accept方法，返回void类型。
 */
public static <T>void forEach(List<T> list, Consumer<T> s){
    for (T t : list) {
        s.accept(t);
    }
}
```
调用该forEach方法：
```java
@Test
public void test01(){
    forEach(Arrays.asList("Lambde", "test", "stream"),
            s -> {
                System.out.println(s);
            });
}
```
Consumer意为消费者，在上面例子中，即拿到集合中所有的元素进行消费。
**代码位置：cn.liweidan.jdk8.ConsumerInterface**
### 三、Function<T, R>接口
Function就好玩了，可以自定义传入的类型以及传出的类型。T表示传入的类型，R表示传出的类型。
Function接口只有一个未实现的`R apply(T t)`方法，用于传入一个T类型的值，而传出一个R类型的值。比如我们现在需要把一组字符串的长度全部输出出来。在以往我们就需要通过遍历所有的元素，然后用一个集合去封装，在遍历中一个一个取出长度放入我们新建的集合当中去，然后将这个集合进行返回。
然而使用Function方法的时候，我们就可以很自由的进行取值。
现在我们来写一个map方法，用于取出集合中元素某一个属性的所有值并且进行返回。
```java
/**
 * Function<T, R>, T表示传入的类型，R表示返回的类型。即传入T类型的参数，返回R类型的参数
 * @param list
 * @param function
 * @param <T>
 * @param <R>
 * @return
 */
public static <T,R> List<R> map(List<T> list, Function<T, R> function){
    List<R> result = new ArrayList<>();
    for (T t : list) {
        result.add(function.apply(t));
    }
    return result;
}

@Test
public void test01(){
    List<String> stringList = Arrays.asList("lambda", "test", "javascript");
    List<Integer> map = map(stringList, s -> s.length());
    System.out.println(map);
}
```
**代码位置：cn.liweidan.jdk8.FunctionDemo**
运行的时候JDK8可以通过传入的集合的泛型是String属性，从而把String赋予给T，然后再根据我们需要取值的结果的属性值，在这里`s.length()`取出来的值是Integer类型的。所以R的类型就是Integer类型。把T和R带入Function中的两个泛型值，我们就可以发现其实就是和我们以前写的遍历取值是一样的，只不过是现在的方式可以更灵活。这里得益于Lambda表达式，通过`() -> expression`或者`() -> {statement;}`来表达我们需要的需求。

### 四、Lambda表达式
Lambda表达式的历史就不说了。这里说的是Lambda的格式，Lambda表达式的前半部分`(Apple a, Apple b)`表示传入给Lambda的参数值，也就是相当于我们方法中的参数。这个不难理解吧。然而后半部分就比较有讲究了。
后半部分`{}`或者直接书写`"result"`，用于表示返回值。在这里需要注意的是`{}`和`"result"`的区别，***`{}`表示书写Java语句***，也就是我们日常写的代码，比如上面的forEach方法中，我们就是通过`s -> {System.out.println(s);}`来写Java代码的，但是如果我们直接返回值，就不需要写花括号，只需要***直接把返回值放在后部分***即可。如Predicate接口中的`apple -> apple.getWight() > 100`后半部分直接用返回值返回boolean值。像函数式接口都支持Lambda的书写方式。
比如：
`() -> return "result";`和`() -> {"result"}`均是错误的写法，应该写成`() -> "result"`或者`() -> {return "result";}`
对于Lambda表达式我是这么去理解的，前半部分就是传入的值，后半部分就是我们需要执行的语句，函数式接口就相当于把我们需要执行的语句放入Lambda的后半部分一样。
这时候我们就需要来一个例子看看了，并且讲解函数式接口。
### 五、自定义函数式接口。
我们现在有一个需求，我需要有个方法可以自定义我们读取文件的内容的方法，但是前后的try-catch均让代码帮我封装了。我只要关心我读取的代码就可以了。
这里就涉及到了环绕的函数式编程了，但是这并不难，我们先来看看传统的方法：
```java
public static String processFileOld(BufferedReaderProcess b){
    StringBuilder str = new StringBuilder();
    try(BufferedReader br =
                new BufferedReader(
                        new FileReader("/Users/liweidan/Java/workspace/Java-jdk8-demo/src/main/java/cn/liweidan/jdk8/Lambda/LambdaDemo01.java"))){
        str.append(br.readLine());
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return str.toString();
}
```
这样可以实现读取指定文件的一行，但是当我们需要读取文件的第二行的时候怎么办，我们又需要修改这段代码。
所以我们应该把读取的内容给封装起来。首先我们先建一个函数式接口BufferedReaderProcess接收一个BufferedReader，然后对BufferedReader的动作进行操作。
```java
package cn.liweidan.jdk8.Lambda.Demo02;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * <p>Desciption:读取流的函数式编程，把行为参数化</p>
 * CreateTime : 2017/6/2 下午3:33
 * Author : Weidan
 * Version : V1.0
 */
@FunctionalInterface // 注意这个注解
public interface BufferedReaderProcess {

    String process(BufferedReader b) throws IOException;

}
```
**@FunctionalInterface**注解，用于只是该接口是函数式接口，如果没有该注释也可以编译通过，但是编译器就不能判断他只能拥有一个未实现的方法。
这时候我们可以编写一个方法，用于接收BufferedReaderProcess，前后包装流的常用动作，包括流的获取以及流的关闭，这里我们使用JDK7的新特性，自动关闭流。
```java
@Test
public void test01(){
    String br = LambdaDemo02.processFile(b -> b.readLine());
    System.out.println(br);
    br = LambdaDemo02.processFile(b -> b.readLine() + b.readLine() + b.readLine());
    System.out.println(br);
}

/**
 * 将读取文件的动作封装成参数
 * @param b
 * @return
 */
public static String processFile(BufferedReaderProcess b){
    try(BufferedReader br =
                new BufferedReader(
                        new FileReader("/Users/liweidan/Java/workspace/Java-jdk8-demo/src/main/java/cn/liweidan/jdk8/Lambda/LambdaDemo01.java"))){
        return b.process(br);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
```
**代码位置：cn.liweidan.jdk8.Lambda.LambdaDemo02**

### 六、Lambda的方法引用
当我们需要使用Lambda表达式去调用一个对象的方法的时候，可以通过`::`来调用，比如调用String的compareToIgnoreCase方法，可以写成`String:: compareToIgnoreCase`，又可以减少代码量了。方法调用可以针对**构造方法**、**普通方法**以及**静态方法**进行调用。下面给出几个需求的实现代码。
```java
public class MethodQuoteDemo01 {

    @Test
    public void test(){
        List<String> list = Arrays.asList("a", "b", "A", "B");
        list.sort(String::compareToIgnoreCase);// 调用compareToIgnoreCase进行比较
        System.out.println(list);
    }

}
```

```java
package cn.liweidan.jdk8.MethodQuote;

import cn.liweidan.jdk8.pojo.Apple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>Desciption:构造方法引用Demo</p>
 * CreateTime : 2017/6/2 下午7:46
 * Author : Weidan
 * Version : V1.0
 */
public class MethodQuoteDemo02 {

    @Test
    public void test01(){
        /**
         * 空构造器
         */
        Supplier<Apple> c1 = Apple::new;
        Apple apple = c1.get();
        System.out.println(apple);
    }

    @Test
    public void test02(){
        /**
         * Apple存在只需要传递一个Integer参数的构造器
         */
        Function<Integer, Apple> c2 = Apple::new;
        Apple apply = c2.apply(100);
        System.out.println(apply);
    }

    /**
     * 使用map批量创建指定重量的苹果
     */
    @Test
    public void test03(){
        List<Integer> integers = Arrays.asList(7, 3, 9, 10);
        List<Apple> appleList = map(integers, Apple::new);
        System.out.println(appleList);
    }
    public static <T, R>List<R> map(List<T> wights, Function<T, R> function){
        List<R> res = new ArrayList<>();
        for (T t : wights) {
            res.add(function.apply(t));
        }
        return res;
    }

    /**
     * 调用getWight对苹果进行按照重量进行排序
     */
    @Test
    public void test04(){
        List<Apple> appleList = Arrays.asList(new Apple("RED", 80),
                new Apple("GREEN", 100),
                new Apple("BLACK", 150));
        appleList.sort(Comparator.comparing(Apple::getWight));
        System.out.println(appleList);
    }

}
```