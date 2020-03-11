package cn.hp._04_scala.foundation

import org.junit.Test

import scala.collection.immutable

object ForArrayDemo {

  def forArrayMethed() = {
    //初始化一个数组
    val arr = Array(1, 2, 3, 4, 5, 6, 7, 8)
    //增强for循环
    for (i <- arr)
      println(i)

    //好用的until会生成一个Range
    //reverse是将前面生成的Range反转
    for (i <- (0 until arr.length).reverse)
      println(arr(i))

    val ints: immutable.IndexedSeq[Int] = for (i <- 1 to 10) yield i * 10
    println(ints)

    for (i <- (0 until 8)) print(i)

  }


  @Test
  def forMethed() = {
    //for(i <- 表达式),表达式1 to 10返回一个Range（区间）
    //每次循环将区间中的一个值赋给i
    for (i <- 1 to 10)
      println(i)

    //for(i <- 数组)
    val arr = Array("a", "b", "c")
    for (i <- arr)
      println(i)

    //高级for循环 迭代/多层for循环
    //每个生成器都可以带一个条件，注意：if前面没有分号
    for (i <- 1 to 3; j <- 1 to 3)
      print((10 * i + j) + " ")
    println("-------------------------------")

    //高级for循环
    //每个生成器都可以带一个条件，注意：if前面没有分号
    for (i <- 1 to 3; j <- 1 to 3 if i != j)
      print((10 * i + j) + " ")
    println("++++++++++++++++++++++++++++++++++")

    //for推导式：如果for循环的循环体以yield开始，则该循环会构建出一个集合
    //每次迭代生成集合中的一个值
    val v = for (i <- 1 to 10) yield i * 10
    println(v)
  }
}