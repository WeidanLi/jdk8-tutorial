package cn.liweidan.demo;

import cn.liweidan.common.utils.DishUtils;
import cn.liweidan.pojo.Dish;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Desciption:查找和匹配</p>
 * CreateTime : 2017/6/12 上午10:29
 * Author : Weidan
 * Version : V1.0
 */
public class Test03 {

    List<Dish> dishList = new ArrayList<>();

    @Before
    public void before(){
        dishList = DishUtils.getDishes();
    }

    /**
     * 部分匹配：检查菜单中是否含有素食菜
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testAnyMatch(){
        boolean anyMatch = dishList.stream()
                .anyMatch(Dish::isVegetarain);
        if(anyMatch){
            System.out.println("这个菜单中含有素食品");
        }
    }

    /**
     * 所有匹配：检查菜单中所有菜单是否卡路里都小于1000
     */
    @Test
    public void testAllMatch(){
        if (dishList.stream().allMatch(d -> d.getColories() < 1000)) {
            System.out.println("所有菜单的卡路里都小于1000");
        }
    }

    /**
     * 所有不匹配：是否所有菜单都没有大于1000卡路里的
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testNoneMatch(){
        if (dishList.stream().noneMatch(d -> d.getColories() >= 1000)) {
            System.out.println("所有菜单卡路里都小于1000");
        }
    }

    /**
     * 查找任意一个符合条件元素
     * @author Weidan
     * @version 1.0
     * @return
     */
    @Test
    public void testFindAny(){
        dishList.stream()
                .filter(Dish::isVegetarain)
                .findAny()
                .ifPresent(d -> System.out.println(d.getName()));
        /*
        Optional: isPresent() 是否存在
                ifPresent(Consumer) 如果存在执行参数中的方法
                get()获取确切的值
                orElse(T other) 如果不存的时候 返回传入的值
          加入这个接口是为了解决长久以来的NULL异常
         */
    }
}
