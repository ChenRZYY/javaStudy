package cn.sdrfengmi.flink.cep_base.state

import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector

import scala.collection.mutable.ListBuffer

/**
  * 状态编程
  */
object LoginFail {

  case class LoginEvent(userId: Long, ip: String, eventType: String, eventTime: Long)

  def main(args: Array[String]): Unit = {
    val sEnv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //设置eventTime
    sEnv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    //设置并行度为1
    sEnv.setParallelism(1)

    //导入本地样例类数据集合
    val loginEventStream = sEnv.fromCollection(List(
      LoginEvent(1, "192.168.0.1", "fail", 1558430842),
      LoginEvent(1, "192.168.0.2", "fail", 1558430843),
      LoginEvent(1, "192.168.0.3", "fail", 1558430844),
      LoginEvent(2, "192.168.10.10", "success", 1558430845)
    )).assignAscendingTimestamps(_.eventTime)
      .keyBy(_.userId)
      .process(new MatchFunction())
      .print()

    sEnv.execute("Login fail detect jon")
    //

  }

  class MatchFunction extends KeyedProcessFunction[Long, LoginEvent, LoginEvent] {
    //定义状态变量
    lazy val loginState: ListState[LoginEvent] = getRuntimeContext.getListState(new ListStateDescriptor[LoginEvent]("saved login", classOf[LoginEvent]))

    override def processElement(login: LoginEvent, ctx: KeyedProcessFunction[Long, LoginEvent, LoginEvent]#Context, out: Collector[LoginEvent]): Unit = {
      if (login.eventType == "fail") {
        loginState.add(login)
      }
      ctx.timerService().registerEventTimeTimer(login.eventTime + 2 * 1000)
    }

    override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[Long, LoginEvent, LoginEvent]#OnTimerContext, out: Collector[LoginEvent]): Unit = {

      val allLogins: ListBuffer[LoginEvent] = ListBuffer()
      import scala.collection.JavaConverters._

      //      for (login <- loginState.get) {
      //        allLogins += login
      //      }
      //TODO 为什么语法不识别
      loginState.clear()

      if (allLogins.length > 1) {
        out.collect(allLogins.head)
      }
    }
  }

}
