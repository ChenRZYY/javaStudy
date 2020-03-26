package cn.sdrfengmi.spark._04_streaming

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object _01_FileStreaming {

  def main(args: Array[String]): Unit = {

    //1、创建StreamingContext
    val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("fileStreaming"))
    val ssc = new StreamingContext(sc, Seconds(5))

    ssc.sparkContext.setLogLevel("warn")
    //2、从数据源获取数据 监控HDFS上的一个目录
    //TODO 在监控文件夹读取数据的时候，可能出现读取不到数据情况，问题应该是时间没有同步:service ntpd restart
    val ds = ssc.textFileStream("hdfs://server02:8020/globalParameter")
    //3、数据处理
    ds.flatMap(_.split(","))
      .map((_, 1))
      .reduceByKey(_ + _)
      //4、结果展示
      .print()
    //5、启动程序
    ssc.start()
    //6、阻塞主线程
    ssc.awaitTermination()
  }
}
