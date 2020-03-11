package cn.hp._05_spark.day1_core;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class _01_JavaWordCount {

    public static void main(String[] args) {
        //1、创建SparkContext
        SparkConf conf = new SparkConf().setMaster("local[4]").setAppName("wordcount");
        JavaSparkContext sc = new JavaSparkContext(conf);
        //2、数据读取
        JavaRDD<String> rdd1 = sc.textFile("dataSet/source.txt");
        //3、数据处理
        //3.1、切分压平
        //第一个泛型是函数的入参的类型
        //第二个泛型是函数的返回值的类型
        JavaRDD<String> rdd2 = rdd1.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                String[] arr = s.split(",");
                List<String> list = Arrays.asList(arr);
                return list.iterator();
            }
        });
        //3.2、给词频1
        //第一个泛型是函数的入参类型
        //第二个泛型是函数的返回值的KEY的类型
        //第三个泛型是函数的返回值的VALUE的类型
        JavaPairRDD<String, Integer> rdd3 = rdd2.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        });
        //3.3、分组聚合
        //第一个泛型是函数第一个参数的类型
        //第二个泛型是函数第二个参数的类型
        //第三个泛型是函数的返回值的类型
        JavaPairRDD<String, Integer> rdd4 = rdd3.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        //3.4、排序
        //[(sparkSession,3),(hello,1)]
        //[(3,sparkSession),(1,hello)]
        JavaPairRDD<Integer, String> rdd5 = rdd4.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
            @Override
            public Tuple2<Integer, String> call(Tuple2<String, Integer> tuple) throws Exception {
                return new Tuple2<>(tuple._2, tuple._1);
            }
        });

        JavaPairRDD<Integer, String> rdd6 = rdd5.sortByKey(false);

        JavaPairRDD<String, Integer> rdd7 = rdd6.mapToPair(new PairFunction<Tuple2<Integer, String>, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Tuple2<Integer, String> tuple) throws Exception {
                return new Tuple2<>(tuple._2, tuple._1);
            }
        });
        //4、结果打印
        List<Tuple2<String, Integer>> result = rdd7.collect();

        for(Tuple2<String,Integer> tuple:result){
            System.out.println(tuple);
        }
    }
}
