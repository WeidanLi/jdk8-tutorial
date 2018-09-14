package cn.liweidan.jdk7;

import cn.liweidan.pojo.Dish;
import cn.liweidan.utils.DishUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/6 下午3:10
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
     * 取出卡路里大于300的菜单。
     */
    @Test
    public void test01(){
        List<Dish> result = new ArrayList<>();
        for (Dish dish : dishList) {
            if(dish.getColories() > 300){
                result.add(dish);
            }
        }
        System.out.println(result);
    }

}
