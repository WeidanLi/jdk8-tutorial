package cn.liweidan.custom.collector1;

import cn.liweidan.common.utils.DishUtils;
import cn.liweidan.pojo.Dish;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Desciption:自定义收集器</p>
 * CreateTime : 2017/7/10 下午6:36
 * Author : Weidan
 * Version : V1.0
 */
public class Demo01 {

    List<Dish> dishList;

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 测试自定义toList收集器
     */
    @Test
    public void test01(){
        List<Dish> collect = dishList.stream().collect(new MyListCollector<Dish>());
        System.out.println(collect);
    }

    /**
     * 使用方式二进行自定义收集
     */
    @Test
    public void test02(){
        ArrayList<Object> collect = dishList.stream().collect(
                ArrayList::new, // 相当于方式一的supplier()方法，用于创建一个容器
                List::add,// 相当于方式一的accumulator方法，用于迭代遍历每个元素进行加入容器
                List::addAll// 规约并行中所有的容器
        );
        System.out.println(collect);
    }
}
