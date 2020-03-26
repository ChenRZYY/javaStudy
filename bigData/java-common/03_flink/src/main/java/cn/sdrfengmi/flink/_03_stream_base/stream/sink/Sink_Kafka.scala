package cn.sdrfengmi.flink._03_stream_base.stream.sink

import java.util.Properties

import cn.sdrfengmi.flink._03_stream_base.stream.datasource.DataSource_MySql.MySql_Source
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011.Semantic
//import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper //1.8.0
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchemaWrapper  //1.7.2版本
import org.apache.kafka.clients.producer.ProducerConfig

object Sink_Kafka {

  def main(args: Array[String]): Unit = {

    //  1. 创建流处理环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //  2. 设置并行度
    env.setParallelism(1)
    //  3. 添加自定义MySql数据源
    val mySqlDataStream: DataStream[(Int, String, String, String)] = env.addSource(new MySql_Source())
    //  4. 转换元组数据为字符串
    val mapDataStream: DataStream[String] = mySqlDataStream.map {
      line => line._1 + "," + line._2 + "" + line._3 + "" + line._4
    }
    //  5. 构建`FlinkKafkaProducer010`
    val kafkaCluster = "node01:9092,node02:9092,node03:9092"
    val flinkKafkaProducer011: FlinkKafkaProducer011[String] = new FlinkKafkaProducer011[String](kafkaCluster,
      "test2", new SimpleStringSchema())

    /**
      * topicId: String,
      * serializationSchema: KeyedSerializationSchema[IN],
      * producerConfig: Properties,
      * semantic: FlinkKafkaProducer011.Semantic
      */
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster)
    //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartition)
    //props.put(ProducerConfig.ACKS_CONFIG, 0)
    val simpleStringSchema = new SimpleStringSchema
    val events: FlinkKafkaProducer011[String] = new FlinkKafkaProducer011[String]("test2",
      new KeyedSerializationSchemaWrapper(simpleStringSchema), props, Semantic.EXACTLY_ONCE)

    //  6. 添加sink
    //mapDataStream.addSink(flinkKafkaProducer011)
    mapDataStream.addSink(events)
    //  7. 执行任务
    env.execute()
  }

}
