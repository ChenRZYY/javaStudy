package cn.sdrfengmi._07_kudu_impala

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}
import org.junit.Test

class KuduTest {
  //kudu master地址
  val KUDU_MASTER = "hadoop01:7051,hadoop02:7051,hadoop03:7051"

  //1、创建SparkSession对象
  val spark: SparkSession = SparkSession.builder().master("local[4]").appName("kudu").getOrCreate()
  //2、创建KuduContext
  val context: KuduContext = new KuduContext(KUDU_MASTER, spark.sparkContext)
  //操作表名
  val TABLE_NAME = "student"

  import spark.implicits._

  //1、创建表
  @Test
  def createTable(): Unit = {

    //3、创建表的schema信息、主键
    val schema = new StructType()
      .add("id", IntegerType)
      .add("name", StringType)
      .add("age", IntegerType)
    //设置主键字段
    val keys = Seq[String]("id")
    //4、指定表的属性: tablet个数、分区规则、副本数
    val options = new CreateTableOptions
    val columns = Seq[String]("id")
    import scala.collection.JavaConversions._
    //指定分区规则与分区数
    //创建分区的时候，分区数必须>=2
    /**
      * 分区规则:
      * hash:
      * id=1  name="zhangsan"  age=20
      * 这条数据分到哪个tablet是由分区规则决定：
      * 分区字段的值%分区数=分区号
      * 1 %  3 = 1
      * range:
      * 创建表的时候：
      * 1号分区 0<=id <10
      * 2号分区 1<=id <20
      * 3号分区 20<=id
      *
      * id=1  name="zhangsan"  age=20 ,
      */
    options.addHashPartitions(columns, 3)
    //指定tablet副本数
    //设置副本数必须是奇数，副本数必须<=活着tabletserver的个数
    options.setNumReplicas(1)
    //5、创建表
    context.createTable(TABLE_NAME, schema, keys, options)
  }

  /** 2、插入数据
    * 如果主键已经存在，则插入报错
    */
  @Test
  def insert(): Unit = {

    val data: DataFrame = spark.sparkContext.parallelize(Seq[(Int, String, Int)](
      (1, "zhangsan", 20),
      (2, "lisi", 20),
      (3, "wangwu", 20),
      (4, "zhaoliu", 20),
      (5, "wagnqi", 20)
    )).toDF("id", "name", "age")

    context.insertRows(data, TABLE_NAME)
  }

  //3、查看数据
  @Test
  def query(): Unit = {
    context.kuduRDD(spark.sparkContext, TABLE_NAME, Seq[String]("id", "name", "age"))
      .foreach(println(_))
  }

  /**
    * 更新数据
    * 在更新数据的时候，必须带上主键字段，不需要更新的字段可以不用带上
    */
  @Test
  def update(): Unit = {
    val data = spark.sparkContext.parallelize(Seq[(Int, String)](
      (1, "zhangsan-3"),
      (2, "zhangsan-4")
    )).toDF("id", "name")
    context.updateRows(data, TABLE_NAME)
  }

  /**
    * 删除数据
    * 删除数据的时候只能用主键字段,如果带上非主键字段，删除报错
    */
  @Test
  def delete(): Unit = {
    //val data = Seq[Int](1,2).toDF("id")
    val data = Seq[(Int, String)]((3, "wangwu"), (4, "zhaoliu")).toDF("id", "name")

    context.deleteRows(data, TABLE_NAME)
  }

  /**
    * 如果主键存在，则更新数据，如果主键不存在，则插入数据
    */
  @Test
  def upsert(): Unit = {

    val data = Seq[(Int, String, Int)](
      (1, "zhangsan", 20),
      (3, "qianqi", 40)
    ).toDF("id", "name", "age")

    context.upsertRows(data, TABLE_NAME)
  }

  //7、删除表
  @Test
  def drop(): Unit = {
    //判断表是否存在
    if (context.tableExists(TABLE_NAME)) {
      context.deleteTable(TABLE_NAME)
    }
  }

  /**
    * 使用read查询kudu
    */
  @Test
  def sqlQuery(): Unit = {

    import org.apache.kudu.spark.kudu._
    spark.read.option("kudu.master", KUDU_MASTER)
      .option("kudu.table", TABLE_NAME)
      .kudu
      .show
  }

  /**
    * 使用write实现数据的写入
    * 使用write写入数据的时候目前只支持Append写入模式
    * 使用write写入数据的时候，效果与upsert类型，如果主键存在，则更新，如果不存在，则插入
    */
  @Test
  def sqlWrite(): Unit = {
    val data: DataFrame = spark.sparkContext.parallelize(Seq[(Int, String, Int)](
      (7, "zhangsan-7", 20),
      (8, "lisi-8", 20),
      (9, "wangwu-9", 20),
      (10, "lisi-10", 20)
    )).toDF("id", "name", "age")
    import org.apache.kudu.spark.kudu._
    data.write.mode(SaveMode.Append).option("kudu.master", KUDU_MASTER).option("kudu.table", TABLE_NAME)
      .kudu
  }
}
