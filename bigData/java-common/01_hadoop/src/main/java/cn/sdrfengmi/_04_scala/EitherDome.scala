package cn.sdrfengmi._04_scala

object EitherDome {

  def process(b: Double): Double = {
    val a = 10
    a / b
  }

  // Either => Left or Right
  // option => Some None  不能使用None
  def safe(f: Double => Double, b: Double): Either[Double, (Double, Exception)] = {
    try {
      val result = f(b)
      Left(result)
    } catch {
      case e: Exception => Right(b, e) //返回错误的数据和异常情况
    }
  }


  def main(args: Array[String]): Unit = {
    val result: Either[Double, (Double, Exception)] = safe(process, 0)
    result match {
      case Left(r) => println(r)
      case Right((b, e)) => println(b)
    }
    if (result.isRight) {
      result.right.get
    }

  }

}
