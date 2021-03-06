package cn.sdrfengmi.spark._02_sql

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, DataFrameReader, SaveMode, SparkSession}
import org.junit.Test

class _01_ReadWrite {
  //  System.setProperty("hadoop.home.dir", "C:\\winutils")
  Logger.getLogger("org").setLevel(Level.ERROR)
  val sparkSession: SparkSession = SparkSession.builder().master("local[6]").appName("reader1").getOrCreate()

  @Test
  def reader1(): Unit = {
    // 1. 创建 SparkSession
    val spark = SparkSession.builder()
      .master("local[6]")
      .appName("reader1")
      .getOrCreate()

    // 2. 框架在哪
    val reader: DataFrameReader = spark.read
  }

  /**
    * 初体验 Reader
    */
  @Test
  def reader2(): Unit = {
    // 1. 创建 SparkSession
    val spark = SparkSession.builder()
      .master("local[6]")
      .appName("reader1")
      .getOrCreate()

    // 2. 第一种形式
    spark.read
      .format("csv")
      .option("header", value = true)
      .option("inferSchema", value = true) //自动识别schema 数据类型
      .load("dataset/BeijingPM_header.csv")
      .show(10)

    // 3. 第二种形式
    spark.read
      .option("header", value = true)
      .option("inferSchema", value = true)
      .csv("dataset/BeijingPM_header.csv")
      .show()
  }

  @Test
  def writer1(): Unit = {
    // 2. 读取数据集
    val df: DataFrame = sparkSession.read.option("header", true).csv("dataset/BeijingPM_header.csv")

    // 3. 写入数据集
    df.write.json("datasetOut/beijing_pm.json")
    df.write.format("json").save("datasetOut/beijing_pm2.json")
  }

  @Test
  def parquet(): Unit = {
    // 1. 读取 CSV 文件的数据
    val df = sparkSession.read.option("header", true).csv("dataset/BeijingPM_header.csv")

    // 2. 把数据写为 Parquet 格式
    // 写入的时候, 默认格式就是 parquet
    // 写入模式, 报错, 覆盖, 追加, 忽略
    df.write
      .mode(SaveMode.Overwrite) //SaveMode.Append
      .save("datasetOut/beijing_pm4")

    // 3. 读取 Parquet 格式文件
    // 默认格式是否是 paruet? 是
    // 是否可能读取文件夹呢? 是
    sparkSession.read
      .load("datasetOut/beijing_pm.json")
      .show()
  }

  /**
    * 表分区的概念不仅在 parquet 上有, 其它格式的文件也可以指定表分区
    */
  @Test
  def parquetPartitions(): Unit = {
    // 1. 读取数据
    val df = sparkSession.read
      .option("header", value = true)
      .csv("dataset/BeijingPM_header.csv")

    // 2. 写文件, 表分区
    df.write
      .mode(SaveMode.Overwrite) //SaveMode.Append
      .partitionBy("year", "month")
      .save("datasetOut/beijing_pm4")

    // 3. 读文件, 自动发现分区
    // 写分区表的时候, 分区列不会包含在生成的文件中
    // 直接通过文件来进行读取的话, 分区信息会丢失
    // sparkSession sql 会进行自动的分区发现
    sparkSession.read
      .parquet("datasetOut/beijing_pm4")
      .printSchema()
  }

  @Test
  def json(): Unit = {
    val df = sparkSession.read
      .option("header", value = true)
      .csv("dataset/BeijingPM20100101_20151231.csv")

    //    df.write
    //      .json("dataset/beijing_pm5.json")

    sparkSession.read
      .json("dataset/beijing_pm5.json")
      .show()
  }

  /**
    * toJSON 的场景:
    * 处理完了以后, DataFrame中如果是一个对象, 如果其他的系统只支持 JSON 格式的数据
    * SParkSQL 如果和这种系统进行整合的时候, 就需要进行转换
    */
  @Test
  def json1(): Unit = {
    val df = sparkSession.read
      .option("header", value = true)
      .csv("dataset/BeijingPM20100101_20151231.csv")

    df.toJSON.show()
  }

  /**
    * 从消息队列中取出JSON格式的数据, 需要使用 SparkSQL 进行处理
    */
  @Test
  def json2(): Unit = {
    val df = sparkSession.read
      .option("header", value = true)
      .csv("dataset/BeijingPM20100101_20151231.csv")

    val jsonRDD: RDD[String] = df.toJSON.rdd

    sparkSession.read.json(jsonRDD).show()
  }

  @Test
  def read_json(): Unit = {
    val df = sparkSession.read
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      //fixme json("") 可以直接是文件,也可以是json文件夹
      .json("dataset/pmt.json")

    df.show()
  }
}
