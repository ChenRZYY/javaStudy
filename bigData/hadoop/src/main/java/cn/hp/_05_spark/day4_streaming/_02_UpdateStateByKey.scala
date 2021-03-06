package cn.hp._05_spark.day4_streaming

import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object _02_UpdateStateByKey {

  def main(args: Array[String]): Unit = {

    //1、创建StreamingContext
    val ssc = new StreamingContext(new SparkContext(new SparkConf().setMaster("local[4]").setAppName("_02_UpdateStateByKey")), Seconds(3))
    ssc.sparkContext.setLogLevel("warn")
    ssc.sparkContext.setCheckpointDir("checkpoint") //TODO checkpoint 都一样
    //    ssc.checkpoint("hdfs://server02:8020/streaming")
    //    ssc.sparkContext.setCheckpointDir("hdfs://server02:8020/streaming")
    //2、从socket读取数据
    val source: ReceiverInputDStream[String] = ssc.socketTextStream("server02", 2181)
    //3、对单词进行全局累加
    source.flatMap(_.split(" "))
      .map((_, 1))
      .updateStateByKey((values: Seq[Int], state: Option[Int]) => {
        val currentCount: Int = values.sum
        val lastCount: Int = state.getOrElse(0)
        Some(currentCount + lastCount)
      })
      //4、结果展示
      .print()
    //5、启动streaming程序
    ssc.start()
    //6、阻塞主线程
    ssc.awaitTermination()
  }
}
