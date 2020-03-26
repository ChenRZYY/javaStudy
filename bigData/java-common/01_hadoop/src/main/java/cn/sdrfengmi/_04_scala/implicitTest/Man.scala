package cn.sdrfengmi._04_scala.implicitTest

//一、概念
//Scala 2.10引入了一种叫做隐式类的新特性。隐式类指的是用implicit关键字修饰的类。在对应的作用域内，带有这个关键字的类的主构造函数可用于隐式转换。
//隐式转换和隐式参数是Scala中两个非常强大的功能，利用隐式转换和隐式参数，你可以提供优雅的类库，对类库的使用者隐匿掉那些枯燥乏味的细节。

//二、作用
//隐式的对类的方法进行增强，丰富现有类库的功能

//三、隐式参数
//1）关键字：implicat
//2）隐式的东西只能在object里面才能使用
//3）作用域

//四、隐式转换函数
//是指那种以implicit关键字声明的带有单个参数的函数。
//可以通过：:implicit –v这个命令显示所有做隐式转换的类。

//四、隐士转换的发生的时机
//1、当一个对象去调用某个方法，但是这个对象并不具备这个方法
//2、调用某个方法的时候，这个方法确实也存在，存入的参数类型不匹配

//方法都是工具, 为了调用方法必须new 对象,但是想不new对象就能调用方法,显示转换,隐式转换 ---------------总结
// TODO 隐式转换就相当于继承了谁  A目标类  B转换类 C隐式转换方法   B___C桥梁___>A
//A B 共同点,1 new类的时候有共同的入参(不对),2 C桥梁放哪都行,谁想把B变成A谁写桥梁,要么导入别的类写的桥梁
// TODO 隐士参数
//3、视图边界

class Man(val name: String){

  def add()={
    println(this+"+add")
  }
}


class SuperMan {

  println(this+"+new SuperMan")

  def fly(): Unit = {
    println(this+"+fly 我要上天")
  }

  def eat() = {
    "chichi"
  }

  def bianshen(jn: String) = {
    jn
  }
}

object SuperMan {
  //隐式转换，将Man转换为SuperMan
  implicit def man2SuperMan(man: Man) = {
    System.err.println(man.toString+"+implicit")
    new SuperMan
  }
//  implicit def manToSuperMan(man: Man) = {   //隐式转换方法只有一个隐士类
//    System.err.println(man.toString+"+implicit")
//    new SuperMan
//  }

  def main(args: Array[String]): Unit = {
    val lang: Man = new Man("灰太狼")
    lang.fly()
    lang.add()

    val str: String = new Man("灰太狼").bianshen("36变换") //有参数的也能调用
    System.out.println(str)
  }
}