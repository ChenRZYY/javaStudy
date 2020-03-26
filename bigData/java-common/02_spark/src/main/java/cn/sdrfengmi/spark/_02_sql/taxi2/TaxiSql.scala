package cn.sdrfengmi.spark._02_sql.taxi2

import java.util.Date

import com.esri.core.geometry.{GeometryEngine, Point, SpatialReference}
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.sql.{Dataset, Row, SparkSession}

import scala.io
import scala.util.Try

object TaxiSql {

  def main(args: Array[String]): Unit = {
    //1、创建SparkSession
    val spark = SparkSession.builder().master("local[4]").appName("taxi").getOrCreate()

    import spark.implicits._
    //2、读取数据
    val source = spark.read.option("header",true).csv("dataset/half_trip.csv")

    //3、去重 、 过滤 、列裁剪
    val filterSource: Dataset[Row] = source.selectExpr("hack_license","pickup_datetime","dropoff_datetime","pickup_longitude",
      "pickup_latitude","dropoff_longitude","dropoff_latitude")
      .distinct()
      .filter(
        """
          |hack_license is not null and hack_license!='' and
          |pickup_datetime is not null and pickup_datetime!='' and
          |dropoff_datetime is not null and dropoff_datetime!=''  and
          |pickup_longitude is not null and pickup_longitude!=''  and
          |pickup_latitude is not null and pickup_latitude!=''  and
          |dropoff_longitude is not null and dropoff_longitude!=''  and
          |dropoff_latitude is not null and dropoff_latitude!=''
        """.stripMargin)


    //注册解析时间与解析经纬度的udf函数
    spark.udf.register("parseTime",parseTime _)
    spark.udf.register("parseLocation",parseLocation _)
    spark.udf.register("hours",hours _)
    val cleanSource = filterSource.selectExpr("hack_license","parseTime(pickup_datetime) pickup_datetime",
    "parseTime(dropoff_datetime) dropoff_datetime","parseLocation(pickup_longitude) pickup_longitude",
    "parseLocation(pickup_latitude) pickup_latitude","parseLocation(dropoff_longitude) dropoff_longitude",
    "parseLocation(dropoff_latitude) dropoff_latitude")
      //过滤掉上下车时间与上下车经纬度=0的数据
      .filter("dropoff_datetime!=0 and pickup_datetime!=0 and pickup_longitude!=0 and pickup_latitude!=0 and dropoff_longitude!=0 and dropoff_latitude!=0")
      //过滤掉一次行程大于3小时的数据
      .filter("hours(pickup_datetime,dropoff_datetime) BETWEEN 0 and 3")

    //4、增加一个行政区的列
    //读取geojson文件
    val json = io.Source.fromFile("dataset/nyc-borough-boundaries-polygon.geojson").mkString
    //解析json
    val featureCollection = FeatureUtils.parseJson(json)
    //为了后续查询效率，进行排序
    import scala.collection.JavaConversions._
    val sortedFeature = featureCollection.features.sortBy(feature=>(feature.properties.boroughCode,-feature.getGeometryObject().calculateArea2D()))

    //广播行政区信息
    val featureBc = spark.sparkContext.broadcast(sortedFeature)

    /**
      * 根据经纬度查询是属于哪个行政区
      * @param dropOffX
      * @param dropOffY
      */
    def findBorough(dropOffX:Double,dropOffY:Double)={
      val boroughOption: Option[Feature] = featureBc.value.find(feature=>GeometryEngine.contains(feature.getGeometryObject(),new Point(dropOffX,dropOffY),SpatialReference.create(4326)))

      boroughOption.map(_.properties.borough).getOrElse("NA")
    }
    //将查询行政区注册为udf函数
    spark.udf.register("findBorough",findBorough _)

    //补充行政区名称
    cleanSource.selectExpr("hack_license","pickup_datetime","dropoff_datetime","pickup_longitude",
      "pickup_latitude","dropoff_longitude","dropoff_latitude","findBorough(dropoff_longitude,dropoff_latitude) borough")
      .createOrReplaceTempView("source")
    //5、计算等客时间
    //8EAF200AE9D8D5BA40319D363D971F3E     2013-01-13 08:13:00    2013-01-13 08:17:00    -73.966393     40.753353    -73.974136     40.759708 Staten Island  1
    //8EAF200AE9D8D5BA40319D363D971F3E     2013-01-13 09:32:00    2013-01-13 09:44:00    -73.972435     40.747585    -74.005524     40.728565 Staten Island  2
    //8EAF200AE9D8D5BA40319D363D971F3E     2013-01-13 10:32:00    2013-01-13 10:45:00    -73.953583     40.779373    -73.833328     40.750477 Staten Island  3
    //8EAF200AE9D8D5BA40319D363D971F3E     2013-01-13 10:49:00    2013-01-13 10:55:00    -73.998482     40.74004     -73.985199     40.742188 Staten Island  4
    //
    //AC2BD2A1D8135033F0EBC7A3AD95FD7E     2013-01-13 10:36:00    2013-01-13 10:38:00    -73.993515     40.747421    -73.983498     40.75573  Brooklyn
    //AC2BD2A1D8135033F0EBC7A3AD95FD7E     2013-01-13 10:47:00    2013-01-13 10:54:00    -73.993576     40.751373    -73.979805     40.753086 Brooklyn
    spark.sql(
      """
        |select hack_license,pickup_datetime,dropoff_datetime,borough,row_number() over(partition by hack_license order by dropoff_datetime asc) rn
        | from source
      """.stripMargin).createOrReplaceTempView("source_rn")

    spark.sql(
      """
        |select a.hack_license,a.borough,(b.pickup_datetime-a.dropoff_datetime)/1000 duration
        | from source_rn a inner join source_rn b
        | on a.hack_license =b.hack_license
        | and a.rn+1 = b.rn
        | where b.pickup_datetime-a.dropoff_datetime>0
      """.stripMargin).createOrReplaceTempView("duration_source")

    spark.sql(
      """
        |select borough,avg(duration)
        | from duration_source
        | group by borough
      """.stripMargin)
  }


  /**
    * 计算一次行程时间[小时]
    * @param pickUpTime
    * @param dropoffTime
    * @return
    */
  def hours(pickUpTime:Long,dropoffTime:Long)={
    (dropoffTime - pickUpTime)/1000/60/60
  }
  /**
    * 解析时间，转换为Long
    * @param
    * @param
    */
  def parseTime(value:String)={
    //1、创建一个时间转换对象
    val formatter = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")

    val date: Date = formatter.parse(value)

    Try(date.getTime).getOrElse(0L)
  }

  /**
    * 转换字符串类型的经纬度为double
    * @param
    * @param
    * @return
    */
  def parseLocation(value:String)={
    Try(value.toDouble).getOrElse(0.0)
  }
}
