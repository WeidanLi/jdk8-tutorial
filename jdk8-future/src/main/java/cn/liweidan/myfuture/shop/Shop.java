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

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
