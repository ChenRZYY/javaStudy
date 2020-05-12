package cn.sdrfengmi.spark._01_core

import java.text.SimpleDateFormat
import java.util.{Date, Random}

import org.apache.hadoop.fs.Path

/**
  * @Author 陈振东
  * @create 2020/5/12 09:19
  */
object SparkUtil {

  var inputFile: String = "../01_dataset/"
  var outputFile: String = "../01_datasetOut/"

  var date: Date = new Date
  var sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss")
  var random: Random = new Random

  def getNextOutputputFile: String = {
    //    val path: Path = new Path(outputFile + sdf.format(date))
    val file: String = outputFile + sdf.format(date)
    println(file)
    file
  }
}
