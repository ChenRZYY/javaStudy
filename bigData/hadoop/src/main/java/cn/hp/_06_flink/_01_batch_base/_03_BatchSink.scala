package cn.hp._06_flink._01_batch_base

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.junit.Test
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem
import org.apache.log4j.{Level, Logger}

class _03_BatchSink {
  val env = ExecutionEnvironment.getExecutionEnvironment
  Logger.getLogger("org").setLevel(Level.ERROR)

  @Test
  def collect() = {
    // 1. env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    // 2. 加载集合
    val listDataSet: DataSet[(Int, String, Double)] = env.fromCollection(List(
      (19, "zhangsan", 178.8),
      (17, "lisi", 168.8),
      (18, "wangwu", 184.8),
      (21, "zhaoliu", 164.8)))

    // 3. 打印输出 错误输出 collect
    listDataSet.print()
    listDataSet.printToErr()
    val tuples: Seq[(Int, String, Double)] = listDataSet.collect()
    println(tuples)
  }

  @Test
  def writeFile(): Unit = {
    // 2. load map
    val mapDataSet: DataSet[(Int, String)] = env.fromCollection(Map(1 -> "spark", 2 -> "flink"))
    // 3. write as text setParallelism(4) 如果并行度大于1 会输出多个文件,test就是一个目录,如果并行度为1,那么test就是文件

    //writeAsText() / TextOutputFormat，将元素以字符串形式写入文件。字符串通过调用每个元素的 toString() 方法获得。FIXME 对象类型要写toString方法
    mapDataSet.setParallelism(4).writeAsText("dataSetOut/writeFile", FileSystem.WriteMode.OVERWRITE)
    mapDataSet.writeAsCsv("file:///dataSetOut/writeCSV", "\n", "|", FileSystem.WriteMode.OVERWRITE)
    // 4. 执行程序  只能有一个sink
    mapDataSet.print()
    //    env.execute("sinkFile")
  }

  //尚不支持全局排序的输出。TODO ??? 下面的为什么不起作用
  @Test
  def sortPartition_writeFile(): Unit = {
    val tData: DataSet[(Int, String, Double)] = env.fromElements((1, "zhangsan", 28D), (2, "zhaosi", 29D), (3, "wangwu", 30D))
    val pData: DataSet[(BookPojo, Double)] = env.fromElements((BookPojo("西游记", 31D), 1D), (BookPojo("红楼梦", 32D), 2D), (BookPojo("三国演义", 33D), 3D), (BookPojo("水浒传", 34D), 4D))
    val sData: DataSet[String] = env.fromElements("a", "b", "d", "c")

    // sort output on String field in ascending order
    tData.sortPartition(1, Order.ASCENDING).print()

    // sort output on Double field in descending and Int field in ascending order
    tData.sortPartition(2, Order.DESCENDING).sortPartition(0, Order.ASCENDING).writeAsText("file:///dataSetOut/writeFile1", FileSystem.WriteMode.OVERWRITE)

    // sort output on the "author" field of nested BookPojo in descending order
    pData.sortPartition("_1.author", Order.DESCENDING).writeAsText("file:///dataSetOut/writeFile1", FileSystem.WriteMode.OVERWRITE)

    // sort output on the full tuple in ascending order  Order.DESCENDING
    tData.sortPartition("_", Order.ASCENDING).writeAsText("file:///dataSetOut/writeFile2", FileSystem.WriteMode.OVERWRITE)

    // sort atomic type (String) output in descending order
    sData.sortPartition("_", Order.DESCENDING).writeAsText("file:///dataSetOut/writeFile3", FileSystem.WriteMode.OVERWRITE)
    env.execute()
  }

}


case class BookPojo(author: String, price: Double)