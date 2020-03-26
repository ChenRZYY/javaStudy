package cn.sdrfengmi._04_scala

/**
  * @Author 陈振东
  * @create 2020/3/24 10:09
  *         Scala的隐式转换的目的
  *          通过隐式转换，程序员可以在编写Scala程序时故意漏掉一些信息，让编译器去尝试在编译期间自动推导出这些信息来，这种特性可以极大的减少代码量，
  *          忽略那些冗长，过于细节的代码。
  *         3、classOf、isInstanceOf、asInstanceOf区别
  *         (1)classOf[T]: 获取类型T的Class对象
  *         (2)isInstanceOf[T]: 判断对象是否为T类型的实例。
  *         (3)asInstanceOf[T]: 强制类型转换
  *
  */
object CastClass {

  /**
    * 第一种类型转换方式
    *
    * @param s string
    * @return 转换后的类
    */
  def parseDouble(s: String): Option[Double] = try {
    Some(s.toDouble)
  } catch {
    case _ => None
  }

  /**
    * 第二种类型转换方式
    *
    * @param s string
    * @tparam T class type
    * @return option
    */
  def parse[T: ParseOp](s: String): Option[T] = try {
    Some(implicitly[ParseOp[T]].op(s))
  } catch {
    case _ => None
  }

  /* 隐式转换类型类*/
  case class ParseOp[T](op: String => T)

  implicit val popDouble = ParseOp[Double](_.toDouble)
  implicit val popInt = ParseOp[Int](_.toInt)
  implicit val popLong = ParseOp[Long](_.toLong)
  implicit val popFloat = ParseOp[Float](_.toFloat)

  // https://stackoverflow.com/questions/9542126/how-to-find-if-a-scala-string-is-parseable-as-a-double-or-not
  def test() = {
    val dd02 = "0.234"
    println(dd02.isInstanceOf[String]) // 判断是否为String类型

    parseDouble(dd02) match { //正确的方式
      case Some(t) => println(t)
      case None => println(None)
    }
    println(parse[Double](dd02)) // 正确的转换方式
    println(dd02.asInstanceOf[Double]) // 错误的转换方式：强制类型转换：java.lang.String cannot be cast to java.lang.Double
    println(dd02.toFloat) // 错误的转换方式，会抛出异常：  java.lang.String cannot be cast to java.lang.Double

  }
}
