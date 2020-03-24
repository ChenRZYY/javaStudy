package cn.hp.project._02_spark_dmp.tag

import ch.hsr.geohash.GeoHash
import cn.hp.project._02_spark_dmp.make.{AgeTag, AppTag, BusinessAreaTag, ChannelTag, DeviceTag, KeywordTag, RegionTag, SexTag}
import cn.hp.project._02_spark_dmp.utils.{ConfigUtils, DateUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{Row, SparkSession}

import scala.collection.mutable.ListBuffer

/**
  * @Author 陈振东
  * @create 2020/3/24 10:24
  */
object TagProcess {
  Logger.getLogger("org").setLevel(Level.ERROR)

  //定义数据读取的表
  val SOURCE_TABLE = s"ODS_${DateUtils.getNow()}"
  val BUSINESS_TABLE = "business_area"

  def main(args: Array[String]): Unit = {
    //标签列表:App[APP_]、
    // 设备[设备型号<iphone>[DX_]、设备类型<ios>[DT_]、运营商<移动>[ISP_]、联网方式<WIFI>[NW_]]、
    // 地域[省份[RG_]、城市[CT_]]、
    // 关键字[KW_]、
    // 渠道[CN_]、
    // 性别[SEX_]、
    // 年龄[AGE_]、
    // 商圈[BA_]

    //1、创建SParkSession
    val spark = SparkSession.builder().master("local[4]").appName("TagProcess")
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
    import org.apache.kudu.spark.kudu._
    //2、读取ODS数据
    val source = spark.read
      //      .option("kudu.master",ConfigUtils.MASTER_ADDRESS)
      //      .option("kudu.table",SOURCE_TABLE)
      //      .kudu
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json(s"dataSetOut/${SOURCE_TABLE}")

    val businessAreaDF = spark.read
      //      .option("kudu.master",ConfigUtils.MASTER_ADDRESS)
      //      .option("kudu.table",BUSINESS_TABLE)
      //      .kudu
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json(s"dataSetOut/${BUSINESS_TABLE}")
    //3 列裁剪,过滤,去重
    //过滤用户标识为空的数据
    val filterDF = source.filter(
      """
        |(imei is not null and imei!='') or
        |(mac is not null  and mac!='') or
        |(idfa is not null and idfa!='') or
        |(openudid is not null and openudid!='') or
        |(androidid is not null and androidid!='')
      """.stripMargin)
      .select("appid", "appname", "device", "client", "region", "city", "ispid", "ispname", "networkmannerid", "networkmannername",
        "imei", "mac", "idfa", "openudid", "androidid", "keywords", "channelid", "sex", "age", "longitude", "latitude")

    //4、将app的字段文件广播出去
    val appData: Map[String, String] = spark.read.textFile(ConfigUtils.APPID_NAME).map(line => { //spark隐式导入
      val arr: Array[String] = line.split("##")
      val appid: String = arr(0)
      val appname: String = arr(1)
      (appid, appname)
    }).collect().toMap

    //fixme 广播 缓存小表,区别什么时候用
    val appBc: Broadcast[Map[String, String]] = spark.sparkContext.broadcast(appData)
    /*.toDF("appid","appname")
         .createOrReplaceTempView("app_info")
         //缓存小表，便于广播小表
         spark.sql("cache table app_info")*/

    //5、将设备的字段文件广播出去
    val deviceData = spark.read.textFile(ConfigUtils.DEVICEDIC)
      .map(line => {
        val arr = line.split("##")
        val device = arr(0)
        val code = arr(1)
        (device, code)
      }).collect().toMap
    val deviceBc: Broadcast[Map[String, String]] = spark.sparkContext.broadcast(deviceData)

    //fixme 方式1
    /*.toDF("device","code")
        .createOrReplaceTempView("device_info")*/

    /*spark.sql("cache table device_info")

    filterDF.createOrReplaceTempView("ods")
    spark.sql(
      """
        |select a.*,b.appname,c.code client_code,d.code network_code,e.code isp_code
        | from ods a left join app_info b
        | on a.appid = b.appid
        | left join device_info c
        | on a.client = c.device
        | left join device_info d
        | on a.networkmannername = d.device
        | left join device_info e
        | on a.ispname = e.device
      """.stripMargin)*/


    //6、生成标签
    /*filterDF.rdd.map(row=>{
      //1、生成app标签
      val appTags = AppTag.make(row,appBc)
      //2、生成设备标签
      val deviceTags = DeviceTag.make(row,deviceBc)
      //3、生成地域标签
      val regionTags = RegionTag.make(row)
      //4、生成关键字标签
      val keywordTags = KeywordTag.make(row)
      //5、生成渠道标签
      val channelTag = ChannelTag.make(row)
      //6、生成性别标签
      val sexTags = SexTag.make(row)
      //7、生成年龄标签
      val ageTags = AgeTag.make(row)
      //8、生成商圈标签
      val areaTags = BusinessAreaTag.make(row)
      appTags ++ deviceTags ++ regionTags ++ keywordTags ++ channelTag ++ sexTags ++ ageTags ++ areaTags
    }).collect().foreach(x=>println(x.toBuffer))*/

    /*filterDF.rdd
        .mapPartitions(it=>{

          //1、加载驱动
          Class.forName("com.cloudera.impala.jdbc41.Driver")
          //2、创建Connection
          var connection:Connection = null
          var statement:PreparedStatement = null
          var result = List[Map[String,Double]]()
          try {

            connection = DriverManager.getConnection("jdbc:impala://hadoop03:21050/default")
            //3、创建Statement
            statement = connection.prepareStatement("select areas from business_area where geoCode=?")

            while (it.hasNext) {
              val row = it.next()

              //1、生成app标签
              val appTags: Map[String, Double] = AppTag.make(row, appBc)
              //2、生成设备标签
              val deviceTags = DeviceTag.make(row, deviceBc)
              //3、生成地域标签
              val regionTags = RegionTag.make(row)
              //4、生成关键字标签
              val keywordTags = KeywordTag.make(row)
              //5、生成渠道标签
              val channelTag = ChannelTag.make(row)
              //6、生成性别标签
              val sexTags = SexTag.make(row)
              //7、生成年龄标签
              val ageTags = AgeTag.make(row)
              //8、生成商圈标签
              val areaTags = BusinessAreaTag.make(row,statement)

              val allTags = appTags ++ deviceTags ++ regionTags ++ keywordTags ++ channelTag ++ sexTags ++ ageTags ++ areaTags
              result = result.+:(allTags)
            }
          }catch {
            case e:Exception=> result
          }finally {
            if(statement!=null)
              statement.close()
            if(connection!=null)
              connection.close()
          }
          result.toIterator
        }).collect().foreach(x=>println(x.toBuffer))*/
    //........

    filterDF.createOrReplaceTempView("ods")
    businessAreaDF.createOrReplaceTempView("business_area")
    //注册udf函数
    spark.udf.register("genGeoHashCode", genGeoHashCode _)
    //缓存小表
    spark.sql("cache table business_area")
    //补充商圈信息
    val joinDF = spark.sql(
      """
        |select o.*,b.areas
        | from ods o left join business_area b
        | on genGeoHashCode(o.longitude,o.latitude) = b.geoCode
      """.stripMargin)
    //生成标签
    //生成标签
    joinDF.rdd.map(row => {
      //1、生成app标签
      val appTags = AppTag.make(row, appBc)
      //2、生成设备标签
      val deviceTags = DeviceTag.make(row, deviceBc)
      //3、生成地域标签
      val regionTags = RegionTag.make(row)
      //4、生成关键字标签
      val keywordTags = KeywordTag.make(row)
      //5、生成渠道标签
      val channelTag = ChannelTag.make(row)
      //6、生成性别标签
      val sexTags = SexTag.make(row)
      //7、生成年龄标签
      val ageTags = AgeTag.make(row)
      //8、生成商圈标签
      val areaTags = BusinessAreaTag.make(row)
      //9、生成用户标识
      val ids: List[String] = getAllUserIds(row)
      //用户唯一标识
      val id = ids.head
      //所有标签
      val allTags = appTags ++ deviceTags ++ regionTags ++ keywordTags ++ channelTag ++ sexTags ++ ageTags ++ areaTags
      (id, (ids, allTags))
    }).repartition(1).saveAsTextFile("datasetOut/tags")


  }

  def genGeoHashCode(longitude: Float, latitude: Float): String = {
    GeoHash.geoHashStringWithCharacterPrecision(latitude.toDouble, longitude.toDouble, 8)
  }

  /**
    * 获取用户所有的非空的标识
    *
    * @param row
    */
  def getAllUserIds(row: Row) = {

    var ids = List[String]() //可以用可变集合
    //    var ids: ListBuffer[String] = ListBuffer[String]()

    //取得用户所有标识的值
    val imei = row.getAs[String]("imei")
    val mac = row.getAs[String]("mac")
    val idfa = row.getAs[String]("idfa")
    val openudid = row.getAs[String]("openudid")
    val androidid = row.getAs[String]("androidid")

    if (StringUtils.isNotBlank(imei)) {
      ids = ids.+:(imei)
    }

    if (StringUtils.isNotBlank(mac)) {
      ids = ids.+:(mac)
    }

    if (StringUtils.isNotBlank(idfa)) {

      ids = ids.+:(idfa)
    }

    if (StringUtils.isNotBlank(openudid)) {
      ids = ids.+:(openudid)
    }

    if (StringUtils.isNotBlank(androidid)) {
      ids = ids.+:(androidid)
    }

    ids
  }
}
