package cn.sdrfengmi.flink._03_stream_base.stream.trans

import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, OutputTag, SplitStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.apache.log4j.{Level, Logger}
import org.junit.Test

/**
  * 按照指定条件分流
  */
class TransformationDemo {
  Logger.getLogger("org").setLevel(Level.ERROR)
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  @Test
  def SplitAndSelect: Unit = {
    env.setParallelism(1)
    // 3. 加载本地集合(1-6)
    val listDataStream: DataStream[Int] = env.fromElements(1, 2, 3, 4, 5, 6)

    // 4. 数据分流,分为奇数和偶数
    val splitStream: SplitStream[Int] = listDataStream.split {
      (num: Int) =>
        num % 2 match {
          case 0 => List("even")
          case 1 => List("odd")
        }
    }
    // 5. 数据查询
    val even: DataStream[Int] = splitStream.select("even")
    val odd: DataStream[Int] = splitStream.select("odd")
    val all: DataStream[Int] = splitStream.select("odd", "even")
    all.print()
    env.execute()
  }

  //数据分流
  @Test
  def keyBy: Unit = {
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
    sumDataStream.print()
    env.execute()
  }

  //  替换SplitAndSelect分流问题，可以参考  * 按照不同的条件分流,和keyBy的区别
  @Test
  def sideOutputTrans: Unit = {
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
