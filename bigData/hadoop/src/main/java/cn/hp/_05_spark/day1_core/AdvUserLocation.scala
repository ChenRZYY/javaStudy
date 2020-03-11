package cn.hp._05_spark.day1_core

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

//手机 停留时间练习
object AdvUserLocation {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("AdvUserLocation").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val rdd0: RDD[((String, String), Long)] = sc.textFile("c://bs_log").map(line => {
      val fields = line.split(",")
      val eventType = fields(3)
      val time = fields(1)
      val timeLong = if (eventType == "1") -time.toLong else time.toLong
      ((fields(0), fields(2)), timeLong)
    })

    val rdd1: RDD[(String, (String, Long))] = rdd0.reduceByKey(_ + _).map(t => {
      val mobile = t._1._1
      val lac = t._1._2
      val time = t._2
      (lac, (mobile, time))
    })

    val rdd2: RDD[(String, (String, String))] = sc.textFile("c://lac_info.txt").map(line => {
      val f = line.split(",")
      //(基站ID， （经度，纬度）)
      (f(0), (f(1), f(2)))
    })
    val rdd3: RDD[(String, String, Long, String, String)] = rdd1.join(rdd2).map(t => {
      val lac = t._1
      val mobile = t._2._1._1
      val time = t._2._1._2
      val x = t._2._2._1
      val y = t._2._2._2
      (mobile, lac, time, x, y)
    })
    //rdd4分组后的
    val rdd4 = rdd3.groupBy(_._1)
    val rdd5 = rdd4.mapValues(it => {
      it.toList.sortBy(_._3).reverse.take(2)
    })
    println(rdd5.collect().toBuffer)
    rdd5.saveAsTextFile("c://out")
    sc.stop()
  }
}
