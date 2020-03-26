package cn.sdrfengmi.project._02_spark_dmp.make

import org.apache.spark.sql.Row

/**
  * @Author 陈振东
  * @create 2020/3/23 18:00
  */
object RegionTag {

  def make(row: Row) = {
    //1、取出省份、城市数据
    val region = row.getAs[String]("region")

    val city = row.getAs[String]("city")
    //2、生成标签
    var result = Map[String, Double]()
    result = result.+((s"RN_${region}", 1.0))
    result = result.+((s"CT_${city}", 1.0))
    //3、数据返回
    result
  }
}