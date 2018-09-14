package cn.liweidan.demo;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>Desciption:构建流的方式</p>
 * CreateTime : 2017/6/12 下午2:20
 * Author : Weidan
 * Version : V1.0
 */
public class Test07 {

    /**
     * 由值创建流
     */
    @Test
    public void test01(){
        Stream<String> stringStream = Stream.of("Java 8", "Lambdas", "In", "Action");
    }

    /**
     * 由数组创建
     */
    @Test
    public void test02(){
        int[] numbers = {1, 2, 3, 4, 5, 6};
        Arrays.stream(numbers);
    }

    /**
     * 由文件中产生
     */
    @Test
    public void test03(){
        long uniqueWords = 0;
        try (Stream<String> lines = Files.lines(Paths.get("/Users/liweidan/Java/workspace/java-jdk8-stream2/src/test/resources/Test08.java"), Charset.defaultCharset())){
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
                                .distinct()
                                .count();
            System.out.println(uniqueWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * iterate生成无限流
     */
    @Test
    public void test04(){
        // 代表从0开始，每隔2就生成一次数值，这个流是个无限流。
        Stream.iterate(0, n -> n+2)
                .limit(10)
                .forEach(System.out::println);
    }

    /**
     * 斐波那契数列
     * 0，1，1，2，3，5，8...
     */
    @Test
    public void test05(){
        Stream.iterate(new int[]{0, 1}, n -> new int[]{n[0] + n[1], n[0] + n[1] + n[1]})
                .limit(10)
                .forEach(t -> System.out.println(t[0] + "," + t[1]));
    }

    /**
     * generate生成
     */
    @Test
    public void test06(){
        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);
    }

    /**
     * 利用generate生成斐波那契数列
     * 并行的时候不安全
     */
    @Test
    public void test07(){
        IntSupplier fib = new IntSupplier() {
            int previous = 0;
            int current = 1;
            @Override
            public int getAsInt() {
                int oldPrev = this.previous;
                int nextValue = this.previous + this.current;
                this.previous = this.current;
                this.current = nextValue;
                return oldPrev;
            }
        };

        IntStream.generate(fib).limit(10).forEach(System.out::println);
    }
}
