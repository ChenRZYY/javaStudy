package com.hello.spark.day5

//import _05_kafka.serializer.StringDecoder
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}


object DirectKafkaWordCount {

  def dealLine(line: String): String = {
    val list: List[String] = line.split(',').toList
    //    val list = AnalysisUtil.dealString(line, ',', '"')// 把dealString函数当做split即可
    list.apply(0).substring(0, 10) + "-" + list.apply(26)
  }

  def processRdd(rdd: RDD[(String, String)]): Unit = {
    val lines = rdd.map(_._2)
    val words = lines.map(_.split(" "))
    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
    wordCounts.foreach(println)
  }

  def main(args: Array[String]) {
    if (args.length < 3) {
      System.err.println(
        s"""
           |Usage: DirectKafkaWordCount <brokers> <topics> <groupid>
           |  <brokers> is a list of one or more Kafka brokers
           |  <topics> is a list of one or more _05_kafka topics to consume from
           |  <groupid> is a consume group
           |
        """.stripMargin)
      System.exit(1)
    }

    Logger.getLogger("org").setLevel(Level.WARN)

    val Array(brokers, topics, groupId) = args

    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("DirectKafkaWordCount")
    sparkConf.setMaster("local[*]")
    sparkConf.set("spark.streaming._05_kafka.maxRatePerPartition", "5")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create direct _05_kafka stream with brokers and topics
    val topicsList = topics.split(",").toList
    val kafkaParams: Map[String, Object] = Map[String, Object](
      "metadata.broker.list" -> brokers,
      "group.id" -> groupId,
      "auto.offset.reset" -> "smallest", //latest
      //      "bootstrap.servers" -> "hadoop01:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topicsList, kafkaParams)
    )

    //    val km = new KafkaManager(kafkaParams)
    //    val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)

    stream.foreachRDD((rdd: RDD[ConsumerRecord[String, String]]) => {
      if (!rdd.isEmpty()) {
        // 先处理消息
        processRdd(rdd)
        // 再更新offsets
        stream.updateZKOffsets(rdd)
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
