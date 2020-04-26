package com.sdrfengmi.study._001_java8;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Streams {


    /**
     * streams 专门用于Collection集合的操作
     * Predicate 谓语
     * <p>
     * stream() − 为集合创建串行流。
     * parallelStream() − 为集合创建并行流。
     *
     * @param
     * @date 2019年5月16日
     * @author 陈振东
     */
    @SuppressWarnings("unused")
    @Test
    public void stream() {
        final Collection<Task> tasks = Arrays.asList(new Task(Status.OPEN, 5), new Task(Status.OPEN, 13), new Task(Status.CLOSED, 8));

        final long totalPointsOfOpenTasks = tasks.stream().filter(task -> task.getStatus() == Status.OPEN).mapToInt(Task::getPoints).sum();

        System.out.println("Total points: " + totalPointsOfOpenTasks);

        // stream 创建流  
        // filter-->Predicate谓语  过滤  函数式接口
        // Collectors.toList()
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd", "", "jkl");
        List<String> filtered = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());

        // 获取对应的平方数
        // map 是映射元素对应结果,可以处理改元素
        // distinct 去重
        // collect 聚合,把list-->list/set/map等
        List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
        List<Integer> numberList = numbers.stream().map(i -> i * i).distinct().collect(Collectors.toList());
        System.out.println("numberList: " + numberList);
        // 转化成 map
        Map<Integer, Integer> numberMap = numbers.stream().map(i -> i * i).distinct().collect(Collectors.toMap(key -> key, value -> value + 1000));
        System.out.println("numberMap: " + numberMap);
        //转换成Set
        Set<Integer> numberSet = numbers.stream().map(i -> i = 3).collect(Collectors.toSet());
        System.out.println("numberSet: " + numberSet);

        //Collectors.joining(", ") 合并字符串 用于list打印出来
        String mergedString = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining(", "));
        System.out.println("mergedString: " + mergedString);

        // count stream流数量
        long count = strings.stream().filter(string -> string.isEmpty()).count();

        // limit 方法用于获取指定数量的流
        Random random = new Random();
        random.ints().limit(10).forEach(System.out::println);

        //sorted 方法用于对流进行排序
        random.ints().limit(10).sorted().forEach(System.out::println);

        //并行（parallel）程序
        long count2 = strings.parallelStream().filter(string -> string.isEmpty()).count();

    }

    @Test
    //    forEach用来对stream中的数据进行迭代，比如上面创建流的操作就使用了forEach。看会上面的例子后理解forEach不会很难的。需要注意的是，forEach操作是不能改变遍历对象本身的。
    public void foreach() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        numbers.stream().forEach(System.out::print);
        numbers.stream().forEach(System.out::print);
        numbers.stream().forEach(i -> System.out.print(i));
    }

    @Test
    public void map() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<Integer> outPutList = numbers.stream().map(i -> i + 2).distinct().collect(Collectors.toList());
        outPutList.forEach(n -> System.out.print(n + " "));
    }

    @Test
//    filter 方法用于通过设置的条件过滤出元素。以下代码片段使用 filter 方法过滤出空字符串：
    public void filter() {
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd", "", "jkl");
// 获取空字符串的数量
        long count = strings.stream().filter(string -> string.isEmpty()).count();
        System.out.println(count);
    }

    @Test
//   limit 方法用于获取指定数量的流。 以下代码片段使用 limit 方法打印出 10 条数据：
    public void limit() {
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd", "", "jkl");
        strings.stream().limit(3).forEach(System.out::println);

//        还有一个常用的是配合skip()方法用来进行分页操作。
        int pageSize = 3;
        int currentPage = 1;
        List<String> collect = strings.stream()
                .skip(pageSize * (currentPage - 1)) //起始页
                .limit(pageSize)//每页显示多少
                .collect(Collectors.toList());
        collect.forEach(System.out::println);
    }


    @Test
//   Collectors 可用于返回列表或字符串，上面介绍map的例子就用到了Collectors，下面给出菜鸟教程的一个例子：
    public void Collectors() {
        List<String>strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
        List<String> filtered = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());

        System.out.println("筛选列表: " + filtered);
        String mergedString = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining("_"));
        System.out.println("合并字符串: " + mergedString);
    }

    @Test
    //  顾名思义，统计就是用来统计数据的，一般用于int、double、long等基本类型上。
    public void count() {
        List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
        IntSummaryStatistics stats = numbers.stream().mapToInt((x) -> x).summaryStatistics();
        //做统计分析`
        System.out.println("列表中最大的数 : " + stats.getMax());
        System.out.println("列表中最小的数 : " + stats.getMin());
        System.out.println("所有数之和 : " + stats.getSum());
        System.out.println("平均数 : " + stats.getAverage());
    }



    private enum Status {
        OPEN, CLOSED
    }

    private static final class Task {

        private final Status status;

        private final Integer points;

        Task(final Status status, final Integer points) {
            this.status = status;
            this.points = points;
        }

        public Integer getPoints() {
            return points;
        }

        public Status getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return String.format("[%s, %d]", status, points);
        }
    }
}