package cn.hp._06_flink.batch_base.batch

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.api.scala._

object BatchFromFile {


  def main(args: Array[String]): Unit = {

    // 创建env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    var filePath: String = "D:\\flink_project\\flink-base-project\\flink-batch-base\\src\\main\\resources\\data.txt"

    // 加载文件
    val textDataSet: DataSet[String] = env.readTextFile(filePath,charsetName="UTF-8")

    // 打印
    textDataSet.sortPartition(k=>k,Order.ASCENDING).setParallelism(1).print()

  }
}
