package cn.hp._04_scala.foundation

object ClassTagExample {


  import scala.reflect._
  def makePair[T: ClassTag](first: T, second: T) = {
    val r = new Array[T](2)
    r(0) = first
    r(1) = second
    r
  }

}
