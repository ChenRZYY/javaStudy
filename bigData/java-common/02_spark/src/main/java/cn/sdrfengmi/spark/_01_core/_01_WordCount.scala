package cn.sdrfengmi.spark._01_core

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object _01_WordCount {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    //1、创建SparkContext
    //setMaster:在本地idea中运行的时候必须设置，如果在集群中运行就不用设置
    //setAppName:必须设置
    val conf = new SparkConf().setMaster("local[4]").setAppName("globalParameter").set("spark.default.parallelism", "500")
    //    conf.set("sparkSession.driver.host", "localhost")
    val sc: SparkContext = new SparkContext(conf)
    //2、数据读取
    //val rdd1: RDD[String] = sc.textFile("src\\main\\java\\cn\\hp\\spark\\_01_core\\source.txt")
    //val rdd1: RDD[String] = sc.textFile("hdfs://server02:8020/globalParameter/globalParameter.txt", 2)
    //val rdd1: RDD[String] = sc.textFile("hdfs:///data/globalParameter.txt")
    //val rdd1: RDD[String] = sc.textFile("hdfs://server02:8020/globalParameter/", 2) //TODO 读取文件夹
    val rdd1: RDD[String] = sc.textFile("01_dataset/wordcount.txt", 2)

    //["hello sparkSession hello","hello python hello"]
    //3、数据处理
    //3.1 单词切割 压平
    val rdd2: RDD[String] = rdd1.flatMap(item => item.split(" "))
    //["hello","sparkSession","hello","hello","python","hello"]
    //3.2 出现一个单词给一个词频1
    val rdd3: RDD[(String, Int)] = rdd2.map(item => (item, 1))
    //[("hello",1),("sparkSession",1),("hello",1),("hello",1),("python",1),("hello",1)]
    //3.3 对单词进行分组聚合
    val rdd4: RDD[(String, Int)] = rdd3.reduceByKey((agg, curr) => agg + curr)
    //数据运行过程:
    ////1、分组
    ////   hello -> List(1,1,1,1)
    ////   sparkSession -> List(1)
    ////   python -> List(1)
    ////2、聚合
    ////    hello
    ////        List(1,1,1,1)
    ////       (agg,curr)=> agg+curr  agg:上一次聚合结果  curr:本次要聚合的元素
    ////         第一次计算:agg:1   curr:1  => 2
    ////         第二次计算:agg:2   curr:1  => 3
    ////         第三次计算:agg:3   curr:1  => 4
    ////   (hello,4)
    ////   sparkSession
    ////          List(1)
    ////      (agg,curr)=> agg+curr
    ////   (sparkSession,1)
    //[(hello,4),(sparkSession,1),(python,1)]
    //3.4 将数据按照单词出现的个数进行降序排列
    val rdd5: RDD[(String, Int)] = rdd4.sortBy(_._2, false)
    //4、数据打印
    //    println(rdd5.collect().toBuffer)
    System.err.println(rdd5.collect().toBuffer)
    Thread.sleep(10000000)

  }
}
