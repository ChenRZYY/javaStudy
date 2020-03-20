package cn.hp._06_flink._02_global

import org.apache.flink.api.common.functions.RichFilterFunction
import org.apache.flink.api.scala._
import org.junit.Test
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.log4j.{Level, Logger}

/**
  * @Author Haishi
  * @create 2020/3/20 11:07
  */
class GlobalParameterDemo {
  Logger.getLogger("org").setLevel(Level.ERROR)
  val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

  //1 众所周知，flink作为流计算引擎，处理源源不断的数据是其本意，但是在处理数据的过程中，往往可能需要一些参数的传递，
  // 那么有哪些方法进行参数的传递？在什么时候使用？这里尝试进行简单的总结。
  //使用configuration
  @Test
  def configuration: Unit = {
    val words: DataSet[(String, Int)] = env.fromCollection(List(("hadoop", 1), ("hadoop", 1), ("hive", 1), ("spark", 1), ("flink", 1)))
    //    val words: DataSet[String] = env.readTextFile("dataset/wordcount.txt")
    val configuration: Configuration = new Configuration()
    configuration.setString("genre", "Action");

    val unit: DataSet[(String, Int)] = words.filter(_._1 == "hadoop").withParameters(configuration)

  }

  //使用参数的function需要继承自一个rich的function，这样才可以在open方法中获取相应的参数。
  @Test
  def configuration2: Unit = {
    val words: DataSet[(String, Int)] = env.fromCollection(List(("hadoop", 1), ("hadoop", 1), ("hive", 1), ("spark", 1), ("flink", 1)))
    val configuration: Configuration = new Configuration()
    configuration.setString("genre", "flink");

    val filterDataSet: DataSet[(String, Int)] = words.filter(new RichFilterFunction[(String, Int)] {

      var genre = ""

      override def open(parameters: Configuration): Unit = {
        //        super.open(parameters)
        genre = parameters.getString("genre", "");
      }

      override def filter(value: (String, Int)): Boolean = {
        if (value._1 == "hadoop" || value._1 == genre) {
          true
        } else {
          false
        }
      }
    }).withParameters(configuration)

    filterDataSet.printToErr()
  }

}
