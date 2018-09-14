package cn.liweidan.demo1;

import cn.liweidan.pojo.Dish;
import cn.liweidan.utils.DishUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午4:36
 * Author : Weidan
 * Version : V1.0
 */
public class Demo06 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 统计集合中卡路里大于500的菜单数
     */
    @Test
    public void test01(){
        long count = dishList.stream()
                .filter(dish -> dish.getColories() > 500)
                .count();// 统计 返回long类型
        System.out.println(count);// 4
    }
    
    /**
     * 打印集合中卡路里大于500的菜单
     */
    @Test
    public void test02(){
        /** 串行 */
        dishList.stream()
                .filter(dish -> dish.getColories() > 500)
                .forEach(dish -> System.out.println(dish));
        /*
        Dish(name=pork, vegetarain=false, colories=800, type=MEAT)
        Dish(name=beef, vegetarain=false, colories=700, type=MEAT)
        Dish(name=french fries, vegetarain=true, colories=530, type=OTHER)
        Dish(name=pizza, vegetarain=true, colories=550, type=OTHER)
         */

        /** 并行 */
        dishList.parallelStream()
                .filter(dish -> dish.getColories() > 500)
                .forEach(dish -> System.out.println(dish));
        /** 每次打印都不同 */
    }

}
