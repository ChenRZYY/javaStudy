package com.hello.scala.implicitTest

class Man(val name: String)
//一、概念
//Scala 2.10引入了一种叫做隐式类的新特性。隐式类指的是用implicit关键字修饰的类。在对应的作用域内，带有这个关键字的类的主构造函数可用于隐式转换。
//隐式转换和隐式参数是Scala中两个非常强大的功能，利用隐式转换和隐式参数，你可以提供优雅的类库，对类库的使用者隐匿掉那些枯燥乏味的细节。

//二、作用
//隐式的对类的方法进行增强，丰富现有类库的功能

//三、隐式参数
//1）关键字：implicat
//2）隐士的东西只能在object里面才能使用
//3）作用域

//四、隐式转换函数
//是指那种以implicit关键字声明的带有单个参数的函数。
//可以通过：:implicit –v这个命令显示所有做隐式转换的类。

//四、隐士转换的发生的时机
//1、当一个对象去调用某个方法，但是这个对象并不具备这个方法
//2、调用某个方法的时候，这个方法确实也存在，存入的参数类型不匹配

//方法都是工具, 为了调用方法必须new 对象,但是想不new对象就能调用方法,显示转换,隐式转换 ---------------总结

//3、视图边界
class SuperMan {
  def fly(): Unit = {
    println("我要上天")
  }

  def eat() = {
    "chichi"
  }
}

object SuperMan {
  //隐式转换，将Man转换为SuperMan
  implicit def man2SuperMan(man: Man) = new SuperMan

  def main(args: Array[String]): Unit = {
    new Man("灰太狼").fly
  }
}