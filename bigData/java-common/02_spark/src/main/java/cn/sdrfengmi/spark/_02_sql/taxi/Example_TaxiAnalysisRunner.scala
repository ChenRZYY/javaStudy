package cn.sdrfengmi.spark._02_sql.taxi

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.io.Source

object Example_TaxiAnalysisRunner {

  def main(args: Array[String]): Unit = {

    val sparkSession: SparkSession = SparkSession.builder().appName("xiaomifeng").master("local[4]").getOrCreate()
    val taxiRaw: DataFrame = sparkSession.read
      .option("header", value = true)
      .csv("dataset/half_trip.csv")

    //    taxiRaw.show()
    //    taxiRaw.printSchema()
    import sparkSession.implicits._
    import org.apache.spark.sql.functions._

    //4 数据操作和转换
    val taxiParsed: RDD[Either[Trip, (Row, Exception)]] = taxiRaw.rdd.map(safe(parse)) //map<==safe返回函数   safe<==parse函数  safe返回是针对Map需要的函数 parse返回是针对safe入参
    //    taxiRaw.rdd.map(parse) //map 可以看出()中识别的是函数的返回函数,或者函数本身
    //        taxiParsed.filter(item => item.isRight).map(e => e.right.get._1).collect().foreach(row=>println(row.get(0))) //不合格数据
    val taxiGood: Dataset[Trip] = taxiParsed.filter(f => f.isLeft).map(either => either.left.get).toDS()
    //    val trips: Array[Trip] = taxiGood.collect().foreach(println(Tri))

    //5 绘制时长直方图
    //5.1 UDF完成时长计算,毫秒转成小时
    val hours: (Long, Long) => Long = (pickUpTime: Long, pickOffTime: Long) => {
      val duration = pickOffTime - pickUpTime
      val hours = TimeUnit.HOURS.convert(duration, TimeUnit.MILLISECONDS)
      hours //返回0,1
    }

    //定义udf函数
    val hoursUDF: UserDefinedFunction = udf(hours)
    // 5.2 进行统计 测试展示
    taxiGood.groupBy(hoursUDF('pickUpTime, 'dropOffTime) as "duration")
      .count()
      .sort("duration")
      .show()

    // 6. 根据直方图的显示, 查看数据分布后, 剪除反常数据
    sparkSession.udf.register("hours", hours)
    val taxiClean: Dataset[Trip] = taxiGood.where("hours(pickUpTime,dropOffTime) between 0 and 3")
    taxiClean.show()

    //7增加行政区信息nyc-borough-boundaries-polygon.geojson
    val geoJson: String = Source.fromFile("dataset/nyc-borough-boundaries-polygon.geojson").mkString
    val featureCollection: FeatureCollection = FeatureExtraction.parseJson(geoJson)
    val sortedFeature: List[Feature] = featureCollection.features.sortBy(feature => {
      (feature.properties("boroughCode"), -feature.getGeometry().calculateArea2D())
    })
    //7.3 广播
    val featuresBC = sparkSession.sparkContext.broadcast(sortedFeature)


  }

  //泛型的应用,函数只有一个出参一个入参, P,R
  def safe[P, R](f: P => R): P => Either[R, (P, Exception)] = {
    //方式1
    new Function[P, Either[R, (P, Exception)]] with Serializable {
      override def apply(param: P): Either[R, (P, Exception)] = {
        try {
          Left(f(param))
        } catch {
          case e: Exception => Right(param, e)
        }
      }
    }
    //方式2 但是不能序列化
    //    val function = (param: P) => {
    //      try {
    //        Left(f(param))
    //      } catch {
    //        case e: Exception => Right(param, e)
    //      }
    //    }
    //    function
  }

  def parse(row: Row): Trip = {
    val richRow = new RichRowC(row)
    val license = richRow.getAs[String]("hack_license").orNull
    val pickUpTime = parseTime(richRow, "pickup_datetime")
    val dropOffTime = parseTime(richRow, "dropoff_datetime")
    val pickUpX = parseLocation(richRow, "pickup_longitude")
    val pickUpY = parseLocation(richRow, "pickup_latitude")
    val dropOffX = parseLocation(richRow, "dropoff_longitude")
    val dropOffY = parseLocation(richRow, "dropoff_latitude")
    Trip(license, pickUpTime, dropOffTime, pickUpX, pickUpY, dropOffX, dropOffY)
  }

  //时间转换
  def parseTime(row: RichRowC, field: String): Long = {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val format: FastDateFormat = FastDateFormat.getInstance(pattern)
    val time: Option[String] = row.getAs[String](field)
    val timeOption: Option[Long] = time.map(time => format.parse(time).getTime) //输入值就是获取到的值  用map就是少了一个步if操作  把这个time时间换成格林时间
    timeOption.getOrElse(0L)
  }

  //数据转换
  def parseLocation(row: RichRowC, field: String): Double = {
    val location: Option[String] = row.getAs[String](field)
    val locationOption: Option[Double] = location.map(loc => loc.toDouble)
    locationOption.getOrElse(0.0)
  }


}

//在相同包中可以重复
class RichRowC(row: Row) {
  def getAs[T](field: String): Option[T] = {
    if (row.isNullAt(row.fieldIndex(field))) { //因为只要角标能判断
      None
    } else {
      Some(row.getAs[T](field))
    }
  }
}


//在一个包中不能重复
case class TripC(
                  license: String, //出租车id
                  pickUpTime: Long, //上车时间
                  dropOffTime: Long, //下车时间
                  pickUpX: Double, //上车经度
                  pickUpY: Double, //上车维度
                  dropOffX: Double, //下车经度
                  dropOffY: Double //下车维度
                )