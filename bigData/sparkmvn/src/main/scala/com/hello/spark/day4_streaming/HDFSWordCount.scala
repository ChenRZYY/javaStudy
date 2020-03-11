package com.hello.spark.day4_streaming

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

//监控HDFS上的一个目录
object HDFSWordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc ,Seconds(2))

    val inDStream: DStream[String] = ssc.textFileStream("hdfs://hadoop1:9000/streaming")
    val socketStreaming : ReceiverInputDStream[String] = ssc.socketTextStream("h71",9999)

    val resultDStream: DStream[(String, Int)] = inDStream.flatMap(_.split(",")).map((_,1)).reduceByKey(_+_)
    resultDStream.print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}