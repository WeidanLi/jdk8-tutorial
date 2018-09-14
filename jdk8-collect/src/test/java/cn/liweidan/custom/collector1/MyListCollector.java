package cn.liweidan.custom.collector1;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * <p>Desciption:自定义ToList收集器</p>
 * CreateTime : 2017/7/10 下午6:37
 * Author : Weidan
 * Version : V1.0
 */
public class MyListCollector<T> implements Collector<T, List<T>, List<T>> {

    /*
        第一个泛型指的是需要收集的流的泛型
        第二个泛型指的是累加器在收集时候的类型
        第三个泛型指的是返回的类型（可能不是集合，比如counting()）
     */

    /**
     * 建立一个新的结果容器
     * @return
     */
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    /**
     * 将元素累加到容器中去
     * @return
     */
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return (list, item) -> list.add(item);
    }

    /**
     * 对结果容器进行最终转换（如需要转换成Long返回，则在这一步体现）
     * @return
     */
    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity();// 此处无需进行转换，直接返回此函数即可
    }

    /**
     * 对每个子流中的数据进行规约操作
     * 即在集合流中，处理器会将集合流进行不停地分割，分割到一定的很多的小子流的时候，再进行操作
     * 在这一步就是将每一个小流中的元素合并到一起
     * @return
     */
    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list1, list2) ->{
            list1.addAll(list2);
            return list1;
        };
    }

    /**
     * 这个方法是定义流返回的情况，一共有三种情况，存放于Characteristics枚举中
     * UNORDERED：规约结果不受项目的遍历和累计顺序的影响
     * CONCURRENT：accumulator函数可以从多个线程去调用。如果收集器没有标记UNORDERED那他仅用在无需数据源才可以规约
     * INDENTITY_FINISH：表明完成器方法返回的是一个恒等函数，可以跳过。标记这种情况则表示累加器A可以不加检查的转换为累加器B
     * @return
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT, Characteristics.IDENTITY_FINISH));
    }
}
