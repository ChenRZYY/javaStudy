package cn.sdrfengmi.flink._03_stream_base.stream.sink

//import com.itheima.utils.{RedisUtil, WordCountData}
import cn.sdrfengmi.flink._03_stream_base.utils.{RedisUtil, WordCountData}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction
import org.apache.flink.streaming.connectors.redis.RedisSink

object Sink_Redis {
  def main(args: Array[String]): Unit = {
    //创建流环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //设置eventTimeshuxing
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    //设置并行度
    env.setParallelism(1)
    //统计一个wordcount 并将统计个数大于5的 存入redis
    val result = env.fromElements(WordCountData.WORDS: _*)
      .map(_.toLowerCase)
      .flatMap(_.split("\\W+"))
      .map((_, 1)).keyBy(0)
      .sum(1)
      .filter(_._2 > 5)

    val redisSink: RedisSink[(String, Int)] = RedisUtil.getRedisSink()
    result.addSink(redisSink)
    env.execute()
  }
}
