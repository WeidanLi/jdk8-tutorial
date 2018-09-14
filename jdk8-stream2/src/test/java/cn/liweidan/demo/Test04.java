package cn.liweidan.demo;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <p>Desciption:归约：求和、求最大、求最小</p>
 * CreateTime : 2017/6/12 上午11:28
 * Author : Weidan
 * Version : V1.0
 */
public class Test04 {

    @Test
    public void testReduce01(){
        List<Integer> numbers = Arrays.asList(4, 5, 3, 9);
        /** 求和 */
        Integer sum1 = numbers.stream().reduce(0, (a, b) -> a + b);// 如果不存在值则返回默认值0
        Integer sum2 = numbers.stream().reduce(0, Integer::sum);// 如果不存在值则返回默认值0
        // 相当于numbers.stream().reduce((a, b) -> a + b).orElse(0)
        Optional<Integer> sumOptional = numbers.stream().reduce((a, b) -> a + b);// 返回Optional进行Null的判断
        /** 最大值 */
        Optional<Integer> max1 = numbers.stream().reduce((a, b) -> a > b ? a : b);
        Optional<Integer> max2 = numbers.stream().reduce(Integer::max);
        /** 最小值 */
        Optional<Integer> min1 = numbers.stream().reduce(Integer::min);
        System.out.println(sum2);
        System.out.println(max2.get());
        System.out.println(min1.get());
    }

}
