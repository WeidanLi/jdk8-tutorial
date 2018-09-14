package cn.liweidan.jdk8.Lambda;

import cn.liweidan.jdk8.pojo.Apple;
import jdk.management.resource.ResourceId;
import org.junit.Test;

/**
 * <p>Desciption:lambda表达式的使用</p>
 * CreateTime : 2017/6/2 下午3:10
 * Author : Weidan
 * Version : V1.0
 */
public class LambdaDemo01 {
    /*
        Lambda表达式使用的两种方式：
            () -> expression
       或者  () -> {statements;}

       注意：在返回值方面：
            如果直接返回，则不需要写{}。如果写{}，则需要在返回值前面写上return语句
    */
    @Test
    public void test01(){
        Runnable runnable = () -> {
            System.out.println(new Apple("RED", 190));
        };
        /** 直接调用，没有使用线程 */
        runnable.run();

        /** 1.1 返回数据的时候直接写返回的结果 */
        ResourceId resourceId = () -> "ssss";
        System.out.println(resourceId.getName());// ssss

        // resourceId = () -> {"ssss"}; 编译出错。

        resourceId = () -> {return "sssss";};
        System.out.println(resourceId.getName());// sssss

        // (Integer i) -> return i; 报错

    }

}
