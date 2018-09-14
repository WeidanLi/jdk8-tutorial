package cn.liweidan.jdk8.Lambda.Demo02;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * <p>Desciption:读取流的函数式编程，把行为参数化</p>
 * CreateTime : 2017/6/2 下午3:33
 * Author : Weidan
 * Version : V1.0
 */
@FunctionalInterface // 注意这个注解
public interface BufferedReaderProcess {

    String process(BufferedReader b) throws IOException;

}
