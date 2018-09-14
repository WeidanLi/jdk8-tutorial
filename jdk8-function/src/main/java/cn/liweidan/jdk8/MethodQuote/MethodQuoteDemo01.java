package cn.liweidan.jdk8.MethodQuote;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Desciption:方法引用Demo</p>
 * CreateTime : 2017/6/2 下午7:46
 * Author : Weidan
 * Version : V1.0
 */
public class MethodQuoteDemo01 {

    @Test
    public void test(){
        List<String> list = Arrays.asList("a", "b", "A", "B");
        list.sort(String::compareToIgnoreCase);
        System.out.println(list);
    }

}
