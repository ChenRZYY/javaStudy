package cn.hp.project._02_spark_dmp.utils

import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

/**
  * @Author Haishi
  * @create 2020/3/23 15:11
  */
object DateUtils {

  /**
    * 获取当天的yyyyMMdd格式的日期字符串
    */
  def getNow()={
    //1、获取当前日期
    val date = new Date()
    //2、定义格式化
    val formatter = FastDateFormat.getInstance("yyyyMMdd")
    //3、格式化
    formatter.format(date)
  }

}
