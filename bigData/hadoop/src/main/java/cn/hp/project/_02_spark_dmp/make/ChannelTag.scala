package cn.hp.project._02_spark_dmp.make

import org.apache.spark.sql.Row

object ChannelTag {

  def make(row: Row) = {
    //1、取出渠道的字段值
    val channel = row.getAs[String]("channelid")
    //3、数据返回
    Map[String, Double](s"CN_${channel}" -> 1.0)
  }
}
