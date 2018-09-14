# jdk8 furture 类增强

github地址：[jdk8-future](https://github.com/WeidanLi/jdk8-tutorial/tree/master/jdk8-future)

# 一、Future接口
Future是jdk5的时候被引入的，目的是为了把耗时的操作解放出来，可以同时使用多核的优势进行并行处理。比如，我有一个页面，需要从多方获取数据比如说从Twitter和Facebook获取数据，然后一起渲染的页面上。这时候如果等待Twitter的数据获取完在获取FB的数据，就显得比较慢了，这时候可以通过Future来让这两个任务并行处理。

<!--more-->

```java
package cn.liweidan.myfuture;

import java.util.concurrent.*;

/**
 * <p>Desciption:Future执行一个耗时的操作</p>
 * CreateTime : 2017/8/3 下午10:28
 * Author : Weidan
 * Version : V1.0
 */
public class Demo01 {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Double> future = executorService.submit(new Callable<Double>() {
            public Double call() throws Exception {
                Thread.sleep(1000L);
                return Math.random();
            }
        });
        Thread.sleep(1000L);
        try {
            /** 获取线程执行的结果，传递的是等待线程的时间，单位是1秒 */
            future.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("共耗时" + (System.currentTimeMillis() - start) + "ms");// 1014ms
    }

}

```

## （一）jdk8的CompletableFuture接口
### 1. Future的局限性
- 等待Future集合所有都完成
- 第二个线程依赖第一个线程的时候
- 仅等待Future集合中最快完成的任务完成
- 通过手工方式完成Future的运行
- 当Future完成的时候通知下一个任务，而不只是简单的阻塞等待操作结果

### 2. 实现异步API
首先模拟在商店里面找到我们自己想要的产品的价格，在寻找的过程中让线程休息1秒模拟网络延迟，然后随机返回一个价格。
第一步：
  写一个模拟延迟的方法delay()，模拟线程休息一秒钟
第二步：
  写一个计算的方法，用于计算价格，这里我们使用随机数生成
第三步：
  暴露getPrice(String prodName)用于外部调用。
第四步：
  暴露异步获取线程的接口
Shop.java：
```java
package cn.liweidan.myfuture.shop;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * <p>Desciption:商店类</p>
 * CreateTime : 2017/8/3 下午11:16
 * Author : Weidan
 * Version : V1.0
 */
public class Shop {

    private String shopName;

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public Shop() {
    }

    /**
     * 获取产品价格
     * @param prodName 产品名
     * @return
     */
    public double getPrice(String prodName){
        return caculatePrice(prodName);
    }

    /**
     * 模拟网络延迟
     */
    private static void delay(){
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算产品价格
     * @param prodName
     * @return
     */
    private double caculatePrice(String prodName){
        delay();
        return new Random().nextDouble() * prodName.charAt(0) + prodName.charAt(1);
    }

    /**
     * 异步请求产品价格
     * @param prodName
     * @return
     */
    public Future<Double> getPriceAsync(String prodName){
        /** 1. 使用第一种方式 */
        // CompletableFuture<Double> future = new CompletableFuture<>();
        // new Thread(() -> {
        //     try {
        //         double price = caculatePrice(prodName);
        //         future.complete(price);
        //     } catch (Exception e) {
        //         /** 出现异常 */
        //         future.completeExceptionally(e);
        //     }
        // }).start();
        // return future;

        return CompletableFuture.supplyAsync(() -> caculatePrice(prodName));// 2. 第二种方式，工厂模式的方式
    }
}

```

接下来就来运行了，代码中模拟获取商店价格的同时还需要执行其他任务，如果串行执行就是需要2s，但是其实使用线程去获取的话，只需要1s，也就是两个耗时的任务是一起完成的。
```java
package cn.liweidan.myfuture.shop;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>Desciption:获取多家商店产品价格Demo</p>
 * CreateTime : 2017/8/3 下午11:15
 * Author : Weidan
 * Version : V1.0
 */
public class Demo02 {
    public static void main(String[] args) throws InterruptedException {
        /** 有个商店 */
        Shop shop = new Shop("BestShop");
        long startTime = System.nanoTime();
        /** 获取运行的线程 */
        Future<Double> shopPriceAsync = shop.getPriceAsync("best product");
        long invocationTime = ((System.nanoTime() - startTime) / 1_000_000);
        System.out.println("Invocation returned after" + invocationTime + "ms");
        
        /** 模拟做其他事情的耗时 */
        Thread.sleep(1000L);

        try {
            /** 获取结果 */
            double aDouble = shopPriceAsync.get(1, TimeUnit.SECONDS);
            System.out.println("Price is " + aDouble);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        long retrievalTime = (System.nanoTime() - startTime) / 1_000_000;
        System.out.println("Price returned after " + retrievalTime + "ms");// 1025ms
    }
}

```

### 3. 实现异步API
现在有个需求是：**对多个商店的产品进行查询，获取最佳价格的产品**。
#### 第一种方式：顺序流查询(Done in 4131ms)
```java
package cn.liweidan.myfuture.shop;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Desciption:对多个商店进行查询</p>
 * CreateTime : 2017/8/5 下午2:47
 * Author : Weidan
 * Version : V1.0
 */
public class Demo03 {

    /** 多家商店 */
    List<Shop> shops;

    public static void main(String[] args) {
        Demo03 demo03 = new Demo03();
        demo03.shops = Arrays.asList(new Shop("Shop1"),
                new Shop("Shop2"),
                new Shop("Shop3"),
                new Shop("Shop4"));
        long start = System.currentTimeMillis();
        List<String> iPhone7 = demo03.findPrices("iPhone7");
        System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");// Done in 4131ms
    }

    public List<String> findPrices(String prodName){
        return shops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(prodName)))
                .collect(Collectors.toList());

    }
}

```

#### 第二种方式：并行流查询所有的商店。(Done in 1162ms)
```java
package cn.liweidan.myfuture.shop;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Desciption:对多个商店进行查询</p>
 * CreateTime : 2017/8/5 下午2:47
 * Author : Weidan
 * Version : V1.0
 */
public class Demo03 {

    /** 多家商店 */
    List<Shop> shops;

    public static void main(String[] args) {
        Demo03 demo03 = new Demo03();
        demo03.shops = Arrays.asList(new Shop("Shop1"),
                new Shop("Shop2"),
                new Shop("Shop3"),
                new Shop("Shop4"));
        long start = System.currentTimeMillis();
        List<String> iPhone7 = demo03.findPrices("iPhone7");
        System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");// stream : Done in 4131ms
    }

    public List<String> findPrices(String prodName){
        /*return shops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(prodName)))
                .collect(Collectors.toList());*/
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(prodName)))
                .collect(Collectors.toList());
    }
}

```
#### 第三种方式：使用CompletableFuture发起异步请求(Done in 2127ms)
```java
package cn.liweidan.myfuture.shop;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>Desciption:对多个商店进行查询</p>
 * CreateTime : 2017/8/5 下午2:47
 * Author : Weidan
 * Version : V1.0
 */
public class Demo03 {

    /** 多家商店 */
    List<Shop> shops;

    public static void main(String[] args) {
        Demo03 demo03 = new Demo03();
        demo03.shops = Arrays.asList(new Shop("Shop1"),
                new Shop("Shop2"),
                new Shop("Shop3"),
                new Shop("Shop4"));
        long start = System.currentTimeMillis();
        List<String> iPhone7 = demo03.findPrices("iPhone7");
        System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");// stream : Done in 4131ms
    }

    public List<String> findPrices(String prodName){
        /** 顺序流查询 */
        /*return shops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(prodName)))
                .collect(Collectors.toList());*/

        /** 并行流查询 */
        /*return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(prodName)))
                .collect(Collectors.toList());*/

        /** CompletableFuture方式异步请求 */
        List<CompletableFuture<String>> completableFutureList = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(prodName))))
                .collect(Collectors.toList());

        return completableFutureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}

```
结果并不像第二种方式中的那样如意，这时候我们可以引出下一个话题，**对CompletableFuture的优化**，CompletableFuture和并行流的对比，优势就在于CompletableFuture允许我们对执行器进行优化。

### 4. 定制Excutor执行器
在CompletableFuture.supplyAsync这个工厂方法中，我们可以传递我们自定义的Executor给线程。执行器的定义就需要我们斟酌了，在《Java并发编程实战》中有一道公式：N(threads)=Ncpu * Ucpu * (1+W/C)
其中，Ncpu是cpu的个数，Ucpu是我们期望的CPU的利用率，而W/C是等待时间与计算时间的比率。我们就可以根据这个公式计算出我们所需要的线程数量了，但是线程数量**不宜过多**，也**不宜过少**
```java
/**
 * 获取一个优化的执行器
 * @return
 */
private final Executor getExecutor(){
    /** 在设置线程数的时候，取一个上限的值，即如果商城超过100个的时候我们永远都只要100个线程数 */
    return Executors.newFixedThreadPool(Math.min(this.shops.size(), 100), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);// 设置守护线程，Java无法停止正在运行的线程
            return t;
        }
    });
}

public List<String> findPrices(String prodName){
    /** CompletableFuture方式异步请求 */
    List<CompletableFuture<String>> completableFutureList = shops.stream()
            .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(prodName)), 
                  getExecutor()))// 将我们自己定制的Executor传递给线程
            .collect(Collectors.toList());

    return completableFutureList.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
}
```

# 二、对多个异步任务进行流水线操作
我们假设现在需要实现一个需求，就是所有商店都允许使用一个折扣，然后我们异步获取所有商店的价格后，再通知另外一个异步线程，计算出价格打折扣以后的数据。这时候求出折扣的价格线程就依赖到了商店获取价格的线程了。

假设需要修改商店价格和折扣返回的形式是：BestShop:123.26:GOLD。这时候我们需要对getPrice进行修改：
Shop.java:
```java
package cn.liweidan.myfuture.streamm;

import java.util.Random;

/**
 * <p>Desciption:参与折扣商店类</p>
 * CreateTime : 2017/8/5 下午4:22
 * Author : Weidan
 * Version : V1.1
 */
public class Shop {


    private String shopName;

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public Shop() {
    }

    /**
     * 获取产品价格
     * @param prodName 产品名
     * @return
     */
    public String getPrice(String prodName){
        double price = caculatePrice(prodName);
        Discount.Code code = Discount.Code.values()[new Random().nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", shopName, price, code);
    }

    /**
     * 模拟网络延迟
     */
    private static void delay(){
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算产品价格
     * @param prodName
     * @return
     */
    private double caculatePrice(String prodName){
        delay();
        return new Random().nextDouble() * prodName.charAt(0) + prodName.charAt(1);
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

}

```

这时候我们需要定义折扣的枚举以及折扣的计算类。
Discount.java:
```java
package cn.liweidan.myfuture.streamm;

import static com.sun.tools.javac.util.Constants.format;

/**
 * <p>Desciption:折扣类，包含折扣方式枚举</p>
 * CreateTime : 2017/8/5 下午4:19
 * Author : Weidan
 * Version : V1.0
 */
public class Discount {

    /**
     * 返回计算折扣后的价格
     * @param price
     * @param discountCode
     * @return
     */
    public static String apply(double price, Code discountCode) {
        delay();// 模拟请求折扣的延迟
        return format(price * (100 - discountCode.per) / 100);
    }

    private static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 应用一个Quote计算的类
     * @param quote
     * @return
     */
    public static String applyDiscount(Quote quote){
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

        /** 折扣百分率 */
        private final int per;

        Code(int per){
            this.per = per;
        }

    }

}

```

Quote.java:
```java
package cn.liweidan.myfuture.streamm;

/**
 * <p>Desciption:实现折扣服务</p>
 * CreateTime : 2017/8/5 下午4:26
 * Author : Weidan
 * Version : V1.0
 */
public class Quote {

    private final String shopName;
    private final double price;
    private final Discount.Code discountCode;

    public Quote(String shopName, double price, Discount.Code discountCode) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = discountCode;
    }

    /**
     * 解析商店返回的字符串，
     * @param s 字符串
     * @return 该类对象
     */
    public static Quote parse(String s){
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Discount.Code code = Discount.Code.valueOf(split[2]);
        return new Quote(shopName, price, code);
    }


    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Discount.Code getDiscountCode() {
        return discountCode;
    }
}

```

这时候开始我们需要编写，两个依赖的异步线程的方法：

```java
package cn.liweidan.myfuture.streamm;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

/**
 * <p>Desciption:对多个异步任务进行流水线操作</p>
 * CreateTime : 2017/8/5 下午4:44
 * Author : Weidan
 * Version : V1.0
 */
public class Demo05 {

    List<Shop> shops;

    public static void main(String[] args) {
        Demo05 demo05 = new Demo05();
        demo05.shops = Arrays.asList(new Shop("Shop1"),
                new Shop("Shop2"),
                new Shop("Shop3"),
                new Shop("Shop4"));
        long start = System.currentTimeMillis();
        List<String> iPhone7 = demo05.findPrices("iPhone7");
        System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");// stream : Done in 4131ms
    }

    /**
     * 寻找打折扣后产品价格
     * @param prodName
     * @return
     */
    public List<String> findPrices(String prodName){
        List<CompletableFuture<String>> completableFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(prodName), getExecutor()))// 以异步的形式取得shop原始价格
                .map(future -> future.thenApply(Quote::parse))// Quote对象存在的时候，对其进行转换
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), getExecutor())))// 使用另一个异步任务构造申请折扣
                .collect(Collectors.toList());

        return completableFutures.stream()
                .map(CompletableFuture::join)// 等待流中所有Future完毕并提取各自返回值
                .collect(Collectors.toList());
    }

```

重点放在`findPrices(String prodName)`这个方法上：
1. 我们拿到所有的shop进行遍历，把shop流变成一个CompletableFuture<String>的流，这个六包含着所有商店获取的价格字符串（BestShop:123.26:GOLD）格式的线程。
2. 拿到字符串的时候，我们通过`future.thenApply `方法将这些字符串转换成quote对象（该对象包含字符串解析出来的信息）
3. future.thenCompose再把上一步拿到的已经转换成Quote对象的CompletableFuture流进行**进一步的通知**，即通知CPU可以**异步**执行计算折扣的代码了
4. 收集到所有的CompletableFuture流
5. CompletableFuture::join意为等待所有的Future线程（获取价格、计算折扣）执行完成的时候，收集到所有的数据进行返回。

## 合并两个完全不相干的任务
上一节，讲了`thenCompose`，接下来有另外一个方法，即`thenCompine`的使用。这个方法和`thenCompose`的区别即：我们需要把两个不互相依赖的结果同时计算出来进行计算，也就是我不希望第一个任务完成以后再来开始第二个任务的时候就可以使用这个方法。

那么现在有个需求，就是我在计算出来商店的价格的时候，同时查找当时的汇率是多少，然后在最后使用这两个数字进行相乘。

```java 
package cn.liweidan.myfuture.streamm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <p>Desciption:thenCompine的使用</p>
 * CreateTime : 2017/8/8 下午2:19
 * Author : Weidan
 * Version : V1.0
 */
public class Demo06 {

    /**
     * 模拟查询价格
     * @return
     */
    public static Double getPrice(){
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
        return Math.random() * 10;
    }

    /**
     * 模拟查询汇率
     * @return
     */
    public static Double getRate(){
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
        return Math.random();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        CompletableFuture<Double> future = CompletableFuture.supplyAsync(() -> getPrice())// 第一个任务并获取返回值
                .thenCombine(CompletableFuture.supplyAsync(() -> getRate()), (price, rate) -> price * rate);// 第二个任务，获取返回值以后与第一个返回值进行计算
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("done in " + (System.currentTimeMillis() - start) + "ms");// done in 1118ms
    }
}

```

## Completion事件
一组CompletableFuture完成以后执行的任务即使Completion事件，当对所有商店进行查询的时候，可能有些快一点，有些慢一点，但是我们会去等待比较慢的任务处理完成再来进行接下来的逻辑，这时候就可以使用`future.thenAccept`方法来完成这个任务。

1. 修改shop获取价格的延迟是随机性的。
```java
/**
 * 模拟网络延迟
 */
private static void delay(){
    int delay = new Random().nextInt(2000) + 500;
    try {
        Thread.sleep(delay);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
```

2. 获取查询价格时候的线程流
```java
/**
 * 获取所有商店查询的线程流
 * @param prodName
 * @return
 */
public Stream<CompletableFuture<String>> findPricesStream(String prodName) {
    return shops.stream()
            .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(prodName), getExecutor()))// 以异步的形式取得shop原始价格
            .map(future -> future.thenApply(Quote::parse))// Quote对象存在的时候，对其进行转换
            .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), getExecutor())));
}
```

3. 这时候我们需要对线程流中的每个线程执行`thenAccept`方法，并且打印所有的价格，顺便打印所有完成的时间。
```java
public static void main(String[] args) {
    long start = System.currentTimeMillis();
    Demo06 demo06 = new Demo06();
    demo06.shops = Arrays.asList(new Shop("Shop1"),
            new Shop("Shop2"),
            new Shop("Shop3"),
            new Shop("Shop4"));
    Stream<CompletableFuture<Void>> myIphoneThread  // 均全部返回CompletableFuture<Void>
            = demo06.findPricesStream("myIphone").map(f -> f.thenAccept(priceStr -> {
        System.out.println(priceStr + "(done in " + (System.currentTimeMillis() - start) + " ms)");// 对每个价格进行输出打印
    }));

    /** 获取数组 */
    CompletableFuture[] completableFutures = myIphoneThread.toArray(size -> new CompletableFuture[size]);

    /** 等待所有线程完成 */
    CompletableFuture.allOf(completableFutures).join();

    System.out.println("all done in " + (System.currentTimeMillis() - start) + "ms");

    /*
    Shop1 price is 112.419(done in 2792 ms)
    Shop4 price is 117.3345(done in 2990 ms)
    Shop2 price is 179.71(done in 3409 ms)
    Shop3 price is 131.82299999999998(done in 3584 ms)
    all done in 3584ms
     */
}
```

可以发现，最长的时间即使整个程序运行的时间，通过allOf方法，等待所有线程执行完毕以后打印出总的时间。