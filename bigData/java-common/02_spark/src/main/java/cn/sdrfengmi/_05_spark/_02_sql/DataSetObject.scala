package cn.sdrfengmi._05_spark._02_sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.junit.Test

//case class Person(name: String, age: Int)

class DataSetObject {

  val spark = SparkSession.builder().master("local[4]").appName("DataSetObject").getOrCreate()

  import spark.implicits._

  @Test
  def rddToDataSet(): Unit = {

    val rdd = spark.sparkContext.parallelize(Seq[Person](Person("lisi", 20), Person("wangwu", 30), Person("zhaoliu", 25)))
    val ds: Dataset[Person] = rdd.toDS()
    val df: DataFrame = rdd.toDF()
  }

  @Test
  def createDataSet(): Unit = {

    //1、通过反射方式创建DataSet
    val rdd = spark.sparkContext.parallelize(Seq[Person](Person("lisi", 20), Person("wangwu", 30), Person("zhaoliu", 25)))
    val ds: Dataset[Person] = rdd.toDS()
    ds.show()

    //2、读取外部文件的方式创建[只有读取文本文件才能创建Dataste]
    val ds2 = spark.read.textFile("data/globalParameter.txt")

    //3、根据本地集合创建DataSet
    val data = Seq[Person](Person("lisi", 20), Person("wangwu", 30), Person("zhaoliu", 25))
    val ds3 = data.toDS()
    //ds3.filter(_.address)
    ds3.show
    val ds4 = spark.createDataset(data)
  }

  @Test
  def dataSetToRdd(): Unit = {

    val ds2 = spark.read.textFile("data/globalParameter.txt")
    val rdd = ds2.rdd

    val rdd2: RDD[WrodCount] = rdd.flatMap(_.split(" ")).map(item => WrodCount(item, 1))
    rdd2.toDF.show


    //rdd2.toDF("word")
    //.createOrReplaceTempView("tmp")


    /*spark.sql(
      """
        |select  word,count(1)
        | from tmp
        | group by word
      """.stripMargin).show*/

  }
}

case class WrodCount(word: String, count: Int)