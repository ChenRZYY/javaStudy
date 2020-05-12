package cn.sdrfengmi.spark._01_core

import org.apache.spark.Partitioner

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
