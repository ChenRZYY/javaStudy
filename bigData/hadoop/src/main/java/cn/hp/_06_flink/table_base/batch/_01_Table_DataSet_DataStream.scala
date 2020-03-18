package cn.hp._06_flink.table_base.batch

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.table.api.{Table, TableEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row
import org.junit.Test

class _01_Table_DataSet_DataStream {

  val env = ExecutionEnvironment.getExecutionEnvironment //    1. 获取批处理环境
  env.setParallelism(1) //    2. 设置并行度
  val tableEnv = TableEnvironment.getTableEnvironment(env) //    3. 获取Table运行环境
  val dataSet: DataSet[(Long, Int, String)] = env.fromCollection(List((1L, 1, "Hello"), (2L, 2, "Hello"), (6L, 6, "Hello"), (7L, 7, "Hello World"), (8L, 8, "Hello World"), (20L, 20, "Hello World")))


  val envStream = StreamExecutionEnvironment.getExecutionEnvironment //    1. 获取流处理环境
  envStream.setParallelism(1) //    2. 设置并行度
  val tableEnvStream: StreamTableEnvironment = TableEnvironment.getTableEnvironment(envStream) //    3. 获取Table运行环境
  val dataStream: DataStream[(Long, Int, String)] = envStream.fromCollection(List((1L, 1, "Hello"), (2L, 2, "Hello"), (6L, 6, "Hello"), (7L, 7, "Hello World"), (8L, 8, "Hello World"), (20L, 20, "Hello World")))

  @Test
  def table_DataSet = {
    //    5. 转换DataSet为Table
    val table: Table = tableEnv.fromDataSet(dataSet)
    val table2: Table = tableEnv.fromDataSet(dataSet, 'id, 'count, 'call) //指定字段
    val table3: Table = dataSet.toTable(tableEnv)
    val table4: Table = dataSet.toTable(tableEnv, 'id, 'count, 'call) //指定字段

    // 6. Table转换为DataSet
    val resultRow: DataSet[Row] = tableEnv.toDataSet[Row](table)
    val resultRow2: DataSet[Row] = table.toDataSet[Row]
    val resultType: DataSet[(Long, Int, String)] = tableEnv.toDataSet[(Long, Int, String)](table) //类型可以写成 样例类
    //    7. 打印输出
    resultType.print()
  }


  @Test
  def table_DataStream: Unit = {
    //    5. 转换DataStream为Table
    val table: Table = tableEnvStream.fromDataStream(dataStream)
    val table2: Table = tableEnvStream.fromDataStream(dataStream, 'id, 'count, 'call) //指定字段
    val table3: Table = dataStream.toTable(tableEnvStream)
    val table4: Table = dataStream.toTable(tableEnvStream, 'id, 'count, 'call) //指定字段

    //    6. 将table转换为DataStream----将一个表附加到流上Append Mode
    val appendDataSteam: DataStream[(Long, Int, String)] = tableEnvStream.toAppendStream[(Long, Int, String)](table)
    //    7. 将table转换为DataStream----Retract Mode true代表添加消息，false代表撤销消息
    val retractDataStream: DataStream[(Boolean, (Long, Int, String))] = tableEnvStream.toRetractStream[(Long, Int, String)](table)
    //    8. 打印输出
    appendDataSteam.print()
    //    9. 执行任务
    envStream.execute()
  }


  @Test
  def registerDataSet: Unit = {
    tableEnv.registerDataSet("myTable1", dataSet)
    tableEnv.registerDataSet("myTable2", dataSet, 'id, 'count, 'call) //指定字段
    val table: Table = tableEnv.sqlQuery("select * from myTable2")
    table.printSchema()
    table.toDataSet[Row].print()
  }


  @Test
  def registerDataStream: Unit = {
    tableEnvStream.registerDataStream("myTable1", dataStream)
    tableEnvStream.registerDataStream("myTable2", dataStream, 'id, 'count, 'call) //指定字段
    val tableStream: Table = tableEnvStream.sqlQuery("select * from myTable2")
    tableStream.printSchema()
    tableStream.toAppendStream[(Long, Int, String)].print()
    envStream.execute()
  }

  @Test
  def registerTable: Unit = {
    val table: Table = dataSet.toTable(tableEnv, 'id, 'count, 'call)//指定字段
    tableEnv.registerTable("chenzhendong", table) //tabel 还能继续注册成新table
    tableEnv.sqlQuery("select * from chenzhendong").where("call == 'Hello'").first(2).print()
  }


}
