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
