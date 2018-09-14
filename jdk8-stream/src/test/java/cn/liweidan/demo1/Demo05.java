package cn.liweidan.demo1;

import cn.liweidan.pojo.Dish;
import cn.liweidan.utils.DishUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午4:07
 * Author : Weidan
 * Version : V1.0
 */
public class Demo05 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 取出卡路里大于100并且根据卡路里数量进行排序
     */
    @Test
    public void test01(){
        List<Dish> collect = dishList.stream() // 获取该集合的流对象
                .filter(dish -> dish.getColories() > 300) // 大于100卡路里
                .sorted(Comparator.comparing(dish -> dish.getColories()))// 根据卡路里从小到大进行排序
                .collect(Collectors.toList()); // 我要封装成一个集合进行返回

        System.out.println(collect);// [Dish(name=rice, vegetarain=true, colories=350, type=OTHER),
        // Dish(name=chicken, vegetarain=false, colories=400, type=MEAT),
        // Dish(name=salmon, vegetarain=false, colories=450, type=FISH),
        // Dish(name=french fries, vegetarain=true, colories=530, type=OTHER),
        // Dish(name=pizza, vegetarain=true, colories=550, type=OTHER),
        // Dish(name=beef, vegetarain=false, colories=700, type=MEAT),
        // Dish(name=pork, vegetarain=false, colories=800, type=MEAT)]]
    }

    /**
     * 取出卡路里大于100并且根据卡路里数量进行排序 取出前三
     */
    @Test
    public void test02(){
        List<Dish> collect = dishList.stream() // 获取该集合的流对象
                .filter(dish -> dish.getColories() > 300) // 大于100卡路里
                .sorted(Comparator.comparing(dish -> dish.getColories()))// 根据卡路里从小到大进行排序
                .limit(3)// 取出前三个的值
                .collect(Collectors.toList()); // 我要封装成一个集合进行返回

        System.out.println(collect);// [Dish(name=rice, vegetarain=true, colories=350, type=OTHER),
        // Dish(name=chicken, vegetarain=false, colories=400, type=MEAT),
        // Dish(name=salmon, vegetarain=false, colories=450, type=FISH)
    }

    /**
     * 取出卡路里大于100并且根据卡路里数量进行排序 取出菜单的名字
     */
    @Test
    public void test03(){
        List<String> collect = dishList.stream() // 获取该集合的流对象
                .filter(dish -> dish.getColories() > 300) // 大于100卡路里
                .sorted(Comparator.comparing(dish -> dish.getColories()))// 根据卡路里从小到大进行排序
                .limit(3)// 取出前三个的值
                .map(Dish::getName)// 取出名字
                .collect(Collectors.toList()); // 我要封装成一个集合进行返回

        System.out.println(collect);// [rice, chicken, salmon]
    }

    /**
     * 取出卡路里大于100并且的菜单
     */
    @Test
    public void test04(){
        List<Dish> dishes = Arrays.asList(new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH),
                new Dish("pork", false, 400, Dish.Type.MEAT));// 与第一个元素名字相同
        List<String> collect = dishes.stream() // 获取该集合的流对象
                .filter(dish -> dish.getColories() > 300) // 大于100卡路里
                .map(Dish::getName)
                .collect(Collectors.toList()); // 我要封装成一个集合进行返回

        System.out.println(collect);// [pork, beef, chicken, french fries, rice, pizza, salmon, pork] 两个pork

        collect = dishes.stream() // 获取该集合的流对象
                .filter(dish -> dish.getColories() > 300) // 大于100卡路里
                .map(Dish::getName)
                .distinct()
                .collect(Collectors.toList()); // 我要封装成一个集合进行返回
        System.out.println(collect);// [pork, beef, chicken, french fries, rice, pizza, salmon] 去除了重复
    }

}
