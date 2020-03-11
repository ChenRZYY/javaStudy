package com.hello.scala.foundation

import scala.util.Random

/**
  * 模式匹配 匹配类型  匹配字符串
  */
object CaseDemo1 extends App {

  val arr = Array("dong", "chen", "zhen")
  val name = arr(Random.nextInt(arr.length))

  name match {
    case "chen" => {
      print("chen")
    }
    case "zhen" => print("zhen")
    case "dong" => print("dong")
    case _ => {
      print("hahaha")
    }

      val arr = Array("hello", 1, 2.0, ApplyDemo)
      val v = arr(Random.nextInt(4))
      print(v)

      v match {
        case x: Int => println("Int" + x)
        case y: Double if (y > 0) => println("Double" + y)
        case z: String if (z == "chen") => println("String" + z)
        case _ => throw new Exception("not match exception")
      }


  }


}
