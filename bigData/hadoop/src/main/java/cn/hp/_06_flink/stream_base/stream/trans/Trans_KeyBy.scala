package cn.hp._06_flink.stream_base.stream.trans

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._

/**
  * 数据分流
  */
object Trans_KeyBy {

  def main(args: Array[String]): Unit = {

    // 1. 获取流环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 2. 设置并行度
    env.setParallelism(1)

    // 3. 获取Socket连接
    val socketDataStream: DataStream[String] = env.socketTextStream("server02", 2181)

    // 4. 转换 以空格切分单词,单词计数1,以单词分组,进行求和
    val wordCountDataStream: KeyedStream[(String, Int), String] = socketDataStream.
      flatMap(_.split("\\s")).
      map((_, 1)).
      keyBy(_._1) //按照下标方式分流,第一个元素分流

    wordCountDataStream.print()

    val sumDataStream: DataStream[(String, Int)] = wordCountDataStream.sum(1)
    //sum(1) //FIXME 只有keyedStream 才能用sum ???  map的用法???

    //    val func: (String, Int) => (String, Int) = (name: String, num: Int) => {
    //      (name, num)
    //    }
    //
    //    val value: DataStream[Int] = wordCountDataStream.map(new MapFunction[(String, Int), Int] {
    //      override def map(t: (String, Int)): Int = {
    //        t._2
    //      }
    //    })
    //    value.map(k => k + 1)

    // 5. 打印数据
    sumDataStream.print()

    // 6. 执行任务
    env.execute()

  }

}
