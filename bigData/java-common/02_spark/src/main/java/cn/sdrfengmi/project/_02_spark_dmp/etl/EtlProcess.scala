package cn.sdrfengmi.project._02_spark_dmp.etl

import cn.hp.project._02_spark_dmp.utils.{ConfigUtils, DateUtils, HttpUtils, IPAddressUtils, IPLocation, KuduUtils}
import com.alibaba.fastjson.{JSON, JSONObject}
import com.maxmind.geoip.{Location, LookupService}
import com.sun.jersey.server.impl.provider.RuntimeDelegateImpl
import javax.ws.rs.ext.RuntimeDelegate
import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}

import scala.util.Try

/**
  * @Author 陈振东
  * @create 2020/3/23 15:59
  * 解析ip获取经纬度 补充道数据中
  */
object EtlProcess {
//  Logger.getLogger("org").setLevel(Level.ERROR)
  //定义数据存储的表名
  val SINK_TABLE = s"ODS_${DateUtils.getNow()}"
  RuntimeDelegate.setInstance(new RuntimeDelegateImpl)

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[4]").appName("EtlProcess")
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

    val source: DataFrame = spark.read
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("dataset/pmt.json")

    val distinctDS: Dataset[Row] = source.select('ip)
      .filter("ip is not null and ip != ''")
      .distinct()
    val sourceDS: Dataset[String] = distinctDS.as[String]
    val mapDS: DataFrame = sourceDS.map((ip: String) => {
      val service: LookupService = new LookupService(ConfigUtils.GEOLITECITY)
      val location: Location = service.getLocation(ip)
      //经度
      val longitude: Float = location.longitude
      //维度
      val latitude: Float = location.latitude

      //第一种方法,静态解析经纬度
      //      val addressUtils: IPAddressUtils = new IPAddressUtils
      //      val regionLocation: IPLocation = addressUtils.getregion(ip)
      //省份 城市
      //      val region: String = regionLocation.getRegion
      //      val city: String = regionLocation.getCity
      val url: String = ConfigUtils.PARSE_IP_URL.format(ip)
      //发起请求，定位ip所在省份城市
      val jsonResult: String = HttpUtils.get(url)
      val (region, city): (String, String) = parseJson(jsonResult)
      (ip, longitude, latitude, region, city)
    }).toDF("ip", "longitude", "latitude", "region", "city")
    mapDS.createOrReplaceTempView("ip_info")
    source.createOrReplaceTempView("source")

    //fixme 缓存小表
    spark.sql("cache table ip_info")

    val result: DataFrame = spark.sql(
      """
        |select
        | s.ip,
        |s.sessionid,
        |s.advertisersid,
        |s.adorderid,
        |s.adcreativeid,
        |s.adplatformproviderid,
        |s.sdkversion,
        |s.adplatformkey,
        |s.putinmodeltype,
        |s.requestmode,
        |s.adprice,
        |s.adppprice,
        |s.requestdate,
        |s.appid,
        |s.appname,
        |s.uuid,
        |s.device,
        |s.client,
        |s.osversion,
        |s.density,
        |s.pw,
        |s.ph,
        |f.longitude,
        |f.latitude,
        |f.region,
        |f.city,
        |s.ispid,
        |s.ispname,
        |s.networkmannerid,
        |s.networkmannername,
        |s.iseffective,
        |s.isbilling,
        |s.adspacetype,
        |s.adspacetypename,
        |s.devicetype,
        |s.processnode,
        |s.apptype,
        |s.district,
        |s.paymode,
        |s.isbid,
        |s.bidprice,
        |s.winprice,
        |s.iswin,
        |s.cur,
        |s.rate,
        |s.cnywinprice,
        |s.imei,
        |s.mac,
        |s.idfa,
        |s.openudid,
        |s.androidid,
        |s.rtbprovince,
        |s.rtbcity,
        |s.rtbdistrict,
        |s.rtbstreet,
        |s.storeurl,
        |s.realip,
        |s.isqualityapp,
        |s.bidfloor,
        |s.aw,
        |s.ah,
        |s.imeimd5,
        |s.macmd5,
        |s.idfamd5,
        |s.openudidmd5,
        |s.androididmd5,
        |s.imeisha1,
        |s.macsha1,
        |s.idfasha1,
        |s.openudidsha1,
        |s.androididsha1,
        |s.uuidunknow,
        |s.userid,
        |s.iptype,
        |s.initbidprice,
        |s.adpayment,
        |s.agentrate,
        |s.lomarkrate,
        |s.adxrate,
        |s.title,
        |s.keywords,
        |s.tagid,
        |s.callbackdate,
        |s.channelid,
        |s.mediatype,
        |s.email,
        |s.tel,
        |s.sex,
        |s.age
        | from source s left join ip_info f
        | on s.ip = f.ip
      """.stripMargin)
    //6、将结果写入kudu
    //    val context = new KuduContext(ConfigUtils.MASTER_ADDRESS, spark.sparkContext)
    //定义schema信息
    //    val schema: StructType = result.schema
    //定义主键
    val keys: Seq[String] = Seq[String]("sessionid")
    //指定表属性
    val options: CreateTableOptions = new CreateTableOptions
    import scala.collection.JavaConversions._
    //指定分区字段与分区规则
    options.addHashPartitions(keys, 3)
    //指定副本数
    options.setNumReplicas(1)
    //    KuduUtils.write(context, SINK_TABLE, schema, keys, options, result)
    result.write
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .mode(SaveMode.Overwrite).json("datasetOut/" + SINK_TABLE)
  }


  /**
    * 解析json数据得到省份 城市
    */
  def parseJson(json: String): (String, String) = {
    //{"status":"1","info":"OK","infocode":"10000","province":"河南省","city":"驻马店市","adcode":"411700","rectangle":"113.8240993,32.85120945;114.2748392,33.108106"}

    val obj: JSONObject = JSON.parseObject(json)
    val province: String = Try(obj.getString("province")).getOrElse("")
    val city: String = Try(obj.getString("city")).getOrElse("")
    (province, city)
  }

}
