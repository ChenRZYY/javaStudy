package cn.sdrfengmi.batch_process.task

import cn.sdrfengmi.batch_process.bean.OrderRecordWide
import cn.sdrfengmi.batch_process.util.HBaseUtil
import org.apache.flink.api.scala._

/**
  * @Author 陈振东
  * @create 2020/4/17 15:35
  *
  *1. 创建样例类 MerchantCountMoney
  *         商家ID, 时间,支付金额,订单数量,
  *
  *2. 编写process方法
  *         转换 flatmap
  *         分组 groupby
  *         聚合 reduce
  *         落地保存到HBase
  *
  */
//分析后的数据结构,统计日,月,年金额
case class MerchantCountMoney(
                               var merchantId: String, //id
                               var date: String, //日期
                               var amount: Double, //订单金额
                               var count: Long //订单总数
                             )

object MerchantCountMoneyTask {

  def process(wideDataSet: DataSet[OrderRecordWide]) = {
    val mapDataSet: DataSet[MerchantCountMoney] = wideDataSet.flatMap(orderRecordWide => List(
      MerchantCountMoney(orderRecordWide.merchantId, orderRecordWide.yearMonthDay, orderRecordWide.payAmount.toDouble, 1),
      MerchantCountMoney(orderRecordWide.merchantId, orderRecordWide.yearMonth, orderRecordWide.payAmount.toDouble, 1),
      MerchantCountMoney(orderRecordWide.merchantId, orderRecordWide.year, orderRecordWide.payAmount.toDouble, 1)
    ))

    val groupDataSet: GroupedDataSet[MerchantCountMoney] = mapDataSet.groupBy(merchant => merchant.merchantId + merchant.date)

    //能用吗???
    //    val value: DataSet[MerchantCountMoney] = groupDataSet.sum("amount").andSum("count")
    //    value.print()

    // 聚合
    val reduceDataSet: DataSet[MerchantCountMoney] = groupDataSet.reduce {
      (p1, p2) => {
        MerchantCountMoney(p1.merchantId, p1.date, p1.amount + p2.amount, p1.count + p2.count)
      }
    }

    // 保存到HBase中
    reduceDataSet.collect().foreach {
      merchant => {
        // HBase相关字段
        val tableName = "analysis_merchant"
        val rowkey = merchant.merchantId + ":" + merchant.date
        val clfName = "info"

        val merchantIdColumn = "merchantId"
        val dateColumn = "date"
        val amountColumn = "amount"
        val countColumn = "count"

        HBaseUtil.putMapData(tableName, rowkey, clfName, Map(
          merchantIdColumn -> merchant.merchantId,
          dateColumn -> merchant.date,
          amountColumn -> merchant.amount,
          countColumn -> merchant.count
        ))
      }
    }

  }

}
