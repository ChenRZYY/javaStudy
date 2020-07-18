package cn.sdrfengmi.spark._04_streaming

import cn.sdrfengmi.spark._03_flume_kafka.LoggerLevels
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.junit.Test

import scala.collection.mutable.ArrayBuffer

object _03_SparkStreamingTransformation {

  /**
    * Transformations on DStreams
    * Transformation	含义
    * map(func)	通过函数func传递源DStream的每个元素，返回一个新的DStream。
    * flatMap(func)	类似于map，但是每个输入项可以映射到0或多个输出项。
    * filter(func)	通过只选择func返回true的源DStream的记录来返回一个新的DStream。
    * repartition(numPartitions)	重分区,通过创建或多或少的分区来更改此DStream中的并行度级别。
    * union(otherStream)	返回一个新的DStream，它包含源DStream和其他DStream中的元素的联合。
    * count()	通过计算源DStream的每个RDD中的元素数量，返回一个新的单元素RDD DStream。
    * reduce(func)	使用func函数(函数接受两个参数并返回一个参数)聚合源DStream的每个RDD中的元素，从而返回单元素RDDs的新DStream。这个函数应该是结合律和交换律的，这样才能并行计算。
    * countByValue()	当对K类型的元素的DStream调用时，返回一个新的(K, Long)对的DStream，其中每个键的值是它在源DStream的每个RDD中的频率。
    * reduceByKey(func, [numTasks])	当对(K, V)对的DStream调用时，返回一个新的(K, V)对的DStream，其中每个键的值使用给定的reduce函数进行聚合。注意:默认情况下，这将使用Spark的默认并行任务数量(本地模式为2，在集群模式下，该数量由config属性Spark .default.parallelism决定)来进行分组。我们可以传递一个可选的numTasks参数来设置不同数量的任务。
    * join(otherStream, [numTasks])	当调用两个(K, V)和(K, W)对的DStream时，返回一个新的(K， (V, W))对的DStream，其中包含每个Key的所有元素对。
    * cogroup(otherStream, [numTasks])	当调用(K, V)和(K, W)对的DStream时，返回一个新的(K, Seq[V]， Seq[W])元组DStream。
    * transform(func)	通过将RDD-to-RDD函数应用于源DStream的每个RDD，返回一个新的DStream。它可以用于应用DStream API中没有公开的任何RDD操作。例如将数据流中的每个批处理与另一个数据集连接的功能并不直接在DStream API中公开。但是你可以很容易地使用transform来实现这一点。这带来了非常强大的可能性。例如，可以通过将输入数据流与预先计算的垃圾信息(也可能是使用Spark生成的)结合起来进行实时数据清理
    * updateStateByKey(func)	返回一个新的“state”DStream，其中每个Key的状态通过将给定的函数应用于Key的前一个状态和Key的新值来更新。这可以用于维护每个Key的任意状态数据。要使用它，您需要执行两个步骤:
    * (1).定义状态——状态可以是任意数据类型;
    * (2).定义状态更新函数——用函数指定如何使用输入流中的前一个状态和新值更新状态。
    *
    * Window Operations(窗口操作)
    * window(windowLength, slideInterval)	返回一个新的DStream，它是基于源DStream的窗口批次计算的。
    * countByWindow(windowLength, slideInterval)	返回流中元素的滑动窗口计数。
    * reduceByWindow(func, windowLength, slideInterval)	返回一个新的单元素流，该流是使用func在滑动间隔上聚合流中的元素创建的。这个函数应该是结合律和交换律的，这样才能并行地正确计算。
    * reduceByKeyAndWindow(func, windowLength, slideInterval, [numTasks])	当对(K, V)对的DStream调用时，返回一个新的(K, V)对的DStream，其中每个Key的值使用给定的reduce函数func在滑动窗口中分批聚合。注意:默认情况下，这将使用Spark的默认并行任务数量(本地模式为2，在集群模式下，该数量由config属性Spark .default.parallelism决定)来进行分组。您可以传递一个可选的numTasks参数来设置不同数量的任务。
    * reduceByKeyAndWindow(func, invFunc, windowLength, slideInterval, [numTasks])	上面reduceByKeyAndWindow()的一个更有效的版本，其中每个窗口的reduce值是使用前一个窗口的reduce值增量计算的。这是通过减少进入滑动窗口的新数据和“反向减少”离开窗口的旧数据来实现的。例如，在窗口滑动时“添加”和“减去”键的计数。但是，它只适用于“可逆约简函数”，即具有相应“逆约简”函数的约简函数(取invFunc参数)。与reduceByKeyAndWindow类似，reduce任务的数量可以通过一个可选参数进行配置。注意，必须启用checkpoint才能使用此操作。
    * countByValueAndWindow(windowLength, slideInterval, [numTasks])	当对(K, V)对的DStream调用时，返回一个新的(K, Long)对的DStream，其中每个Key的值是它在滑动窗口中的频率。与reduceByKeyAndWindow类似，reduce任务的数量可以通过一个可选参数进行配置。
    *
    * Output Operations on DStreams(输出操作)
    * print()	在运行流应用程序的驱动程序节点上打印DStream中每批数据的前10个元素。这对于开发和调试非常有用。这在Python API中称为pprint()。
    * saveAsTextFiles(prefix, [suffix])	将此DStream的内容保存为文本文件。每个批处理间隔的文件名是根据前缀和后缀生成的:“prefix- time_in_ms [.suffix]”。
    * saveAsObjectFiles(prefix, [suffix])	将此DStream的内容保存为序列化Java对象的sequencefile。每个批处理间隔的文件名是根据前缀和后缀生成的:“prefix- time_in_ms [.suffix]”。这在Python API中是不可用的。
    * saveAsHadoopFiles(prefix, [suffix])	将这个DStream的内容保存为Hadoop文件。每个批处理间隔的文件名是根据前缀和后缀生成的:“prefix- time_in_ms [.suffix]”。这在Python API中是不可用的。
    * foreachRDD(func)	对流生成的每个RDD应用函数func的最通用输出操作符。这个函数应该将每个RDD中的数据推送到外部系统，例如将RDD保存到文件中，或者通过网络将其写入数据库。请注意，函数func是在运行流应用程序的驱动程序进程中执行的，其中通常会有RDD操作，这将强制流RDDs的计算。在func中创建远程连接时可以使用foreachPartition 替换foreach操作以降低系统的总体吞吐量。
    */
  //DStream可以通过transform做RDD到RDD的任意操作。
  @Test
  def streamingToDataframe(): Unit = {

    val brokers = "hadoop01:9092,hadoop02:9092,hadoop03:9092"

    val sparkconf = new SparkConf().setMaster("local[2]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkconf, Seconds(10))
    val accumlators = ssc.sparkContext.accumulator(0)
    val topics = Set("spark11")
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "*g",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    var kafkaStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, ConsumerStrategies.Subscribe[String, String](topics, kafkaParams))
    var events: DStream[String] = kafkaStream.map(record => record.value())
    events.foreachRDD((rdd: RDD[String]) => {
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._
      rdd.map(pair => (dataArray(pair))).map(v => (v(0), v(1), v(2))).toDF(colNames = "PROID", "KEY", "VALUE").registerTempTable("speedtable")
      val dataframe = sqlContext.sql("select * from  speedtable ")
      dataframe.show()
      println(dataframe.count())
    })

    ssc.start()
    ssc.awaitTermination()
  }

  def dataArray(pair: String): ArrayBuffer[String] = {
    import com.alibaba.fastjson.JSONObject
    //    val JsonObject=JSONObject.fromObject(pair)
    //    val value: JSONObject = JSONObject.parseObject(pair)
    //    JSONObject.SecureObjectInputStream
    val value: JSONObject = new JSONObject()
    val array: ArrayBuffer[String] = ArrayBuffer(value.getString("A"), value.get("B").toString, value.get("C").toString)
    array
  }

  @Test
  def TransformDemo(): Unit = {
    //设置日志级别
    Logger.getLogger("org").setLevel(Level.WARN)
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(2))

    //创建一个黑名单的RDD
    val blackRDD = ssc.sparkContext.parallelize(Array(("zs", true), ("lisi", true)))
    //通过socket从nc中获取数据
    val linesDStream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 6666)

    //过滤黑名单用户发言
    // zs sb sb sb sb
    // lisi fuck fuck fuck
    // jack hello
    val mapDStream: DStream[(String, String)] = linesDStream.map(x => {
      val info = x.split(" ")
      (info(0), info.toList.tail.mkString(" "))
    })

    val transformDS: DStream[(String, (String, Option[Boolean]))] = mapDStream.transform((rdd: RDD[(String, String)]) => { //transform是一个RDD->RDD的操作，所以返回值必须是RDD
      /**
        * 经过leftouterjoin操作之后，产生的结果如下：
        * (zs,(sb sb sb sb),Some(true)))
        * (lisi,(fuck fuck fuck),some(true)))
        * (jack,(hello,None))
        */
      val joinRDD: RDD[(String, (String, Option[Boolean]))] = rdd.leftOuterJoin(blackRDD)
      //如果是Some(true)的，说明就是黑名单用户，如果是None的，说明不在黑名单内，把非黑名单的用户保留下来
      val filterRDD = joinRDD.filter(x => x._2._2.isEmpty)
      filterRDD
    })

    transformDS.map(x => (x._1, x._2._1)).print()

    ssc.start()
    ssc.awaitTermination()

  }

  @Test
  def map(): Unit = {
    LoggerLevels.setStreamingLogLevels()
    val conf = new SparkConf().setAppName("_03_SparkStreamingTransformation").setMaster("local[2]")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(5))
    val dStream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)
    val value: DStream[String] = dStream.flatMap(_.split(" "))
    val value2: DStream[(String, Int)] = dStream.map(lines => (lines, 1))

    value.reduceByWindow(_ + "-" + _, Seconds(3), Seconds(1))
    //        value2.reduceByWindow((param:[(String,int),(String,int)])=>(param._1._1,param._2._2),Seconds( 3) , Seconds( 1 ))

  }

  def getc[U](body: => U): U = {
    body
  }

}
