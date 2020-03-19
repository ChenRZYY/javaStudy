package cn.hp._06_flink._03_stream_base.stream.window

import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment, WindowedStream}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.junit.Test
import org.apache.log4j.{Level, Logger}

class TumblingTimeWindow {
  Logger.getLogger("org").setLevel(Level.ERROR)

  @Test
  def timeWindow: Unit = {

    // 1. env
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 2. 定义数据源  socket nc -lk 9999 [ 1,2 2,2 ]
    val socketDataStream: DataStream[String] = env.socketTextStream("server02", 2181, delimiter = '\n', 5)
    // 3. 转换数据  1,2 2,2
    val mapDataStream: DataStream[CountCar] = socketDataStream.map {
      line =>
        val strs: Array[String] = line.split(",")
        CountCar(strs(0).toInt, strs(1).toInt)
    }
    // 4. 执行统计
    // 以红绿灯进行分组
    val keyedStream: KeyedStream[CountCar, Int] = mapDataStream.keyBy(_.sen)
    // FIXME 每5s 计算一次  timeWindow 怎么详细操作???
    val value: WindowedStream[CountCar, Int, TimeWindow] = keyedStream.timeWindow(Time.seconds(5))

    val countCarDataStream: DataStream[CountCar] = keyedStream.timeWindow(Time.seconds(5)).sum(1)
    // 5. 打印结果
    countCarDataStream.print()
    // 6. 执行任务
    env.execute()

  }

  @Test
  def timeWindow2: Unit = {
    // 1. env
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 2. 定义数据源  socket nc -lk 9999 [ 1,2 2,2 ]
    val socketDataStream: DataStream[String] = env.socketTextStream("server02", 2181)
    // 3. 转换数据  1,2 2,2
    val mapDataStream: DataStream[CountCar] = socketDataStream.map {
      line =>
        val strs: Array[String] = line.split(",")
        CountCar(strs(0).toInt, strs(1).toInt)
    }
    // 4. 执行统计
    // 以红绿灯进行分组
    val keyedStream: KeyedStream[CountCar, Int] = mapDataStream.keyBy(_.sen)
    //会丢失数据
    val countCarDataStream: DataStream[CountCar] = keyedStream.timeWindow(Time.seconds(4), Time.seconds(8)).sum(1)
    // 5. 打印结果
    countCarDataStream.print()
    // 6. 执行任务
    env.execute()
  }


}
