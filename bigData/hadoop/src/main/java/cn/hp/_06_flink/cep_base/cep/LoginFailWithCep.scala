package cn.hp._06_flink.cep_base.cep

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala._
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.cep.scala.{CEP, PatternStream}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

object LoginFailWithCep {

  case class LoginEvent(userId: Long, ip: String, eventType: String, eventTime: Long)

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(1)

    val loginEventStream = env.fromCollection(List(
      LoginEvent(1, "192.168.0.1", "fail", 1558430842),
      LoginEvent(1, "192.168.0.2", "fail", 1558430843),
      LoginEvent(1, "192.168.0.3", "fail", 1558430844),
      LoginEvent(2, "192.168.10.10", "success", 1558430845)
    )).assignAscendingTimestamps(_.eventTime * 1000)

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

    //    val loginFailDataStream: Any = patternStream.select[TypeInformation[(Int,String,String)]((pattern: Map[String, Iterable[LoginEvent]]) => {
    //      val first: LoginEvent = pattern.getOrElse("begin", null).iterator.next()
    //      val second: LoginEvent = pattern.getOrElse("next", null).iterator.next()
    //      (second.userId, second.ip, second.eventType)
    //    })

    //将匹配到的复合条件的事件打印出来
    //    loginFailDataStream.print()
    env.execute("Login Fail Detect job")

  }
}
