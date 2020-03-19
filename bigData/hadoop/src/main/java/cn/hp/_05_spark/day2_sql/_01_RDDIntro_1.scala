package cn.hp._05_spark.day2_sql

import org.apache
import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.Test

class _01_RDDIntro_1 {

  @Test
  def rddIntro(): Unit = {
    val conf = new SparkConf().setMaster("local[4]").setAppName("_01_RDDIntro_1")
    val context: SparkContext = new SparkContext(conf)

    context.textFile("dataset/globalParameter.txt")
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .foreachPartition(it => println(it.toBuffer))
  }

  @Test
  def dsIntro(): Unit = {
    val sparkSession: SparkSession = new SparkSession.Builder()
      .appName("xiaomifeng")
      .master("local[4]")
      .getOrCreate()

    import sparkSession.implicits._

    val sparkRdd: RDD[Person] = sparkSession.sparkContext.parallelize(Seq(Person("陈振东", 10), Person("陈振佳", 9), Person("小明", 2)))
    val personDS: Dataset[Person] = sparkRdd.toDS()
    val resultDF: DataFrame = personDS.where('age > 9)
      .where('age < 20)
      .select('name) //可以多个colnum

    val personDS1: Dataset[String] = resultDF.as[String] //应用as 进行dataset转换  datafarm来回转换
    personDS1.show()
  }

  //对于表的操作都是dataFrame
  @Test
  def dfIntro(): Unit = {
    val sparkSession: SparkSession = new spark.sql.SparkSession.Builder().appName("xiaomifeng").master("local[4]").getOrCreate()

    val sparkRdd: RDD[Person] = sparkSession.sparkContext.parallelize(Seq(Person("陈振东", 10), Person("陈振佳", 9), Person("小明", 2)))
    import sparkSession.implicits._
    val df: DataFrame = sparkRdd.toDF
    df.createOrReplaceTempView("person")
    val frame: DataFrame = sparkSession.sql("select * from person p where p.age > 9")
    frame.show()
  }

  @Test
  def dataset1(): Unit = {
    val sparkSession = new apache.spark.sql.SparkSession.Builder().appName("xiaomifeng").master("local[4]").getOrCreate()
    import sparkSession.implicits._
    // 3. 演示
    val sourceRDD = sparkSession.sparkContext.parallelize(Seq(Person("zhangsan", 10), Person("lisi", 15)))
    val dataset: Dataset[Person] = sourceRDD.toDS()
    dataset.explain(true)
    dataset.filter(_.age > 10).show()
    dataset.filter('age > 10).show()
    dataset.filter("age>10").show()


  }


  //创建df三种方式
  @Test
  def dataframe2(): Unit = {
    val sparkSession: SparkSession = SparkSession.builder()
      .appName("dataframe1")
      .master("local[6]")
      .getOrCreate()

    import sparkSession.implicits._

    val personList = Seq(Person("zhangsan", 15), Person("lisi", 20))

    val frame1: DataFrame = personList.toDF()
    val frame2: DataFrame = sparkSession.sparkContext.parallelize(personList).toDF()

    val frame3: DataFrame = sparkSession.createDataFrame(personList)

    val frame4: DataFrame = sparkSession.read
      .option("header", true)
      //      .option("scam")
      .csv("dataset/BeijingPM_header.csv")

    frame4.select('year, 'month, 'PM_Dongsi)
      .where('PM_Dongsi =!= "NA")
      .groupBy('year, 'month)
      .count()
      .show()

  }

  @Test
  //dataframe 和 dataset关系
  def dataframe4(): Unit = {
    val spark = SparkSession.builder()
      .appName("dataframe1")
      .master("local[6]")
      .getOrCreate()

    import spark.implicits._

    val personList = Seq(Person("zhangsan", 15), Person("lisi", 20))

    val df: DataFrame = personList.toDF()
    import org.apache.spark.sql.catalyst.encoders.RowEncoder

    df.map((row: Row) => Person(row.getAs(0), row.getAs[Int](1))).show()

    df.map((row: Row) => Row(row.get(0), row.getAs[Int](1) * 2))(RowEncoder.apply(df.schema)) //必须指定一个类型
      .show()



  }

}
