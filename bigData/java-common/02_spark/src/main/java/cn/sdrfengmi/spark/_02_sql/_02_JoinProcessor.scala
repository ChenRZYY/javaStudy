package cn.sdrfengmi.spark._02_sql

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit.Test

class _02_JoinProcessor {
  val spark = SparkSession.builder()
    .master("local[6]")
    .appName("join")
    .getOrCreate()

  import spark.implicits._

  private val person: DataFrame = Seq((0, "Lucy", 0), (1, "Lily", 0), (2, "Tim", 2), (3, "Danial", 3)).toDF("id", "name", "cityId")
  person.createOrReplaceTempView("person")

  private val cities: DataFrame = Seq((0, "Beijing"), (1, "Shanghai"), (2, "Guangzhou")).toDF("id", "name")
  cities.createOrReplaceTempView("cities")

  @Test
  //join操作
  def introJoin(): Unit = {
    val person = Seq((0, "Lucy", 0), (1, "Lily", 0), (2, "Tim", 2), (3, "Danial", 0)).toDF("id", "name", "cityId")

    val cities = Seq((0, "Beijing"), (1, "Shanghai"), (2, "Guangzhou")).toDF("id", "name")

    val df = person.join(cities, person.col("cityId") === cities.col("id"))
      .select(person.col("id"),
        person.col("name"),
        cities.col("name") as "city")
    //      .show()
    df.createOrReplaceTempView("user_city")

    spark.sql("select id, name, city from user_city where city = 'Beijing'").show()
  }

  @Test
  //crossJoin 是笛卡尔集 全连接
  def crossJoin(): Unit = {
    person.crossJoin(cities).where(person.col("cityId") === cities.col("id")).show()

    import org.apache.spark.sql.functions._
    val frame: DataFrame = person.crossJoin(cities) //下面这个为什么不对
    person.crossJoin(cities).where(exp("cityId") === exp("id")).show()
    //fixme 指定那个表里面的字段  https://www.cnblogs.com/lyy-blog/p/9567101.html
    person.crossJoin(cities).where(person("cityId") === cities("id")).show()
    spark.sql("select u.id, u.name, c.name from person u cross join cities c where u.cityId = c.id").show()
  }

  @Test
  def inner(): Unit = {
    person.join(cities, person.col("cityId") === cities.col("id"), joinType = "inner").show()
    person.join(cities, person.col("cityId") === cities.col("id"), joinType = "inner").show()
    spark.sql("select p.id, p.name, c.name  from person p inner join cities c on p.cityId = c.id").show()
  }

  @Test
  def fullOuter(): Unit = {
    // 内连接, 就是只显示能连接上的数据, 外连接包含一部分没有连接上的数据, 全外连接, 指左右两边没有连接上的数据, 都显示出来
    person.join(cities, person.col("cityId") === cities.col("id"), joinType = "full").show()
    person.join(cities, person.col("cityId") === cities.col("id"), "full").show()
    spark.sql("select p.id, p.name, c.name from person p full outer join cities c on p.cityId = c.id").show()
  }

  @Test
  def leftRight(): Unit = {
    // 左连接
    person.join(cities,
      person.col("cityId") === cities.col("id"),
      joinType = "left")
      .show()

    spark.sql("select p.id, p.name, c.name from person p left join cities c on p.cityId = c.id").show()

    // 右连接
    person.join(cities,
      person.col("cityId") === cities.col("id"),
      joinType = "right")
      .show()

    spark.sql("select p.id, p.name, c.name from person p right join cities c on p.cityId = c.id").show()
  }

  @Test
  def leftAntiSemi(): Unit = {
    // 左连接 anti
    person.join(cities,
      person.col("cityId") === cities.col("id"),
      joinType = "leftanti")
      .show()

    spark.sql("select p.id, p.name from person p left anti join cities c on p.cityId = c.id").show()

    // 右连接 leftsemi
    person.join(cities,
      person.col("cityId") === cities.col("id"),
      joinType = "leftsemi")
      .show()

    spark.sql("select p.id, p.name from person p left semi join cities c  on p.cityId = c.id").show()
  }
}
