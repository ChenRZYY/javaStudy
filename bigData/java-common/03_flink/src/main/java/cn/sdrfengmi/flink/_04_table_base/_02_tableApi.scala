package cn.sdrfengmi.flink._04_table_base

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.{ExecutionEnvironment, _}
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.core.fs.FileSystem.WriteMode
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.scala._
import org.apache.flink.table.api.{Table, TableEnvironment, Types}
import org.apache.flink.table.sinks.CsvTableSink
import org.apache.flink.table.sources.CsvTableSource
import org.apache.log4j.{Level, Logger}
import org.junit.Test


class _02_tableApi {

  Logger.getLogger("org").setLevel(Level.ERROR)
  
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

  @Test
  def in: Unit = {
    val dataSet = env.fromElements((1, "a", 10), (2, "b", 20), (2, "b", 20), (2, "b", 20), (3, "c", 30))
    val dataSet2 = env.fromElements((1, "a", 100), (2, "b", 20), (2, "b", 20), (20, "b", 20), (30, "c", 30))
    //列不能重复
    val table = tableEnv.fromDataSet(dataSet, 'a, 'b, 'c)
    val table2: Table = tableEnv.fromDataSet(dataSet2, 'd)
    table2.toDataSet[(Int)].print()
    table2.toDataSet[Integer].print()

    /**
      * 表和子表的关系
      * 子查询只能由一列组成，表的查询条件的列类型需要和子查询保持一致
      * 如果子查询中的值在表中存在就返回真，这个元素就满足条件可以被返回来
      */
    table.where('a.in(table2)).first(100).print()
  }

  @Test
  def orderBy: Unit = {
    tableEnv.scan("user1").as('id, 'name, 'value)
      //.orderBy('id.asc)  //按id列，升序排序(注意是按分区来排序)
      .orderBy('id.desc)
      //.orderBy('value.asc)
      .first(1000).print()
  }

  @Test
  def fetch: Unit = {
    tableEnv.scan("user1").as('id, 'name, 'value)
      //.orderBy('id.asc)  //按id列，升序排序(注意是按分区来排序)
      .orderBy('id.desc) //和first区别,fetch执行过后还是表
      .fetch(2) //只有有序的才能用，只取了前2个元素 必须跟在orderBy后面 org.apache.flink.table.api.ValidationException: Limit operator must be preceded by an OrderBy operator.
      .first(100).print()
  }

  /**
    * offset + fetch =limit 组合以后才能和limit一样的效果
    */
  @Test
  def offset: Unit = {
    tableEnv.scan("user1").as('id, 'name, 'value)
      .orderBy('id.asc) //按id列，升序排序(注意是按分区来排序)
      //      .orderBy('id.desc)
      .offset(1) //只有有序的才能用，偏移了2个元素
      .first(100).print()
  }

  /**
    * sink类继承图
    * TableSink (org.apache.flink.table.sinks)
    * BatchTableSink (org.apache.flink.table.sinks)
    * --CsvTableSink (org.apache.flink.table.sinks)
    * TableSinkBase (org.apache.flink.table.sinks)
    * --CsvTableSink (org.apache.flink.table.sinks)
    * StreamTableSink (org.apache.flink.table.sinks)
    * --RetractStreamTableSink (org.apache.flink.table.sinks)
    * --AppendStreamTableSink (org.apache.flink.table.sinks)
    * ----KafkaTableSinkBase (org.apache.flink.streaming.connectors.kafka)
    * ----CsvTableSink (org.apache.flink.table.sinks)
    * --UpsertStreamTableSink (org.apache.flink.table.sinks)
    */
  @Test
  def csvSink: Unit = {
    env.setParallelism(1) //TODO 不设置并行度,默认cpu核数,打印出来结果不是按照排序,sink结果也是乱序的.内部实际上有序的
    val cvsTableSink = new CsvTableSink("datasetOut/user1.csv", ",", 1, WriteMode.OVERWRITE)
    val fieldNames: Array[String] = Array("id", "name", "value")
    val fieldTypes: Array[TypeInformation[_]] = Array(Types.INT, Types.STRING, Types.INT)
    tableEnv.registerTableSink("cvsTableSink", fieldNames, fieldTypes, cvsTableSink)

    table.as('id, 'name, 'value).orderBy('id.desc).insertInto("cvsTableSink")
    env.execute()
  }

  @Test
  def vsvSource: Unit = {
    // 1. 加载外部CSV文件
    val csvTableSource: CsvTableSource = CsvTableSource.builder()
      .path("./dataset/score.csv") // 加载文件路径
      .field("id", Types.INT) // 列名,类型定义
      .field("name", Types.STRING)
      .field("subjectId", Types.INT)
      .field("score", Types.DOUBLE)
      .fieldDelimiter(",") // 属性间分隔符
      .lineDelimiter("\n") // 换行符
      //      .ignoreFirstLine()              // 忽略第一行内容
      .ignoreParseErrors() // 忽略解析错误
      .build()
    // 2. 将外部数据构建成表
    tableEnv.registerTableSource("tableA", csvTableSource)

    val table: Table = tableEnv.scan("tableA")
      .select("id,name,subjectId,score")
      .filter("name=='张三'")
      .orderBy('id.desc)

    // 6. 打印表结构
    table.printSchema()
    // 7. 将数据落地到新的CSV文件中
    table.writeToSink(new CsvTableSink("./datasetOut/score_table.csv", ",", 1, FileSystem.WriteMode.OVERWRITE))
    // 8. 执行任务
    env.execute()
  }


}
