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
        System.out.println("Price returned after " + retrievalTime + "ms");
    }
}
