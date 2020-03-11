package cn.hp._06_flink.batch_base.batch

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.configuration.Configuration

object BatchFromFolder {


  def main(args: Array[String]): Unit = {

    // env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    // 读取目录
    def params: Configuration = new Configuration()

    params.setBoolean("recursive.file.enumeration", true)
    val folderDataSet: DataSet[String] = env.readTextFile("D:\\flink_project\\flink-base-project\\flink-batch-base\\src\\main\\resources").withParameters(params)

    folderDataSet.print()

  }
}
