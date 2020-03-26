package cn.sdrfengmi._04_scala.foundation

import scala.util.Random

/**
  * 模式匹配 匹配类型  匹配字符串
  */
object CaseDemo extends App {

  val arr = Array("dong", "chen", "zhen")
  val name = arr(Random.nextInt(arr.length))

  System.exit(0)
  name match {
    case "chen" => {
      print("chen")
    }
    case "zhen" => print("zhen")
    case "dong" => print("dong")
    case _ => {
      print("hahaha")
    }

      val classType = Array("hello", 1, 2.0)
      val v = classType(Random.nextInt(4))
      print(v)
      v match {
        case x: Int => println("Int" + x)
        case y: Double if (y > 0) => println("Double" + y)
        case z: String if (z == "chen") => println("String" + z)
        case _ => throw new Exception("not match exception")
      }


      //匹配数组.元组
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


}
