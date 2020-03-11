package com.hello.scala.foundation

/**
  * 匹配数组.元组
  */
object CaseDemo03 extends App {

  val arr = Array(1, 3, 5)
  arr match {
    case Array(1, x, y) => println(x + " " + y)
    case Array(0) => println("only 0")
    case Array(0, _*) => println("0 ...")
    case _ => println("something else")
  }

  val lst = List(3, -1)
  lst match {
    case 0 :: Nil => println("only 0")
    case x :: y :: Nil => println(s"x: $x y: $y")
    case 0 :: tail => println("0 ...")
    case _ => println("something else")
  }

  val tup = (2, 3, 7)
  tup match {
    case (_, z, 5) => println(z)
    case (x, y, z) => println(s"1, $x , $y")
    case _ => println("else")
  }
}
