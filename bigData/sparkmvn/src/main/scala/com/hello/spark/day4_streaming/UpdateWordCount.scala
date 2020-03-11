package com.hello.spark.day4_streaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

//第二次运行的时候更新原先的结果
object UpdateWordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[2]")
    System.setProperty("HADOOP_USER_NAME","hadoop")
    val sparkContext = new SparkContext(conf)

    val sc = new StreamingContext(sparkContext,Seconds(2))

    //而5.3里面的代码有一个checkpoint方法，它会把上一次Driver里面的运算结果状态保存在checkpoint的目录里面，我们在第二次启动程序时，从checkpoint里面取出上一次的运行结果状态，把这次的Driver状态恢复成和上一次Driver一样的状态
    sc.checkpoint("hdfs://hadoop1:9000/streaming")
    val inDStream: ReceiverInputDStream[String] = sc.socketTextStream("hadoop1",9999)

    val resultDStream: DStream[(String, Int)] = inDStream.flatMap(_.split(","))
      .map((_, 1))
      .updateStateByKey((values: Seq[Int], state: Option[Int]) => {
        val currentCount: Int = values.sum
        val lastCount: Int = state.getOrElse(0)
        Some(currentCount + lastCount)
      })
    resultDStream.print()

    sc.start()
    sc.awaitTermination()
    sc.stop()

//    5.4　DriverHA
//    5.3的代码一直运行，结果可以一直累加，但是代码一旦停止运行，再次运行时，结果会不会接着上一次进行计算，上一次的计算结果丢失了，主要原因上每次程序运行都会初始化一个程序入口，而2次运行的程序入口不是同一个入口，所以会导致第一次计算的结果丢失，第一次的运算结果状态保存在Driver里面，所以我们如果想用上一次的计算结果，我们需要将上一次的Driver里面的运行结果状态取出来，而5.3里面的代码有一个checkpoint方法，它会把上一次Driver里面的运算结果状态保存在checkpoint的目录里面，我们在第二次启动程序时，从checkpoint里面取出上一次的运行结果状态，把这次的Driver状态恢复成和上一次Driver一样的状态
  }
}