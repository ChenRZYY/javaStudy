package cn.sdrfengmi._04_scala.foundation

import java.io.File

import scala.io.Source

//隐式转换和隐式参数
//隐式的增强File类的方法
class RichFile(var from: File) {

  //  def this() = this(new File()) //给个默认值,类可以有无惨构造, TODO 打开报错,后面研究
  var path1: String = ""

  def this(path: String, from: File) { // 接收2个参数
    this(from)
    this.path1 = path
  }


  def strRead(path: String) = {
    Source.fromFile(path1).mkString
  }

  def read = Source.fromFile(from.getPath).mkString
}

object RichFile {
  //隐式转换方法
  implicit def file2RichFile(from: File) = new RichFile(from)

  implicit def fileStr2RichFile(from: String) = {
    val richFile = new RichFile(new File(from))
    richFile.path1 = from
    richFile
  }

  implicit def add2(a: Int) = new RichFile(new File("d://words.txt"))

  implicit def add(a: Int) = a + 1

}

object MainApp {
  def main(args: Array[String]): Unit = {
    //导入隐式转换
    import RichFile._ //所有隐式转换方法导入进来

    println(new File("d://words.txt").read) //new File 就是调用入参为File的隐式转换方法file2RichFile,方法返回参数为类,所以才能调用
    println("d://words.txt".strRead("")) //string也是调用入参为String的方法 fileStr2RichFile,方法返回RichFile 才能调用strRead
    println(add(1)) //隐式转换方法能直接调用
    println(1.read) //调用入参为Int的隐式转换方法add2 ,返回RichFile
    println(file2RichFile(new File("d://words.txt")).read) //调用隐式转换方法,和直接调用方法是一样的

    // 定义隐式转换方法
    implicit def fileDouble2RichFile(from: Double) = new RichFile(new File("d://words.txt"))

    println(5.42D.read) //直接写隐式转转这个Double类型就能应用

  }
}
