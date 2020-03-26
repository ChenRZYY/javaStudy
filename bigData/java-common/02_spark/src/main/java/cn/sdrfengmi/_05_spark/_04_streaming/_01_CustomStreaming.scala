package cn.sdrfengmi._05_spark._04_streaming

import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object _01_CustomStreaming {

  def main(args: Array[String]): Unit = {

    //1、创建StreamingContext
    //sparkstraming程序中有一个线程专门用来接收数据，最少还要一个线程用来处理数据，所有在设置master的时候线程数必须>=2
    val sc = new SparkContext(new SparkConf().setMaster("local[2]").setAppName("streaming"))
    //streaming第二个参数是批次时间，streaming的程序会将数据进行累积，累积到批次时间之后才会进行处理
    //一般在项目中，一个批次的时间一般设置为500ms-1s
    val ssc = new StreamingContext(sc, Seconds(10))
    //设置日志级别
    ssc.sparkContext.setLogLevel("warn")
    //2、从数据源获取数据
    //val ds: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop01",9999)
    //使用自定义的receiver接收消息
    val ds: ReceiverInputDStream[String] = ssc.receiverStream(new MyReceiver("47.105.158.112", 2181))
    //3、数据处理
    ds.flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      //4、结果展示
      .print()
    //5、启动streaming程序
    ssc.start()
    //6、阻塞主线程，等待外部停止
    ssc.awaitTermination()
  }
}
