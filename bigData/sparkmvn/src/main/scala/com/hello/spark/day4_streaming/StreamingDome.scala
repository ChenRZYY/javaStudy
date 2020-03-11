package com.hello.spark.day4_streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingDome {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("StringDome").setMaster("lacal[2]")
    val context: StreamingContext = new StreamingContext(conf, Seconds(2))
    val recever: ReceiverInputDStream[String] = context.socketTextStream("10.137.36.47", 9999)
    //    recever.flatMap()

  }

  import scala.reflect._
  def makePair[T: ClassTag](first: T, second: T) = {
    val r = new Array[T](2);
    r(0) = first;
    r(1) = second;
    r
  }

  //TODO 在数组中必须指定类型, 使用ClassTage 数据类型
//  def makePair2[T](first: T, second: T) = {
//    val r = new Array[T](2);
//    r(0) = first;
//    r(1) = second;
//    r
//  }

}
