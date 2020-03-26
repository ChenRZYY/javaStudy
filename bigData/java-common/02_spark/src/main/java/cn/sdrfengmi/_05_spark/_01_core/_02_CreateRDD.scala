package cn.sdrfengmi._05_spark._01_core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.Test

class _02_CreateRDD {

  val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("spark_context"))

  @Test
  def createRddFromLocal(): Unit = {
    val data = Seq[Int](1, 4, 7, 8, 9)
    val rdd1 = sc.parallelize(data)
    //如果不设置分区数，分区默认=线程数
    //查看分区数的个数
    println(rdd1.partitions.length)
  }

  @Test
  def createRddFromFile(): Unit = {
    val rdd1 = sc.textFile("dataset/globalParameter.txt", 6)
    println(rdd1.partitions.length)
  }

  @Test
  def createRddFromRdd(): Unit = {
    val data = Seq[Int](1, 4, 7, 8, 9)
    val rdd1 = sc.parallelize(data)

    val rdd2 = rdd1.map(_ * 10)
    //rdd的分区数默认是继承父RDD的分区数
    println(rdd2.partitions.length)
  }


  @Test
  def reduceByKey(): Unit = {
    //(id,name,address,age)
    val data = Seq[(Int, String, String, Int)](
      (1, "aa", "shenzhen", 13),
      (2, "bb", "shanghai", 15),
      (3, "cc", "shenzhen", 17),
      (4, "dd", "shenzhen", 12),
      (5, "ee", "shanghai", 20),
      (6, "ff", "shanghai", 30),
      (7, "tt", "beijing", 34),
      (7, "tt", "beijing", 23),
      (7, "tt", "beijing", 26),
      (7, "tt", "beijing", 28)
    )

    //需求:求每个城市的年龄总和
    //[(shenzhen,240),....]
    //1、创建RDD
    val rdd1 = sc.parallelize(data)
    //2、取出年龄与地址

    val rdd2 = rdd1.map(x => (x._3, x._4))
    //[(shenzhen,13),(shanghai,15),......]
    //3、分组聚合
    val rdd3 = rdd2.reduceByKey((agg, curr) => agg + curr)

    rdd3.foreach(println(_))

    /**
      * 数据处理过程：
      * 1、分组
      * [
      * shenzhen -> List(13,17,12)
      * shanghai-> List(15,20,30)
      * beijing-> List(34,23,26,28)
      * ]
      * 2、聚合【针对每一个key进行聚合】
      * [
      * shenzhen -> List(13,17,12)
      * (agg,curr)=>agg+curr  agg:上一次的聚合结果  curr:本次要聚合的value
      * 第一次计算: agg=13  curr:17 => 30
      * 第二次计算: agg:30  curr:12  =>42
      * shanghai-> List(15,20,30)
      * (agg,curr)=>agg+curr  agg:上一次的聚合结果  curr:本次要聚合的value
      * 第一次计算: agg:15  curr:20 =>35
      * 第二次计算: agg:35  curr:30 =>65
      * beijing-> List(34,23,26,28)
      * (agg,curr)=>agg+curr  agg:上一次的聚合结果  curr:本次要聚合的value
      * 第一次计算: agg:34  curr:23 => 57
      * 第二次计算: agg:57  curr:26 =>83
      * 第三次计算: agg:83  curr:28 =>111
      * ]
      */
  }

  @Test
  def sparkContext(): Unit = {
    // 1. Spark Context 如何编写
    //     1. 创建 SparkConf
    val conf = new SparkConf().setMaster("local[6]").setAppName("spark_context")
    //     2. 创建 SparkContext
    val sc = new SparkContext(conf)

    // SparkContext身为大入口API, 应该能够创建 RDD, 并且设置参数, 设置Jar包...
    //    sc....

    // 2. 关闭 SparkContext, 释放集群资源
  }

  // 从本地集合创建
  @Test
  def rddCreationLocal(): Unit = {
    val seq = Seq(1, 2, 3)
    val rdd1: RDD[Int] = sc.parallelize(seq, 2)
    sc.parallelize(seq)
    val rdd2: RDD[Int] = sc.makeRDD(seq, 2)
  }

  // 从文件创建
  @Test
  def rddCreationFiles(): Unit = {
    sc.textFile("file:///...")

    // 1. textFile 传入的是什么
    //    * 传入的是一个 路径, 读取路径
    //    * hdfs://  file://   /.../...(这种方式分为在集群中执行还是在本地执行, 如果在集群中, 读的是hdfs, 本地读的是文件系统)
    // 2. 是否支持分区?
    //    * 假如传入的path是 hdfs:///....
    //    * 分区是由HDFS中文件的block决定的
    // 3. 支持什么平台
    //    * 支持aws和阿里云
  }

  // 从RDD衍生
  @Test
  def rddCreateFromRDD(): Unit = {
    val rdd1 = sc.parallelize(Seq(1, 2, 3))
    // 通过在rdd上执行算子操作, 会生成新的 rdd
    // 原地计算
    // str.substr 返回新的字符串, 非原地计算
    // 和字符串中的方式很像, 字符串是可变的吗?
    // RDD可变吗?不可变
    val rdd2: RDD[Int] = rdd1.map(item => item)
  }

  @Test
  def mapTest(): Unit = {
    // 1. 创建 RDD
    val rdd1 = sc.parallelize(Seq(1, 2, 3))
    // 2. 执行 map 操作
    val rdd2 = rdd1.map(item => item * 10)
    // 3. 得到结果
    val result: Array[Int] = rdd2.collect()
    result.foreach(item => println(item))
  }

  @Test
  def flatMapTest(): Unit = {
    // 1. 创建 RDD
    val rdd1: RDD[String] = sc.parallelize(Seq("Hello lily", "Hello lucy", "Hello tim"))
    // 2. 处理数据
    val rdd2 = rdd1.flatMap(item => item.split(" "))
    // 3. 得到结果
    val result = rdd2.collect()
    result.foreach(item => println(item))
    // 4. 关闭sc
    sc.stop()
  }

  @Test
  def reduceByKeyTest(): Unit = {
    // 1. 创建 RDD
    val rdd1 = sc.parallelize(Seq("Hello lily", "Hello lucy", "Hello tim"))
    // 2. 处理数据
    val rdd2 = rdd1.flatMap(item => item.split(" "))
      .map(item => (item, 1))
      .reduceByKey((curr, agg) => curr + agg)
    // 3. 得到结果
    val result = rdd2.collect()
    result.foreach(item => println(item))
    // 4. 关闭sc
    sc.stop()
  }


}
