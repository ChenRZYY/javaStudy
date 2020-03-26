package cn.sdrfengmi._05_spark._02_sql.taxi2

import java.util.Date

import com.esri.core.geometry.{GeometryEngine, Point, SpatialReference}
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{Dataset, Row, SparkSession}

import scala.collection.mutable
import scala.io.Source
import scala.util.Try

object Taxi {

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

    val mapSource = filterSource.rdd.map(parse)
    //4、清除掉行程时间超过3小时的数据
/*    mapSource.filter(item=>{
      val hours = (item.dropOffTime - item.pickUpTime)/1000/60/60
      hours >=0 && hours<=3
    })*/
    spark.udf.register("hours",hours _)
    val cleanSource = mapSource.toDS().filter("hours(dropOffTime,pickUpTime) BETWEEN 0 AND 3")

    //读取geoJson文件
    val geoJson = Source.fromFile("dataset/nyc-borough-boundaries-polygon.geojson").mkString
    //解析geojson
    val featureCollection: FeatureCollection = FeatureUtils.parseJson(geoJson)
    //为了后续搜索的效率比较高，将行政区进行排序，将面积大的放在前面更容易命中
    //导入隐式转换将java的list转成scala的list
    import scala.collection.JavaConversions._
    val features = featureCollection.features.sortBy(feature=>(feature.properties.boroughCode,- feature.getGeometryObject().calculateArea2D()))

    //将行政区广播出去，因为所有的task后续都会用这部分数据进行行政区的搜索
    val featureBc: Broadcast[mutable.Buffer[Feature]] = spark.sparkContext.broadcast(features)

    /**
      * 通过下车经纬度从所有的行政区中查询属于哪个行政区
      * @param dropOffX
      * @param dropOffY
      */
    def findBorough(dropOffX:Double,dropOffY:Double)= {
      //从所有的行政区信息中查询当前的经纬度是属于哪个行政区
      val boroughOption: Option[Feature] = featureBc.value.find(feature=>GeometryEngine.contains(feature.getGeometryObject(),new Point(dropOffX,dropOffY), SpatialReference.create(4326)))

      boroughOption.map(feature=>feature.properties.borough).getOrElse("NA")
    }

   import org.apache.spark.sql.functions._
    /*  //注册udf函数
     val findBoroughUdf = udf(findBorough _)
     cleanSource.groupBy(findBoroughUdf('dropOffX,'dropOffY))
       .count()
       .show()*/

    def getBoroughAndTime(first:Trip,last:Trip)={
      //根据第一条数据的下车经纬度得到区域名称
      val boroughName = findBorough(first.dropOffX,first.dropOffY)
      //根据第二条数据的上车时间-第一条数据的下车时间得到等客时间
      val duration = (last.pickUpTime-first.dropOffTime)/1000
      (boroughName,duration)
    }
    // 过滤经纬度为0的数据
    cleanSource.filter("pickUpX!=0 and pickUpY!=0 and dropOffX!=0 and dropOffY!=0")
      //因为要计算等客时间，所有需要保证一个司机的所有数据在一个分区中
      .repartition('license)
      .sortWithinPartitions('license,'dropOffTime)
      .mapPartitions(it=>{
        //1、两两计算
        val slidingData: Iterator[Seq[Trip]] = it.sliding(2,1)
          .filter(arr=>arr.head.license == arr.last.license)
          .filter(_.size==2)
        //2、得到区域以及等客时间

        slidingData.map(arr=>getBoroughAndTime(arr.head,arr.last))
      }).toDF("borough","duration")
      .filter("duration>0")
      .groupBy('borough)
      .agg(avg('duration))
      .show
  }

  def hours(dropOffTime:Long,pickUpTime:Long)={
    val hours = (dropOffTime - pickUpTime)/1000/60/60
    hours
  }

  /**
    * 转换row为Trip
    * @param row
    * @return
    */
  def parse(row:Row):Trip={
    val license = row.getAs[String]("hack_license")
	
    val pickUpTime = parseTime(row,"pickup_datetime")
    val dropOffTime = parseTime(row,"dropoff_datetime")

    val pickUpX = parseLocation(row,"pickup_longitude")
    val pickUpY = parseLocation(row,"pickup_latitude")
    val dropOffX = parseLocation(row,"dropoff_longitude")
    val dropOffY = parseLocation(row,"dropoff_latitude")
    Trip(license,pickUpTime,dropOffTime,pickUpX,pickUpY,dropOffX,dropOffY)
  }

  /**
    * 解析时间，转换为Long
    * @param row
    * @param filedName
    */
  def parseTime(row:Row,filedName:String)={
    val time = row.getAs[String](filedName) //123456
    //1、创建一个时间转换对象
    val formatter = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")

    val date: Date = formatter.parse(time)

    Try(date.getTime).getOrElse(0L)
  }

  /**
    * 转换字符串类型的经纬度为double
    * @param row
    * @param fieldName
    * @return
    */
  def parseLocation(row:Row,fieldName:String)={
    Try(row.getAs[String](fieldName).toDouble).getOrElse(0.0)
  }
}
case class Trip(
                 license: String,
                 pickUpTime: Long,
                 dropOffTime: Long,
                 pickUpX: Double,
                 pickUpY: Double,
                 dropOffX: Double,
                 dropOffY: Double
               )