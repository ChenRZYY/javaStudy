package cn.hp._04_scala.implicitTest

// 2、调用某个方法的时候，这个方法确实也存在，存入的参数类型不匹配  隐式转换,直接相当于双重身份,一个对象变成两个对象
//方法都是工具, 为了调用方法必须new 对象,但是想不new对象就能调用方法,显示转换,隐式转换

//特殊人群（儿童和老人）
class SpecialPerson(var name: String)

//儿童
class Children(var name: String)

//老人
class Older(var name: String)

//青年工作者
class Worker(var name: String)

//特殊人群买票窗口
class TicketHouse {
  def buyTicket(p: SpecialPerson): Unit = {
    println(p.name + "买到票了")
  }
}

object MyPredef {
  //隐式转换，将儿童转换为特殊人群
  implicit def children2SpecialPerson(c: Children) = new SpecialPerson(c.name)

  //隐式转换，将老人转换为特殊人群
  implicit def older2SpecialPerson(o: Older) = new SpecialPerson(o.name)

}

object TestBuyTicket {
  def main(args: Array[String]): Unit = {
    //导入MyPredef类中的所有隐式转换
    import MyPredef._
    val house = new TicketHouse
    //测试儿童买票
    val children = new Children("wangbaoqiang")
    house.buyTicket(children)
    //测试老人买票
    val older = new Older("xuzheng")
    house.buyTicket(older)
    //测试青年工作者买票
    val worker = new Worker("huangbo")
    //house.buyTicket(worker)//放开的话会报错
  }
}