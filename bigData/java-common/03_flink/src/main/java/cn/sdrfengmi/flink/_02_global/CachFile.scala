package cn.sdrfengmi.flink._02_global

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration

/**
  * 缓存
  */
object CachFile {

  def main(args: Array[String]): Unit = {
    // 1. env
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 2. load list
    val list: DataSet[String] = env.fromCollection(List("a", "b", "c", "d"))
    // 3. 注册文件
    // 参数1:文件路径,可以是HDFS的路径,参数2:文件的名称,自定义
    env.registerCachedFile("01_dataset/globalParameter.txt", "globalParameter.txt")
//    env.registerCachedFile("hdfs:///path/to/your/file", "hdfsFile")
//    env.registerCachedFile("file:///path/to/exec/file", "localExecFile", true)
    // 4. map open 获取文件
    val result: DataSet[String] = list.map(new RichMapFunction[String, String] {
      override def open(parameters: Configuration): Unit = {
        // 获取文件
        val file: File = getRuntimeContext.getDistributedCache.getFile("globalParameter.txt")
        // 打印文件内容
        val str: String = FileUtils.readFileToString(file)
        println(str)
      }

      override def map(value: String): String = {
        value
      }
    })

    // 5.print
    result.print
  }
}
