# jdk8 函数式综合应用

## 零、导读

### （一）文章中心

我用jdk8已经半年多了，基本操作已经入门，但是jdk8魅力并非集合流以及线程等等，它要传输的更加是编程方式的改变，要知道像javascript是可以传递函数的，而在以前的jdk版本中，虽然可以但是并非十分的方便。现在官方已经可以通过传递行为的方式传递给函数，让编程中可以对一些基本的动作更加一步的抽象，而不需要写更多的重复代码。

文章中会提到常用的几个函数式接口，这几个接口无疑带来了很多方便。像之前用jdk8的时候写的第一篇文章**[《[jdk8]Lambda、JDK8接口使用、自定义函数接口》](http://c.liweidan.cn/java/jdk8/2017/10/08/39/)**的时候还懵懵懂懂这个过程，所以这篇算是响应第一篇的使用了。


### （二）文章结构

|-- 函数的使用以及特性
|-- 函数的分类
|-- 通过商城打折功能使用函数式编程

### （三）适用范围

一个基本功能，比如折扣，计算方法大致一致，传入原价格返回新的价格，那么这个动作就可以通过定义函数式接口来传递行为，在下面所有的子类优惠类型中，就可以直接通过定义折扣的动作来实现折扣的功能。听起来跟以前差不多一样，但是通过定义函数式方程能够让子类的动作看起来更加清晰，代码维护性更高。

## 一、含义

跟我们平常开发的面向对象函数具有一点非常重要的区别就是，面向对象函数可能会修改传入进来的对象。

那么函数式编程大概就有以下几个特点：
1. 幂等性：无论调用多少次只要传入的值是一样的，那么返回的值也是一样的。
2. 不会对传入的对象值进行修改，所以期待传入的值是`final`修饰的，当我们需要返回这个对象的时候，克隆并且操作然后返回。
3. 使用`Optional`包装返回值，当输入的值不正确（比如除法函数中输入0为除数），就可以返回包含控制的`Optional`对象，并且表明不合法的参数，而不是使用抛异常的方式，抛异常会导致函数式编程多了一个方法的出口。
4. 一个入口一个出口，通过入口输入所需要的参数，出口返回就可以了。相当于把函数式编程看成一个黑匣子，输入什么，返回什么，不会有第三个方向。

![](http://c.liweidan.cn/wp-content/uploads/2017/12/d4182226bc63260ce4d01d0893df9538.png)


### （一）引用透明性

以上几个特点即隐含着**引用透明性**，像`String.replace`方法，返回的是一个新的字符串而不是更新所输入的对象，所以这个方法即是一个函数式。

### （二）递归和迭代

传统的递归：通过控制条件分支调用自己的方法，这个是最常见的，但是性能也不会很好，用得不好的话会造成栈内存溢出。内存占用也会根据调用的层次成倍增长。

尾-调优化递归：通俗来说就是把计算结果当成参数进行传递，不需要jvm来保存递归调用结果作为中间值，也就不需要分配单独的栈帧从而节省资源。**但是但是，现在jvm还不支持这种操作，但是使用这种操作可以为jvm调优带来一个机会。**

举个例子：通过传入一个n值，进行阶乘计算。

```java
public class Factorial {
    public static void main(String[] args) {
        long start = System.nanoTime();
        long l = factorialTailRecursive(4L);
        System.out.println(l + ", time: " + (System.nanoTime() - start));
    }
    /**
     * 传统递归方式
     * 24, time: 50614
     * @param n
     * @return
     */
    public static long factorialRecursive(long n){
        return n == 1 ? n : n * factorialRecursive(n-1);
    }
    /**
     * 使用尾-调优化
     * 24, time: 66532
     * @param n
     * @return
     */
    public static long factorialTailRecursive(long n){
        return factorialHelper(1, n);
    }
    private static long factorialHelper(long acc, long n) {
        return n == 1 ? acc : factorialHelper(acc * n, n - 1);
    }
}
```

## 二、两个函数类型

### 1. 高阶函数

#### 定义（满足其一即可称为高阶函数）

1. 接受至少一个函数作为参数
2. 返回的结果是一个函数

从定义来看很抽象，举个例子，jdk自带的比较器

```java
/** 通过传递一个函数返回比较器函数 */
Comparator<Apple> comparing = comparing(Apple::getWight);
```

因为比较器只知道比较两个数值的大小，但是比较什么数值（重量？数量？）他并不知道，所以抽象成你告诉我要比较哪些数字（更准确的说你通过方法引用传给我两个数字），如代码中所示就是苹果重量的比较器，jvm就会知道你用这个比较器的时候只需要给我两个苹果，我就给你做作比较。可以说在面向对象函数上面，在抽象出来参数和返回值是一个函数，这时候这个比较器就可以在项目中复用，这个比较器也可以在项目中复用。

所以通过传递一个函数或者返回一个计算函数，就可以称为高阶函数（以我个人理解就是，抽象了计算，再把计算中可以复用的函数给抽象出来）

### 2. 科里化函数

#### 定义

就是把具有两个参数（x和y）的函数f转换为使用一个参数的函数g，并且返回的也是一个函数，只需要传递一个参数即可实现与原函数一样的返回结果。

公式：f（x，y）= （g（x））（y）

即把原有的函数更推进一步，只需要传入y即可实现计算。

举个例子，国际化单位的转换。我们知道，国际化单位的转换一般是通过转换因子与基线调整两个值来进行转换的。为此，我们可以通过抽象化这个过程来达到将公式应用于转换方面的函数。

首先我们确定函数：`x * f + b`，其中f是转换因子，b是基线调整。通过jdk8将函数进行传递的方式，我们可以将转换因子和基线调整传入给这个函数从而获取不同的转换公式，通过工厂模式：

```java
/**
 * 通过不同传入不同的转换因子和基数调整获取公式
 * @param f
 * @param b
 * @return
 */
static DoubleUnaryOperator curriedConverter(double f, double b) {
    return (double x) -> x * f + b;
}

/** 获取不同的转换公式 */
/** 获取摄氏度转换华氏度公式 */
DoubleUnaryOperator convertCtoF = curriedConverter(9.0 / 5, 32);
/** 获取人民币转美元的公式 当前汇率1人民币=0.1527美元 */
DoubleUnaryOperator convertRMBtoUSD = curriedConverter(0.1527, 0);
```

这样既可达到对转换公式的复用，当然项目中会不会这么用，可能就比较难说了。

## 三、实际应用

讲了这么多抽象的东西，其实就是对以下三个jdk8特有的函数类进行说明：
1. `Supplier<R>`：生产者，R为返回的类型，没有输入参数
2. `Consumer<T>`：消费者，输入T参数进行操作，没有返回值
3. `Function<T, R>`：函数，在我看来这个有输入参数以及输出结果的函数类，可能在实际中用得比较多。

在接下来的例子讲解中，我们就是用`Function<T, R>`函数来做为操作，示例就用我们电商行业最常见的折扣来做。现在我们假设有这么一个需求，用户结账的时候需要使用优惠券的情况下，对用户的订单进行优惠操作，当然如果只是简单的使用一个方法来操作，那就很难应付以后的打折、活动等等的优惠需求。

第一步：把订单减价行为定义为一个函数，传入原价，返回优惠价。这时候我为了能够方便的通过订单获取支付单对象，我还定义了一个默认方法，可以用于在不同的优惠对象中进行方便的获取。我们知道`Function`接口刚好可以满足我们这个要求，所以通过集成指定输入输出类型来定义这个函数式接口。

```java
/**
 * 折扣减价计算函数
 * @author liweidan
 */
@FunctionalInterface
public interface DiscountFunction extends Function<Long, Long> {
    default PayOrderDo getPayOrderDo(SaleDo saleDo) {
        Long newPrice = apply(saleDo.getSalePrice());
        return new PayOrderDo(UUID.randomUUID().toString(),
                saleDo.getId(), newPrice, saleDo.getSalePrice() - newPrice);
    }
}
```

第二步：定义优惠接口，也就是通过这个接口传入订单就可以生成支付单，接下来我们所有的优惠券都需要实现这个接口。

```java
/**
 * 折扣接口
 * @author liweidan
 * @date 2017.12.28 下午3:15
 * @email toweidan@126.com
 */
public interface DiscountType {
    PayOrderDo discount(SaleDo saleDo);
}
```

第三步：定义两种不同的优惠类型，有满减的优惠也有根据积分的多少来优惠，都实现上面定义的折扣接口，实现方法。这时候我们就可以在实现方法中使用我们第一步定义的函数式方程，不同的优惠券传递不同的优惠计算方式就可以获取优惠后的价格。

```java
/**
 * 积分折扣
 * @author liweidan
 * @date 2017.12.28 下午3:16
 * @email toweidan@126.com
 */
public class IntegralType implements DiscountType {
    /** 积分 */
    private long integral;
    public IntegralType(long integral) {
        this.integral = integral;
    }
    @Override
    public PayOrderDo discount(SaleDo saleDo) {
        System.out.println("积分为：" + integral);
        if (integral > 100) {
            DiscountFunction function = (Long oldPrice) -> oldPrice - 100 + 10;
            return function.getPayOrderDo(saleDo);
        } else if (integral > 50) {
            DiscountFunction function = (Long oldPrice) -> oldPrice - 50 + 5;
            return function.getPayOrderDo(saleDo);
        } else {
            DiscountFunction function = (Long oldPrice) -> oldPrice;
            return function.getPayOrderDo(saleDo);
        }
    }
}

/**
 * 满减折扣
 * @author liweidan
 * @date 2017.12.28 下午3:25
 * @email toweidan@126.com
 */
public class CouponType implements DiscountType{
    /** 满多少钱 */
    private Long fillPrice;
    /** 减多少钱 */
    private Long discountPrice;
    public CouponType(Long fillPrice, Long discountPrice) {
        this.fillPrice = fillPrice;
        this.discountPrice = discountPrice;
    }
    @Override
    public PayOrderDo discount(SaleDo saleDo) {
        System.out.println("满" + fillPrice + "减" + discountPrice);
        DiscountFunction function = (Long oldPrice) -> oldPrice >= fillPrice ? oldPrice - discountPrice : oldPrice;
        PayOrderDo payOrderDo = function.getPayOrderDo(saleDo);
        return payOrderDo;
    }
}
```

第四步：我们可以应用第二步中定义的优惠券接口来对订单进行操作生成支付单。调用者只需要把对应的订单信息的，优惠的具体类型传递给这个函数就能获取到该订单最后的支付单。

```java
private PayOrderDo discount(SaleDo saleDo, DiscountType discountType) {
    return discountType.discount(saleDo);
}
```

到这里就已经大概完成了函数式方程应用在实际项目的使用，要说我们用函数式方程做了什么，无非就是把**做优惠的操作预留给了每个优惠券具体类型去做**，从而复用了我们的方程式。在我看来，更像是对代码的抽象。获取的效果是什么，就是让我们的代码更加的简洁。

整个项目的GitHub地址于：**[jdk8-mall-functional](https://github.com/WeidanLi/jdk8-tutorial/tree/master/jdk8-mall-functional)**   ，项目中还包含了测试的过程。