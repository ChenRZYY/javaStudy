package cn.sdrfengmi.spark._01_core

import java.text.SimpleDateFormat

import org.apache.commons.lang3.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.junit.Test
import java.util.Calendar

import org.apache.log4j.{Level, Logger}

/**
  * cache 和persist对比 .
  *
  */
class _05_CacheObject {
  Logger.getLogger("org").setLevel(Level.ERROR)

  val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("AccessLog"))
  val calendar: Calendar = Calendar.getInstance
  val formatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss")

  @Test
  def prepare(): Unit = {
    sc.setCheckpointDir(s"../01_dataSetOut/checkpoint_${formatter.format(calendar.getTime)}")

    val source: RDD[String] = sc.textFile("../01_dataSet/access_log_sample.txt")
    val countRDD: RDD[(String, Int)] = source.map(f => (f.split(" ")(0), 1))

    //清洗数据
    val cleanRDD: RDD[(String, Int)] = countRDD.filter(f => StringUtils.isNotEmpty(f._1))
    var aggRDD: RDD[(String, Int)] = cleanRDD.reduceByKey((curr, agg) => curr + agg)

    aggRDD = aggRDD.cache() // checkpoint 也是相当于一个action操作 ,这里做了缓存 下面3个RDD能公用
    //took 0.069957 s   took 0.175974 s
    aggRDD.checkpoint()
    //TODO 每个action都会完整运行这个RDD的血统  运行2遍RDD
    val lessIP: (String, Int) = aggRDD.sortBy(f => f._2, true).first()

    val moreIP: (String, Int) = aggRDD.sortBy(f => f._2, false).first()

    println(lessIP, moreIP)

  }


  //job1 job2 job3 为了看sparkUI
  @Test
  def cache(): Unit = {
    //2、读取数据
    val source: RDD[String] = sc.textFile("../01_dataset/access_log_sample.txt")
    //3、过滤、去重、列裁剪
    val filterRdd = source.filter(line => {
      val ip = line.split(" ")(0)
      StringUtils.isNotBlank(ip)
    })

    val mapRdd: RDD[(String, Int)] = filterRdd.map(line => (line.split(" ")(0), 1))
    //name,age,address,sex,...
    //select  name,age from student
    //4、数据处理
    //4.1、给初始次数
    //4.2、聚合统计
    val reduceRdd: RDD[(String, Int)] = mapRdd.reduceByKey(_ + _)
    //TODO 缓存使用
    //val cacheRdd = reduceRdd.cache()
    reduceRdd.persist(StorageLevel.MEMORY_AND_DISK)
    //4.3、排序

    val result1: Array[Int] = reduceRdd.map(_._2 * 10).take(10) // action算子 job1
    val result2: Array[(String, Int)] = reduceRdd.take(10) //action 算子 job2
    //不用reduceByKey 做,其他方式实现
    val groupRdd: RDD[(String, Iterable[(String, Int)])] = mapRdd.groupBy(tum => tum._1)
    val groupMapRdd: RDD[(String, Int)] = groupRdd.map((f: (String, Iterable[(String, Int)])) => {
      var count: Int = 0
      f._2.foreach(tm => count += tm._2)
      (f._1, count)
    })
    val result3: Array[(String, Int)] = groupMapRdd.take(10)  //action 算子 job3

//    println(result1, result2)
    result1.foreach(println(_))
    result2.foreach(println(_))
    println()
    result3.foreach(println(_))
    //    result2.foreach(f=>error(f._1+f._2))

    Thread.sleep(1000000)
    //5、取出数据，结果展示
  }

  @Test
  def checkpoint(): Unit = {

    //1、创建SparkContext
    sc.setCheckpointDir(s"../01_dataSetOut/checkpoint_${formatter.format(calendar.getTime)}")
    //2、读取数据
    val source: RDD[String] = sc.textFile("../01_dataSet/BeijingPM.csv")
    //3、过滤、去重、列裁剪
    val filterRdd = source.filter(line => {
      val ip = line.split(" ")(0)
      StringUtils.isNotBlank(ip)
    })

    val mapRdd: RDD[(String, Int)] = filterRdd.map(line => (line.split(" ")(0), 1))
    //name,age,address,sex,...
    //select  name,age from student
    //4、数据处理
    //4.1、给初始次数
    //4.2、聚合统计
    var reduceRdd: RDD[(String, Int)] = mapRdd.reduceByKey(_ + _)

    reduceRdd = reduceRdd.cache()
    reduceRdd.checkpoint()
    //val cacheRdd = reduceRdd.cache()

    //reduceRdd.persist(StorageLevel.MEMORY_AND_DISK)
    //4.3、排序

    val result1 = reduceRdd.map(_._2 * 10).take(1)
    val result2 = reduceRdd.take(1)


    println(result1, result2)
    System.err.println(reduceRdd.toDebugString)
    //    Thread.sleep(100)
  }


  @Test
  def checkpoint2(): Unit = {

    //1、创建SparkContext
    sc.setCheckpointDir(s"../01_dataSetOut/checkpoint_${formatter.format(calendar.getTime)}")

    val rdd1 = sc.parallelize(Seq[String]("hello java hello sparkSession"))

    val rdd2 = rdd1.flatMap(line => {
      println("-------------------------------------------")
      line.split(" ")
    })
    val rdd3 = rdd2.map((_, 1))

    val rdd4 = rdd3.reduceByKey(_ + _)

    //rdd4.checkpoint()

    rdd4.map(_._2 * 10).first()
    rdd4.filter(x => true).first()

  }
}
