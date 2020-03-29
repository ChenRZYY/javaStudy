package cn.sdrfengmi.realprocess

import java.util.Properties

import cn.sdrfengmi.realprocess.bean.{ClickLog, Message}
import cn.sdrfengmi.realprocess.util.GlobalConfigUtil
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010


object App {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 设置处理的时间为EventTime 处理时间类型
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // 设置并行度
    env.setParallelism(1)
    //Flink容错机制checkport 5s一次
    env.enableCheckpointing(5000)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(1000)
    env.getCheckpointConfig.setCheckpointTimeout(60000)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    // 设置checkpoint的地址
    env.setStateBackend(new FsStateBackend("hdfs://node01:8020/flink-checkpoint/"))

    // 本地测试 加载本地集合 成为一个DataStream 打印输出
    import org.apache.flink.api.scala._
    val localDataStream: DataStream[String] = env.fromCollection(List("hadoop", "hive", "hbase", "flink"))
    localDataStream.print()

    // -------------整合Kafka----------
    val properties = new Properties()
    //    # Kafka集群地址
    properties.setProperty("bootstrap.servers", GlobalConfigUtil.bootstrapServers)
    //    # ZooKeeper集群地址
    properties.setProperty("zookeeper.connect", GlobalConfigUtil.zookeeperConnect)
    //    # Kafka Topic名称
    properties.setProperty("input.topic", GlobalConfigUtil.inputTopic)
    //    # 消费组ID
    properties.setProperty("group.id", GlobalConfigUtil.groupId)
    //    # 自动提交拉取到消费端的消息offset到kafka
    properties.setProperty("enable.auto.commit", GlobalConfigUtil.enableAutoCommit)
    //    # 自动提交offset到zookeeper的时间间隔单位（毫秒）
    properties.setProperty("auto.commit.interval.ms", GlobalConfigUtil.autoCommitIntervalMs)
    //    # 每次消费最新的数据
    properties.setProperty("auto.offset.reset", GlobalConfigUtil.autoOffsetReset)

    // 话题 反序列化器 属性集合
    val consumer = new FlinkKafkaConsumer010[String](GlobalConfigUtil.inputTopic, new SimpleStringSchema(), properties)
    val kafkaDatastream: DataStream[String] = env.addSource(consumer)
    //    kafkaDatastream.print()

    kafkaDatastream.map(msgJson => {
      val jsonObject: JSONObject = JSON.parseObject(msgJson)
      val message = jsonObject.getString("message")
      val count = jsonObject.getLong("count")
      val timeStamp = jsonObject.getLong("timeStamp")
      //        (message,count,timeStamp)
      //        (ClickLog(message),count,timeStamp)
      Message(ClickLog(message), count, timeStamp)
    })

    env.execute()
  }

}


