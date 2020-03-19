package cn.hp._06_flink._03_stream_base.stream.window

import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment, WindowedStream}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow
import org.junit.Test

/**
  * Global windows: Global window的范围是无限的，你需要指定触发器来触发窗口。通常来讲，每个数据按照指定的key分配到不同的窗口中，如果不指定触发器，则窗口永远不会触发。
  * Tumbling Windows: Tumbling窗口是基于特定时间创建的，他们的大小固定，窗口间不会发生重合。例如你想基于event timen每隔10分钟计算一次，这个窗口就很适合。
  * Sliding Windows: Sliding窗口的大小也是固定的，但窗口之间会发生重合，例如你想基于event time每隔1分钟，统一过去10分钟的数据时，这个窗口就很适合。
  * Session Windows: Session窗口允许我们设置一个gap时间，来决定在关闭一个session之前，我们要等待多长时间，是衡量用户活跃与否的标志。
  * WindowAll: WindowAll操作不是基于key的，是对全局数据进行的计算。由于不基于key，因此是非并行的，即并行度是1.在使用时性能会受些影响。
  */
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