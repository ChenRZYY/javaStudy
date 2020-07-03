package cn.sdrfengmi.spark._01_core

import cn.sdrfengmi.spark.project._01_spark_dmp.utils.ConfigUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.Test

/**
  * 广播变量
  */
class _08_BroadCast {

  @Test
  def bc(): Unit = {
    val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("Accumulator"))

    val students: RDD[(Int, String, Int, String)] = sc.parallelize(Seq[(Int, String, Int, String)](
      (1, "aa", 20, "class_01"),
      (2, "bb", 20, "class_02"),
      (3, "cc", 20, "class_02"),
      (4, "dd", 20, "class_03"),
      (5, "ee", 20, "class_03"),
      (6, "ff", 20, "class_01")
    ))

    val clazz: RDD[(String, String)] = sc.parallelize(Seq[(String, String)](
      ("class_01", "java"),
      ("class_02", "python"),
      ("class_03", "大数据")
    ))

    //获取学生的详细信息以及班级名称
    val kvStudent = students.map(item => (item._4, item))
    val infoRdd: RDD[(String, ((Int, String, Int, String), String))] = kvStudent.join(clazz)

    val resultRdd = infoRdd.map {
      case (classId, ((id, name, age, studentClassID), cassName)) =>
        (id, name, age, cassName)
    }

    resultRdd.foreach(println(_))
    Thread.sleep(100000000)
  }


  @Test
  def bc2(): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[5]").setAppName("bc2").set("spark.default.parallelism", "500")
    val sc = new SparkContext(sparkConf)

    val students: RDD[(Int, String, Int, String)] = sc.parallelize(Seq[(Int, String, Int, String)](
      (1, "aa", 20, "class_01"),
      (2, "bb", 20, "class_02"),
      (3, "cc", 20, "class_02"),
      (4, "dd", 20, "class_03"),
      (5, "ee", 20, "class_03"),
      (6, "ff", 20, "class_01")
    ))

    val clazz: RDD[(String, String)] = sc.parallelize(Seq[(String, String)](
      ("class_01", "java"),
      ("class_02", "python"),
      ("class_03", "大数据")
    ))

    //获取学生的详细信息以及班级名称
    val tuples: Array[(String, String)] = clazz.collect()
    val clazzData: Map[String, String] = tuples.toMap

    // 什么时候使用collect算子  广播变量使用
    val bc: Broadcast[Map[String, String]] = sc.broadcast(clazzData)

    students.map(item => (item._1, item._2, item._3, bc.value.getOrElse(item._4, "other"))).foreach(println(_))

    Thread.sleep(100000000)
  }
}
