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
