package cn.sdrfengmi._05_spark._01_core

import cn.hp.util.PathUtil
import org.apache.commons.lang3.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.junit.Test

class _05_CacheObject {

  val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("AccessLog"))

  @Test
  def prepare(): Unit = {

    sc.setCheckpointDir("checkpoint")
    val source: RDD[String] = sc.textFile(PathUtil.getPath(classOf[_05_CacheObject], "access_log_sample.txt"))
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


  @Test
  def cache(): Unit = {
    //1、创建SparkContext

    //2、读取数据
    val source: RDD[String] = sc.textFile(PathUtil.getPath(classOf[_05_CacheObject], "access_log_sample.txt"))
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

    //reduceRdd.persist(StorageLevel.MEMORY_AND_DISK)
    //4.3、排序

    val result1: Array[Int] = reduceRdd.map(_._2 * 10).take(1)
    val result2: Array[(String, Int)] = reduceRdd.take(1)

    println(result1, result2)
    result1.foreach(println(_))
    result2.foreach(println(_))
    //    result2.foreach(f=>error(f._1+f._2))

    Thread.sleep(100000)
    //5、取出数据，结果展示
  }

  @Test
  def checkpoint(): Unit = {

    //1、创建SparkContext
    sc.setCheckpointDir("checkpoint")
    //2、读取数据
    val source: RDD[String] = sc.textFile("dataSet/BeijingPM.csv")
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
    sc.setCheckpointDir("checkpoint")

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
