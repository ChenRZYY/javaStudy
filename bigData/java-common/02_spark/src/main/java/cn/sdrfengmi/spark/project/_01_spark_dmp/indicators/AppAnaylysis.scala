package cn.sdrfengmi.spark.project._01_spark_dmp.indicators

import org.apache.kudu.client.CreateTableOptions
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import cn.sdrfengmi.spark.project._01_spark_dmp.utils.{ConfigUtils, DateUtils}

/**
  * @Author Haishi
  * @create 2020/3/23 17:55
  *         广告投放的APP分布情况统计
  *         作用表:ods
  *         分组字段:group by appid,appname
  *         统计字段;sum
  */
object AppAnaylysis {

  //定义数据读取的原表
  val SOURCE_TABLE = s"ODS_${DateUtils.getNow()}"

  val SINK_TABLE = s"app_analysis_${DateUtils.getNow()}"

  def main(args: Array[String]): Unit = {

    //1、创建SparkSession
    val spark = SparkSession.builder().master("local[4]").appName("AppAnaylysis")
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
    import spark.implicits._
    //2、读取数据
    import org.apache.kudu.spark.kudu._
    val source = spark.read
      //      .option("kudu.master", ConfigUtils.MASTER_ADDRESS)
      //      .option("kudu.table", SOURCE_TABLE)
      //      .kudu
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("dataSetOut/" + SOURCE_TABLE)
    //3、过滤 列裁剪
    val selectDF = source.selectExpr("appid", "appname", "adplatformproviderid", "requestmode", "processnode", "iseffective", "isbilling",
      "isbid", "iswin", "adorderid", "adcreativeid", "winprice", "adpayment")

    //4、将appName为空的数据补充
    //4.1、读取appId与appname的字段文件
    val appDS: Dataset[String] = spark.read.textFile(ConfigUtils.APPID_NAME)
    //4.2、对数据进行切割，返回(appid,appname)
    val mapDS = appDS.map(item => {
      val arr = item.split("##")
      val appid = arr(0)
      val appname = arr(1)
      (appid, appname)
    })
    //4.3、将数据转为DF，与源数据join，补充appname
    mapDS.toDF("appid", "appname").createOrReplaceTempView("app_info")
    selectDF.createOrReplaceTempView("source")
    //缓存小表
    spark.sql("cache table app_info")

    spark.sql(
      """
        |select s.adplatformproviderid,s.appid,a.appname,s.requestmode,s.processnode,s.iseffective,
        | s.isbilling,s.isbid,s.iswin,s.adorderid,s.adcreativeid,s.winprice,s.adpayment
        |  from source s left join app_info a
        |  on s.appid = a.appid
      """.stripMargin).createOrReplaceTempView("ods")
    //5、指标统计
    //5.1 、统计原始请求、有效请求、广告请求、参与竞价、竞价成功、广告成本、广告消费、展示量、点击量
    spark.sql(
      """
        |select appid,appname,
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
        | group by appid,appname
      """.stripMargin).createOrReplaceTempView("tmp")
    //5.2、通过上一步的结果，计算竞价成功率、点击率
    val result = spark.sql(
      """
        |select *,bid_success_num/join_num bid_success_rat,media_click_num/media_show_num media_click_rat
        |   from tmp
      """.stripMargin)

    //6、写入kudu
    //    val context = new KuduContext(ConfigUtils.MASTER_ADDRESS, spark.sparkContext)
    //指定shema
    val schema = result.schema
    //指定主键
    val keys = Seq[String]("appid")
    //指定表的属性
    val options = new CreateTableOptions
    //指定表的分区规则 分区字段 分区数
    import scala.collection.JavaConversions._
    options.addHashPartitions(keys, 3)
    //指定副本数
    options.setNumReplicas(1)
    //    KuduUtils.write(context, SINK_TABLE, schema, keys, options, result)

    result.coalesce(1).write.mode(SaveMode.Overwrite)
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("dataSetOut/" + SINK_TABLE)
  }
}
