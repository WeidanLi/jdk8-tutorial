package cn.liweidan.demo1;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午3:46
 * Author : Weidan
 * Version : V1.0
 */
public class Demo02 {

    List<Integer> integerList = new ArrayList<>();

    @Before
    public void before(){
        integerList.add(10);
        integerList.add(20);
        integerList.add(30);
    }

    @Test
    public void test01(){
        long start = System.nanoTime();
        integerList.parallelStream().filter(integer -> integer > 25).collect(toList());
        System.out.println((System.nanoTime() - start));// 133276458
    }

    @Test
    public void test02(){
        long start = System.nanoTime();
        integerList.stream().filter(integer -> integer > 25).collect(toList());
        System.out.println((System.nanoTime() - start));// 338833
    }
}
