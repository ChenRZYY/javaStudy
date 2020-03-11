package cn.hp._05_spark.day2_sql

import org.apache.spark.sql.{DataFrame, SparkSession}

object pmObject {

  def main(args: Array[String]): Unit = {

    //1、创建SparkSession对象
    val spark = SparkSession.builder().master("local[4]").appName("pm").getOrCreate()
    //2、读取数据
    /**
      * sep:设置字段之间的分隔符,默认为 ,
      * header:设置文件中是否包含头信息，true包含 false不包含
      * inferSchema: 是否自动推断列的类型
      */
    import spark.implicits._
    val source: DataFrame = spark.read
      .option("header",true)
      .option("inferSchema",true)
      .csv("data/BeijingPM20100101_20151231.csv")
    //3、数据处理三要素：列裁剪、过滤、去重
    source
      .filter("PM_Dongsi!='NA'")
      .selectExpr("year","month","cast(PM_Dongsi as DOUBLE) as PM_Dongsi")
      //.select('year,'month,'PM_Dongsi)
      .createOrReplaceTempView("pm")
    //需求：统计每个月份的pm值的总和
    //4、数据统计分析
    spark.sql(
      """
        |select year,month,sum(PM_Dongsi)
        | from pm
        | group by year,month
      """.stripMargin).show
    //5、结果展示
  }
}
