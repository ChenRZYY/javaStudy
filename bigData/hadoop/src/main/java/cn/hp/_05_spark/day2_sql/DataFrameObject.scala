package cn.hp._05_spark.day2_sql

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit.Test

class DataFrameObject {

  val spark = SparkSession.builder().master("local[4]").appName("test").getOrCreate()

  import spark.implicits._
  @Test
  def createDataFrame(): Unit ={

    //1、反射机制  toDF
    val data = Seq[Person](Person("zhangsan",13),Person("lisi",20),Person("wangwu",35))
    val rdd = spark.sparkContext.parallelize(data)

    val df: DataFrame = rdd.toDF()
    val df2 = data.toDF()

    //2、读取文件创建
/*    spark.read.csv("data/BeijingPM20100101_20151231_noheader.csv")
      .select('_c0,'_c1,'_c2,'_c3)
      .toDF("id","year","month","day")
      .show*/
    //select _c0 as id ...
    spark.read.csv("data/BeijingPM20100101_20151231_noheader.csv")
      .select('_c0 as("id"),'_c1 as("year"),'_c2 as "month",'_c3 as "day")
      .show

    //3、通过createDataFrame
    val df5 = spark.createDataset(data)
  }
}
