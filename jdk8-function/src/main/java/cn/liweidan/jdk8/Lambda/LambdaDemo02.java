package cn.liweidan.jdk8.Lambda;

import cn.liweidan.jdk8.Lambda.Demo02.BufferedReaderProcess;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * <p>Desciption:环绕方式的函数式编程</p>
 * CreateTime : 2017/6/2 下午3:32
 * Author : Weidan
 * Version : V1.0
 */
public class LambdaDemo02 {

    @Test
    public void test01(){
        String br = LambdaDemo02.processFile(b -> b.readLine());
        System.out.println(br);
        br = LambdaDemo02.processFile(b -> b.readLine() + b.readLine() + b.readLine());
        System.out.println(br);
    }

    /**
     * 将读取文件的动作封装成参数
     * @param b
     * @return
     */
    public static String processFile(BufferedReaderProcess b){
        try(BufferedReader br =
                    new BufferedReader(
                            new FileReader("/Users/liweidan/Java/workspace/Java-jdk8-demo/src/main/java/cn/liweidan/jdk8/Lambda/LambdaDemo01.java"))){
            return b.process(br);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String processFileOld(BufferedReaderProcess b){
        StringBuilder str = new StringBuilder();
        try(BufferedReader br =
                    new BufferedReader(
                            new FileReader("/Users/liweidan/Java/workspace/Java-jdk8-demo/src/main/java/cn/liweidan/jdk8/Lambda/LambdaDemo01.java"))){
            str.append(br.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

}
