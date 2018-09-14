package cn.liweidan.demo;

import cn.liweidan.common.utils.DishUtils;
import cn.liweidan.pojo.Dish;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Desciption:筛选和切片测试</p>
 * CreateTime : 2017/6/12 上午9:31
 * Author : Weidan
 * Version : V1.0
 */
public class Test08 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }


    /**
     * 使用Filter进行筛选
     * @param
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testFilter(){
        /** 筛选素食菜单 */
        List<String> collect = dishList.stream()
                .filter(Dish::isVegetarain)
                .map(Dish::getName)
                .collect(Collectors.toList());
        System.out.println(collect);// [french fries, rice, season fruit, pizza]

    }

    /**
     * 筛选出来各异的元素, 从数组中提取出来大于1的元素，不要重复
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testDistinct(){
        List<Integer> integerList = Arrays.asList(1, 2, 3, 2);
        List<Integer> collect = integerList.stream()
                .filter(i -> i > 1)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(collect);// [2, 3]
    }

    /**
     * 提取菜单中前三个素食菜单的名字
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testLimit(){
        List<String> collect = dishList.stream()
                .filter(Dish::isVegetarain)// [french fries, rice, season fruit, pizza]
                .limit(3)// [french fries, rice, season fruit]
                .map(Dish::getName)
                .collect(Collectors.toList());
        System.out.println(collect);
    }

    /**
     * 提取菜单中素食菜单的名字 并且跳过第2个元素
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testSkip(){
        List<String> collect = dishList.stream()
                .filter(Dish::isVegetarain)// [french fries, rice, season fruit, pizza]
                .skip(1)// [rice, season fruit, pizza]
                .map(Dish::getName)
                .collect(Collectors.toList());
        System.out.println(collect);
    }




}
