package com.sdrfengmi.study._001_java8;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Interface {

    /**
     * 函数接口定义 lambda表达式
     *
     * @param args
     * @date 2019年5月16日
     * @author 陈振东
     */
    public static void main(String[] args) {

        DefaultableImpl defaultableImpl = new DefaultableImpl();
        String notRequired = defaultableImpl.notRequired();
        String notRequired3 = Defaulable.notRequired3();


        //函数接口 lambda表达式
        Defaulable defaulable = DefaulableFactory.create(DefaultableImpl::new);
        Defaulable defaulable2 = DefaulableFactory.create(() -> new DefaultableImpl());
        System.out.println(defaulable.notRequired());


        Defaulable overridableImpl = DefaulableFactory.create(OverridableImpl::new);
        System.out.println(overridableImpl.notRequired());


    }

    private interface Defaulable {
        // Interfaces now allow default methods, the implementer may or 
        // may not implement (override) them.
        default String notRequired() {
            return "Default implementation";
        }

        default String notRequired2() {
            return "Default implementation2";
        }

        static String notRequired3() {
            return "Default implementation3";
        }

        static String notRequired4() {
            return "Default implementation4";
        }
    }

    private static class DefaultableImpl implements Defaulable {
    }

    private static class OverridableImpl implements Defaulable {
        @Override
        public String notRequired() {
            return "Overridden implementation";
        }
    }

    private interface DefaulableFactory {
        // Interfaces now allow static methods
        static Defaulable create(Supplier<Defaulable> supplier) {
            return supplier.get();
        }
    }

}


@FunctionalInterface
interface AA {
    //只能有一个方法
    void get();

    //可以有 多个 default
    default void set() {

    }

    default <R> R apply(Function<Integer, R> t) {
        return t.apply(1);
    }

//    <R, A,T> R collect(Collector<? super T, A, R> collector);


}
