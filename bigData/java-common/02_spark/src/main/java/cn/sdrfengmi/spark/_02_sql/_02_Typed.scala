package cn.sdrfengmi.spark._02_sql

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit.Test

class _02_Typed {
  val spark = SparkSession.builder().master("local[4]").appName("test").getOrCreate()

  import spark.implicits._

  @Test
  def as(): Unit = {
    val source: DataFrame = spark.read.option("sep", "\t").csv("dataset/studenttab10k")
    val ds = source.as[(String, String, String)]
    ds.show
  }

  @Test
  def filter(): Unit = {
    val data = Seq(Person("zhangsan", 20), Person("lisi", 50)).toDS()
    data.filter(p => p.age >= 30).show
    data.filter("age>=30").show
  }

  /**
    * distinct:去重条件是所有的列都相同才算重复
    * dropDuplicates：去重条件时指定列的值相同算重复
    */
  @Test
  def distinct(): Unit = {
    val data = Seq(Person("zhangsan", 20), Person("lisi", 50), Person("wangwu", 50), Person("zhangsan", 30)).toDS()
    data.distinct().show
    data.dropDuplicates("age").show
  }

  @Test
  def select(): Unit = {
    val data = Seq(Person("zhangsan", 20), Person("lisi", 50), Person("wangwu", 50), Person("zhangsan", 30)).toDS()
    data.select('name).show
    data.selectExpr("name", "age").show
    data.selectExpr("sum(age)").show()
    import org.apache.spark.sql.functions._
    data.select(expr("sum(age)")).show
    //    data.select("sum(age)").show/ 错误的方式
    //    data.select('sum ('age)).show
  }

  @Test
  def column(): Unit = {
    val data = Seq(Person("zhangsan", 20), Person("lisi", 50), Person("wangwu", 50), Person("zhangsan", 30)).toDS()
    import org.apache.spark.sql.functions._
    //增加一列
    data.withColumn("rand", expr("rand()")).show
    data.selectExpr("name", "age", "rand()").show
    //增加一列，列值用拥有的列的值
    data.withColumn("name_new", 'name).show()
    data.selectExpr("name", "age", "name name_new").show
    //重命名
    data.withColumnRenamed("name", "name_new").show
    data.selectExpr("age", "name name_new").show

  }

  @Test
  def agg(): Unit = {
    val data = Seq(Person("zhangsan", 20), Person("lisi", 50), Person("wangwu", 50), Person("zhangsan", 30)).toDS()
    //需求:根据年龄分组，获取年龄平均值
    data.groupBy('name).avg("age").show
    import org.apache.spark.sql.functions._
    //需求:根据年龄分组，获取年龄平均值,年龄的总值
    data.groupBy('name).agg(avg("age"), sum("age")).show
  }
}
