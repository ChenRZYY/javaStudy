package cn.sdrfengmi.spark._02_sql

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext, sql}
import org.junit.{Before, Test}

case class Person(name: String, age: Int)

class RDDIntro {
  Logger.getLogger("org").setLevel(Level.ERROR)

  //  val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local[6]").setAppName("rdd intro").set("spark.driver.allowMultipleContexts", "true"))
  val sparkSession: SparkSession = new sql.SparkSession.Builder().master("local[6]").appName("dataset1").getOrCreate()
  sparkSession.sparkContext.setLogLevel("ERROR")

  /**
    * rdd方式读取文件,操作
    */
  @Test
  def rddIntro(): Unit = {
    val ss: RDD[String] = sparkSession.sparkContext.textFile("../01_dataset/wordcount.txt")
    ss.flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .foreachPartition(it => println(it.toBuffer))
  }

  /**
    * dataset 处理数据
    */
  @Test
  def rdd_dataset(): Unit = {
    import sparkSession.implicits._
    val sourceRDD: RDD[Person] = sparkSession.sparkContext.parallelize(Seq(Person("zhangsan", 10), Person("lisi", 15)))
    val personDS: Dataset[Person] = sourceRDD.toDS()
    //1 dataset支持的api
    val resultDS: Dataset[String] = personDS.where('age > 10)
      .where('age < 20)
      .select('name)
      .as[String] //转换成String类型

    //2 dataset支持的强类型api
    personDS.filter(item => item.age > 10).show() // Dataset 支持强类型的 API

    //3 dataset 支持弱类型 API
    personDS.filter('age > 10).show()
    personDS.filter($"age" > 10).show()
    personDS.filter("age > 10").show() // Dataset 可以直接编写 SQL 表达式

    resultDS.show()
  }

  /**
    * dataset和rdd相互转换
    */
  @Test
  def dataset_rdd(): Unit = {
    // 2. 导入隐式转换
    import sparkSession.implicits._

    // 3. dataset 生成两种方式
    //    val sourceRDD = sparkSession.sparkContext.parallelize(Seq(Person("zhangsan", 10), Person("lisi", 15)))
    //    val dataset = sourceRDD.toDS()
    val dataset: Dataset[Person] = sparkSession.createDataset(Seq(Person("zhangsan", 10), Person("lisi", 15)))

    //    dataset.explain(true)
    // 无论Dataset中放置的是什么类型的对象, 最终执行计划中的RDD上都是 InternalRow
    // 直接获取到已经分析和解析过的 Dataset 的执行计划, 从中拿到 RDD
    // fixme 这种效果好,但是要重新转换类型,下面不用重新转换类型,  .rdd也是通过上面的转换过来的
    val executionRdd: RDD[InternalRow] = dataset.queryExecution.toRdd

    // 通过将 Dataset 底层的 RDD[InternalRow] 通过 Decoder 转成了和 Dataset 一样的类型的 RDD
    val typedRdd: RDD[Person] = dataset.rdd

    println(executionRdd.toDebugString)
    println(typedRdd.toDebugString)
  }


  @Test
  //dataframe 和 dataset关系 操作row对象
  def dataset_trans(): Unit = {
    import sparkSession.implicits._
    val personList: Seq[Person] = Seq(Person("zhangsan", 15), Person("lisi", 20))

    // DataFrame 是弱类型的  row转换的时候必须指定强类型
    val df: DataFrame = personList.toDF()
    df.map((row: Row) => Row(row.get(0), row.getAs[Int](1) * 2))(RowEncoder.apply(df.schema)) //必须指定一个类型
      .show()

    // DataFrame 所代表的弱类型操作是编译时不安全
    //    df.groupBy("name, school")

    // Dataset 是强类型的 比DataFrame更强
    val ds: Dataset[Person] = personList.toDS()
    val psDataset: Dataset[Person] = ds.map((person: Person) => Person(person.name, person.age * 2))
    psDataset.show()
    // Dataset 所代表的操作, 是类型安全的, 编译时安全的
    //    ds.filter( person => person.school )
  }

  //创建df三种方式
  @Test
  def dataframe_create(): Unit = {
    import sparkSession.implicits._
    val personList = Seq(Person("zhangsan", 15), Person("lisi", 20))
    // 1. toDF
    val df1: DataFrame = personList.toDF()
    val df2: DataFrame = sparkSession.sparkContext.parallelize(personList).toDF()
    // 2. createDataFrame
    val df3: DataFrame = sparkSession.createDataFrame(personList)

    // 3. read
    val df4: DataFrame = sparkSession.read.csv("../01_dataset/BeijingPM.csv")
    println(df4.count())
    df4.show()
  }

  /**
    * dataframe 操作
    */
  @Test
  def dataframe_rdd(): Unit = {

    import sparkSession.implicits._
    val sourceRDD: RDD[Person] = sparkSession.sparkContext.parallelize(Seq(Person("zhangsan", 10), Person("lisi", 15)))
    val df: DataFrame = sourceRDD.toDF()
    df.createOrReplaceTempView("person")
    //缓存：(1)dataFrame.cache	(2)sparkSession.catalog.cacheTable(“tableName”)
    //释放缓存：(1)dataFrame.unpersist	(2)sparkSession.catalog.uncacheTable(“tableName”)
    sparkSession.catalog.cacheTable("person")
    //    sparkSession.catalog.uncacheTable("person") //清除固定表
    //sparkSession.catalog.clearCache() //清除所有表

    //sql 执行
    val resultDF = sparkSession.sql("select name from person where age > 10 and age < 20")
    resultDF.show()
    //api执行
    df.where('age > 10)
      .select('name)
      .show()
    Thread.sleep(1000000)
  }

  @Test
  def dataframe_createTable(): Unit = {

    import sparkSession.implicits._
    val sourceRDD: RDD[Person] = sparkSession.sparkContext.parallelize(Seq(Person("zhangsan", 10), Person("lisi", 15)))
    val df: DataFrame = sourceRDD.toDF()
    df.createOrReplaceTempView("person")//创建临时表 一般都用这个
    df.createTempView("person1") //创建普通临时表
    df.createGlobalTempView("person2") //创建全局表
    df.registerTempTable("person3") //删除api

    //sql 执行
    val resultDF = sparkSession.sql("select name from person where age > 10 and age < 20")
    resultDF.show()
    //api执行
    df.where('age > 10)
      .select('name)
      .show()
  }

  /**
    * api操作
    */
  @Test
  def dataframe_api(): Unit = {
    // 2. 读取数据集
    val sourceDF: DataFrame = sparkSession.read
      .option("header", value = true) //第一行为header信息
      .csv("../01_dataset/BeijingPM_header.csv")

    // 查看 DataFrame 的 Schema 信息, 要意识到 DataFrame 中是有结构信息的, 叫做 Schema
    sourceDF.printSchema()

    // 3. 处理
    //     1. 选择列
    //     2. 过滤掉 NA 的 PM 记录
    //     3. 分组 select year, month, count(PM_Dongsi) from ... where PM_Dongsi != NA group by year, month
    //     4. 聚合
    // 4. 得出结论
    //    sourceDF.select('year, 'month, 'PM_Dongsi)
    //      .where('PM_Dongsi =!= "NA")
    //      .groupBy('year, 'month)
    //      .count()
    //      .show()

    // 是否能直接使用 SQL 语句进行查询
    // 1. 将 DataFrame 注册为临表
    sourceDF.createOrReplaceTempView("pm")

    // 2. 执行查询
    val resultDF: DataFrame = sparkSession.sql("select year, month, count(PM_Dongsi) from pm where PM_Dongsi != 'NA' group by year, month")
    resultDF.show()
    sparkSession.stop()
  }

  @Test
  def rdd_df_ds(): Unit = {
    //转换的三种方式
    //    rdd-.toDS->ds  rdd-.toDF case class->df
    //    df-.rdd->rdd  df-.as[case class] case class->ds
    //    ds-.rdd->rdd  ds-.toDF case class->df
  }

  @Test
  def row(): Unit = {
    // 1. Row 如何创建, 它是什么
    // row 对象必须配合 Schema 对象才会有 列名
    val p = Person("zhangsan", 15)
    val row = Row("zhangsan", 15)

    // 2. 如何从 Row 中获取数据
    row.getString(0)
    row.getInt(1)

    // 3. Row 也是样例类
    row match {
      case Row(name, age) => println(name, age)
    }
  }

  @Test
  def filter(): Unit = {

  }

  @Test
  def select(): Unit = {

  }


}

