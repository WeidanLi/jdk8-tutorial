package cn.liweidan.demo;

import org.junit.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>Desciption:数值范围 range rangeClosed</p>
 * CreateTime : 2017/6/12 上午11:48
 * Author : Weidan
 * Version : V1.0
 */
public class Test06 {

    /**
     * 生成勾股数组
     */
    @Test
    public void testRange(){
        Stream<int[]> stream = IntStream.rangeClosed(1, 100).boxed()// 生成1-100的int数并且进行装箱
                .flatMap(a ->// 把每个a进行流的扁平化，即所有流放在一起
                        IntStream.rangeClosed(a, 100)// 继续生成第二个数字
                                .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)// 如果第三个数也是整数的话
                                .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)})// 返回数组对象
                );
        stream.forEach(t -> System.out.println(t[0] + "," + t[1] + "," + t[2]));

        /*
        当然以上的操作进行了两次开方以及平方计算，这时候我们进行优化，只要一次性生成所有，然后通过filter去筛选出来。
         */

        Stream<double[]> stream1 = IntStream.rangeClosed(1, 100).boxed()
                .flatMap(a ->
                        IntStream.rangeClosed(a, 100)
                                .mapToObj(b -> new double[]{a, b, (double) Math.sqrt(a * a + b * b)})
                                .filter(t -> t[2] % 1 == 0)
                );
        stream1.forEach(t -> System.out.println(t[0] + "," + t[1] + "," + t[2]));

    }

}
