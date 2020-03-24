package cn.hp._05_spark.day2_sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext, sql}
import org.junit.Test

case class Person(name: String, age: Int)

class RDDIntro {

  @Test
  def rddIntro(): Unit = {
    val conf = new SparkConf().setMaster("local[6]").setAppName("rdd intro")
    val sc = new SparkContext(conf)

    sc.textFile("dataset/globalParameter.txt")
      .flatMap(_.split(","))
      .map((_, 1))
      .reduceByKey(_ + _)
      .collect()
      .foreach(println(_))
  }

  @Test
  def rdd_dataset(): Unit = {
    val spark: SparkSession = new SparkSession.Builder()
      .appName("ds intro")
      .master("local[6]")
      .getOrCreate()

    import spark.implicits._
    val sourceRDD: RDD[Person] = spark.sparkContext.parallelize(Seq(Person("zhangsan", 10), Person("lisi", 15)))
    val personDS: Dataset[Person] = sourceRDD.toDS()
    val resultDS: Dataset[String] = personDS.where('age > 10)
      .where('age < 20)
      .select('name)
      .as[String]

    resultDS.show()
  }

  @Test
  def rdd_dataset2(): Unit = {
    // 1. 创建 SparkSession
    val spark = new sql.SparkSession.Builder()
      .master("local[6]")
      .appName("dataset1")
      .getOrCreate()

    // 2. 导入隐式转换
    import spark.implicits._

    // 3. 演示
    val sourceRDD = spark.sparkContext.parallelize(Seq(Person("zhangsan", 10), Person("lisi", 15)))
    val dataset: Dataset[Person] = sourceRDD.toDS()

    dataset.filter(item => item.age > 10).show() // Dataset 支持强类型的 API
    // Dataset 支持弱类型 API
    dataset.filter('age > 10).show()
    dataset.filter($"age" > 10).show()
    dataset.filter("age > 10").show() // Dataset 可以直接编写 SQL 表达式
  }

  @Test
  def rdd_dataframe(): Unit = {
    val spark: SparkSession = new SparkSession.Builder()
      .appName("ds intro")
      .master("local[6]")
      .getOrCreate()

    import spark.implicits._
    val sourceRDD: RDD[Person] = spark.sparkContext.parallelize(Seq(Person("zhangsan", 10), Person("lisi", 15)))
    val df: DataFrame = sourceRDD.toDF()
    df.createOrReplaceTempView("person")
    val resultDF = spark.sql("select name from person where age > 10 and age < 20")
    resultDF.show()
  }

  @Test
  def dataset_rdd(): Unit = {
    // 1. 创建 SparkSession
    val sparkSession: SparkSession = new sql.SparkSession.Builder()
      .master("local[6]")
      .appName("dataset1")
      .getOrCreate()

    // 2. 导入隐式转换
    import sparkSession.implicits._

    // 3. 演示
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
    println()
    println()
    println(typedRdd.toDebugString)
  }

  @Test
  def dataframe1(): Unit = {
    // 1. 创建 SparkSession
    val sparkSession = SparkSession.builder()
      .appName("dataframe1")
      .master("local[6]")
      .getOrCreate()

    // 2. 创建 DataFrame
    import sparkSession.implicits._

    val dataFrame: DataFrame = Seq(Person("zhangsan", 15), Person("lisi", 20)).toDF()
    val products: Seq[Product] = Seq(Person("张三", 20), ("李四", 70))
    products.toDF
    // 3. 看看 DataFrame 可以玩出什么花样
    // select name from ... t where t.age > 10
    dataFrame.where('age > 10)
      .select('name)
      .show()
  }

  //创建df三种方式
  @Test
  def dataframe_create(): Unit = {
    val sparkSession: SparkSession = SparkSession.builder()
      .appName("dataframe1")
      .master("local[6]")
      .getOrCreate()

    import sparkSession.implicits._
    val personList = Seq(Person("zhangsan", 15), Person("lisi", 20))
    // 1. toDF
    val df1: DataFrame = personList.toDF()
    val df2: DataFrame = sparkSession.sparkContext.parallelize(personList).toDF()
    // 2. createDataFrame
    val df3: DataFrame = sparkSession.createDataFrame(personList)

    // 3. read
    val df4: DataFrame = sparkSession.read.csv("dataset/BeijingPM.csv")
    println(df4.count())
    df4.show()
  }

  @Test
  def dataframe3(): Unit = {
    // 1. 创建 SparkSession
    val sparkSession: SparkSession = SparkSession.builder()
      .master("local[6]")
      .appName("pm analysis")
      .getOrCreate()

    import sparkSession.implicits._

    // 2. 读取数据集
    val sourceDF: DataFrame = sparkSession.read
      .option("header", value = true) //第一行为header信息
      .csv("dataset/BeijingPM_header.csv")

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
  //dataframe 和 dataset关系
  def dataset_trans(): Unit = {
    val spark = SparkSession.builder()
      .appName("dataframe1")
      .master("local[6]")
      .getOrCreate()

    import spark.implicits._
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

