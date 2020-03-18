package cn.hp._06_flink.table_base.batch

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.table.api.{Table, TableEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row
import org.junit.Test


class _02_tableApi {

  val env = ExecutionEnvironment.getExecutionEnvironment //    1. 获取批处理环境
  val tableEnv = TableEnvironment.getTableEnvironment(env) //    3. 获取Table运行环境
  val dataSet = env.fromElements((1, "zhangsan", 10), (2, "lisi", 20), (3, "wangwu", 30), (4, "zhaoliu", 20))
  val table: Table = tableEnv.fromDataSet(dataSet)
  tableEnv.registerTable("user1", table)

  val envStream = StreamExecutionEnvironment.getExecutionEnvironment //    1. 获取流处理环境
  val tableEnvStream: StreamTableEnvironment = TableEnvironment.getTableEnvironment(envStream) //    3. 获取Table运行环境

  @Test
  def first: Unit = {
    table.first(1000).print()
  }

  @Test
  def scan: Unit = {
    val table2: Table = tableEnv.scan("user1") //scan查询table 所有数据
    table2.first(10).print()
  }

  @Test
  def select: Unit = {
    //查询table 所有数据 //select选择需要的字段  一直都是Table类型
    tableEnv.scan("user1").select('_1, '_2).first(100).print()
    //select重命名字段名称
    tableEnv.scan("user1").select('_1 as 'id, '_2 as 'name).where('name === "zhangsan").first(100).print()
    //去掉字段重复的再求和  10+20+30(去除重复的20)=60
    tableEnv.scan("user1").select('_3.sum.distinct).first(100).print()

  }

  @Test
  def as: Unit = {
    //as 重命令字段名称
    tableEnv.scan("user1").as('id, 'name, 'value).select('id, 'name, 'value).first(100).print()
  }

  @Test
  def where: Unit = {
    tableEnv.scan("user1").as('id, 'name, 'value).select('id, 'name, 'value)
      .where('value === 20) //三个==== 对类型判断
      .where("id=4").first(100).print() //直接写sql
  }

  @Test
  def groupBy: Unit = {
    //查询table 所有数据
    tableEnv.scan("user1").as('id, 'name, 'value)
      //选择需要的字段
      .groupBy('name).select('name, 'value.sum as 'value).first(100).print()
  }

  @Test
  def distinct: Unit = {
    val dataSet = env.fromElements((1, "zhangsan", 10), (2, "lisi", 20), (3, "wangwu", 30), (4, "zhaoliu", 20), (4, "zhaoliu", 20))
    tableEnv.registerDataSet("user2", dataSet)
    tableEnv.scan("user2")
      //一列中所有记录相同 去重
      .distinct().first(100).print()
  }

  @Test
  def join: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (20, "b", 20), (30, "c", 30))
    //列不能重复
    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2 = tableEnv.fromDataSet(dataSet2, 'd, 'e, 'f)
    table.join(table2).where(" a = d ").first(100).print()
  }

  @Test
  def left_rightOuterJoin: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (20, "b", 20), (30, "c", 30))
    //列不能重复
    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2 = tableEnv.fromDataSet(dataSet2, 'd, 'e, 'f)
    //table.leftOuterJoin(table2,"a=d").first(100).print()
    table.leftOuterJoin(table2, 'a === 'd).first(100).print()
    table.rightOuterJoin(table2, "a = d").first(100).print()
  }

  @Test
  def union: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (20, "b", 20), (30, "c", 30))
    //列不能重复
    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2 = tableEnv.fromDataSet(dataSet2, 'd, 'e, 'f)
    //两个表串连，取并集(会去重)
    table.union(table2).first(100).print()
  }

  @Test
  def unionAll: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (20, "b", 20), (30, "c", 30))
    //列不能重复
    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2 = tableEnv.fromDataSet(dataSet2, 'd, 'e, 'f)
    //两个表串连，取并集(不会去重)
    table.unionAll(table2).first(100).print()
  }

  @Test
  def intersect: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (2, "b", 20), (20, "b", 20), (30, "c", 30))
    //列不能重复
    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2 = tableEnv.fromDataSet(dataSet2, 'd, 'e, 'f)
    //两个表相连接，取交集 (会去重)  必须所有列都相同才行
    table.intersect(table2).first(100).print()
  }

  @Test
  def intersectAll: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (2, "b", 20), (20, "b", 20), (30, "c", 30))
    //列不能重复
    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2 = tableEnv.fromDataSet(dataSet2, 'd, 'e, 'f)
    //两个表相连接，取交集 (不会去重)
    table.intersectAll(table2).first(100).print()
  }


  @Test //TODO ???
  def minus: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (2, "b", 20), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (2, "b", 20), (2, "b", 20), (20, "b", 20), (30, "c", 30))

    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2 = tableEnv.fromDataSet(dataSet2, 'd, 'e, 'f)
    //左表不存在于右表中的数据，会去重
    table.minus(table2).first(100).print()
  }

  @Test //TODO ???
  def minusAll: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (2, "b", 20), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (2, "b", 20), (2, "b", 20), (20, "b", 20), (30, "c", 30))

    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2 = tableEnv.fromDataSet(dataSet2, 'd, 'e, 'f)
    //左表不存在于右表中的数据，不会去重，如果左表某个元素有n次，右表中有m次，那这个元素出现的是n - m次
    table.minusAll(table2).first(100).print()
  }


}
