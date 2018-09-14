package cn.liweidan.group;

import cn.liweidan.common.utils.DishUtils;
import cn.liweidan.pojo.CaloricLevel;
import cn.liweidan.pojo.Dish;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>Desciption:测试分组函数</p>
 * CreateTime : 2017/7/3 上午11:10
 * Author : Weidan
 * Version : V1.0
 */
public class Demo01 {

    List<Dish> dishList;

    @Before
    public void before() {
        dishList = DishUtils.getDishes();
    }

    /**
     * 简单分组
     */
    @Test
    public void test01() {
        /** 按照属性类型进行分组 */
        Map<Dish.Type, List<Dish>> collect = dishList.stream().collect(Collectors.groupingBy(Dish::getType));
        System.out.println(collect);
        // {FISH=[Dish(name=prawns, vegetarain=false, colories=300, type=FISH),
        // Dish(name=salmon, vegetarain=false, colories=450, type=FISH)],
        // OTHER=[Dish(name=french fries, vegetarain=true, colories=530, type=OTHER),
        // Dish(name=rice, vegetarain=true, colories=350, type=OTHER),
        // Dish(name=season fruit, vegetarain=true, colories=120, type=OTHER),
        // Dish(name=pizza, vegetarain=true, colories=550, type=OTHER)],
        // MEAT=[Dish(name=pork, vegetarain=false, colories=800, type=MEAT),
        // Dish(name=beef, vegetarain=false, colories=700, type=MEAT), Dish(name=chicken, vegetarain=false, colories=400, type=MEAT)]}

        /** 自定义简单的分组方式 */
        Map<CaloricLevel, List<Dish>> map = dishList.stream().collect(Collectors.groupingBy(d -> {
            /** 此处写if的时候注意要顾及到所有的情况 */
            if (d.getColories() <= 400) {
                return CaloricLevel.DIET;
            } else if (d.getColories() <= 700) {
                return CaloricLevel.NORMAL;
            } else {
                return CaloricLevel.FAT;
            }
        }));
        System.out.println(map);
        // {FAT=[Dish(name=pork, vegetarain=false, colories=800, type=MEAT)],
        // NORMAL=[Dish(name=beef, vegetarain=false, colories=700, type=MEAT), Dish(name=french fries, vegetarain=true, colories=530, type=OTHER), Dish(name=pizza, vegetarain=true, colories=550, type=OTHER), Dish(name=salmon, vegetarain=false, colories=450, type=FISH)],
        // DIET=[Dish(name=chicken, vegetarain=false, colories=400, type=MEAT), Dish(name=rice, vegetarain=true, colories=350, type=OTHER), Dish(name=season fruit, vegetarain=true, colories=120, type=OTHER), Dish(name=prawns, vegetarain=false, colories=300, type=FISH)]}
    }

    /**
     * 多级分组
     */
    @Test
    public void test02() {
        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> collect =
                dishList.stream().collect(Collectors.groupingBy(Dish::getType,
                        Collectors.groupingBy(d -> {
                            if (d.getColories() <= 400) {
                                return CaloricLevel.DIET;
                            } else if (d.getColories() <= 700) {
                                return CaloricLevel.NORMAL;
                            } else {
                                return CaloricLevel.FAT;
                            }
                        })));
        System.out.println(collect);
    }

    /*################### 按子组收集数据 #######################*/

    /**
     * 多级分组收集数据
     */
    @Test
    public void test03() {
        /** 计算每一种品类的菜单个数 */
        Map<Dish.Type, Long> typeLongMap = dishList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.counting()));
        System.out.println(typeLongMap);
    }

    /**
     * 收集每一种类型卡路里最高的菜单
     */
    @Test
    public void test04() {
        Map<Dish.Type, Optional<Dish>> map = dishList.stream()
                .collect(Collectors.groupingBy(Dish::getType, Collectors.maxBy(Comparator.comparing(Dish::getColories))));
        System.out.println(map);
    }

    /**
     * 从Optional里提取需要的对象，
     */
    @Test
    public void test05() {
        dishList = new ArrayList<>();// 如果reducing为空的话，就不返回数据，所以Optional在这里提取是安全的
        Map<Dish.Type, Dish> map = dishList.stream()
                .collect(Collectors.groupingBy(Dish::getType,
                        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(Dish::getColories)), Optional::get)));

        System.out.println(map);
    }

    /**
     * 另外一个常常与groupingBy配合使用的函数mapping的使用
     * mapping接收两个参数，一个是转换函数（注意涵盖所有的情况）， 另外一个是输出的类型
     */
    @Test
    public void test06() {
        Map<Dish.Type, Set<CaloricLevel>> collect = dishList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.mapping(dish -> {
            if (dish.getColories() < 400)
                return CaloricLevel.DIET;
            else if (dish.getColories() < 700)
                return CaloricLevel.NORMAL;
            else
                return CaloricLevel.FAT;
        }, Collectors.toSet())));
        System.out.println(collect);
    }

    /*######################## 分区 ############################*/

    /**
     * 分区：是分组的特殊情况，由一个谓词（返回布尔值的函数）作为分类函数
     * partitioningBy，传递一个返回布尔值的函数
     */
    @Test
    public void test07(){
        Map<Boolean, List<Dish>> collect = dishList.stream().collect(Collectors.partitioningBy(Dish::isVegetarain));
        System.out.println(collect);
    }

    /**
     * 将数字按质数以及非质数分区
     */
    @Test
    public void test08(){
        int demoInt = 100;
        Map<Boolean, List<Integer>> collect = IntStream.rangeClosed(2, demoInt).boxed()
                .collect(Collectors.partitioningBy(candidate -> isPrime(candidate)));
        System.out.println(collect);
    }

    public boolean isPrime(int candidate){
        /** 通过传递的数字进行开方，我们只需要对传递的数字与开方的数字进行比对即可，计算次数会减少 */
        int candidateRoot = (int) Math.sqrt((double) candidate);
        /** 产生一个从2开始到开方跟的数字的数据流，与该数据流的每一个元素进行求余 */
        return IntStream.rangeClosed(2, candidateRoot)
                .noneMatch(i -> candidate % i == 0);// 表示没有一个元素与开方根的数字求余等于0的
    }

}
