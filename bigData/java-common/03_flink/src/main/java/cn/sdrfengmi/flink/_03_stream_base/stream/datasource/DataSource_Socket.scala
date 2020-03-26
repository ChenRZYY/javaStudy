package cn.sdrfengmi.flink._03_stream_base.stream.datasource

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction

object DataSource_Socket {

  def main(args: Array[String]): Unit = {

    //1. 创建流式环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 2. 构建socket数据源
    val socketDataStream: DataStream[String] = env.socketTextStream("node01",9999)

    // 3. 数据转换,空格切分
    val mapDataStream: DataStream[String] = socketDataStream.flatMap(
      line => line.split(" ")
    )

    // 4. 打印
    mapDataStream.print()
   /* mapDataStream.addSink(new RichSinkFunction[String] {

    })*/

    // 5. 执行任务
    env.execute()

  }

}
