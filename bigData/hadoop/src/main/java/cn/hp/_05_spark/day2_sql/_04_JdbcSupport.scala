package cn.hp._05_spark.day2_sql

import java.util.Properties

import cn.hp._05_spark.day2_sql.mysqlUtil.MySQLUtils
import org.apache.spark.sql.types._
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.junit.Test

/**
  * MySQL 的访问方式有两种: 使用本地运行, 提交到集群中运行
  *
  * 写入 MySQL 数据时, 使用本地运行, 读取的时候使用集群运行
  */
class _04_JdbcSupport {

  // 1. 创建 SparkSession 对象
  val spark = SparkSession.builder().master("local[6]").appName("mysql write").getOrCreate()
  val jdbcUrl = "jdbc:mysql://cdb-ljt08kf8.cd.tencentcdb.com:10007/chenzhendong"
  val table = "student_test"
  val prop = new Properties()
  prop.setProperty("user", "root")
  prop.setProperty("password", "zd521707")
  prop.setProperty("driver", "com.mysql.jdbc.Driver")
  prop.setProperty("charset", "UTF-8")

  @Test
  //TDP 会默认修改表字段数据类型,字段名要是不一样会修改字段名
  def readDemo: Unit = {
    // 2. 读取数据创建 DataFrame
    //    1. 拷贝文件
    //    2. 读取
    val schema = StructType(
      List(
        StructField("name", StringType),
        StructField("age", IntegerType),
        StructField("gpa", FloatType)
      )
    )

    val df = spark.read
      .schema(schema)
      .option("delimiter", "\t")
      .csv("dataset/studenttab10k")

    // 3. 处理数据
    //    val resultDF = df.where("age > 30").limit(10)
    //    resultDF.show()

    // 4. 落地数据
    //    df.write
    //      .format("jdbc")
    //      .option("url", jdbcUrl)
    //      .option("dbtable", "student_test")
    //      .option("user", "root")
    //      .option("password", "zd521707")
    //      .mode(SaveMode.Overwrite)
    //      .save()


    //方式2
    //先清空MySQL表
    //    Class.forName("com.mysql.jdbc.Driver")
    //    val conn = DriverManager.getConnection(jdbcUrl, prop)
    //    val sm = conn.prepareCall("truncate table " + "student_test")
    //    sm.execute()
    //    sm.close()

    //    resultDF.write
    //      .mode(SaveMode.Overwrite)
    //      .jdbc(jdbcUrl, "student_test", prop)

    MySQLUtils.saveDFtoDBUsePool(table, df)

  }

  /**
    * 此种方式只适合读取的数据量比较小
    */
  @Test
  def read1(): Unit = {
    //设置jdbc读取的参数 账号 密码 driver
    val df = spark.read.jdbc(jdbcUrl, table, prop)
    df.show()
    println(df.rdd.partitions.size)
    //分区数：1
  }

  @Test
  def read2(): Unit = {
    //设置jdbc读取的参数 账号 密码 driver

    //设置分区的范围
    //predicates.size等于分区数，每个分区的数据有predicates元素决定
    val predicates: Array[String] = Array[String](
      "age<20",
      "age>=20 and age<50",
      "age>=50"
    )
    val df = spark.read.jdbc(jdbcUrl, table, predicates, prop)

    df.rdd.mapPartitionsWithIndex((index, it) => {
      println(s"index:${index}  data:${it.toBuffer}")
      it
    }).collect()
  }

  /**
    * 此种方式适合大数据量的表
    */
  @Test
  def read3(): Unit = {
    //设置jdbc读取的参数 账号 密码 driver
    /**
      * columnName:指定分区的字段
      * lowerBound:字段最小值，用于分区规则
      * upperBound:字段的最大值,用于分区规则
      * numPartitions:指定分区数
      *
      * columnName只能是数字类型
      */
    val df = spark.read.jdbc(jdbcUrl, table, "age", 0, 100, 5, prop)

    //println(df.rdd.partitions.size)
    df.rdd.mapPartitionsWithIndex((index, it) => {
      println(s"index:${index} data:${it.toBuffer}")
      it
    }).collect()


    //if (partitioning == null || partitioning.numPartitions <= 1 ||
    //      partitioning.lowerBound == partitioning.upperBound) {
    //      return Array[Partition](JDBCPartition(null, 0))
    //    }
    //
    //    val lowerBound = partitioning.lowerBound = 0
    //    val upperBound = partitioning.upperBound = 100
    //
    //
    //    val numPartitions =
    //      if ((upperBound - lowerBound) >= partitioning.numPartitions) {
    //      if ((100 - 0) >= 1000) {
    //
    //        partitioning.numPartitions
    //      } else {
    //
    //        100-0
    //      }
    //    // Overflow and silliness can happen if you subtract then divide.
    //    // Here we get a little roundoff, but that's (hopefully) OK.
    //    val stride: Long = upperBound / numPartitions - lowerBound / numPartitions
    //    val stride: Long = 100 / 5 - 0 / 5 = 20
    //    val column = partitioning.column = "age"
    //    var i: Int = 0
    //    var currentValue: Long = lowerBound = 0
    //    var ans = new ArrayBuffer[Partition]()
    //    while (i < numPartitions) {
    //      val lBound = if (i != 0) s"$column >= $currentValue" else null
    //      currentValue += stride
    //      val uBound = if (i != numPartitions - 1) s"$column < $currentValue" else null
    //      val whereClause =
    //        if (uBound == null) {
    //          lBound
    //        } else if (lBound == null) {
    //          s"$uBound or $column is null"
    //        } else {
    //          s"$lBound AND $uBound"
    //        }
    //      ans += JDBCPartition(whereClause, i)
    //      i = i + 1
    //    }
    //    ans.toArray
    //  }
    //第一次循环
    //  0< 5
    //   val lBound = null
    //   currentValue  = currentValue+stride = 0 +20
    //   val uBound = s"age < 20"
    //   val whereClause = s"age < 20 or age is null"
    // 1<5
    //   val lBound = s"age >= 20"
    //   currentValue = currentValue + stride = 20 + 20 = 40
    //   val uBound = s"age < 40"
    //   val whereClause =  s"age >= 20 AND age < 40"
    // 2<5
    //         val lBound = s"age >= 40"
    //         currentValue = 60
    //         val uBound = s"age < 60"
    //         val whereClause = s"age>=40 AND age<60"
    // 3<5
    //     val lBound = s"age >= 60"
    //     currentValue = 80
    //      val uBound = s"age < 80"
    //     val whereClause = s"age>=60 AND age<80"
    //4<5
    //   val lBound = s"age >= 80"
    //   currentValue =100
    //   val uBound = null
    //   val whereClause = s"age >= 80"
    //
  }

  @Test
  def write(): Unit = {
    import spark.implicits._

    val df = spark.read.option("sep", "\t").csv("dataset/studenttab10k").toDF("name", "age", "gpa")
    df.show()
    //设置jdbc读取的参数 账号 密码 driver //(StringType, IntegerType, FloatType) TODO 这种转数据类型的格式报错?????
    //    val dfType: Dataset[(String, Integer, Float)] = df.as[(String, Integer, Float)]
    //    val valueLimit: Dataset[(String, Integer, Float)] = dfType.limit(10)
    //    valueLimit.show()
    df.write.mode(SaveMode.Overwrite).jdbc(jdbcUrl, table, prop)
  }

  @Test
  def write2(): Unit = {
    import spark.implicits._

    val df = spark.read.option("sep", "\t").csv("dataset/studenttab10k").toDF("name", "age", "gpa")
    df.show()
    //设置jdbc读取的参数 账号 密码 driver //(StringType, IntegerType, FloatType) TODO 这种转数据类型的格式报错?????
    //    val dfType: Dataset[(String, Integer, Float)] = df.as[(String, Integer, Float)]
    //    val valueLimit: Dataset[(String, Integer, Float)] = dfType.limit(10)
    //    valueLimit.show()
    df.write.mode(SaveMode.Overwrite).jdbc(jdbcUrl, table, prop)


  }
}
