package cn.sdrfengmi.project._02_spark_dmp.history

import cn.hp.project._02_spark_dmp.agg.AggTag
import cn.hp.project._02_spark_dmp.graphx.UserGraphx
import cn.hp.project._02_spark_dmp.utils.{ConfigUtils, DateUtils}
import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * @Author 陈振东
  * @create 2020/3/24 15:52
  */
object HistoryUnionCurrent {
  val SINK_TABLE = s"tags_${DateUtils.getYesterDay()}"

  /**
    * 1、历史数据中也有标签和权重，对历史数据的标签权重进行衰减
    * 公式: 当前权重 = 历史权重* exp（-衰减系数 * 间隔时间）
    * 当前我们的公式: 当前权重 = 历史权重 * 衰减系数[每种不同的标签，衰减系数都不一样]
    *
    * 2、将历史数据与当天的数据合并 [union]
    *
    * 3、将合并后的数据进行用户统一识别 []
    *
    * 4、标签聚合
    */

  def union(currentTags: RDD[(String, (List[String], Map[String, Double]))], spark: SparkSession, context: KuduContext) = {

    /*import spark.implicits._
    //1、伪造历史数据
    val currentDF = currentTags.map{
      case (userid,(alluserids,tags))=>
        val idsStr = alluserids.mkString("#")
        val tagStr = tags.mkString("#")
        (userid,idsStr,tagStr)
    }.toDF("userid","ids","tags")
    //指定表的schema信息
    val schema = currentDF.schema
    //指定主键
    val keys = Seq[String]("userid")
    //指定表的属性
    val options = new CreateTableOptions
    import scala.collection.JavaConversions._
    //指定分区规则 分区字段 分区数
    options.addHashPartitions(keys,3)
    //指定副本数
    options.setNumReplicas(1)

    KuduUtils.write(context,SINK_TABLE,schema,keys,options,currentDF)*/
    //2、将历史数据读取出来
    import org.apache.kudu.spark.kudu._
    val historyDF = spark.read.option("kudu.master", ConfigUtils.MASTER_ADDRESS)
      .option("kudu.table", SINK_TABLE)
      .kudu
    //3、对历史数据进行标签衰减
    val historyData = historyDF.rdd.map(row => {
      val userid = row.getAs[String]("userid")
      val idsStr = row.getAs[String]("ids")
      val allUserIds = idsStr.split("#").toList

      val tagStr = row.getAs[String]("tags")
      //KW_时政 -> 2.0#BA_北京->1
      val tags: Map[String, Double] = tagStr.split("#").map(item => {
        val arr = item.split(" -> ")
        val tagName = arr(0)
        //对历史数据的权重进行衰减
        val attr = arr(1).toDouble * ConfigUtils.ATTNU.toDouble
        (tagName, attr)
      }).toMap
      (userid, (allUserIds, tags))
    })
    //4、将衰减之后的数据与当天的数据进行合并
    val allData: RDD[(String, (List[String], Map[String, Double]))] = historyData.union(currentTags)
    //5、将合并后的数据进行统一用户识别
    val graphed = UserGraphx.graph(allData)
    //6、将识别后的数据进行标签聚合
    val agged: RDD[(String, (List[String], Map[String, Double]))] = AggTag.agg(graphed)
    //7、返回聚合结果
    agged
  }
}
