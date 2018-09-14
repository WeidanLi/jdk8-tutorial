package cn.liweidan.collect;

import cn.liweidan.common.utils.DishUtils;
import cn.liweidan.pojo.Dish;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>Desciption:JDK8收集器</p>
 * CreateTime : 2017/6/30 上午10:22
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
     * 收集器是可以收集一个流中的数据的一个容器
     */
    @Test
    public void demo01(){
        List<Dish> collect = dishList.stream().collect(Collectors.toList());
        System.out.println(collect);
    }

    /**
     * cn.liweidan.collect.Demo01#demo02()
     * counting()使用
     */
    @Test
    public void demo02(){
        /* 查询卡路里大于400的菜单的个数 */
        Long count = dishList.stream().filter(dish -> dish.getColories() > 400).collect(Collectors.counting());
        System.out.println("卡路里大于400的菜单个数：" + count);

        /* 第二种写法 */
        count = dishList.stream().filter(dish -> dish.getColories() > 400).count();
        System.out.println("卡路里大于400的菜单个数：" + count);
    }

    /**
     * cn.liweidan.collect.Demo01#demo03()
     * 取出最大值以及最小值
     */
    @Test
    public void demo03(){
        /* 定义一个卡路里比较器 */
        Comparator<Dish> comparator = Comparator.comparingInt(Dish::getColories);
        /* Collectors.maxBy(comparator)即取出流中的最大值 */
        Optional<Dish> collect = dishList.stream().collect(Collectors.maxBy(comparator));
        System.out.println(collect.get());
        /* Collectors.minBy(comparator)即取出流中的最小值 */
        Optional<Dish> collect1 = dishList.stream().collect(Collectors.minBy(comparator));
        System.out.println(collect1.get());
    }

    /**
     * cn.liweidan.collect.Demo01#demo04()
     * 汇总：对集合中所有菜单的卡路里进行统计计算
     */
    @Test
    public void demo04(){
        int collect = dishList.stream().collect(Collectors.summingInt(Dish::getColories));
        System.out.println(collect);
    }

    /**
     * 查询菜单集合中卡路里的平均值以及所有统计数据
     */
    @Test
    public void demo05(){
        /* 查询所有菜单卡路里的平均值 */
        Double collect = dishList.stream().collect(Collectors.averagingInt(Dish::getColories));
        System.out.println("卡路里均值：" + collect);
        /* 查询菜单中所有的汇总数据 */
        IntSummaryStatistics collect1 = dishList.stream().collect(Collectors.summarizingInt(Dish::getColories));
        System.out.println(collect1);// IntSummaryStatistics{count=9, sum=4200, min=120, average=466.666667, max=800}
    }

    /**
     * joining连接字符串
     */
    @Test
    public void demo06(){
        String collect = dishList.stream().map(Dish::getName).collect(Collectors.joining(", "));
        System.out.println(collect);
        // pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon
    }

    /**
     * cn.liweidan.collect.Demo01#demo07()
     * Collectors.reducing的使用
     */
    @Test
    public void demo07(){
        /**
         * 取出卡路里最大的菜单
         */
        Optional<Dish> collect = dishList.stream().collect(Collectors.reducing((d1, d2) -> d1.getColories() > d2.getColories() ? d1 : d2));
        System.out.println(collect.get());

        /**
         * 计算菜单总卡路里值
         */
        Integer integer1 = dishList.stream().collect(Collectors.reducing(0,// 初始值
                Dish::getColories,// 转换函数
                Integer::sum));// 累积函数
        System.out.println(integer1);

        Integer integer2 = dishList.stream().map(Dish::getColories).reduce(Integer::sum).get();
        System.out.println(integer2);

        int sum = dishList.stream().mapToInt(Dish::getColories).sum();// 推荐
        System.out.println(sum);

    }
}
