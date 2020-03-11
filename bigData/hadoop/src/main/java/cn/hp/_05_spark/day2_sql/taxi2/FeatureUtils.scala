package cn.hp._05_spark.day2_sql.taxi2

import java.util

import com.alibaba.fastjson.serializer.SerializeFilter
import com.alibaba.fastjson.{JSON, JSONObject}
import com.esri.core.geometry.{Geometry, GeometryEngine}

case class FeatureCollection(features:util.List[Feature]){
/*  def getFeatures()={
    this.features
  }*/
}
case class Feature(properties:Properties,geometry:JSONObject){
/*  def getProperties()={
    this.properties
  }
  def getGeometry()={
    this.geometry
  }*/

  def getGeometryObject()={
    val mapGeo = GeometryEngine.geoJsonToGeometry(geometry.toJSONString,0,Geometry.Type.Unknown)
    mapGeo.getGeometry
  }

}
case class Properties(boroughCode:Int,borough:String){
/*  def getBoroughCode()={
    this.boroughCode
  }
  def getBorough(): String ={
    this.borough
  }*/
}
object FeatureUtils {
  /**
    * json字符串转对象
    * @param json
    * @return
    */
  def parseJson(json:String): FeatureCollection ={

    JSON.parseObject(json,classOf[FeatureCollection])
  }

  /**
    * 对象转json字符串
    * @param obj
    * @return
    */
  //def toJson(obj:FeatureCollection)= JSON.toJSONString(obj, null.asInstanceOf[Array[SerializeFilter]])

  /*def main(args: Array[String]): Unit = {
    val json =
      """
        |{
        |	"type": "FeatureCollection",
        |	"features": [
        |    {
        |      "type": "Feature",
        |      "id": 0,
        |      "properties": {
        |        "boroughCode": 5,
        |        "borough": "Staten Island",
        |        "@id": "http:\/\/nyc.pediacities.com\/Resource\/Borough\/Staten_Island"
        |      },
        |      "geometry": {
        |        "type": "Polygon",
        |        "coordinates": [
        |          [
        |            [-74.050508064032471, 40.566422034160816],
        |            [-74.049983525625748, 40.566395924928273]
        |          ]
        |        ]
        |      }
        |    }
        |  ]
        |}
      """.stripMargin

    println(toJson(parseJson(json)))
  }*/
}
