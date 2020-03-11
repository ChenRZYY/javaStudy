package cn.hp._05_spark.day2_sql

import org.apache.spark.sql.SparkSession

object _03_Udf {
  /**
    * udf函数作用在每一行数据上
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[4]").appName("test").getOrCreate()
    import spark.implicits._
    val data = Seq(("00012", "zhangsan"), ("020", "lisi"), ("340", "wangwu")).toDF("id", "name")

    //需求: id正常数据是八位数，将没有八位的id补全，不满八位在id前面补0
    data.createOrReplaceTempView("person")
    //注册udf函数
    spark.udf.register("addPrfix", addPrfix _) //TODO 直接传入方法 要是不行 加 _

    spark.sql(
      """
        |select addPrfix(p.id) id,p.name
        | from person p
      """.stripMargin).show
  }

  /**
    * 补全id
    *
    * @param id
    * @return
    */
  def addPrfix(id: String): String = {
    "0" * (8 - id.length) + id
  }

}
