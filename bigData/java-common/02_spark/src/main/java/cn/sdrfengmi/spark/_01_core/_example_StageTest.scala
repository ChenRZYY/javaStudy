package cn.sdrfengmi.spark._01_core

import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Try

object _example_StageTest {

  def main(args: Array[String]): Unit = {

    //1、创建SparkContext
    val sc = new SparkContext(new SparkConf().setMaster("local[5]").setAppName("_example_StageTest"))
    //2、读取数据
    sc.textFile("dataset/BeijingPM20100101_20151231_noheader.csv")
    //3、列裁剪、过滤、去重
      .map(line=>{
      val arr = line.split(",")
      val year = arr(1)
      val month = arr(2)

      val pm: Int = Try(arr(6).toInt).getOrElse(0)
      ((year,month),pm)
    }).filter(_._2!=0)
    //4、数据处理
    //4.1、统计PM总和
      .reduceByKey((agg,curr)=>agg+curr)
    //4.2、排序
      .sortBy(_._2,false)
      .take(10)
      .foreach(println(_))
    //5、结果展示
    Thread.sleep(1000000)
  }
}
