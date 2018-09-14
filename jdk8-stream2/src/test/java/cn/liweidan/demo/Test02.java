package cn.liweidan.demo;

import cn.liweidan.common.utils.DishUtils;
import cn.liweidan.pojo.Dish;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Desciption:映射</p>
 * CreateTime : 2017/6/12 上午10:04
 * Author : Weidan
 * Version : V1.0
 */
public class Test02 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 获取所有菜单名字的长度
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testMap(){
        List<Integer> collect = dishList.stream()
                .map(Dish::getName)// 首先获取所有菜单名字
                .map(String::length)// 再通过菜单名字的流获取长度并且收集起来
                .collect(Collectors.toList());
        System.out.println(collect);// [4, 4, 7, 12, 4, 12, 5, 6, 6]
    }

    /**
     * 将多个流合并成一个流 flatMap
     * 需求：将["Hello", "World"]分成各不相同的字符
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testFlatMap(){
        // Method 1
        List<String> strings = Arrays.asList("Hello", "World");
        List<String[]> collect = strings.stream()
                .map(w -> w.split(""))
                .distinct()
                .collect(Collectors.toList()); // 来到这一步的时候发现拿出来的是两个数组集合
        /*
            这是因为在流的过程中，调用String的切管方法，得到的时候两个单词的数组的流，也就是Stream<String[]>的形式，但这个并不是我们想要的
        */
        System.out.println(collect);// [[Ljava.lang.String;@2f333739, [Ljava.lang.String;@77468bd9]

        // Method 2
        List<Stream<String>> collect1 = strings.stream()
                .map(w -> w.split(""))
                .map(Arrays::stream)
                .collect(Collectors.toList());// 拿到的是String流的集合。这也不是我们想要的

        // Method 3 flatMap
        List<String> collect2 = strings.stream()
                .map(w -> w.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(collect2);// [H, e, l, o, W, r, d]
        /*
        flatMap 可以把多个流合并成一个流进行操作。
         */
    }
}
