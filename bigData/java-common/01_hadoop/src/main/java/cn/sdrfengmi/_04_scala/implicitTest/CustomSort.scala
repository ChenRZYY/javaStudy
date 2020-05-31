package cn.sdrfengmi._04_scala.implicitTest

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

//Scala提供两个特质（trait）Ordered与Ordering用于比较。其中，Ordered混入（mix）Java的Comparable接口，而Ordering则混入Comparator接口。众所周知，在Java中
//实现Comparable接口的类，其对象具有了可比较性；
//实现comparator接口的类，则提供一个外部比较器，用于比较两个对象。
object OrderContext {

  implicit val girlOrdering = new Ordering[Girl] {
    override def compare(x: Girl, y: Girl): Int = {
      if (x.faceValue > y.faceValue) 1
      else if (x.faceValue == y.faceValue) {
        if (x.age > y.age) -1 else 1
      } else -1
    }
  }
}


/**
  * Created by root on 2016/5/18.
  */
//sort =>规则 先按faveValue，比较年龄
//name,faveValue,age

object CustomSort {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("CustomSort").setMaster("local[2]")
    val sc: SparkContext = new SparkContext(conf)
    val rdd1: RDD[(String, Int, Int, Int)] = sc.parallelize(List(("yuihatano", 90, 28, 1), ("angelababy", 90, 27, 2), ("JuJingYi", 95, 22, 3)))
    import OrderContext._  //应用上面的Ordering 对比
    val rdd2 = rdd1.sortBy(x => Girl(x._2, x._3), false)
    println(rdd2.collect().toBuffer)
    sc.stop()
  }
}

/**
  * 第一种方式
  * //  * @param faceValue
  * //  * @param age
  * *
  * case class Girl(val faceValue: Int, val age: Int) extends Ordered[Girl] with Serializable {
  * override def compare(that: Girl): Int = {
  * if(this.faceValue == that.faceValue) {
  *that.age - this.age
  * } else {
  *this.faceValue -that.faceValue
  * }
  * }
  * }
  */

/**
  * 第二种，通过隐式转换完成排序
  *
  * @param faceValue
  * @param age
  */
case class Girl(faceValue: Int, age: Int) extends Serializable