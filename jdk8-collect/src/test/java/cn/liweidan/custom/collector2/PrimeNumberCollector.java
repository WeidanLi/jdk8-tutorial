package cn.liweidan.custom.collector2;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * <p>Desciption:质数收集器</p>
 * CreateTime : 2017/7/11 上午10:43
 * Author : Weidan
 * Version : V1.0
 */
public class PrimeNumberCollector implements Collector<Integer,
                                            Map<Boolean, List<Integer>>,
                                        Map<Boolean, List<Integer>>> {

    public static <A> List<A> takeWhile(List<A> list, Predicate<A> p){
        int i = 0;
        for (A a : list) {
            if(!p.test(a)){
                return list.subList(0, i);
            }
            i++;
        }
        return list;
    }

    /**
     * 拿到所有的质数，以及被测数字。取出小于被测数的平方根与所有质数比较，只拿被测数与小于平方根的质数做计算
     * @param primes
     * @param candidate
     * @return
     */
    public static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return takeWhile(primes, i -> i <= candidateRoot)
                .stream()
                .noneMatch(p -> candidate % p == 0);
    }

    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<Boolean, List<Integer>>(){{
            put(true, new ArrayList<>());
            put(false, new ArrayList<>());
        }};
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            acc.get(isPrime(acc.get(true), candidate))
                    .add(candidate);
        };
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return null;
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }
}
