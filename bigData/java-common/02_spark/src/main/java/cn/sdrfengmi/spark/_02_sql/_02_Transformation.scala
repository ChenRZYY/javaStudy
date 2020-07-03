package cn.sdrfengmi.spark._02_sql

import java.lang

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, KeyValueGroupedDataset, SparkSession}
import org.junit.Test

class TypedTransformation {
  Logger.getLogger("org").setLevel(Level.ERROR)
  // 1. 创建 SparkSession
  val spark: SparkSession = SparkSession.builder().master("local[6]").appName("typed").getOrCreate()


  //  @Test
  //怎么支持
  def enableHiveSupport(): Unit = {
    import spark.implicits._
    val sparkHive: SparkSession = SparkSession.builder().master("local[6]").appName("typed").enableHiveSupport().getOrCreate()
    val f: DataFrame = sparkHive.sparkContext.parallelize(Seq(Person("zhangsan", 15), Person("lisi", 20))).toDF
    f.createOrReplaceTempView("person")
    sparkHive.sql("select * from person")
  }

  @Test
  def map(): Unit = {
    import spark.implicits._
    // 3. flatMap
    val ds1 = Seq("hello spark", "hello hadoop").toDS
    ds1.flatMap(item => item.split(" ")).show()

    // 4. map
    val ds2 = Seq(Person("zhangsan", 15), Person("lisi", 20)).toDS()
    ds2.map(person => Person(person.name, person.age * 2)).show()

    // 5. mapPartitions
    ds2.mapPartitions(
      // iter 不能大到每个 Executor 的内存放不下, 不然就会 OOM
      // 对每个元素进行转换, 后生成一个新的集合
      iter => {
        val result = iter.map(person => Person(person.name, person.age * 2))
        result
      }
    ).show()
  }

  @Test
  def transform(): Unit = {
    import spark.implicits._
    //[id: bigint, bb: bigint, cc: bigint]  用range生成的数据的表头
    //增加一列 使用的withColum 列值必须为之前列的字段,或者随机数等
    val ds: Dataset[lang.Long] = spark.range(10)
    ds.transform(dataset => dataset.withColumn("doubled", $"id")).show()

    import org.apache.spark.sql.functions._
    ds.transform(dataset => dataset.withColumn("doubled", expr("rand()"))).show()

  }

  /**
    * distinct:去重条件是所有的列都相同才算重复
    * dropDuplicates：去重条件时指定列的值相同算重复
    */
  @Test
  def distinct(): Unit = {
    import spark.implicits._
    val data = Seq(Person("zhangsan", 20), Person("lisi", 50), Person("wangwu", 50), Person("zhangsan", 30)).toDS()
    data.distinct().show
    data.dropDuplicates("age").show
  }

  /**
    * 名称转换,类型转换
    */
  @Test
  def as(): Unit = {
    import spark.implicits._
    // 1. 读取
    val schema = StructType(
      Seq(
        StructField("name", StringType),
        StructField("age", IntegerType),
        StructField("gpa", FloatType)
      )
    )

    val df: DataFrame = spark.read.schema(schema).option("delimiter", "\t").csv("../01_dataset/studenttab10k")

    // 1. 转换
    // 本质上: Dataset[Row].as[Student] => Dataset[Student]
    // Dataset[(String, int, float)].as[Student] => Dataset[Student]
    val ds: Dataset[Student] = df.as[Student]
    ds.show()

    import spark.implicits._
    val source: DataFrame = spark.read.option("sep", "\t").csv("../01_dataset/studenttab10k")
    //1 类型转换
    val ds1: Dataset[(String, String, String)] = source.as[(String, String, String)]
    ds1.show

    val ds2: Dataset[Person] = Seq(Person("zhangsan", 15), Person("lisi", 10)).toDS()
    //2 名称转换
    ds2.select('name as "new_name").show()
    //3 类型转换
    ds2.select('age.as[Long]).show()
  }

  @Test
  def select(): Unit = {
    import spark.implicits._
    val ds: Dataset[Person] = Seq(Person("zhangsan", 20), Person("lisi", 50), Person("wangwu", 50), Person("zhangsan", 30)).toDS()
    ds.select('name).show
    ds.selectExpr("name", "age").show
    ds.selectExpr("sum(age)").show()
    ds.selectExpr("name", "age", "rand()").show
    ds.selectExpr("name", "age", "name name_new").show

    import org.apache.spark.sql.functions._
    ds.select(expr("sum(age)")).show
    //    data.select("sum(age)").show/ 错误的方式
    //    data.select('sum ('age)).show
  }


  @Test
  def column(): Unit = {
    import spark.implicits._
    val ds = Seq(Person("zhangsan", 20), Person("lisi", 50), Person("wangwu", 50), Person("zhangsan", 30)).toDS()
    import org.apache.spark.sql.functions._
    // 1. 使用 functions.xx
    // 2. 使用表达式, 可以使用 expr("...") 随时随地编写表达式
    //增加一列 列值为自定义
    ds.withColumn("rand", expr("rand()")).show
    //增加一列，列值用拥有的列的值
    ds.withColumn("name_new", 'name).show()
    //增加一列, 列值为判断后结果
    ds.withColumn("name_jok", 'name === "zhangsan").show()
    //重命名
    ds.withColumnRenamed("name", "name_new").show

  }

  @Test
  def agg(): Unit = {
    import spark.implicits._
    val data: Dataset[Person] = Seq(Person("zhangsan", 20), Person("lisi", 50), Person("wangwu", 50), Person("zhangsan", 30)).toDS()
    //需求:根据年龄分组，获取年龄平均值
    data.groupBy('name).avg("age").show
    import org.apache.spark.sql.functions._
    //需求:根据年龄分组，获取年龄平均值,年龄的总值
    data.groupBy('name).agg(avg("age"), sum("age")).show
  }


  @Test
  def where(): Unit = {
    import spark.implicits._
    val ds: Dataset[Person] = Seq(Person("zhangsan", 15), Person("lisi", 10)).toDS()

    // 需求一, ds 增加列, 双倍年龄
    // 'age * 2 其实本质上就是将一个表达式(逻辑计划表达式) 附着到 column 对象上
    // 表达式在执行的时候对应每一条数据进行操作
    ds.withColumn("doubled", 'age * 2).show
    //    ds.withColumn("doubled", 'age.*(2).show()  scala中可以自定义符号表达式

    // 需求二, 模糊查询
    // select * from table where name like zhang%
    ds.where('name like "zhang%").show()

    // 需求三, 排序, 正反序
    ds.sort('age asc).show()

    // 需求四, 枚举判断
    ds.where('name isin("zhangsan", "wangwu", "zhaoliu")).show()
  }

  @Test
  def filter(): Unit = {
    import spark.implicits._
    val ds = Seq(Person("zhangsan", 15), Person("lisi", 50)).toDS()
    ds.filter(person => person.age > 15).show()
    ds.filter("age>=30").show

  }


  @Test
  def groupByKey(): Unit = {
    import spark.implicits._
    val ds = Seq(Person("zhangsan", 15), Person("zhangsan", 16), Person("lisi", 20)).toDS()

    // select count(*) from person group by name
    val grouped: KeyValueGroupedDataset[String, Person] = ds.groupByKey(person => person.name)
    val result: Dataset[(String, Long)] = grouped.count()

    result.show()
  }

  @Test
  def groupBy(): Unit = {
    import spark.implicits._
    val ds = Seq(Person("zhangsan", 12), Person("zhangsan", 8), Person("lisi", 15)).toDS()

    // 为什么 GroupByKey 是有类型的, 最主要的原因是因为 groupByKey 所生成的对象中的算子是有类型的
    //    ds.groupByKey( item => item.name ).mapValues()

    // 为什么  GroupBy 是无类型的, 因为 groupBy 所生成的对象中的算子是无类型的, 针对列进行处理的
    import org.apache.spark.sql.functions._

    ds.groupBy('name).agg(mean("age")).show()
  }

  //把Dataset切分成多个
  @Test
  def split(): Unit = {
    val ds: Dataset[lang.Long] = spark.range(15)
    // randomSplit, 切多少份, 权重多少
    val datasets: Array[Dataset[lang.Long]] = ds.randomSplit(Array(5, 2, 3))
    datasets.foreach(_.show())

    // sample 抽取0.4比例
    ds.sample(withReplacement = false, fraction = 0.4).show()
  }

  @Test
  def sort(): Unit = {
    import spark.implicits._
    val ds = Seq(Person("zhangsan", 12), Person("zhangsan", 8), Person("lisi", 15)).toDS()
    ds.orderBy('age.desc).show() // select * from ... order by ... asc
    ds.sort('age.asc).show()
  }

  //去除指定列下的重复行
  @Test
  def dropDuplicates(): Unit = {
    import spark.implicits._
    val ds = spark.createDataset(Seq(Person("zhangsan", 15), Person("zhangsan", 15), Person("lisi", 15)))
    ds.distinct().show()
    ds.dropDuplicates("age").show()
    //    ds.withWatermark("", "").show()
    ds.drop("name").show() //删除一列
  }

  @Test
  def collection(): Unit = {
    val ds1: Dataset[lang.Long] = spark.range(1, 10)
    val ds2: Dataset[lang.Long] = spark.range(5, 15)

    // 差集
    ds1.except(ds2).show()

    // 交集
    ds1.intersect(ds2).show()

    // 并集
    ds1.union(ds2).show()

    // limit
    ds1.limit(3).show()
  }

}

case class Student(name: String, age: Int, gpa: Float)
