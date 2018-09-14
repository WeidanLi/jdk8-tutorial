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
