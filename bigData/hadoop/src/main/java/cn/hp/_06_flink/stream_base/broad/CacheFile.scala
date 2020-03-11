package cn.hp._06_flink.stream_base.broad

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration

object CacheFile {
  final val CACHEFILEPATH = "D:\\flink_project\\flink-base-project\\flink-stream-base\\src\\main\\resources\\data.txt";

  def main(args: Array[String]): Unit = {

    val params = ParameterTool.fromArgs(args)
    var cacheFilePath = CACHEFILEPATH
    if (params.has("cacheFile")) {
      cacheFilePath = params.get("cacheFile", CACHEFILEPATH)
    }
    // 1. env
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 2. load list
    val list: DataSet[String] = env.fromCollection(List("a", "b", "c", "d"))

    // 3. 注册文件
    // 参数1:文件路径,可以是HDFS的路径,参数2:文件的名称,自定义
    env.registerCachedFile(cacheFilePath, "data.txt")

    // 4. map open 获取文件
    val result: DataSet[String] = list.map(new RichMapFunction[String, String] {
      override def open(parameters: Configuration): Unit = {
        // 获取文件
        val file: File = getRuntimeContext.getDistributedCache.getFile("data.txt")
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
