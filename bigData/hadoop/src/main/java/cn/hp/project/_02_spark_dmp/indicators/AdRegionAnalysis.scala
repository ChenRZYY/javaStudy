package cn.hp.project._02_spark_dmp.indicators

import org.apache.kudu.client.CreateTableOptions
import org.apache.spark.sql.{SaveMode, SparkSession}
import cn.hp.project._02_spark_dmp.utils.{ConfigUtils, DateUtils, KuduUtils}

/**
  * @Author 陈振东
  * @create 2020/3/23 17:47
  *         广告投放的地域分布情况统计
  *         作用表:ods
  *         分组字段:group by region,city
  *         统计字段;sum
  */
object AdRegionAnalysis {
  //定义数据的原表
  val SOURCE_TABLE = s"ODS_${DateUtils.getNow()}"
  //定义数据存入的表名
  val SINK_TABLE = s"ad_region_analysis_${DateUtils.getNow()}"

  def main(args: Array[String]): Unit = {

    //1、创建SparkSession
    val spark = SparkSession.builder().master("local[4]").appName("AdRegionAnalysis")
      .config("spark.sql.autoBroadcastJoinThreshold", ConfigUtils.SPARK_SQL_AUTOBROADCASTJOINTHRESHOLD)
      .config("spark.sql.shuffle.partitions", ConfigUtils.SPARK_SQL_SHUFFLE_PARTITIONS)
      .config("spark.shuffle.compress", ConfigUtils.SPARK_SHUFFLE_COMPRESS)
      .config("spark.shuffle.io.maxRetries", ConfigUtils.SPARK_SHUFFLE_IO_MAXRETRIES)
      .config("spark.shuffle.io.retryWait", ConfigUtils.SPARK_SHUFFLE_IO_RETRYWAIT)
      .config("spark.broadcast.compress", ConfigUtils.SPARK_BROADCAST_COMPRESS)
      .config("spark.serializer", ConfigUtils.SPARK_SERIALIZER)
      .config("spark.memory.fraction", ConfigUtils.SPARK_MEMORY_FRACTION)
      .config("spark.memory.storageFraction", ConfigUtils.SPARK_MEMORY_STORAGEFRACTION)
      .config("spark.default.parallelism", ConfigUtils.SPARK_DEFAULT_PARALLELISM)
      .config("spark.speculation", ConfigUtils.SPARK_SPECULATION)
      .config("spark.speculation.multiplier", ConfigUtils.SPARK_SPECULATION_MULTIPLIER)
      .getOrCreate()
    //2、读取ODS数据
    import org.apache.kudu.spark.kudu._
    val source = spark.read
      //      .option("kudu.master", ConfigUtils.MASTER_ADDRESS)
      //      .option("kudu.table", SOURCE_TABLE)
      //      .kudu
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("dataSetOut/" + SOURCE_TABLE)
    //3、列裁剪、过滤、去重
    val filterDF = source.selectExpr("adplatformproviderid", "requestmode", "processnode", "iseffective", "isbilling",
      "isbid", "iswin", "adorderid", "adcreativeid", "region", "city", "winprice", "adpayment")
      .filter("region is not null and region!='' and city is not null and city!=''")
    //4、统计指标
    filterDF.createOrReplaceTempView("ods")
    //4.1、统计原始请求数、有效请求、广告请求、参数竞价、竞价成功、展示量、点击率、广告成本、广告消费
    spark.sql(
      """
        |select region,city,
        |  sum(case when requestmode=1 and processnode>=1 then 1 else 0 end) org_request,
        |  sum(case when requestmode=1 and processnode>=2 then 1 else 0 end) valid_request,
        |  sum(case when requestmode=1 and processnode=3 then 1 else 0 end) ad_request,
        |  sum(case when adplatformproviderid>=100000 and iseffective=1 and isbilling=1 and isbid=1 and adorderid!=0 then 1 else 0 end)  join_num,
        |  sum(case when adplatformproviderid>=100000 and iseffective=1 and isbilling=1 and iswin=1 then 1 else 0 end) bid_success_num,
        |  sum(case when requestmode=2 and iseffective=1 then 1 else 0 end) advertisers_show_num,
        |  sum(case when requestmode=3 and iseffective=1 then 1 else 0 end) advertisers_click_num,
        |  sum(case when requestmode=2 and iseffective=1 and isbilling=1 then 1 else 0 end) media_show_num,
        |  sum(case when requestmode=3 and iseffective=1 and isbilling=1 then 1 else 0 end) media_click_num,
        |  sum(case when adplatformproviderid>=100000 and iseffective=1 and isbilling=1 and iswin=1 and adorderid>200000 and adcreativeid>200000 then winprice/1000 else 0 end) ad_comsumention,
        |  sum(case when adplatformproviderid>=100000 and iseffective=1 and isbilling=1 and iswin=1 and adorderid>200000 and adcreativeid>200000 then adpayment/1000 else 0 end) ad_cost
        | from ods
        | group by region,city
      """.stripMargin).createOrReplaceTempView("tmp")
    //4.2、通过上一步的结果，计算竞价成功率、点击率
    val result = spark.sql(
      """
        |select *,bid_success_num/join_num bid_success_rat,media_click_num/media_show_num media_click_rat
        |   from tmp
      """.stripMargin)
    //5、结果写入kudu
    //创建kuduContext
    //    val context = new KuduContext(ConfigUtils.MASTER_ADDRESS, spark.sparkContext)
    //定义表的schema
    val schema = result.schema
    //定义主键字段
    val keys = Seq[String]("region", "city")
    //定义表的属性
    val options = new CreateTableOptions
    //设置分区规则 分区字段 分区数
    import scala.collection.JavaConversions._
    options.addHashPartitions(keys, 3)
    //设置副本数
    options.setNumReplicas(1)
    //    KuduUtils.write(context, SINK_TABLE, schema, keys, options, result)

    result.coalesce(1).write.mode(SaveMode.Overwrite)
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("dataSetOut/" + SINK_TABLE)

  }

}
