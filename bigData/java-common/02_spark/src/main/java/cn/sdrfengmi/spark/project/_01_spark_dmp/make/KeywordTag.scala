package cn.sdrfengmi.spark.project._01_spark_dmp.make

import org.apache.spark.sql.Row
/**
  * @Author 陈振东
  * @create 2020/3/23 18:00
  */
object KeywordTag {

  def make(row: Row) = {
    //1、取出关键字数据
    val keywords = row.getAs[String]("keywords")
    //2、因为多个关键字是以,分割，需要进行切割，一个关键字为一个标签
    var result = Map[String, Double]()
    val keywordArr = keywords.split(",")
    keywordArr.foreach(item => {
      result = result.+((s"KW_${item}", 1.0))
    })
    //3、数据返回
    result
  }
}