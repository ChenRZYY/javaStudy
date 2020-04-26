package com.sdrfengmi.study._001_java8;

import org.junit.Test;

import java.util.function.Function;

public class _01_FunctionTest {


    @Test
    public void function() {
        Function<Integer, Integer> test1 = i -> i + 1;
        Function<Integer, Integer> test2 = i -> i * i;
        System.out.println(calculate(test1, 5));
        System.out.println(calculate(test2, 5));


        Function<Integer, Integer> A = i -> i + 1;
        Function<Integer, Integer> B = i -> i * i;
        System.out.println("F1:" + B.apply(A.apply(5)));
        System.out.println("F2:" + A.apply(B.apply(5)));
    }

    // compose和andThen可以解决我们的问题。先看compose的源码
    // compose接收一个Function参数，返回时先用传入的逻辑执行apply，然后使用当前Function的apply。
    //    andThen跟compose正相反，先执行当前的逻辑，再执行传入的逻辑。
    //这样说可能不够直观，我可以换个说法给你看看
    //compose等价于B.apply(A.apply(5))，而andThen等价于A.apply(B.apply(5))。
    public void compose() {
        Function<Integer, Integer> A = i -> i + 1;
        Function<Integer, Integer> B = i -> i * i;
        System.out.println("F1:" + B.apply(A.apply(5)));
        System.out.println("F1:" + B.compose(A).apply(5));
        System.out.println("F2:" + A.apply(B.apply(5)));
        System.out.println("F2:" + B.andThen(A).apply(5));

        //        我们可以看到上述两个方法的返回值都是一个Function，这样我们就可以使用建造者模式的操作来使用。
        B.compose(A).compose(A).andThen(A).apply(5);
    }

    public Integer calculate(Function<Integer, Integer> test, Integer number) {
        return test.apply(number);
    }


}
