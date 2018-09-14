package cn.liweidan.demo;

import cn.liweidan.common.utils.DishUtils;
import cn.liweidan.pojo.Dish;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>Desciption:数值流转换：IntSream LongStream DoubleStream</p>
 * CreateTime : 2017/6/12 上午11:42
 * Author : Weidan
 * Version : V1.0
 */
public class Test05 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 计算所有菜单的卡路里总和
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testNumberStream(){
        int sum = dishList.stream()
                .mapToInt(Dish::getColories)
                .sum();
        System.out.println(sum);
    }

    /**
     * 转换为对象流
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testToObj(){
        IntStream intStream = dishList.stream()
                .mapToInt(Dish::getColories);
        Stream<Integer> boxed = intStream.boxed();
    }

    /**
     * 默认值OptionalInt
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testOptionalInt(){
        OptionalInt max = dishList.stream()
                .mapToInt(Dish::getColories)
                .max();
        System.out.println(max.orElse(1));
    }
}
