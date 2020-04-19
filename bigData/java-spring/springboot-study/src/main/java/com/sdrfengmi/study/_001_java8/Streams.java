package com.sdrfengmi.study._001_java8;

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
     * 
     * stream() − 为集合创建串行流。
     * parallelStream() − 为集合创建并行流。
     * 
     * @date 2019年5月16日
     * @author 陈振东
     * @param args
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        final Collection<Task> tasks =
            Arrays.asList(new Task(Status.OPEN, 5), new Task(Status.OPEN, 13), new Task(Status.CLOSED, 8));
        
        final long totalPointsOfOpenTasks =
            tasks.stream().filter(task -> task.getStatus() == Status.OPEN).mapToInt(Task::getPoints).sum();
        
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
        System.out.println("numberList: "+numberList);
        // 转化成 map
        Map<Integer, Integer> numberMap =
            numbers.stream().map(i -> i * i).distinct().collect(Collectors.toMap(key -> key, value -> value + 1000));
        System.out.println("numberMap: "+numberMap);
        //转换成Set
        Set<Integer> numberSet = numbers.stream().map(i -> i = 3).collect(Collectors.toSet());
        System.out.println("numberSet: "+numberSet);
        
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
        
        //做统计分析
        IntSummaryStatistics stats = numbers.stream().mapToInt((x) -> x).summaryStatistics();
        System.out.println("列表中最大的数 : " + stats.getMax());
        System.out.println("列表中最小的数 : " + stats.getMin());
        System.out.println("所有数之和 : " + stats.getSum());
        System.out.println("平均数 : " + stats.getAverage());
    }
    
    private enum Status {
        OPEN, CLOSED
    };
    
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