package cn.sdrfengmi._05_spark._01_core;//package cn.hp._05_spark._01_core;
//
//import com.google.common.collect.Lists;
//import org.apache.sparkSession.SparkConf;
//import org.apache.sparkSession.api.java.JavaPairRDD;
//import org.apache.sparkSession.api.java.JavaRDD;
//import org.apache.sparkSession.api.java.JavaSparkContext;
//import org.apache.sparkSession.api.java.function.FlatMapFunction;
//import org.apache.sparkSession.api.java.function.VoidFunction;
//import scala.Tuple2;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class FlatMap {
//    public static void main(String[] args) {
////        System.setProperty("hadoop.home.dir", "E:\\hadoop-2.7.1");
//        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Spark_DEMO");
//        JavaSparkContext sc = new JavaSparkContext(sparkConf);
//
//        List<Tuple2<String, String>> tuple2s = Lists.newArrayList(new Tuple2<String, String>("cat", "11"), new Tuple2<String, String>("dog", "22"), new Tuple2<String, String>("cat", "13"), new Tuple2<String, String>("pig", "44"));
//
//        JavaPairRDD<String, String> javaPairRDD1 = sc.parallelizePairs(tuple2s, 2);
//
//        // 数据扁平化
//        JavaRDD<String> javaRDD = javaPairRDD1.flatMap(new FlatMapFunction<Tuple2<String, String>, String>() {
//            public Iterator<String> call(Tuple2<String, String> stringStringTuple2) throws Exception {
//                List<String> list = Lists.newArrayList();
//                list.add(stringStringTuple2._1);
//                list.add(stringStringTuple2._2);
//                return list.iterator();
//            }
//        });
//
//        // 输出数据
//        javaRDD.foreach(new VoidFunction<String>() {
//            public void call(String s) throws Exception {
//                System.out.println(s);
//            }
//        });
//    }
//}
//
