package cn.liweidan.utils;

import cn.liweidan.pojo.Dish;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午2:55
 * Author : Weidan
 * Version : V1.0
 */
public class DishUtils {

    public static List<Dish> getDishes(){
        return Arrays.asList(new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH));
    }

}
