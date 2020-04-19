package com.sdrfengmi.study._001_java8;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Lambda {
    
    /**
     * 只能实例化接口,同时实现一个接口的方法,实例化接口--->lambda 表达式就是一个匿名内部类(闭包)
     * 就是生命一个接口实例
     * @date 2019年5月16日
     * @author 陈振东
     * @param args
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        
        //1TODO 示例
        List<String> names1 = Arrays.asList("Google ", "Runoob ", "Taobao ", "Taobao ", "Baidu ", "Sina ");
        List<String> names2 = Arrays.asList("Google ", "Runoob ", "Taobao ", "Taobao ", "Baidu ", "Sina ");
        
        sortUsingJava7(names1);
        System.out.println("使用 Java 7 语法: " + names1);
        sortUsingJava8(names2);
        System.out.println("使用 Java 8 语法: " + names2);
        
        //2TODO 示例 实现方法定义接口实例
        // 类型声明  声明一个接口(实现多参数接口)
        MathOperation addition = (int a, int b) -> a + b;
        // 不用类型声明
        MathOperation subtraction = (a, b) -> a - b;
        // 大括号中的返回语句
        MathOperation multiplication = (int a, int b) -> {
            return a * b;
        };
        // 没有大括号及返回语句
        MathOperation division = (int a, int b) -> a / b;
        //java7的方法
        MathOperation addition7 = new MathOperation() {
            @Override
            public int operation(int a, int b) {
                return a + b;
            }
            
        };
        
        Lambda lambda = new Lambda();
        
        System.out.println("10 + 5 = " + addition.operation(10, 5));
        
        System.out.println("10 - 5 = " + lambda.operate(10, 5, subtraction));
        System.out.println("10 - 5 = " + lambda.operate(10, 5, (a, b) -> a - b));
        
        System.out.println("10 x 5 = " + lambda.operate(10, 5, multiplication));
        System.out.println("10 x 5 = " + lambda.operate(10, 5, (int a, int b) -> a - b));
        
        System.out.println("10 / 5 = " + lambda.operate(10, 5, division));
        System.out.println("10 / 5 = " + lambda.operate(10, 5, (a, b) -> a / b));
        
        //实现单参数接口 不用括号
        GreetingService greetService1 = message -> System.out.println("Hello " + message);
        // 用括号
        GreetingService greetService2 = (message) -> System.out.println("Hello " + message);
        greetService1.sayMessage("Runoob");
        greetService2.sayMessage("Google");
        
        // 最简单的Lambda表达式可由逗号分隔的参数列表、->符号和语句块组成，例如：
        Arrays.asList("a", "b", "d").forEach(e -> System.out.println(e));
        
        // 在上面这个代码中的参数e的类型是由编译器推理得出的，你也可以显式指定该参数的类型，例如：
        Arrays.asList("a", "b", "d").forEach((String e) -> System.out.println(e));
        
        // 如果Lambda表达式需要更复杂的语句块，则可以使用花括号将该语句块括起来，类似于Java中的函数体，例如：
        Arrays.asList("a", "b", "d").forEach(e -> {
            System.out.print(e);
            System.out.print(e);
        });
        
        // Lambda表达式可以引用类成员和局部变量（会将这些变量隐式得转换成final的），例如下列两个代码块的效果完全相同：
        String separator = ",";
        Arrays.asList("a", "b", "d").forEach((String e) -> System.out.print(e + separator));
        
        final String separator2 = ",";
        Arrays.asList("a", "b", "d").forEach((String e) -> System.out.print(e + separator2));
        
        
        //TODO 3 自己写lambda表达式
        // Lambda表达式有返回值，返回值的类型也由编译器推理得出。如果Lambda表达式中的语句块只有一行，则可以不用使用return语句，下列两个代码片段效果相同：
        Arrays.asList("a", "b", "d").sort((e1, e2) -> e1.compareTo(e2));
        
        //加括号
        Arrays.asList("a", "b", "d").sort((e1, e2) -> {
            int result = e1.compareTo(e2);
            return result;
        });
        
        // java7
        Arrays.asList("a", "b", "d").sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        
    }
    
    // 使用 java 7 排序
    public static void sortUsingJava7(List<String> names) {
        Collections.sort(names, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
    }
    
    // 使用 java 8 排序
    public static void sortUsingJava8(List<String> names) {
        Collections.sort(names, (s1, s2) -> s1.compareTo(s2));
    }
    
    interface MathOperation {
        int operation(int a, int b);
    }
    
    interface GreetingService {
        void sayMessage(String message);
    }
    
    private int operate(int a, int b, MathOperation mathOperation) {
        return mathOperation.operation(a, b);
    }
    
}
