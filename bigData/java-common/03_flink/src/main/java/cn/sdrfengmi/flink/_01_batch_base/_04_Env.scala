package cn.sdrfengmi.flink._01_batch_base

import java.util.Date

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala._
import org.apache.log4j.{Level, Logger}
import org.junit.Test

class _04_Env {

  Logger.getLogger("org").setLevel(Level.ERROR)
  
  @Test
  def remoteEnv(): Unit = {
    // 1. 创建远程执行环境
    val env = ExecutionEnvironment.createRemoteEnvironment("server02", 8081, "target/flink-batch-base-1.0-SNAPSHOT.jar")
    // 2. 读取hdfs中csv文件,转换为元组
    val csvFile: DataSet[(Long, String, Long, Double)] = env.readCsvFile[(Long, String, Long, Double)]("hdfs://node01:9000/user/root/score.csv")
    // 3. 根据元组的姓名分组,以成绩降序,取第一个值
    val result: DataSet[(Long, String, Long, Double)] = csvFile.groupBy(1).sortGroup(3, Order.DESCENDING).first(1)
    // 4. 打印
    result.print()
  }

  @Test
  def localEnv = {
    // 开始时间
    val startTime = new Date().getTime
    // env
    val localEnv: ExecutionEnvironment = ExecutionEnvironment.createLocalEnvironment(2)
    val collectEnv: ExecutionEnvironment = ExecutionEnvironment.createCollectionsEnvironment

    // load list
    val listDataSet: DataSet[Int] = localEnv.fromCollection(List(1, 2, 3, 4))
    // print
    listDataSet.print()
    // 开始时间
    val endTime = new Date().getTime
    println(endTime - startTime)
  }

}
