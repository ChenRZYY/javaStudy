package cn.hp._06_flink.stream_base.stream.window

import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment, WindowedStream}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow
import org.junit.Test

class CountTumblingWindow {

  @Test
  def countWindow1: Unit = {

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
    val countCarDataStream: DataStream[CountCar] = keyedStream.countWindow(5).sum(1)
    // 5. 打印结果
    countCarDataStream.print()
    // 6. 执行任务
    env.execute()

  }


  @Test
  def countWindow2: Unit = {
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
    //FIXME  sum的具体操作 ???
    val value: WindowedStream[CountCar, Int, GlobalWindow] = keyedStream.countWindow(5, 10)

    val countCarDataStream: DataStream[CountCar] = keyedStream.countWindow(2, 4).sum(1)
    // 5. 打印结果 ??? 什么时候需要execute
    countCarDataStream.print()
    // 6. 执行任务
    env.execute()
  }


}

/**
  *
  * @param sen    哪个红绿灯
  * @param carNum 多少辆车
  *               类写 class中一个包下能重名,放在class外面不能重名
  */
case class CountCar(sen: Int, carNum: Int)