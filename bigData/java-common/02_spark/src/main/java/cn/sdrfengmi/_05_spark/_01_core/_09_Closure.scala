package cn.sdrfengmi._05_spark._01_core

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 闭包,因为闭包都要把stack应用到的 类 序列化到每个stack中,所以会用到广播变量
  */
object _09_Closure {

  val factor = 3.14

  def areFunction() = {
    val func = (r: Int) => {
      factor * r * r
    }
    func
  }

  def main(args: Array[String]): Unit = {

    val func: Int => Double = areFunction() //TODO 高阶函数,返回一个函数,  一个闭包

    val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("test"))

    val rdd = sc.parallelize(Seq[Int](1, 2, 3, 4, 5, 6))
    //id-> id,name,age,address
    //val connection:Connection = DriverManager.getConnection("")
    rdd.mapPartitions(itm => {
      while (itm.hasNext) {
        val id = itm.next()
      }
      itm
    })
  }
}
