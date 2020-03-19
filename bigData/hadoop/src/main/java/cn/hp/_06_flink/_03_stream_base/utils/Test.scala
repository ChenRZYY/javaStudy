package cn.hp._06_flink._03_stream_base.utils

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

object Test {
  def main(args: Array[String]): Unit = {
  val params = 1 to 5
    println(params)
  }
  private val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
  private val dataset: DataSet[Int] = env.fromElements(1 to 5 :_*)
  dataset.print()
}
