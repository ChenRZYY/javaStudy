package cn.sdrfengmi.spark._07_youhua

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.junit.Test

import scala.util.Random

class DataTest extends Serializable {
  Logger.getLogger("org").setLevel(Level.ERROR)

  /**
    * 数据倾斜出现的原因:
    *    1、在进行shuffle操作的时候,很多的key都是null,这些key为null的数据全部聚集在0号分区，0号出现了数据倾斜
    *    2、当分区数设置的过少，导致shuffle操作的时候，很多不同的key聚集在一个分区，导致数据倾斜
    *       spark.sql.shuffle.partitions
    *       spark.default.parallelism
    *    3、在groupby的时候，某一个key的数据特别多，该key的数据都会聚集在一个分区，导致数据倾斜
    *    4、大表 join 小表，产生的结果中某一个key的数据量特别大，导致数据倾斜
    *    5、大表1 join 大表2，大表1中存在个别key数据量特别大，导致数据倾斜-------------->多数key倾斜不能用
    *    6、大表1 join 大表2，大表1中存在有很多的key数据量特别大，导致数据倾斜---------->
    */
  val spark = SparkSession.builder()
    .master("local[4]")
    .appName("datatest")
    //关闭自动广播
    .config("spark.sql.autoBroadcastJoinThreshold",-1)
    //.config("spark.sql.autoBroadcastJoinThreshold",10485760)
    .getOrCreate()
  import spark.implicits._
  /**
    * 在进行shuffle操作的时候,很多的key都是null,这些key为null的数据全部聚集在0号分区，0号出现了数据倾斜
    *    解决: 过滤掉null的数据
    */
  @Test
  def solution1(): Unit ={

    val student= Seq[(Int,String,Int,String)](
      (1,"zhangsan-1",20,null),
      (2,"zhangsan-2",20,null),
      (3,"zhangsan-3",20,null),
      (4,"zhangsan-4",20,"class_01"),
      (5,"zhangsan-5",20,null),
      (6,"zhangsan-6",20,null),
      (7,"zhangsan-7",20,"class_02"),
      (8,"zhangsan-8",20,null),
      (9,"zhangsan-9",20,null),
      (10,"zhangsan-10",20,"class_03")
    ).toDF("id","name","age","clazz_id")


    val clazz = Seq[(String,String)](
      ("class_01","java班"),
      ("class_02","python班"),
      ("class_03","大数据班")
    ).toDF("id","name")

    //student.createOrReplaceTempView("student")
    clazz.createOrReplaceTempView("clazz")

    //需求：获取学生的详细信息以及对应的班级的名称
    student.filter("clazz_id is null").createOrReplaceTempView("student_null")
    student.filter("clazz_id is not null").createOrReplaceTempView("student_not_null")
    spark.sql(
      """
        |select t.id,t.name,t.age,null as c_name
        | from student_null t
        |union
        |select s.id,s.name,s.age,c.name c_name
        | from student_not_null s left join clazz c
        | on s.clazz_id = c.id
      """.stripMargin).rdd.mapPartitionsWithIndex((index,it)=>{
      println(s"index:${index}  data:${it.toBuffer}")
      it
    }).collect()
  }

  /**
    * 在groupby的时候，某一个key的数据特别多，该key的数据都会聚集在一个分区，导致数据倾斜
    * 解决方案:
    *     局部聚合+全局聚合
    */
  @Test
  def solution2(): Unit ={
    val student= Seq[(Int,String,Int,String)](
      (1,"zhangsan-1",20,"class_01"),
      (2,"zhangsan-2",20,"class_01"),
      (3,"zhangsan-3",20,"class_01"),
      (4,"zhangsan-4",20,"class_01"),
      (5,"zhangsan-5",20,"class_01"),
      (6,"zhangsan-6",20,"class_0"),
      (7,"zhangsan-7",20,"class_02"),
      (8,"zhangsan-8",20,"class_01"),
      (9,"zhangsan-9",20,"class_02"),
      (10,"zhangsan-10",20,"class_03")
    ).toDF("id","name","age","clazz_id")


    //需求:统计每个班的年龄的平均值
    /*spark.sql("select clazz_id,avg(age) avg_age from student group by clazz_id")*/

    //局部聚合<对分组的key进行加盐处理，其实添加随机数>
    //1、对分组的key添加随机数
    spark.udf.register("addPrefix",addPrefix _)
    spark.udf.register("unPrefix",unPrefix _)
    student.selectExpr("id","name","age","addPrefix(clazz_id) clazz_id").createOrReplaceTempView("tmp1")
    //2、局部聚合
    spark.sql("select clazz_id,sum(age) age_sum,count(age) age_count from tmp1 group by clazz_id").createOrReplaceTempView("tmp2")

    //3、全局聚合
    spark.sql(
      """
        |select unPrefix(clazz_id),sum(age_sum)/sum(age_count)
        | from tmp2
        | group by unPrefix(clazz_id)
      """.stripMargin).show
  }

  /**
    * 大表 join 小表，产生的结果中某一个key的数据量特别大，导致数据倾斜
    *    解决方案: 小表广播出去，避免shuffle,从而避免数据倾斜
    */
  @Test
  def solution3(): Unit ={
    val student= Seq[(Int,String,Int,String)](
      (1,"zhangsan-1",20,"class_01"),
      (2,"zhangsan-2",20,"class_01"),
      (3,"zhangsan-3",20,"class_01"),
      (4,"zhangsan-4",20,"class_01"),
      (5,"zhangsan-5",20,"class_01"),
      (6,"zhangsan-6",20,"class_01"),
      (7,"zhangsan-7",20,"class_02"),
      (8,"zhangsan-8",20,"class_01"),
      (9,"zhangsan-9",20,"class_02"),
      (10,"zhangsan-10",20,"class_03")
    ).toDF("id","name","age","clazz_id")
      .createOrReplaceTempView("student")

    val clazz = Seq[(String,String)](
      ("class_01","java班"),
      ("class_02","python班"),
      ("class_03","大数据班")
    ).toDF("id","name")
      .createOrReplaceTempView("clazz")

    //缓存小表，从而达到让小表100%广播出去
    //spark.sql("cache table clazz")

    spark.sql(
      """
        |select  s.id,s.name,s.age,c.name
        | from student s left join clazz c
        | on s.clazz_id = c.id
      """.stripMargin).show
  }

  /**
    * 大表1 join 大表2，大表1中存在个别key数据量特别大，导致数据倾斜
    *  解决方案:
    *     1、将没有出现数据倾斜的key的数据照常处理
    *     2、将处理数据倾斜key的数据添加随机数，进行操作
    *   随机数可以是1000以下
    */
  @Test
  def solution4(): Unit ={

    val student= Seq[(Int,String,Int,String)](
      (1,"zhangsan-1",20,"class_01"),
      (2,"zhangsan-2",20,"class_01"),
      (3,"zhangsan-3",20,"class_01"),
      (4,"zhangsan-4",20,"class_01"),
      (5,"zhangsan-5",20,"class_01"),
      (6,"zhangsan-6",20,"class_01"),
      (7,"zhangsan-7",20,"class_02"),
      (8,"zhangsan-8",20,"class_01"),
      (9,"zhangsan-9",20,"class_02"),
      (10,"zhangsan-10",20,"class_03")
    ).toDF("id","name","age","clazz_id")

    val clazz = Seq[(String,String)](
      ("class_01","java班"),
      ("class_02","python班"),
      ("class_03","大数据班")
    ).toDF("id","name")
    //fixme  通过抽样，得知哪个key出现了数据倾斜 ??? 怎么定位是数据倾斜,怎么确认有多少个task   tag图
    //student.sample(false,0.6).show()

    //1、将两个表中出现数据倾斜的key的数据过滤出来
    val solutionStudentDF: Dataset[Row] = student.filter("clazz_id='class_01'")
    val solutionClazzDF: Dataset[Row] = clazz.filter("id='class_01'")
    //2、将两个表中没有出现数据倾斜的key的数据过滤出来
    student.filter("clazz_id!='class_01'").createOrReplaceTempView("student_tmp1")
    clazz.filter("id!='class_01'").createOrReplaceTempView("clazz_tmp1")
    //3、对没有出现数据倾斜的key的数据正常操作
      spark.sql(
        """
          |select s.id,s.name,s.age,c.name
          | from student_tmp1 s left join clazz_tmp1 c
          | on s.clazz_id = c.id
        """.stripMargin).createOrReplaceTempView("total_tmp1")
    //4、对出现数据倾斜的key的数据，添加随机数，扩容数据，进行操作

    spark.udf.register("addPrefix",addPrefix _)
    spark.udf.register("addIndexPrefix",addIndexPrefix _)
    solutionStudentDF.selectExpr("id","name","age","addPrefix(clazz_id) clazz_id").createOrReplaceTempView("student_tmp2")

    //对另外一个表扩容
    capacity(solutionClazzDF,spark).createOrReplaceTempView("clazz_tmp2")

    spark.sql(
      """
        |select s.id,s.name,s.age,c.name
        | from student_tmp2 s left join clazz_tmp2 c
        | on s.clazz_id = c.id
      """.stripMargin).createOrReplaceTempView("total_tmp2")


    spark.sql(
      """
        |select * from total_tmp2
        |union
        |select * from total_tmp1
      """.stripMargin).show
    /**
      * student
      * id  name age clazz_id
      * 1  zhangsan-1 20 class_01#1
      * 2  zhangsan-2 20 class_01#5
      * 3  zhangsan-3 20 class_01#7
      * 4  zhangsan-4 20 class_01#3
      * 5  zhangsan-5 20 class_01#9
      *
      * clazz
      * id  name
      * class_01#1 java班
      * class_01#2 java班
      * class_01#3 java班
      * class_01#4 java班
      * class_01#5 java班
      * class_01#6 java班
      * class_01#7 java班
      * class_01#8 java班
      * class_01#9 java班
      */
  }

  /**
    * 大表1 join 大表2，大表1中存在有很多的key数据量特别大，导致数据倾斜
    * 解决方案:
    *    直接将有数据倾斜key的表添加随机数，将另一个表扩容
    *    一般随机数是10以下
    */
  @Test
  def solution5(): Unit ={
    val student= Seq[(Int,String,Int,String)](
      (1,"zhangsan-1",20,"class_01"),
      (2,"zhangsan-2",20,"class_01"),
      (3,"zhangsan-3",20,"class_01"),
      (4,"zhangsan-4",20,"class_01"),
      (5,"zhangsan-5",20,"class_01"),
      (6,"zhangsan-6",20,"class_01"),
      (7,"zhangsan-7",20,"class_02"),
      (8,"zhangsan-8",20,"class_01"),
      (9,"zhangsan-9",20,"class_02"),
      (10,"zhangsan-10",20,"class_03")
    ).toDF("id","name","age","clazz_id")

    val clazz = Seq[(String,String)](
      ("class_01","java班"),
      ("class_02","python班"),
      ("class_03","大数据班")
    ).toDF("id","name")

    spark.udf.register("addPrefix",addPrefix _)
    spark.udf.register("addIndexPrefix",addIndexPrefix _)
    //1、对有数据倾斜的key的表进行随机数添加
    student.selectExpr("id","name","age","addPrefix(clazz_id) clazz_id").createOrReplaceTempView("student")
    //2、对另一个表扩容,扩容的大小是添加数据数的大小(添加10以内随机数,扩容10倍)
    capacity(clazz,spark).createOrReplaceTempView("clazz")

    spark.sql(
      """
        |select * from clazz
      """.stripMargin).show

    spark.sql(
      """
        |select s.id,s.name,s.age,c.name,s.clazz_id,c.id
        | from student s left join clazz c
        | on s.clazz_id = c.id
      """.stripMargin).show
  }

  /**
    * 对数据进行扩容
    * @param clazzDF
    */
  def capacity(clazzDF:DataFrame,spark:SparkSession)={

    //1、创建一个空的dataFrame与参数的DataFrame的shema信息一样
    val emptyRdd: RDD[Row] = spark.sparkContext.emptyRDD[Row]
    var emptyDF: DataFrame = spark.createDataFrame(emptyRdd,clazzDF.schema)

    //2、循环1-9，添加给定的数字作为后缀，将其产生的dataFrame添加到创建的dataFrame中

    for(i<- 0 to 10){
      emptyDF = emptyDF.union(clazzDF.selectExpr(s"addIndexPrefix(id,${i}) id","name"))
    }
    //3、数据返回
    emptyDF
  }

  /**
    * 给classid添加一个指定的数字后缀
    * @param id
    * @param i
    * @return
    */
  def addIndexPrefix(id:String,i:Int)={
    s"${id}#${i}"
  }

  /**
    * 对指定的字段添加一个随机数
    * @param classId
    * @return
    */
  def addPrefix(classId:String):String={
    s"${classId}#${Random.nextInt(10)}"
  }

  /**
    * 去掉随机数后缀
    * @param classId
    * @return
    */
  def unPrefix(classId:String):String={
    classId.split("#")(0)
  }
}
