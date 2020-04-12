package cn.sdrfengmi.spark._07_youhua

import org.apache.spark.sql.SparkSession

object Product {

  def main(args: Array[String]): Unit = {

    //1、创建SparkSession
    val spark = SparkSession.builder().master("local[4]").appName("test").getOrCreate()
    import spark.implicits._
    //2、读取双11的数据
    val productDF = spark.read.option("header",true).csv("data/双十一淘宝美妆数据.csv")

    //3、读取主类、子类的数据
    val productTypeDS = spark.read.textFile("data/productType.txt")

    //4、转换主类、子类的数据格式
    //主类1  子类1  关键字1  关键字2  关键字3 ....
    //主类1 子类1 关键字1
    //主类1 子类1 关键字2
    //主类1 子类1 关键字3
    productTypeDS.flatMap(line=>{
      //主类1  子类1  关键字1  关键字2  关键字3 ....
      val arr = line.split("\t")
      val mainType = arr(0)
      val subType = arr(1)
      val keywords = arr.slice(2,arr.length) //[关键字2，关键字2，...]
      //[(主类1，子类1，关键字1)，(主类1，子类1，关键字2)]
      keywords.map(item=>(mainType,subType,item))
    }).toDF("mainType","subType","keyword").createOrReplaceTempView("productType")

    spark.udf.register("containsKeyword",containsKeyword _)

    productDF.createOrReplaceTempView("product")
    spark.sql(
      """
        |select p.*,t.mainType,t.subType
        | from product p left join productType t
        | on containsKeyword(p.title,t.keyword)
      """.stripMargin).foreach(println(_))
  }

  /**
    * 定义udf函数，查看title是否包含关键字
    * @param title
    * @param kw
    * @return
    */
  def containsKeyword(title:String,kw:String):Boolean={
    title.contains(kw)
  }
}
