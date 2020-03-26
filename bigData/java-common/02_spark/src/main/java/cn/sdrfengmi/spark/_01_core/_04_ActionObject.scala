package cn.sdrfengmi.spark._01_core

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.Test


class _04_ActionObject {
  val sc = new SparkContext(new SparkConf().setMaster("local[5]").setAppName("_04_ActionObject"))

  @Test
  def reduce(): Unit = {
    val rdd1: RDD[(String, Double)] = sc.parallelize(Seq[(String, Double)](
      ("手机", 10.0),
      ("手机", 25.0),
      ("电脑", 55.0)
    ))
    val result: (String, Double) = rdd1.reduce((agg, curr) => ("总价", agg._2 + curr._2))

    /**
      * 数据处理流程：
      * (agg, curr)=>("总价",agg._2+curr._2)  agg:上一次聚合结果值  curr:本次要聚合的元素
      * 第一次计算: agg=("手机",10.0)  curr=("手机",25.0)   =>("总价",10.0+25.0)
      * 第二次计算: agg=("总价",10.0+25.0) curr=("电脑",55.0)  => ("总价",10.0+25.0+55.0)
      */
    println(result)
  }

  @Test
  def foreach(): Unit = {
    val rdd1: RDD[(String, Double)] = sc.parallelize(Seq[(String, Double)](
      ("手机", 10.0),
      ("手机", 25.0),
      ("电脑", 55.0)
    ), 2)

    rdd1.foreach(println(_))
  }

  @Test
  def foreachPartition(): Unit = {
    val rdd1: RDD[(String, Double)] = sc.parallelize(Seq[(String, Double)](
      ("手机", 10.0),
      ("手机", 25.0),
      ("电脑", 55.0)
    ), 2)

    //    rdd1.foreachPartition(inst(_)_) TODO 可以把 connection函数抽取出来
    rdd1.foreachPartition(it => {
      var connection: Connection = null
      var statement: PreparedStatement = null
      try {
        connection = DriverManager.getConnection("...")
        statement = connection.prepareStatement("insert into table values(?,?)")
        var i = 0
        while (it.hasNext) {
          val element = it.next()
          statement.setString(1, element._1)
          statement.setDouble(2, element._2)
          //添加到批次中提交
          statement.addBatch()
          //每一千条数据提交一次
          if (i % 1000 == 0) {
            statement.executeBatch()
          }
          i = i + 1
        }
        //最后一个批次可能不满一次，需要再提交一次
        statement.executeBatch()
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        if (statement != null)
          statement.close()
        if (connection != null)
          connection.close()
      }
    })
  }


  @Test
  def take(): Unit = {
    val rdd1: RDD[(String, Double)] = sc.parallelize(Seq[(String, Double)](
      ("手机", 10.0),
      ("手机", 11.0),
      ("手机", 12.0),
      ("手机", 25.0),
      ("电脑", 55.0)
    ), 2)
    //获取前3位的数据
    //rdd1.take(3)
    ////获取第一个数据
    //rdd1.first()
    ////统计rdd中元素的总个数
    //rdd1.count()
    ////统计rdd中key出现的次数
    //rdd1.countByKey()
    ////抽样
    //rdd1.takeSample(false,1)

    rdd1.saveAsTextFile("result")
  }
}
