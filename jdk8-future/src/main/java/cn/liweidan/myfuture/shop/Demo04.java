package cn.liweidan.myfuture.shop;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

/**
 * <p>Desciption:定制执行器</p>
 * CreateTime : 2017/8/5 下午3:16
 * Author : Weidan
 * Version : V1.0
 */
public class Demo04 {


    /** 多家商店 */
    List<Shop> shops;

    public static void main(String[] args) {
        Demo04 demo04 = new Demo04();
        demo04.shops = Arrays.asList(new Shop("Shop1"),
                new Shop("Shop2"),
                new Shop("Shop3"),
                new Shop("Shop4"));
        long start = System.currentTimeMillis();
        List<String> iPhone7 = demo04.findPrices("iPhone7");
        System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");// stream : Done in 4131ms
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

    public List<String> findPrices(String prodName){
        /** CompletableFuture方式异步请求 */
        List<CompletableFuture<String>> completableFutureList = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(prodName)), getExecutor()))// 将我们自己定制的Executor传递给线程
                .collect(Collectors.toList());

        return completableFutureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

}
