package cn.sdrfengmi.flink._03_stream_base.stream.sink

import java.net.{InetAddress, InetSocketAddress}
import java.util.Properties

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests


object Sink_ElasticSearch {
  def main(args: Array[String]): Unit = {
    val params: ParameterTool = ParameterTool.fromArgs(args)

    // 1.创建流处理环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // 2.设置并行度
    env.setParallelism(params.getInt("parallelism", 1))
    // 3.加载kakfa数据流
    val kafkaCluster = params.get("bootstrap-servers", "192.168.139.181:9092")
    println("kafkaCluster is :" + kafkaCluster)
    val kafkaTopic = "test"
    val props = new Properties()
    props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster)
    props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test")
    props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    //props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    val kafkaDataStream: DataStream[String] = env.addSource(new FlinkKafkaConsumer011[String](kafkaTopic, new SimpleStringSchema(), props))

    // 4.写入sink到elastic search
    val config = new java.util.HashMap[String, String]
    config.put("cluster.name", "my-es")
    // This instructs the sink to emit after every element, otherwise they would be buffered
    config.put("bulk.flush.max.actions", "20")

    val transportAddresses = new java.util.ArrayList[InetSocketAddress]
    //transportAddresses.add(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9200))
    transportAddresses.add(new InetSocketAddress(InetAddress.getByName("node01"), 9300))

    kafkaDataStream.addSink(new ElasticsearchSink(config, transportAddresses, new ElasticsearchSinkFunction[String] {
      def createIndexRequest(element: String): IndexRequest = {
        val json = new java.util.HashMap[String, String]
        json.put("data", element)

        return Requests.indexRequest()
          .index("my-index")
          .`type`("my-type")
          .source(json)
      }

      override def process(t: String, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
        //println(t)
        /*implicit val formats = org.json4s.DefaultFormats
        val jsonStr: String = org.json4s.native.Serialization.write(logs)*/

        val request: IndexRequest = Requests.indexRequest().index("").`type`("_doc").source(t)

        requestIndexer.add(request)
      }
    }))
    // 5.执行任务
    env.execute()
  }
}
