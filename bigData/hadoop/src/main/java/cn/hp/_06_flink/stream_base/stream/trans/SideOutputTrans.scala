package cn.hp._06_flink.stream_base.stream.trans

import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector

/**
  * 替换SplitAndSelect分流问题，可以参考
  * 按照不同的条件分流,和keyBy的区别
  */
object SideOutputTrans {

  def main(args: Array[String]): Unit = {
    //1.创建流式运行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //2.加载数据集合
    val dataset: DataStream[Int] = env.fromElements(1, 2, 3, 4, 5, 6)
    //3.sideoutput操作
    val outputTag1 = OutputTag[String]("side-output1")
    val outputTag2 = OutputTag[String]("side-output2")
    val sideoutputDatastream: DataStream[Int] = dataset.process(new ProcessFunction[Int, Int] {
      override def processElement(num: Int, ctx: ProcessFunction[Int, Int]#Context, out: Collector[Int]): Unit = {
        num % 3 match {
          case 0 => out.collect(num)
          case 1 => ctx.output(outputTag1, "outside-1-" + num)
          case 2 => ctx.output(outputTag2, "outside-2-" + num)
        }
      }
    })

    //4.打印输出
    val sideOutput1: DataStream[String] = sideoutputDatastream.getSideOutput[String](outputTag1)
    val sideOutput2: DataStream[String] = sideoutputDatastream.getSideOutput[String](outputTag2)
    sideOutput1.print()
    sideOutput2.print()
    sideoutputDatastream.print()
    //5.执行程序
    env.execute()
  }
}
