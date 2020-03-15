package cn.hp._06_flink.batch_base._04_broad

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, _}
import org.apache.flink.configuration.Configuration

object StuBroadCast {
  def main(args: Array[String]): Unit = {
    //创建环境变量
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    //从集合中获得数据
    val stuInfo: DataSet[(String, String)] = env.fromCollection(List(("1", "张三"), ("2", "李四"), ("3", "王五")))
    val scoreData: DataSet[(Int, String, Int)] = env.fromCollection(List((1, "语文", 60), (2, "数学", 90), (3, "政治", 89)))
    //转换操作
    val result: DataSet[(String, String, Int)] = scoreData.map(new RichMapFunction[(Int, String, Int), (String, String, Int)] {

      var stuInfo: List[(String, String)] = null

      override def map(value: (Int, String, Int)): (String, String, Int) = {
        val stuNo = value._1
        val tuples: List[(String, String)] = stuInfo.filter(stu => stu._1.toInt == stuNo)
        (tuples(0)._2, value._2, value._3)
      }

      override def open(parameters: Configuration): Unit = {
        //FIXME 把java 的list转成 scala中的list
        import scala.collection.JavaConversions._
        stuInfo = getRuntimeContext.getBroadcastVariable[(String, String)]("stuInfo").toList
      }
    }).withBroadcastSet(stuInfo, "stuInfo")

    //打印数据
    result.print()
  }
}
