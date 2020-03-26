package cn.sdrfengmi.flink._01_batch_base

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, _}
import org.apache.flink.configuration.Configuration
import org.apache.log4j.{Level, Logger}
import org.junit.Test

case class subject(id: String, subjectName: String, sored: String, score: String)

//case class subject(id: Int, subjectName: String, sored: Int, score: Int)

class BatchSource {

  Logger.getLogger("org").setLevel(Level.ERROR)

  @Test
  def fromElement: Unit = {
    // 新建SparkConf,SparkContext,StreamingContext -- spark
    // 1. 创建批处理运行环境
    //StreamExecutionEnvironment.getExecutionEnvironment
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    // 2. 加载本地数据
    val localEle: DataSet[String] = env.fromElements("hadoop", "spark", "flink")
    localEle.print()

    // 加载元组
    val tupleDataSet: DataSet[(String, Int)] = env.fromElements(("spark", 1), ("flink", 2))
    tupleDataSet.print()

    // 加载本地集合
    val listDataSet: DataSet[String] = env.fromCollection(List("flink", "spark", "hadoop"))
    listDataSet.print()

    // 加载序列
    val seqDataSet: DataSet[Long] = env.generateSequence(1, 9)
    seqDataSet.print()
  }


  @Test
  def readFile = {
    // 创建env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    val filePath: String = "../dataset/score.csv" // hdfs文件是一样的,换个地址就行
    // 加载文件
    val textDataSet: DataSet[String] = env.readTextFile(filePath, charsetName = "UTF-8")
    // 打印
    textDataSet.sortPartition(k => k, Order.ASCENDING).setParallelism(1).print()
  }

  @Test
  def readCsvFile = {
    //设置环境变量
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    //创建样例类
    val filePath: String = "../dataset/score.csv"

    //加载csv文件 TODO 为什么对象不能????  设置类型可以转换成 样例类 元组
    val csvDataSet1: DataSet[subject] = env.readCsvFile[subject](filePath)
    val sortDataset1 = csvDataSet1.sortPartition(0, Order.ASCENDING).setParallelism(1)
    sortDataset1.print()

    //    val csvDataSet2: DataSet[(String, String, String, String)] = env.readCsvFile[(String, String, String, String)](filePath)
    //    val sortDataset2 = csvDataSet2.sortPartition(0, OrderSql.ASCENDING).setParallelism(1)
    //    sortDataset2.print()
  }

  @Test
  def readFolder = {
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 读取目录
    def params: Configuration = new Configuration()
    params.setBoolean("recursive.file.enumeration", true) //设置递归读取文件

    val folderDataSet: DataSet[String] = env.readTextFile("../datasetOut/").withParameters(params)
    folderDataSet.printToErr()
  }

}
