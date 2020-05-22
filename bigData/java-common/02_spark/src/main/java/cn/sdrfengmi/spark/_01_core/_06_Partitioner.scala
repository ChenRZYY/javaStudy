package cn.sdrfengmi.spark._01_core

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

object _06_PartitionerTest {
  Logger.getLogger("org").setLevel(Level.ERROR)

  def main(args: Array[String]): Unit = {

    val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("test"))
    val rdd1 = sc.parallelize(Seq[(String, Int)](("hello", 1), ("Spark", 1), ("flume", 1), ("1111", 1), ("hadoop", 1), ("flink", 3), ("flink", 2), (null, 3), (null, 4)))

    rdd1.reduceByKey(new _06_MyPartitioner(5), _ + _)
      .mapPartitionsWithIndex((index, it) => {
        println(s"-------------------index:${index} data:${it.toBuffer}")
        it
      }).collect()
  }
}

/**
  * 自定义分区函数
  *
  * @param numpartitions
  */
class _06_MyPartitioner(numpartitions: Int) extends Partitioner {
  override def numPartitions: Int = numpartitions

  /**
    * 假设key都是字符串，分区规则:
    * null ->0
    * 字符串以a-z开头的字母 ->1
    * A-Z开头的字符串 ->2
    * _ => 3
    *
    * @param key
    * @return
    */
  override def getPartition(key: Any): Int = {
    key match {
      case null => 0
      case x: String if "a" <= x.take(1) && "z" >= x.take(1) => 1
      case x: String if "A" <= x.take(1) && "Z" >= x.take(1) => 2
      case _ => 3
    }
  }
}