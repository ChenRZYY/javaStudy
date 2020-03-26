package cn.sdrfengmi.flink._03_stream_base.stream.datasource

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig

object DataSource_Kafka {

  def main(args: Array[String]): Unit = {
    //1 从参数来获取
    ParameterTool.fromArgs(args)
    // 1. 创建流式环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 2.指定kafak相关信息
    //2 配置文件
    val kafkaCluster = "192.168.139.150:9092,node02:9092,node03:9092"
    val kafkaTopic = "kafkatopic"
    // 3. 创建Kafka数据流
    val props = new Properties()
    props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,kafkaCluster)
    props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "metrics-group")
    props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

    //4 .设置数据源
    val kafkaDataStream: DataStream[String] = env.addSource(new FlinkKafkaConsumer011[String](
      kafkaTopic,
      new SimpleStringSchema(), props)).setParallelism(1)

    // 5. 打印数据
    kafkaDataStream.print()


    // 6.执行任务
    env.execute()

  }

}
