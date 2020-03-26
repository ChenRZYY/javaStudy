package cn.sdrfengmi.spark._02_sql.taxi2

object EitherTest {

  def main(args: Array[String]): Unit = {

    val parse: Int => Int = (b:Int) => {
      10 / b
    }

    def safe(f:(Int)=>Int,b:Int):Either[(Int,Exception),Int]={
      try{

        val result: Int = f(b)
        Right(result)
      }catch {
        case e:Exception => Left(b,e)
      }
    }

    val result: Either[(Int, Exception), Int] = safe(parse,10)

    result match {
      case Left((b,e)) => println(s"Left:${b} ,${e}")
      case Right(r) =>println(s"Right:${r}")
    }

    println(result.right.get)
  }
}
