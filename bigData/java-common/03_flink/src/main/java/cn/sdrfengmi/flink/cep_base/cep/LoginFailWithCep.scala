package cn.sdrfengmi.flink.cep_base.cep

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala._
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.cep.scala.{CEP, PatternStream}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

object LoginFailWithCep {

  case class LoginEvent(userId: Long, ip: String, eventType: String, eventTime: Long)

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //    ProcessingTime是以operator处理的时间为准，它使用的是机器的系统时间来作为data stream的时间
    //    IngestionTime是以数据进入flink streaming data flow的时间为准
    //    EventTime是以数据自带的时间戳字段为准，应用程序需要指定如何从record中抽取时间戳字段
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(1)

    val loginEventStream: DataStream[LoginEvent] = env.fromCollection(List(
      LoginEvent(1, "192.168.0.1", "fail", 1558430842),
      LoginEvent(1, "192.168.0.2", "fail", 1558430843),
      LoginEvent(1, "192.168.0.3", "fail", 1558430844),
      LoginEvent(2, "192.168.10.10", "success", 1558430845)
    )).assignAscendingTimestamps(_.eventTime * 1000) //指定那个时间是 eventTime

    //定义匹配模式
    val loginFailPattern: Pattern[LoginEvent, LoginEvent] = Pattern.begin[LoginEvent]("begin")
    loginFailPattern
      .where(_.eventType == "fail")
      .next("next")
      .where(_.eventType == "fail")
      .within(Time.seconds(2))

    //在数据流中匹配出定义好的模式
    val patternStream: PatternStream[LoginEvent] = CEP.pattern(loginEventStream.keyBy(_.userId), loginFailPattern)
    import org.apache.flink.api.scala._
    import org.apache.flink.streaming.api.scala._

    //fixme https://blog.csdn.net/haibucuoba/article/details/97051972 参考

    //        val loginFailDataStream: Any = patternStream.select[TypeInformation[(Int,String,String)]((pattern: Map[String, Iterable[LoginEvent]]) => {
    //          val first: LoginEvent = pattern.getOrElse("begin", null).iterator.next()
    //          val second: LoginEvent = pattern.getOrElse("next", null).iterator.next()
    //          (second.userId, second.ip, second.eventType)
    //        })

    //将匹配到的复合条件的事件打印出来
    //    loginFailDataStream.print()
    //    patternStream.flatSelect()
    env.execute("Login Fail Detect job")

  }
}
