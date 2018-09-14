package cn.liweidan.jdk8.FunctionDemo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/6/2 下午4:04
 * Author : Weidan
 * Version : V1.0
 */
public class FunctionDemo01 {

    @Test
    public void test01(){
        List<String> stringList = Arrays.asList("lambda", "test", "javascript");
        List<Integer> map = map(stringList, s -> s.length());
        System.out.println(map);
    }

    /**
     * Function<T, R>, T表示传入的类型，R表示返回的类型。即传入T类型的参数，返回R类型的参数
     * @param list
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> List<R> map(List<T> list, Function<T, R> function){
        List<R> result = new ArrayList<>();
        for (T t : list) {
            result.add(function.apply(t));
        }
        return result;
    }

}
