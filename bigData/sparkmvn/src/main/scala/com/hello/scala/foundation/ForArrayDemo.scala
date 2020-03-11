package com.hello.scala.foundation

import scala.collection.immutable

object ForArrayDemo {

  def main(args: Array[String]) {
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
}