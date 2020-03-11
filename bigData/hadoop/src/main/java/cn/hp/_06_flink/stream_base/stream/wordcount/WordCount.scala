package cn.hp._06_flink.stream_base.stream.wordcount

import cn.hp._06_flink.stream_base.utils.WordCountData
//import com.itheima.utils.WordCountData
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._

object WordCount {

  def main(args: Array[String]): Unit = {
    val params: ParameterTool = ParameterTool.fromArgs(args)
    //环境变量
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //设置全局变量
    env.getConfig.setGlobalJobParameters(params)

    var text = if(params.has("input")){
      env.readTextFile(params.get("input"))
    }else{
      env.fromElements(WordCountData.WORDS:_*)
    }
    //求和
    val result: DataStream[(String, Int)] = text.flatMap(_.toLowerCase.split("\\W+"))
      .filter(_.nonEmpty)
      .map((_, 1))
      .keyBy(0)
      .sum(1)

    if(params.has("output")){
      result.writeAsText(params.get("output"))
    }else{
      //datastream print
      result.print()
    }
    //执行
    env.execute("wordcount example!")
  }
}
