package cn.hp._04_scala.implicitTest


import java.io.File

import scala.io.Source

//使用隐式转换方式 使用
class RichFile(val file: File) {
  //定义一个read方法，返回String类型
  def read(): String = {
    println(this)
    Source.fromFile(file.getPath).mkString
  }

}

object RichFile {
  //隐式转换方法（将原有的File类型转成了file类型，在用的时候需要导入相应的包）
  implicit def file2RichFile(file: File) = new RichFile(file)

  def getName(name: String): String = {
    name
  }
}

object MainApp {

  def main(args: Array[String]): Unit = {
    //目的是使用File的时候不知不觉的时候直接使用file.read()方法，所以这里就要做隐式转换
    val file = new File("dataset/source.txt")
    //导入隐式转换，._将它下满的所有的方法都导入进去了。
    import RichFile._
    //这里没有的read()方法的时候，它就到上面的这一行中的找带有implicit的定义方法
    //隐士转换,自动增强类的功能,替代了extends,上面导包,
    // 1 隐士方法和变量直接能被应用,
    // 2 非隐士public方法变量调用或者点击的时候被调用  直接能进行方法使用,因为这是伴生对象
    // 3 private方法和变量必须 new对象的时候才能被调用
    val str = file.read()
    val str2 = file.read()
    val str3 = file read() //可以不用点
    val str4: String = getName("") //可以直接调用
    //打印读取的内容
    println(str)
//    val richFile: RichFile = new RichFile(new File())
//    richFile.read

  }

}