package cn.sdrfengmi.spark.project._01_spark_dmp.make

import org.apache.spark.sql.Row

/**
  * @Author 陈振东
  * @create 2020/3/23 18:00
  */
object ChannelTag {

  def make(row: Row) = {
    //1、取出渠道的字段值
    val channel = row.getAs[String]("channelid")
    //3、数据返回
    Map[String, Double](s"CN_${channel}" -> 1.0)
  }
}
