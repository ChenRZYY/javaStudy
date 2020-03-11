package com.hello.spark.day4_streaming

import com.hello.spark.day3_flume_kafka.LoggerLevels
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by root on 2016/5/21.
  * 简单的单词计数
  */
object StreamingWordCount {

  def main(args: Array[String]) {

    LoggerLevels.setStreamingLogLevels()
    Logger.getRootLogger.setLevel(Level.ERROR)

    //StreamingContext
    val conf = new SparkConf().setAppName("StreamingWordCount").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(2))
    //接收数据
    val ds = ssc.socketTextStream("10.137.36.37", 9999)
    //DStream是一个特殊的RDD
    //hello tom hello jerry
    val result = ds.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
    //打印结果
    result.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
