package cn.hp._06_flink.stream_base.stream.datasource

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

object DataSource_MySql_181 {

  //  1. 自定义Source,继承自RichSourceFunction
  class MySql_Source_181 extends RichSourceFunction[(String,Int,Double)]{
    //  2. 实现run方法
    override def run(ctx: SourceFunction.SourceContext[(String,Int,Double)]): Unit = {
      //    1. 加载驱动
      Class.forName("com.mysql.jdbc.Driver")
      //    2. 创建连接
      val connection: Connection = DriverManager.getConnection("jdbc:mysql://node01:3306/test","root","123456")
      //    3. 创建PreparedStatement
      val sql = "select name,age,gpa from student"
      val ps: PreparedStatement = connection.prepareStatement(sql)
      //    4. 执行查询
      val resultSet: ResultSet = ps.executeQuery()
      //    5. 遍历查询结果,收集数据
      while(resultSet.next()){
        val name = resultSet.getString("name")
        val age = resultSet.getInt("age")
        val gpa = resultSet.getDouble("gpa")

        // 收集数据
        ctx.collect((name,age,gpa))
      }

    }

    override def cancel(): Unit = {

    }
  }

  def main(args: Array[String]): Unit = {
    // 1. env
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 2 使用自定义Source
    val mySqlDataStream: DataStream[(String,Int,Double)] = env.addSource(new MySql_Source_181)

    // 3. 打印结果
    mySqlDataStream.print()

    // 4. 执行任务
    env.execute()
  }

}
