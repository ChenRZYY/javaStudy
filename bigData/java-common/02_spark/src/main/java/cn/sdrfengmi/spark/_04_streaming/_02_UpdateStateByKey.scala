package cn.sdrfengmi.spark._04_streaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
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
    val result: DStream[(String, Int)] = source.flatMap(_.split(" "))
      .map((_, 1))
      .updateStateByKey((values: Seq[Int], state: Option[Int]) => {
        val currentCount: Int = values.sum // 计算当前批次相同key的单词总数
        val lastCount: Int = state.getOrElse(0) // 获取上一次保存的单词总数
        Some(currentCount + lastCount) // 返回新的单词总数
      })
    //4、结果展示
    result.print()
    result.saveAsTextFiles("01_dataset/", "_02_UpdateStateByKey")
    //5、启动streaming程序
    ssc.start()
    //6、阻塞主线程
    ssc.awaitTermination()
  }
}
