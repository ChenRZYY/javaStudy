package cn.sdrfengmi.realprocess.task

import cn.sdrfengmi.realprocess.bean.ClickLogWide
import cn.sdrfengmi.realprocess.util.HBaseUtil
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

/**
  * @Author 陈振东
  * @create 2020/3/30 13:58
  *
  *         频道热点分析
  * 1. 字段转换
  * 2. 分组
  * 3. 时间窗口
  * 4. 聚合
  * 5. 落地HBase
  */
case class ChannelRealHot(var channelid: String, var visited: Long)

object ChannelRealHotTask {

  def process(clickLogWideDataStream: DataStream[ClickLogWide]) = {
    val realHostDataStream: DataStream[ChannelRealHot] = clickLogWideDataStream.map(
      clickLogWide => {
        ChannelRealHot(clickLogWide.channelID, clickLogWide.count)
      }
    )
    //分组
    val keyedStream: KeyedStream[ChannelRealHot, String] = realHostDataStream.keyBy(_.channelid)
    //都是先分组,后窗口函数操作
    val windowedStream: WindowedStream[ChannelRealHot, String, TimeWindow] = keyedStream.timeWindow(Time.seconds(3))
    //   TODO windowedStream.sum() ???
    val resultDataStream: DataStream[ChannelRealHot] = windowedStream.reduce((c1: ChannelRealHot, c2: ChannelRealHot) => {
      ChannelRealHot(c1.channelid, c1.visited + c2.visited)
    })
    // 5. 落地HBase
    resultDataStream.addSink(new SinkFunction[ChannelRealHot] {

      override def invoke(value: ChannelRealHot, context: SinkFunction.Context[_]): Unit = {
        // hbase相关字段
        val tableName = "channel"
        val clfName = "info"
        val channelIdColumn = "channelId"
        val visitedColumn = "visited"

        val rowkey = value.channelid
        // 查询HBase,获取相关记录
        val visitedValue: String = HBaseUtil.getData(tableName, rowkey, clfName, visitedColumn)
        // 创建总数的临时变量
        var totalCount: Long = 0
        if (StringUtils.isBlank(visitedValue)) {
          totalCount = value.visited
        } else {
          totalCount = visitedValue.toLong + value.visited
        }

        // 保存数据
        HBaseUtil.putMapData(tableName, rowkey, clfName, Map(
          channelIdColumn -> value.channelid,
          visitedColumn -> totalCount.toString
        ))
      }
    })

  }


}
