package cn.sdrfengmi.project._02_spark_dmp.indicators

import org.apache.kudu.client.CreateTableOptions
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import cn.hp.project._02_spark_dmp.utils.{ConfigUtils, DateUtils, KuduUtils}

/**
  * @Author 陈振东
  * @create 2020/3/23 18:00
  *        统计各省市的地域分布情况
  *        作用表:ods
  *        分组字段:group by region,city
  *        统计字段;count(1)
  */
object ProviceCityAnalysis {

  //定义数据源表名
  val SOURCE_TABLE = s"ODS_${DateUtils.getNow()}"
  //定义数据存入的表名
  val SINK_TABLE = s"provice_city_analysis_${DateUtils.getNow()}"

  def main(args: Array[String]): Unit = {

    //1、创建SparkSession
    val spark = SparkSession.builder().master("local[4]").appName("ProviceCityAnalysis")
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
      //      .option("kudu.master",ConfigUtils.MASTER_ADDRESS)
      //      .option("kudu.table",SOURCE_TABLE)
      //      .kudu
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("dataSetOut/" + SOURCE_TABLE)
    //3、去重 过滤 列裁剪
    val filterDF = source.selectExpr("region", "city")
      .filter("region is not null and region!='' and city is not null and city!=''")
    //4、统计
    filterDF.createOrReplaceTempView("ods")
    val result = spark.sql(
      """
        |select region,city,count(1) num
        | from ods
        | group by region,city
      """.stripMargin)
    //5、结果写入kudu
    //    val context = new KuduContext(ConfigUtils.MASTER_ADDRESS, spark.sparkContext)
    //定义表的schema
    val schema = result.schema
    //定义表的主键
    val keys = Seq[String]("region", "city")
    //定义表的属性
    val options = new CreateTableOptions
    //指定分区规则 分区字段 分区数 转换成java的2种方式
//    import scala.collection.JavaConversions._
//    options.addHashPartitions(keys, 3)
    import scala.collection.JavaConverters._
    options.addHashPartitions(keys.asJava, 3)
    //指定副本数
    options.setNumReplicas(1)
    //    KuduUtils.write(context, SINK_TABLE, schema, keys, options, result)

    result.coalesce(1).write.mode(SaveMode.Overwrite)
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("datasetOut/" + SINK_TABLE)
  }
}
