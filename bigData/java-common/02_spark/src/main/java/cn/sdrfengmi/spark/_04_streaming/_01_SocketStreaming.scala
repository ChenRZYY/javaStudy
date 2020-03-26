package cn.sdrfengmi.spark._04_streaming

import org.apache.log4j.{Level, Logger}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

//简单的单词计数
object _01_SocketStreaming {

  def main(args: Array[String]): Unit = {

    //    LoggerLevels.setStreamingLogLevels()
    //    Logger.getRootLogger.setLevel(Level.ERROR)
    Logger.getRootLogger.setLevel(Level.ERROR)

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[2]")
    val sparkContext = new SparkContext(conf)
    val sc = new StreamingContext(sparkContext, Seconds(2))

    /**
      * 数据的输入   StorageLevel序列化类型
      **/
    val inDStream: ReceiverInputDStream[String] = sc.socketTextStream("server02", 2181, StorageLevel.MEMORY_AND_DISK_SER)
    inDStream.print()

    val resultDStream: DStream[(String, Int)] = inDStream.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    resultDStream.print()

    /**
      * 启动应用程序
      **/
    sc.start() //启动流和 JobGenerator, 开始流式处理数据
    sc.awaitTermination() //阻塞主线程, 后台线程开始不断获取数据并处理
    //    sc.stop()  不注释stop 打印不出来结果
  }
}