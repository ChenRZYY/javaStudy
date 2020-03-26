package cn.sdrfengmi._05_spark._01_core

import org.apache.spark.util.{AccumulatorV2, LongAccumulator}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.Test

import scala.collection.mutable

/**
  * spark中默认只有数值型的累加器全局累加器,自定义全局累加器
  */
class Accumulator {

  val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("Accumulator"))

  @Test
  def accu(): Unit = {
    var sum = 0
    sc.parallelize(Seq(1, 2, 3))
      .foreach(item => sum = sum + item)
    //sum = 0
    println(sum)
  }

  @Test
  def accu2(): Unit = {
    val accuAccumulator: LongAccumulator = sc.longAccumulator("accu")
    sc.parallelize(Seq(1, 2, 3))
      .foreach(item => accuAccumulator.add(item)) //只有一个add方法
    //sum = 0
    println(accuAccumulator.value)
  }

  @Test
  def accu3(): Unit = {

    val accu: NumAccumulator = new NumAccumulator
    sc.register(accu, "accu")
    sc.parallelize(Seq("aa", "bb", "cc"))
      .foreach(item => accu.add(item))

    println(accu.value)
  }
}

/**
  * AccumulatorV2
  * IN:累加元素的类型
  * OUT:结果的类型
  */
class NumAccumulator extends AccumulatorV2[String, Set[String]] {
  val num = mutable.Set[String]()

  /**
    * 判断累加器是否为空
    *
    * @return
    */
  override def isZero: Boolean = {
    num.isEmpty
  }

  /**
    * 对当前的累加器进行复制
    *
    * @return
    */
  override def copy(): AccumulatorV2[String, Set[String]] = {
    val accu = new NumAccumulator
    accu.num ++= this.num
    accu
  }

  /**
    * 清空累加器
    */
  override def reset(): Unit = {
    num.clear()
  }

  /**
    * 累加元素
    *
    * @param v
    */
  override def add(v: String): Unit = {
    num += v
  }

  /**
    * 多个分区的累加器进行合并
    *
    * @param other
    */
  override def merge(other: AccumulatorV2[String, Set[String]]): Unit = {
    num ++= other.value
  }

  /**
    * 取出累加器的值
    *
    * @return
    */
  override def value: Set[String] = {
    num.toSet
  }
}


