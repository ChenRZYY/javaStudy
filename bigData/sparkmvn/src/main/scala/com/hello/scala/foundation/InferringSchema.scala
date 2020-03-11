package com.hello.scala.foundation

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object InferringSchema {
  def main(args: Array[String]): Unit = {

    // 设置Spark的序列化方式
//    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    //创建sparkContext
    val conf = new SparkConf().setAppName("SQL_1")
      .setMaster("local")
//      .set("spark.executor.memory", "1g")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc) //创建sqlContext

    //从指定的地址创建RDD
    val lineRDD = sc.textFile(args(0)).map(_.split(" "))
    val personRDD = lineRDD.map(x => Person(x(0).toInt, x(1), x(2).toInt))

    import sqlContext.implicits._   //导入隐式转换，如果不到人无法将RDD转换成DataFrame
    val personDF = personRDD.toDF
    personDF.registerTempTable("t_person") //注册表
//    personDF.collect()
    //传入SQL
    val df = sqlContext.sql("select * from t_person order by age desc limit 2")
    //将结果以JSON的方式存储到指定位置
    df.write.json(args(1))
    df.show()

    sc.stop() //停止Spark Context

  }
}

case class Person(id: Int, name: String, age: Int)
