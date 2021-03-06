package com.sdrfengmi.syncdb

import com.sdrfengmi.syncdb.bean.{Canal, HBaseOperation}
import com.sdrfengmi.syncdb.task.PreprocessTask
import com.sdrfengmi.syncdb.util.{FlinkUtils, HBaseUtil}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.watermark.Watermark

/**
  * mysql操作->canal_kafka获取操作写到kafka-->sync_db从kafka中获取数据到Hbase-->batch_process flink批处理从hbase获取数据再存储到hbase中
  *
  *
  *
  */
object App {

  def main(args: Array[String]): Unit = {
    // Flink流式环境的创建
    val env = FlinkUtils.initFlinkEnv()

    // 整合Kafka
    val consumer = FlinkUtils.initKafkaFlink()

    // 测试打印
    val kafkaDataStream: DataStream[String] = env.addSource(consumer)

    val canalDs: DataStream[Canal] = kafkaDataStream.map {
      json =>
        Canal(json)
    }
    //    canalDs.print()

    val waterDs: DataStream[Canal] = canalDs.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[Canal] {

      // 当前的时间戳
      var currentTimestamp = 0L

      // 延迟的时间
      val delayTime = 2000l

      // 2 返回水印时间 窗口向后延迟多长时间 被评估
      override def getCurrentWatermark: Watermark = {
        new Watermark(currentTimestamp - delayTime)
      }

      // 比较当前元素的时间和上一个元素的时间,取最大值,防止时光倒流,获取消息真正到达的时间
      // 1消息真正到达的时间 比如窗口[5s,10s] 这时候系统时间是9s,消息时间是7s,以7s为准
      override def extractTimestamp(element: Canal, previousElementTimestamp: Long): Long = {
        currentTimestamp = Math.max(element.timestamp, previousElementTimestamp)
        currentTimestamp
      }
    })
    waterDs.print()

    val hbaseDs: DataStream[HBaseOperation] = PreprocessTask.process(waterDs)
    hbaseDs.print()

    hbaseDs.addSink(new SinkFunction[HBaseOperation] {
      override def invoke(value: HBaseOperation): Unit = {
        value.opType match {
          case "DELETE" => HBaseUtil.deleteData(value.tableName, value.rowkey, value.cfName)
          case _ => HBaseUtil.putData(value.tableName, value.rowkey, value.cfName, value.colName, value.colValue)
        }
      }
    })

    // 执行任务
    env.execute("sync-db")
  }
}
