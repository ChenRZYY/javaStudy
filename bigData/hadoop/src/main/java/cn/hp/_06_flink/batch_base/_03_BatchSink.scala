package cn.hp._06_flink.batch_base

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.junit.Test

import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem

class _03_BatchSink {


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
    // 1. env
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 2. load map
    val mapDataSet: DataSet[(Int, String)] = env.fromCollection(Map(1 -> "spark", 2 -> "flink"))
    // 3. write as text setParallelism(4) 如果并行度大于1 会输出多个文件,test就是一个目录,如果并行度为1,那么test就是文件
    mapDataSet.setParallelism(4).writeAsText("datasetOut/writeFile", FileSystem.WriteMode.OVERWRITE)
    // 4. 执行程序  只能有一个sink
    mapDataSet.print()
//    env.execute("sinkFile")
  }
}
