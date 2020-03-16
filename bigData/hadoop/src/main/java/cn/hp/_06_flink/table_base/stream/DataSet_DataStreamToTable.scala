package cn.hp._06_flink.table_base.stream

import org.apache.flink.core.fs.FileSystem
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.scala.StreamTableEnvironment
import org.apache.flink.table.api.{Table, TableEnvironment}
import org.apache.flink.table.sinks.CsvTableSink
import org.apache.flink.api.scala._

object DataSet_DataStreamToTable {

  def main(args: Array[String]): Unit = {

    // 1. 获取流式处理环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 2. 获取Table处理环境
    val tableEnv: StreamTableEnvironment = TableEnvironment.getTableEnvironment(env)

    // 3. 加载本地集合
    val dataStream: DataStream[OrderTable] = env.fromCollection(List(
      OrderTable(1, "beer", 3),
      OrderTable(2, "diaper", 4),
      OrderTable(3, "rubber", 2)
    ))

    // 4. 转换为表
    tableEnv.registerDataStream("order1", dataStream)

    // 5. 执行SQL
    val table: Table = tableEnv.sqlQuery("select * from order1 where id=2")

    // 6.写入CSV
    // 打印表结构
//    table.printSchema()
    //    @param path CSV写入的文件路径
    //    @param fieldDelim 各个字段的分隔符
    //    @param numFiles 写入的文件的个数
    //    @param writeMode 是否覆盖文件 ./
    table.writeToSink(new CsvTableSink("datasetOut/score_sql.csv", ",", 1, FileSystem.WriteMode.OVERWRITE))
    //table.insertInto("order1")
    // 7.执行任务
    env.execute()
  }

  case class OrderTable(id: Long, proudct: String, amount: Int)

}
