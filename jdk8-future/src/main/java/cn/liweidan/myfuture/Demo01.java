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
