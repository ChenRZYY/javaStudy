package cn.hp.project._02_spark_dmp.make

import java.sql.PreparedStatement

import ch.hsr.geohash.GeoHash
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

object BusinessAreaTag {

  /*def make(row:Row)={

    var result = Map[String,Double]()
    //1、取出经纬度
    val longitude = row.getAs[Float]("longitude")
    val latitude = row.getAs[Float]("latitude")
    if(longitude!=null && latitude!=null){
    //2、如果经纬度都存在，生成geoCode
      val geoCode = GeoHash.geoHashStringWithCharacterPrecision(latitude.toDouble,longitude.toDouble,8)
      //3、根据geoCode从商圈表中查询数据
      val areas = JdbcUtils.getAreas(geoCode)
      //4、对商圈的查询结果进行切分，生成商圈标签
      areas.split(",").foreach(item=>result=result.+((s"BA_${item}",1.0)))
    }

    //5、数据返回
    result
  }*/


  /*def make(row:Row,statement:PreparedStatement)={

    var result = Map[String,Double]()
    //1、取出经纬度
    val longitude = row.getAs[Float]("longitude")
    val latitude = row.getAs[Float]("latitude")
    if(longitude!=null && latitude!=null){
      //2、如果经纬度都存在，生成geoCode
      val geoCode = GeoHash.geoHashStringWithCharacterPrecision(latitude.toDouble,longitude.toDouble,8)
      //3、根据geoCode从商圈表中查询数据
      //val areas = JdbcUtils.getAreas(geoCode)
      //4、设置参数
      statement.setString(1,geoCode)
      //5、执行查询
      val resultSet = statement.executeQuery()
      while (resultSet.next()){
        //4、对商圈的查询结果进行切分，生成商圈标签
        resultSet.getString("areas").split(",").foreach(item=>result=result.+((s"BA_${item}",1.0)))
      }
    }

    //5、数据返回
    result
  }*/

  def make(row: Row) = {

    var result = Map[String, Double]()
    //1、取出商圈信息
    val areas = row.getAs[String]("areas")

    if (StringUtils.isNotBlank(areas)) {
      areas.split(",").foreach(item => result = result.+((s"BA_${item}", 1.0)))
    }

    //5、数据返回
    result
  }
}