package cn.sdrfengmi.spark.project._01_spark_dmp.make

import org.apache.spark.sql.Row

/**
  * @Author 陈振东
  * @create 2020/3/23 18:00
  */
object AgeTag {

  def make(row: Row) = {
    //1、取出年龄的字段值
    val age = row.getAs[String]("age")
    //3、数据返回
    Map[String, Double](s"AGE_${age}" -> 1.0)
  }
}