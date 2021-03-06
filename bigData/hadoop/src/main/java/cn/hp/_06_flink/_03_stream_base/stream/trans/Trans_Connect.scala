package cn.hp._06_flink._03_stream_base.stream.trans

import java.util.concurrent.TimeUnit

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{ConnectedStreams, DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.log4j.{Level, Logger}

/**
  * 把两个数据源合成一个
  */
object Trans_Connect {
  Logger.getLogger("org").setLevel(Level.ERROR)

  def main(args: Array[String]): Unit = {

    // 1. env
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 2. 配置数据源
    val longDataStream: DataStream[Long] = env.addSource(new MyLongSource)
    val stringDataStream: DataStream[String] = env.addSource(new MyStringSource)

    // 3. connect
    val connectedStreams: ConnectedStreams[Long, String] = longDataStream.connect(stringDataStream)

    // 4. 转换
    val ds: DataStream[Any] = connectedStreams.map(
      (line1: Long) => line1,
      (line2: String) => line2
    )

    val value: DataStream[String] = connectedStreams.map(line1 => line1.toString, line2 => line2)

    // 5. 打印数据
    ds.print().setParallelism(1)
    value.print()

    // 6. 执行任务
    env.execute()

  }
}


// 实现一个从1开始递增的数字,每隔一秒生成一次

class MyLongSource extends SourceFunction[Long] {

  // 程序运行状态
  var isRunning = true
  var count = 0L

  override def run(ctx: SourceFunction.SourceContext[Long]): Unit = {
    while (isRunning) {
      count += 1
      ctx.collect(count)
      TimeUnit.SECONDS.sleep(1)
    }
  }

  override def cancel(): Unit = {
    isRunning = false
  }
}

// 实现一个从1开始递增的数字,每隔一秒生成一次

class MyStringSource extends SourceFunction[String] {

  // 程序运行状态
  var isRunning = true
  var count = 0L

  override def run(ctx: SourceFunction.SourceContext[String]): Unit = {
    while (isRunning) {
      count += 1
      ctx.collect("str_" + count)
      TimeUnit.SECONDS.sleep(1)
    }
  }

  override def cancel(): Unit = {
    isRunning = false
  }
}