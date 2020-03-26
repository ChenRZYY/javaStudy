package cn.sdrfengmi.project._02_spark_dmp.make

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.Row

/**
  * @Author 陈振东
  * @create 2020/3/23 18:00
  *        生成app标签
  */
object AppTag {

  def make(row: Row, appBc: Broadcast[Map[String, String]]) = {
    //1、取出appid与appname字段
    val appid: String = row.getAs[String]("appid")
    val appname: String = row.getAs[String]("appname")
    //2、取出广播变量的值
    val appInfo = appBc.value
    //3、判断，如果appname为空，从广播变量中取值填充,如果有值，用原来的值
    val appName_new: String = Option(appname) match {
      case Some(x) => x
      case None => appInfo.getOrElse(appid, "other")
    }
    //4、生成标签，给一个默认的权重[权重:代表当前这个标签对用户的重要的程序]
    var result = Map[String, Double]()
    result = result.+((s"APP_${appName_new}", 1.0))
    //5、返回数据
    result
  }
}