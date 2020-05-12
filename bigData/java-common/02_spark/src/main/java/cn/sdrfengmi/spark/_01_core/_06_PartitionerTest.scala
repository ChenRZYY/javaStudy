package cn.sdrfengmi.spark._01_core

import org.apache.spark.{SparkConf, SparkContext}

object _06_PartitionerTest {

  def main(args: Array[String]): Unit = {

    val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("test"))
    val rdd1 = sc.parallelize(Seq[(String, Int)](("hello", 1), ("Spark", 1), ("flume", 1), ("1111", 1), ("hadoop", 1), ("flink", 3), ("flink", 2), (null, 3)))

    rdd1.reduceByKey(new _06_MyPartitioner(5), _ + _)
      .mapPartitionsWithIndex((index, it) => {
        println(s"-------------------index:${index} data:${it.toBuffer}")
        it
      }).collect()
  }
}
