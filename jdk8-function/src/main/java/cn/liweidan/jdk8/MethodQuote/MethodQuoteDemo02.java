package cn.liweidan.jdk8.MethodQuote;

import cn.liweidan.jdk8.pojo.Apple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>Desciption:构造方法引用Demo</p>
 * CreateTime : 2017/6/2 下午7:46
 * Author : Weidan
 * Version : V1.0
 */
public class MethodQuoteDemo02 {

    @Test
    public void test01(){
        /**
         * 空构造器
         */
        Supplier<Apple> c1 = Apple::new;
        Apple apple = c1.get();
        System.out.println(apple);
    }

    @Test
    public void test02(){
        /**
         * Apple存在只需要传递一个Integer参数的构造器
         */
        Function<Integer, Apple> c2 = Apple::new;
        Apple apply = c2.apply(100);
        System.out.println(apply);
    }

    /**
     * 使用map批量创建指定重量的苹果
     */
    @Test
    public void test03(){
        List<Integer> integers = Arrays.asList(7, 3, 9, 10);
        List<Apple> appleList = map(integers, Apple::new);
        System.out.println(appleList);
    }
    public static <T, R>List<R> map(List<T> wights, Function<T, R> function){
        List<R> res = new ArrayList<>();
        for (T t : wights) {
            res.add(function.apply(t));
        }
        return res;
    }

    /**
     * 调用getWight对苹果进行按照重量进行排序
     */
    @Test
    public void test04(){
        List<Apple> appleList = Arrays.asList(new Apple("RED", 80),
                new Apple("GREEN", 100),
                new Apple("BLACK", 150));
        appleList.sort(Comparator.comparing(Apple::getWight));
        System.out.println(appleList);
    }

}
