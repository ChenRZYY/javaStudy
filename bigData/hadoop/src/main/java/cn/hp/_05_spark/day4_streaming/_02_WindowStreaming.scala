package cn.hp._05_spark.day4_streaming

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.Test

class _02_WindowStreaming {

  @Test
  def window = {
    val sparkConf = new SparkConf().setAppName("NetworkWordCount").setMaster("local[6]")
    val sc = new SparkContext(sparkConf)
    sc.setLogLevel("ERROR")
    val ssc = new StreamingContext(sc, Seconds(1))

    //DStream[String]
    val lines: ReceiverInputDStream[String] = ssc.socketTextStream(
      hostname = "localhost",
      port = 9999,
      storageLevel = StorageLevel.MEMORY_AND_DISK_SER)

    val words = lines.flatMap(_.split(" ")).map(x => (x, 1))

    // 通过 window 操作, 会将流分为多个窗口
    val wordsWindow = words.window(Seconds(30), Seconds(10))
    // 此时是针对于窗口求聚合
    val wordCounts = wordsWindow.reduceByKey((newValue, runningValue) => newValue + runningValue)

    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }

  @Test
  def educeByKeyAndWindow: Unit = {
    //1、创建StreamingContext
    //sparkstraming程序中有一个线程专门用来接收数据，最少还要一个线程用来处理数据，所有在设置master的时候线程数必须>=2
    val sc = new SparkContext(new SparkConf().setMaster("local[2]").setAppName("streaming"))
    //streaming第二个参数是批次时间，streaming的程序会将数据进行累积，累积到批次时间之后才会进行处理
    //一般在项目中，一个批次的时间一般设置为500ms-1s
    val ssc = new StreamingContext(sc, Seconds(5))
    //设置日志级别
    ssc.sparkContext.setLogLevel("warn")
    //2、从数据源获取数据
    //val ds: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop01",9999)
    //使用自定义的receiver接收消息
    val ds: ReceiverInputDStream[String] = ssc.receiverStream(new MyReceiver("server02", 2181))
    //3、数据处理
    ds.flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKeyAndWindow(
        reduceFunc = (agg, curr) => agg + curr,
        windowDuration = Seconds(10),
        slideDuration = Seconds(5))
      //4、结果展示
      .print()
    //5、启动streaming程序
    ssc.start()
    //6、阻塞主线程，等待外部停止
    ssc.awaitTermination()
  }
}
