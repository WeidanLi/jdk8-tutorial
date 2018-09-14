package cn.liweidan.custom.collector2;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/7/11 上午10:55
 * Author : Weidan
 * Version : V1.0
 */
public class Demo02 {

    @Test
    public void test01(){
        Map<Boolean, List<Integer>> collect = IntStream.rangeClosed(2, 100).boxed().collect(new PrimeNumberCollector());
        System.out.println(collect);
    }

}
