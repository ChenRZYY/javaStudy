package cn.sdrfengmi.spark._02_sql

import org.apache.spark.sql
import org.apache.spark.sql.{ColumnName, DataFrame, Dataset, SparkSession}
import org.junit.Test

class _02_Column {
  // 1. 创建 spark 对象
  val spark: SparkSession = SparkSession.builder()
    .master("local[6]")
    .appName("column")
    .getOrCreate()

  import spark.implicits._

  @Test
  def creation(): Unit = {
    val ds: Dataset[Person] = Seq(Person("zhangsan", 15), Person("lisi", 10)).toDS()
    val ds1: Dataset[Person] = Seq(Person("zhangsan", 15), Person("lisi", 10)).toDS()
    val df: DataFrame = Seq(("zhangsan", 15), ("lisi", 10)).toDF("name", "age")

    // 2. ' 必须导入spark的隐式转换才能使用 str.intern()
    val column0: Symbol = 'name

    // 3. $ 必须导入spark的隐式转换才能使用
    val column1: ColumnName = $"name"

    // 4. col 必须导入 functions
    import org.apache.spark.sql.functions._

    val column2: sql.Column = col("name")

    // 5. column 必须导入 functions
    val column3: sql.Column = column("name")  //TODO column0 为什么都能用

    // 这四种创建方式, 有关联的 Dataset 吗?
    ds.select(column0).show()

    // Dataset 可以, DataFrame 可以使用 _02_Column 对象选中行吗?
    df.select(column0).show()

    // select 方法可以使用 column 对象来选中某个列, 那么其他的算子行吗?
    df.where(column0 === "zhangsan").show()

    // column 有几个创建方式, 四种
    // column 对象可以用作于 Dataset 和 DataFrame 中
    // column 可以和命令式的弱类型的 API 配合使用 select where

    // 6. dataset.col
    // 使用 dataset 来获取 column 对象, 会和某个 Dataset 进行绑定, 在逻辑计划中, 就会有不同的表现
    val column4: sql.Column = ds.col("name")
    val column5: sql.Column = ds1.col("name")

    // 这会报错
    //    ds.select(column5).show()

    // 为什么要和 dataset 来绑定呢?
    //    ds.join(ds1, ds.col("name") === ds1.col("name"))

    // 7. dataset.apply
    val column6: sql.Column = ds.apply("name")
    val column7: sql.Column = ds("name")
  }

  @Test
  def create(): Unit = {

    val ds: Dataset[Person] = Seq(Person("zhangsan", 20), Person("lisi", 30)).toDS

    //无绑定方式
    val column1: Symbol = 'name

    val column2: ColumnName = $"name"

    import org.apache.spark.sql.functions._
    val column3 = col("name")

    val column4 = column("name")

    //有绑定方式
    val column5 = ds.col("name")

    val column6 = ds.apply("name")

    val column7 = ds("name")


    /**
      * student
      * id name age classId
      *
      * clazz
      * clazzId  name
      *
      * 获取学生详细信息以及班级信息
      * select s.id,s.name,s.age,c.name
      * from student s left join clazz c
      * on s.classId = c.classId
      */
  }



}
