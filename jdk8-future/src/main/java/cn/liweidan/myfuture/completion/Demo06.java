package cn.liweidan.myfuture.completion;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

/**
 * <p>Desciption:对多个异步任务进行流水线操作</p>
 * CreateTime : 2017/8/5 下午4:44
 * Author : Weidan
 * Version : V1.0
 */
public class Demo06 {

    List<Shop> shops;

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
}
