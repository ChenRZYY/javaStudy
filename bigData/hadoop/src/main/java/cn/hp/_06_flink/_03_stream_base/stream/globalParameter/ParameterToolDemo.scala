package cn.hp._06_flink._03_stream_base.stream.globalParameter

import cn.hp._06_flink._03_stream_base.utils.WordCountData
//import com.itheima.utils.WordCountData
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._

object ParameterToolDemo {

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val params: ParameterTool = ParameterTool.fromArgs(Array("--input", "dataset/globalParameter.txt", "--output", "datasetOut/globalParameter"))
    //设置全局变量
    env.getConfig.setGlobalJobParameters(params)

    val text = if (params.has("input")) {
      env.readTextFile(params.get("input"))
    } else {
      env.fromElements(WordCountData.WORDS: _*)
    }
    //求和
    val result: DataStream[(String, Int)] = text.flatMap(_.toLowerCase.split("\\W+"))
      .filter(_.nonEmpty)
      .map((_, 1))
      .keyBy(0)
      .sum(1)

    if (params.has("output")) {
      result.writeAsText(params.get("output"))
    } else {
      result.print()
    }
    env.execute("globalParameter example!")
  }
}
