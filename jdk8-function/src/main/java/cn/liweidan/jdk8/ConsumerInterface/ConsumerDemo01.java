package cn.liweidan.jdk8.ConsumerInterface;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>Desciption:Consumer的使用</p>
 * CreateTime : 2017/6/2 下午3:59
 * Author : Weidan
 * Version : V1.0
 */
public class ConsumerDemo01 {

    @Test
    public void test01(){
        forEach(Arrays.asList("Lambde", "test", "stream"),
                s -> {
                    System.out.println(s);
                });
    }

    /**
     * Consumer<T>提供了一个accept方法，返回void类型。
     */
    public static <T>void forEach(List<T> list, Consumer<T> s){
        for (T t : list) {
            s.accept(t);
        }
    }

}
