package com.hello.spark.day4_streaming

import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

//简单的单词计数
object NetWordCount {
  def main(args: Array[String]): Unit = {

    Logger.getRootLogger.setLevel(Level.ERROR)

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[2]")
    val sparkContext = new SparkContext(conf)
    val sc = new StreamingContext(sparkContext, Seconds(2))
    /**
      * 数据的输入
      **/
    val inDStream: ReceiverInputDStream[String] = sc.socketTextStream("10.137.36.37", 9999)
    inDStream.print()
    /**
      * 数据的处理
      **/
    val resultDStream: DStream[(String, Int)] = inDStream.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

    /**
      * 数据的输出
      **/
    resultDStream.print()

    /**
      * 启动应用程序
      **/
    sc.start()
    sc.awaitTermination()
//    sc.stop()  不注释stop 打印不出来结果
  }
}