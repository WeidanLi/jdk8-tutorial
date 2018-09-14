package cn.liweidan.demo1;

import cn.liweidan.pojo.Dish;
import cn.liweidan.utils.DishUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午3:59
 * Author : Weidan
 * Version : V1.0
 */
public class Demo04 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    @Test
    public void test01(){
        Stream<Dish> stream =
                dishList.stream();
        stream.forEach(dish -> System.out.println(dish));
        stream.forEach(dish -> System.out.println(dish));// stream has already been operated upon or closed
    }

}
