package cn.hp._04_scala.implicitTest

class ImplicitParameter {
  def write(content: String) = println(content)
}

object ImplicitTest{
  implicit val signPen = new ImplicitParameter   //必须导入或者
}

object ImplicitParameter {

  def signForExam(name: String)(implicit signPen: ImplicitParameter) {
    signPen.write(name + " come to exam in time.")
  }

//  implicit val signPen = new ImplicitParameter   //TODO 1自己写 必须导入或者
  import ImplicitTest._  //TODO 2导入外部隐士转换
  def main(args: Array[String]): Unit = {
    signForExam("chenzhendong")
  }

}

