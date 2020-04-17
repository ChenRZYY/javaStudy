package cn.sdrfengmi.batch_process

import cn.sdrfengmi.batch_process.bean.{OrderRecord, OrderRecordWide}
import cn.sdrfengmi.batch_process.task.{MerchantCountMoneyTask, PreprocessTask}
import cn.sdrfengmi.batch_process.util.HBaseTableInputFormat
import org.apache.flink.api.java.tuple
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

/**
  * @Author 陈振东
  * @create 2020/4/17 13:58
  *         1 统计 mysql订单中付款方式
  *         2 统计 每人每年每月每日消费
  */
object App {

  def main(args: Array[String]): Unit = {
    //1 初始化环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    //设置并行度
    env.setParallelism(1)
    // 测试输出
    val source: DataSet[String] = env.fromCollection(List("1", "2", "3"))
    source.print()
    val tupleDataSet: DataSet[tuple.Tuple2[String, String]] = env.createInput(new HBaseTableInputFormat("mysql.pyg.orderRecord"))
    val orderRecordDataSet: DataSet[OrderRecord] = tupleDataSet.map(tuple2 => OrderRecord(tuple2.f1))

    // 数据预处理,拓宽
    val wideDataSet: DataSet[OrderRecordWide] = PreprocessTask.process(orderRecordDataSet)

    //    PaymethodMoneyCountTask.process(wideDataSet)
    MerchantCountMoneyTask.process(wideDataSet)

    //    env.execute("batch-process")
  }
}
