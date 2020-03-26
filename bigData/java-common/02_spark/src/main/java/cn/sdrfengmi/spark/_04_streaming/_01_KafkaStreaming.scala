package cn.sdrfengmi.spark._04_streaming

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object _01_KafkaStreaming {

  def main(args: Array[String]): Unit = {

    //1、创建StreamingContext
    val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("kafka"))
    val ssc = new StreamingContext(sc, Seconds(5))

    ssc.sparkContext.setLogLevel("warn")
    //2、从kafka读取数据
    //指定消费的topic
    val topics = Array("spark11")
    //指定kafka的参数
    val kafkaParams = Map[String, Object](
      //指定kafka broker的节点
      "bootstrap.servers" -> "hadoop01:9092,hadoop02:9092,hadoop03:9092",
      //kafka消息key的反序列化方式
      "key.deserializer" -> classOf[StringDeserializer],
      //kafka消息的value的反序列化方式
      "value.deserializer" -> classOf[StringDeserializer],
      //消费者组
      "group.id" -> "gruop_kafka_spark_11",
      //从哪个offset开始消费 earliest:最小的offset    latest:最新的offset
      "auto.offset.reset" -> "latest",
      //是否自动提交offset
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val source = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](topics, kafkaParams))
    //3、数据处理
    source.foreachRDD(rdd => {
      //数据处理
      rdd.map(_.value()).foreach(println(_))
      //提交offset
      //1、获取本次的offset
      val offsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      //2、提交offset
      source.asInstanceOf[CanCommitOffsets].commitAsync(offsets)
    })
    //4、结果展示

    //5、启动streaming
    ssc.start()
    //6、阻塞主线程
    ssc.awaitTermination()
  }
}