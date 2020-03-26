package cn.sdrfengmi._04_scala.foundation

/**
  * // 有时我们可以在创建类的对象时，指定该对象混入某个trait，这样，就只有这个对象混入该trait的方法，而类的其他对象则没有
  */
trait Logged {
  def log(msg: String) {}

}

trait AMyLogger extends Logged {
  override def log(msg: String): Unit = {
    println("test:" + msg)
  }

  //  def copy(name: String): String = {
  //    name + "AMyLogger"
  //  }  有相同的方法为什么不行
}

trait BMyLogger extends Logged {
  override def log(msg: String): Unit = {
    println("log:" + msg)
  }

  //  def copy(name: String): String = {
  //    name + "BMyLogger"
  //  } 有相同的方法为什么不行
}

class Person1(val name: String) extends AMyLogger {
  def sayHello(): Unit = {
    println("Hi ,i'm name")
    log("sayHello is invoked!")
  }

}

object Test {
  def main(args: Array[String]) {
    val p1 = new Person1("liudehua")
    p1.sayHello()


    val p2 = new Person1("zhangxueyou") with BMyLogger //什么时候用,什么时候继承
    p2.sayHello()
    p2.log("陈振东")
    //    p2.copy("陈振佳")
  }
}