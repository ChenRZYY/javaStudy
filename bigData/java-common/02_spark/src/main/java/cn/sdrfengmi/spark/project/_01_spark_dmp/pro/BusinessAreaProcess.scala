package cn.sdrfengmi.spark.project._01_spark_dmp.pro

import ch.hsr.geohash.GeoHash
import com.alibaba.fastjson.JSON
import org.apache.kudu.client.CreateTableOptions
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import cn.sdrfengmi.spark.project._01_spark_dmp.utils.{ConfigUtils, DateUtils, HttpUtils}
import org.apache.log4j.{Level, Logger}

import scala.util.Try

/**
  * 生成商圈库
  * 商圈，是指商店以其所在地点为中心,以key:ip  value: 地点 存储数据
  */
object BusinessAreaProcess {
  Logger.getLogger("org").setLevel(Level.ERROR)

  val SOURCE_TABLE = s"ODS_${DateUtils.getNow()}"
  //商圈库表名
  val SINK_TABLE = "business_area"

  def main(args: Array[String]): Unit = {

    //1、创建SparkSession
    val spark = SparkSession.builder().master("local[4]").appName("BusinessAreaProcess")
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
    //2、读取ODS表的数据
    import org.apache.kudu.spark.kudu._
    val source = spark.read
      //      .option("kudu.master", ConfigUtils.MASTER_ADDRESS)
      //      .option("kudu.table", SOURCE_TABLE)
      //      .kudu
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("datasetOut/" + SOURCE_TABLE)
    //3、列裁剪、去重、过滤
    val filterDF: Dataset[Row] = source.selectExpr("longitude", "latitude")
      .filter("longitude is not null and latitude is not null")
      .distinct()
    //4、读取商圈库的表，与现在的数据进行对比，将商圈库中不存在经纬度进行生成商圈
    //    val context = new KuduContext(ConfigUtils.MASTER_ADDRESS, spark.sparkContext)
    val context = null
    var result: DataFrame = null
    //判断表是否存在，如果表不存在，当天所有的经纬度都需要生成商圈库
    if (context == null) {
      //    if (!context.tableExists(SINK_TABLE)) {
      result = getBusinessAreas(filterDF, spark)
    } else {
      //如果表存在,
      //1、读取商圈表数据
      val businessDF = spark.read.option("kudu.master", ConfigUtils.MASTER_ADDRESS)
        .option("kudu.table", SINK_TABLE)
        .kudu

      //2、定义udf函数，注册udf函数
      spark.udf.register("geoHash", geoHashStringWithCharacterPrecision _)

      filterDF.createOrReplaceTempView("source")
      businessDF.createOrReplaceTempView("business")

      val whereDF = spark.sql(
        """
          |select s.longitude,s.latitude,b.geoCode,b.areas
          | from source s left join business b
          | on geoHash(s.longitude,s.latitude) = b.geoCode
        """.stripMargin)
      //没有生成过商圈的geocode ，需要重新生成
      val notExistsDF = whereDF.filter("geoCode is null").selectExpr("longitude", "latitude")
      //生成过的需要冲入插入，因为在插入数据的时候，有一个删除表过程，导致以前的数据不在
      val existsDF = whereDF.filter("geoCode is not null").selectExpr("geoCode", "areas")
      result = existsDF.union(getBusinessAreas(notExistsDF, spark))
    }
    //5、将结果写入商圈表中
    val schema = result.schema
    println(result.printSchema())
    //指定主键
    val keys = Seq[String]("geoCode", "areas")
    //指定表属性
    val options = new CreateTableOptions
    import scala.collection.JavaConversions._
    options.addHashPartitions(keys, 3)
    options.setNumReplicas(1)
    //    KuduUtils.write(context, SINK_TABLE, schema, keys, options, result)
    result.coalesce(1).write.mode(SaveMode.Overwrite)
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("datasetOut/" + SINK_TABLE)

  }

  /**
    * 获取商圈列表
    *
    * @param data
    * @param spark
    * @return
    */
  def getBusinessAreas(data: DataFrame, spark: SparkSession): DataFrame = {
    import spark.implicits._
    data.as[(Double, Double)].map(item => {
      //获取http请求的url
      val longitude = item._1 //经度
      val latitude = item._2 //纬度
      val url = ConfigUtils.BUSINESS_AREA.format(s"${longitude},${latitude}")
      //发起http请求，获取经纬度对应的商圈
      val jsonResult = HttpUtils.get(url)
      //解析json获取商圈列表，多个商圈以,分割
      val areas = Try(parseJson(jsonResult)).getOrElse("")
      //17.123456  23.456789   中关村,西苑
      //17.123455  23.456788   中关村,西苑
      //将经纬度进行geo编码，比较近的经纬度生成的geo编码是一样的
      val geoCode = geoHashStringWithCharacterPrecision(longitude.asInstanceOf[Float], latitude.asInstanceOf[Float])

      (geoCode, areas)
    }).toDF("geoCode", "areas")
      //解析json有可能报错出现area为空，这些数据不用保存
      .filter("areas !='' and areas is not null and geoCode is not null and geoCode!=''")
      //比较进度经纬度生成的geoCode是一样的，商圈列表应该也是一样的，只需要保存一份就可以了
      .distinct()
  }

  /**
    * 根据经纬度获取geo编码
    *
    * @param longitude
    * @param latitude
    * @return
    */
  def geoHashStringWithCharacterPrecision(longitude: Float, latitude: Float): String = {
    GeoHash.geoHashStringWithCharacterPrecision(latitude.toDouble, longitude.toDouble, 8)
  }

  /**
    * 解析json
    *
    * @param json
    */
  def parseJson(json: String) = {
    //{
    //	"status": "1",
    //	"regeocode": {
    //		"addressComponent": {
    //			"city": [],
    //			"province": "北京市",
    //			"adcode": "110108",
    //			"district": "海淀区",
    //			"towncode": "110108015000",
    //			"streetNumber": {
    //				"number": "5号",
    //				"location": "116.310454,39.9927339",
    //				"direction": "东北",
    //				"distance": "94.5489",
    //				"street": "颐和园路"
    //			},
    //			"country": "中国",
    //			"township": "燕园街道",
    //			"businessAreas": [{
    //				"location": "116.303364,39.97641",
    //				"name": "万泉河",
    //				"id": "110108"
    //			}, {
    //				"location": "116.314222,39.98249",
    //				"name": "中关村",
    //				"id": "110108"
    //			}, {
    //				"location": "116.294214,39.99685",
    //				"name": "西苑",
    //				"id": "110108"
    //			}],
    //			"building": {
    //				"name": "北京大学",
    //				"type": "科教文化服务;学校;高等院校"
    //			},
    //			"neighborhood": {
    //				"name": "北京大学",
    //				"type": "科教文化服务;学校;高等院校"
    //			},
    //			"citycode": "010"
    //		},
    //		"formatted_address": "北京市海淀区燕园街道北京大学"
    //	},
    //	"info": "OK",
    //	"infocode": "10000"
    //}
    import scala.collection.JavaConversions._
    JSON.parseObject(json)
      .getJSONObject("regeocode")
      .getJSONObject("addressComponent")
      .getJSONArray("businessAreas")
      .toJavaList(classOf[BusinessArea])
      .map(_.name)
      .mkString(",")
    //万泉河,西苑,中关村
  }
}

case class BusinessArea(location: String, name: String, id: String)
