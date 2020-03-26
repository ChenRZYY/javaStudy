package cn.sdrfengmi.spark.project._01_spark_dmp.utils

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.{DataFrame, SaveMode, Row, SparkSession}
import org.apache.spark.sql.types.StructType

/**
  * @Author Haishi
  * @create 2020/3/23 15:07
  */
object KuduUtils {
  /**
    * 数据写入kudu
    */
  def write(context: KuduContext, tableName: String,
            schema: StructType,
            keys: Seq[String],
            options: CreateTableOptions, data: DataFrame) = {
    //如果表存在，删除
    if (context.tableExists(tableName)) {
      context.deleteTable(tableName)
    }
    context.createTable(tableName, schema, keys, options)

    //写入数据
    import org.apache.kudu.spark.kudu._
    data.write
      .mode(SaveMode.Append)
      .option("kudu.master", ConfigUtils.MASTER_ADDRESS)
      .option("kudu.table", tableName)
      .kudu
  }

  def businessWrite(context: KuduContext, tableName: String,
                    schema: StructType,
                    keys: Seq[String],
                    options: CreateTableOptions, data: DataFrame) = {
    //如果表存在，删除
    if (!context.tableExists(tableName)) {
      context.createTable(tableName, schema, keys, options)
    }

    //写入数据
    import org.apache.kudu.spark.kudu._
    data.write
      .mode(SaveMode.Append)
      .option("kudu.master", ConfigUtils.MASTER_ADDRESS)
      .option("kudu.table", tableName)
      .kudu
  }
}
