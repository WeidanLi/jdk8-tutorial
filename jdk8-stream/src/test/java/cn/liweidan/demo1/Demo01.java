package cn.liweidan.demo1;

import cn.liweidan.pojo.Dish;
import cn.liweidan.utils.DishUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午3:07
 * Author : Weidan
 * Version : V1.0
 */
public class Demo01 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 取出卡路里大于300并且类型为FISH的菜单
     */
    @Test
    public void test01(){
        long l = System.nanoTime();
        List<Dish> collect = dishList.stream() // 获取该集合的流对象
                .filter(dish -> dish.getColories() > 300) // 大于300卡路里
                .filter(dish -> dish.getType().equals(Dish.Type.FISH)) // 类型是鱼的
                .collect(Collectors.toList()); // 我要封装成一个集合进行返回
        System.out.println(collect);// [Dish(name=salmon, vegetarain=false, colories=450, type=FISH)]
        System.out.println((System.nanoTime() - l) / 1000000);// 118ms
    }

    /**
     * 取出卡路里大于300并且类型为FISH的菜单(多线程)
     */
    @Test
    public void test02(){
        long l = System.nanoTime();
        List<Dish> collect = dishList.parallelStream() // 获取该集合的流对象(多条流水线并行处理)
                .filter(dish -> dish.getColories() > 300) // 大于300卡路里
                .filter(dish -> dish.getType().equals(Dish.Type.FISH)) // 类型是鱼的
                .collect(Collectors.toList()); // 我要封装成一个集合进行返回
        System.out.println(collect);// [Dish(name=salmon, vegetarain=false, colories=450, type=FISH)]
        System.out.println((System.nanoTime() - l) / 1000000);// 4ms
    }

}
