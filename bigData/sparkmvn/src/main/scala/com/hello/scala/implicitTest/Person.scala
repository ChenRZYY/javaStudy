package com.hello.scala.implicitTest

//人狗之恋 3、视图边界
class Person(val name: String) {
  def sayHello: Unit = {
    println("Hello, my name is " + name)
  }

  //2个人交朋友
  def mkFridens(p: Person): Unit = {
    sayHello
    p.sayHello
  }
}

class Student(name: String) extends Person(name)

class Dog(val name: String)

//聚会时2个人交朋友
class Party[T <% Person](p1: Person, p2: Person) {
  p1.mkFridens(p2)
}

object Test {
  //隐式转换，将狗转换成人
  implicit def dog2Person(dog: Dog): Person = {
    new Person(dog.name)
  }

  def main(args: Array[String]): Unit = {
    val huangxiaoming = new Person("huangxiaoming")
    val angelababy = new Student("angelababy")
    new Party[Person](huangxiaoming, angelababy)

    println("------------------------------------------------")

    val erlangshen = new Person("erlangshen")
    val xiaotianquan = new Dog("xiaotianquan")
    new Party[Person](erlangshen, xiaotianquan)
  }
}