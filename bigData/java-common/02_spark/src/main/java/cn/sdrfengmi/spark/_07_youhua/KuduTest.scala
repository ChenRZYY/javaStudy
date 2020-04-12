package cn.sdrfengmi.spark._07_youhua

import cn.sdrfengmi.spark.project._01_spark_dmp.utils.{ConfigUtils, DateUtils}
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.SparkSession

object KuduTest {

  def main(args: Array[String]): Unit = {

    //1、创建SparkSession
    val spark = SparkSession.builder().master("local[4]").appName("test").getOrCreate()

    val context = new KuduContext(ConfigUtils.MASTER_ADDRESS,spark.sparkContext)
    import org.apache.kudu.spark.kudu._

    //context.deleteTable("business_area")
    //3、读取数据
    val data = spark.read.option("kudu.master",ConfigUtils.MASTER_ADDRESS)
      .option("kudu.table",s"tags_${DateUtils.getNow()}")
      .kudu
      data.show
  }
}
