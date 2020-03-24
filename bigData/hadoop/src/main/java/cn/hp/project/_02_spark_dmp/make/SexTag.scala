package cn.hp.project._02_spark_dmp.make

import org.apache.spark.sql.Row

object SexTag {

  def make(row: Row) = {
    //1、取出性别的字段值
    val sex = row.getAs[String]("sex")

    val sexName = if (sex == "1") "男" else "女"
    //3、数据返回
    Map[String, Double](s"SEX_${sexName}" -> 1.0)
  }
}