package cn.hp.project._02_spark_dmp.utils

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod


/**
  * @Author Haishi
  * @create 2020/3/23 14:15
  */
object HttpUtils {

  //ip 解析经纬度
  def get(url: String): String = {
    val client: HttpClient = new HttpClient
    val method: GetMethod = new GetMethod(url)
    val code: Int = client.executeMethod(method)
    if (code == 200) {
      method.getResponseBodyAsString
    } else {
      ""
    }

  }
}
