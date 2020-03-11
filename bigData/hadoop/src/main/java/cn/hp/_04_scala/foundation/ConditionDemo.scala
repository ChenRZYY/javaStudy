package cn.hp._04_scala.foundation

/**
  * TODO 条件表达式比较简洁
  */
object ConditionDemo {
  def main(args: Array[String]) {
    val x = 3
    //判断x的值，将结果赋给y
    val y = if (x > 0) 1 else -1
    //打印y的值
    println(y)
    //支持混合类型表达式
    val z = if (x > 1) 1 else "error"
    //打印z的值
    println(z)

    //如果缺失else，相当于if (x > 2) 1 else ()
    val m = if (x > 2) 1
    println(m)

    //在scala中每个表达式都有值，scala中有个unit类，写做(),相当于Java中的void
    val n = if (x > 2) 1 else ()
    println(n)
    //if和else if
    val k = if (x < 0) 0
    else if (x >= 1) 1 else -1
    println(k)

    val s = if (x > 10) 1
    else if (x > 2) 2
    else if (x > 1) 1
    else "沙雕"
    println(s)
  }
}