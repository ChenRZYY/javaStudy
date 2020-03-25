package cn.hp.project._02_spark_dmp.make

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.Row

/**
  * @Author 陈振东
  * @create 2020/3/23 18:00
  */
object DeviceTag {

  def make(row: Row, deviceBc: Broadcast[Map[String, String]]) = {
    //1、取出设备型号、设备类型、联网方式、运营商数据
    //设备型号
    val device = row.getAs[String]("device")
    //设备类型
    val client = row.getAs[Long]("client")
    //联网方式
    val network = row.getAs[String]("networkmannername")
    //运营商
    val isp = row.getAs[String]("ispname")
    //2、取出广播变量值

    val deviceInfo = deviceBc.value
    //3、将设备类型转换为编码、将联网方式转换为编码、将运营商转换为编码
    val clientCode = deviceInfo.getOrElse(client.toString, "OTHER")

    val networkCode = deviceInfo.getOrElse(network, "OTHER")

    val ispCode = deviceInfo.getOrElse(isp, "OTHER")
    //4、生成标签
    var result = Map[String, Double]()
    result = result.+((s"DX_${device}", 1.0))
    result = result.+((s"DT_${clientCode}", 1.0))
    result = result.+((s"NW_${networkCode}", 1.0))
    result = result.+((s"ISP_${ispCode}", 1.0))
    //5、数据返回
    result

  }
}