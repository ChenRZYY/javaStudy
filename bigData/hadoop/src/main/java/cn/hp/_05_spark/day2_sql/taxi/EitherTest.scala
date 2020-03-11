package cn.hp._05_spark.day2_sql.taxi

import scala.util.Try

object EitherTest {

  def main(args: Array[String]): Unit = {
    /**
      * 相当于 Parse 方法
      */
    def process(b: Int): Double = {
      val a = 10
      a / b
    }

    // Either => Left Or Right
    // Option => Some None

    def safe(f: Int => Double, b: Int): Either[(Int, Exception),Double] = {
      try {
        val result = f(b)
        Right(result)
      } catch {
        case e: Exception => Left(b, e)
      }
    }

    //  process(0.0)
    val result = safe(process, 0)


    result match {
      case Right(r) => println(r)
      case Left((b, e)) => println(b, e)
    }

  }
}
