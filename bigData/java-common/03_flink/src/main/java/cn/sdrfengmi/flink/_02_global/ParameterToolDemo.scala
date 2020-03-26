package cn.sdrfengmi.flink._02_global

import cn.hp._06_flink._03_stream_base.utils.WordCountData
import org.apache.flink.api.common.functions.RichFilterFunction
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
//import org.apache.flink.streaming.api.scala._   TODO 下面有用全局设置的时候不能导入streaming.api.scala._   会隐式报错 ???
/**
  * 全局参数
  * 1  众所周知，flink作为流计算引擎，处理源源不断的数据是其本意，但是在处理数据的过程中，往往可能需要一些参数的传递，那么有哪些方法进行参数的传递？在什么时候使用？这里尝试进行简单的总结。
  * 使用configuration
  * 使用参数的function需要继承自一个rich的function，这样才可以在open方法中获取相应的参数。filter中的 new RichFilterFunction{} open方法中使用
  *
  * 2 使用configuration虽然传递了参数，但显然不够动态，每次参数改变，都涉及到程序的变更，既然main函数能够接受参数，flink自然也提供了相应的承接的机制，即ParameterTool。
  * 如果使用ParameterTool，则在参数传递上如下
  * 如上面代码，使用parameterTool来承接main函数的参数，通过env来设置全局变量来进行分发，那么在继承了rich函数的逻辑中就可以使用这个全局参数。
  * val parameters: ParameterTool = getRuntimeContext.getExecutionConfig.getGlobalJobParameters.asInstanceOf[ParameterTool]
  *
  * 3 在上面使用configuration和parametertool进行参数传递会很方便，但是也仅仅适用于少量参数的传递，如果有比较大量的数据传递，flink则提供了另外的方式来进行，其中之一即是broadcast，这个也是在其他计算引擎中广泛使用的方法之一。
  * 广播的变量会保存在tm的内存中，这个也必然会使用tm有限的内存空间，也因此不能广播太大量的数据。
  * 使用broadcast变量
  * 方法末尾用  .withBroadcastSet(wordsToIgnore, "wordsToIgnore");
  * 获取  var stuList: List[(Int, String)]  = getRuntimeContext.getBroadcastVariable[(Int, String)]("stuDataSet").toList
  *
  * 4 那么，对于数据量更大的广播需要，要如何进行？flink也提供了缓存文件的机制，如下。
  * flink本身支持指定本地的缓存文件，但一般而言，建议指定分布式存储比如hdfs上的文件，并为其指定一个名称。
  * 使用distributedCache
  * env.registerCachedFile("dataset/globalParameter.txt", "globalParameter.txt")
  * val file: File = getRuntimeContext.getDistributedCache.getFile("globalParameter.txt")
  * flink本身支持指定本地的缓存文件，但一般而言，建议指定分布式存储比如hdfs上的文件，并为其指定一个名称。
  * 使用起来也很简单，在rich函数的open方法中进行获取。
  *
  * 5 使用connectStream
  * 上面的代码忽略了对文件内容的处理。
  * 在上面的几个方法中，应该说参数本身都是static的，不会变化，那么如果参数本身随着时间也会发生变化，怎么办？
  * 嗯，那就用connectStream，其实也是流的聚合了。
  * 使用ConnectedStream的前提当然是需要有一个动态的流，比如在主数据之外，还有一些规则数据，这些规则数据会通过Restful服务来发布，假如我们的主数据来自于kafka，
  *
  * 6 累加器 AccumulatorDemo 样例
  * flink本身提供了一些内置的accumulator:
  * IntCounter, LongCounter, DoubleCounter
  * AverageAccumulator
  * LongMaximum, LongMinimum, IntMaximum, IntMinimum, DoubleMaximum, DoubleMinimum
  * Histogram
  * 首先需要定义一个accumulator，然后在某个自定义函数中来注册它，这样在客户端就可以获取相应的的值。
  * 当然，如果内置的accumulator不能满足需求，可以自定义accumulator，只需要继承两个接口之一即可，Accumulator或者SimpleAccumulato。
  *
  *
  *
  */
object ParameterToolDemo {

  //2 使用configuration虽然传递了参数，但显然不够动态，每次参数改变，都涉及到程序的变更，既然main函数能够接受参数，flink自然也提供了相应的承接的机制，即ParameterTool。

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val params: ParameterTool = ParameterTool.fromArgs(Array("--input", "dataset/wordcount.txt", "--output", "datasetOut/globalParameter"))
    //设置全局变量
    env.getConfig.setGlobalJobParameters(params)

    //第一种方法获取
    val text = if (params.has("input")) {
      env.readTextFile(params.get("input", "dataset/wordcount.txt"))
    } else {
      env.fromElements(WordCountData.WORDS: _*)
    }
    //求和
    val result: DataStream[(String, Int)] = text.flatMap(_.toLowerCase.split("\\W+"))
      .filter(_.nonEmpty)
      .map((_, 1))
      .keyBy(0)
      .sum(1)

    //第二种获取的方式
    text.filter(new RichFilterFunction[String] {
      //        val sum: ValueState[(Long, Long)] = getRuntimeContext.getState(new ValueStateDescriptor[(Long, Long)]("average", createTypeInformation[(Long, Long)]))

      override def filter(value: String): Boolean = {
        val parameters: ParameterTool = getRuntimeContext.getExecutionConfig.getGlobalJobParameters.asInstanceOf[ParameterTool]
        print(parameters.get("input"))
        true
      }
    })


    if (params.has("output")) {
      result.writeAsText(params.get("output", "datasetOut/globalParameter"))
    } else {
      result.print()
    }


    env.execute("globalParameter example!")

  }


  def fromPropertiesFile(): Unit = {
    //    import org.apache.flink.api.java.utils.ParameterTool
    //    import java.io.FileInputStream
    //    import java.io.InputStream

    //    val propertiesFilePath = "/home/sam/flink/myjob.properties"
    //    val parameter: ParameterTool = ParameterTool.fromPropertiesFile(propertiesFilePath)
    //
    //    val propertiesFile: File = new File(propertiesFilePath)
    //    new File()
    //    val parameter: ParameterTool = ParameterTool.fromPropertiesFile(propertiesFile)
    //
    //    val propertiesFileInputStream: InputStream = new FileInputStream(file)
    //    val parameter: ParameterTool = ParameterTool.fromPropertiesFile(propertiesFileInputStream)

  }

  def connect: Unit = {

    //    DataStreamSource < String > input = (DataStreamSource) KafkaStreamFactory.getKafka08Stream(env, srcCluster, srcTopic, srcGroup);
    //    DataStream<Tuple2<String, String>> appkeyMeta = env.addSource(new AppKeySourceFunction(), "appkey")
    //    ConnectedStreams<String, Tuple2<String, String>> connectedStreams = input.connect(appkeyMeta.broadcast());
    //    DataStream<String> cleanData = connectedStreams.flatMap(new DataCleanFlatMapFunction())

    //    public class DataCleanFlatMapFunction extends RichCoFlatMapFunction<String, Tuple2<String, String>, String>{
    //      public void flatMap1(String s, Collector<String> collector){...}
    //      public void flatMap2(Tuple2<String, String> s, Collector<String> collector) {...}
    //    }

  }


}
