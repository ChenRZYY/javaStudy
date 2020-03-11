package cn.hp._04_scala

class functionDome {

}


// 1>.有参函数和无参函数以及方法转换成函数案例 (有参方法,无参方法)
object Method {
  /**
    * 定义有参函数 :
    * 定义个 sum 方法(用关键字def来标识方法), 该方法有 3 个参数(即a,b和c), 参数类型为整型, 方法的返回值为整型，
    */
  def sum(a: Int, b: Int, c: Int): Int = {
    //方法体是将形参列表的三个参数进行相加操作，相加的结果就是返回值的整形
    a + b + c
  }

  /**
    * 定义无参函数 :
    * 改方法没有任何参数, 也没有返回值。注意：如果方法没有括号"()" 调用时不能加"()"
    */
  def sayHello() = {
    println("I'm Yinzhengjie！")
  }

  def main(args: Array[String]): Unit = {
    var res = sum(100, 200, 300) //调用有参函数
    println(res)
    sayHello() //调用无参函数,调用时可以省略括号"()", 也可以不省略。如果方法没有括号"()"，调用时不能加"()"
    var f1 = sum _ //方法可转换为函数，格式很简单，只需要在方法名后面加个空格和下划线即可。
    var res1 = f1(1, 2, 3)   //调用我们将方法转换过来的函数
    println(res1)
  }
}