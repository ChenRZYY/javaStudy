package cn.hp._04_scala

import java.util.Date

object FunctionDome {

  /**
    * 函数定义方式一:
    * f1 : 其中f1是对匿名函数签名的一个引用，我们可以通过f1去调用这个函数，
    * (x : Int) : 其中x是变量的名称，而Int是变量的类型
    * => : 表示的是函数标志
    * x * 2 : 表示的具体的函数体，即也是最终的返回值哟
    **/
  val f1 = (x: Int) => x * 2
  val function = (count: Int) => {
    count * 2
  }: Int //最后的Int定义出参类型
  /**
    * 函数定义方式而:
    * f2 : 其中f2是对匿名函数签名的一个引用，我们可以通过f2去调用这个函数，
    * (Int) : Int定义的是函数的参数类型，我这个定义了一个Int类型的参数，如果有多个用逗号分隔即可
    * => : 表示的是函数标志
    * Int : 表示的是返回值类型为Int
    * (x) : 注意，这里的x实际上是形参，这个参数的类型就是前面我们定义的Int类型
    * x * 2 : 表示的具体的函数体，即也是最终的返回值哟
    **/
  val f2: (Int) => Int = (x) => x * 2

  /**
    * 下面为没有任何参数的匿名函数, 函数的返回值为String类型.
    */
  val f3: () => String = () => "尹正杰"
  val f4 = () => 2 * 2

  def main(args: Array[String]): Unit = {
    //调用匿名函数f1
    var res1 = f1(10)
    println(res1)

    //调用匿名函数f2
    var res2 = f1(20)
    println(res2)

    //调用参数为空的匿名函数f3
    val Name = f3();
    println(Name)

  }
}

//一.传值调用和传名调用
object CalculateDemo {

  /**
    * 定义传值的方法 :
    * add方法拥有2个Int类型的参数， 返回值为2个Int的和。
    */
  def add(a: Int, b: Int)() = {
    a + b
  }

  /**
    * 定义传参的方法 :
    * add2方法拥有3个参数，第一个参数是一个函数(它拥有2个Int类型的参数，返回值为Int类型的函数)， 第二个，第三个为Int类型的参数
    */
  def add2(f: (Int, Int) => Int, a: Int, b: Int) = {
    f(a, b) // f(1, 2) => 1 + 2
  }

  /**
    * 定义传参的方法 :
    * 第一个参数是一个函数(它拥有1个Int类型的参数，返回值为Int类型的函数),第二个为Int类型的参数。
    */
  def add3(a: (Int) => Int, b: Int) = {
    //这里我们将第二个参数作为第一个函数的签名传递进去
    a(b) + b
  }

  /**
    * fxx你们函数是符合add2中的“f:(Int, Int) => Int”这个方法签名的，因此我们可以把它当做第一个参数进行传递
    */
  val f1 = (a: Int, b: Int) => a + b

  /**
    * 定义一个匿名函数，它需要传递一个参数，函数体的返回值是将传入的值乘以10并返回，返回值类型为Int。
    */
  val f2 = (x: Int) => (x * 10): Int

  def main(args: Array[String]): Unit = {

    //传值方式调用
    val res1 = add(100, 10 + 20)
    println(res1)

    //传参方式调用一,我们给匿名函数传参数，最终返回的结果和第二个参数以及第三个参数进行运算
    var res2 = add(f1(10, 20), 30)
    println(res2)

    //传参方式调用二,我们给匿名函数传参数，
    var res3: Int = add2(f1, 10, 20)
    println(res3)

    //传参方式调用
    val res4 = add3(f2, 8)
    println(res4)
  }
}

// 二.可变参数
object VariableParameter {

  /**
    * 定义一个可变参数的方法,参数的类型为任意类型，相当于Java中的Object类，当然你也可以为AnyVal或者AnyRef的子类
    */
  def methodManyParams(args: Any*) = {
    for (item <- args) {
      print(item + "|")
    }
    println()
  }

  /**
    * 可变参数一般放在参数列表的末尾。
    */
  def add(des: String, ints: Int*): Int = {
    var sum = 0
    for (value <- ints) {
      sum += value
    }
    print(des)
    sum
  }

  def main(args: Array[String]): Unit = {
    methodManyParams("尹正杰", "大数据", "云计算", "人工智能", "机器学习", "自动化运维", 2018)

    var res = add("计算结果 : ", 10, 20, 30, 40)
    println(res)

  }
}

//三.参数的默认值
object DefaultValuesParameters {

  /**
    * 参数定义时可以指定一个默认值
    *
    * @param path :   指定程序的安装路径
    * @param user :   指定安装的用户
    */
  def installationSoftware(path: String = "D:/yinzhengjie/BigData/Spark", user: String = "yinzhengjie") = {
    print(s"安装路径是: $path,当然的安装用户是 : ${user}\n")
  }

  def main(args: Array[String]): Unit = {
    //调用时如果不传递参数，就会使用函数或者方法的默认值
    installationSoftware()
    //调用时，如果传递了参数值，就会使用传递的参数值
    installationSoftware("E:/yinzhengjie/Hadoop/Scala", "Administrator")
    //调用时，如果传递了一个参数，那么就会覆盖第一个参数的值
    installationSoftware("/home/yinzhengjie/Spark/Scala")
    //如果想要给指定的参数赋值，可以采用键值对的方式赋值,赋值参数时，参数的名称和方法定义的名称需要保持一致！
    installationSoftware(user = "root")
    //当然赋值的方式可以打乱顺序，但是需要以键值对的方式传递哟！
    installationSoftware(user = "Scala", path = "/home/yinzhengjie/Hadoop/Spark")
  }
}

//四.高阶函数 　　高阶函数的定义：将其他函数作为参数或其结果是函数的函数。要注意的是传入的函数必须符合高阶函数参数中定义的函数签名。
object HigherFunction {
  /**
    * 定义一个高级函数：
    * f: (Int) => String
    * 第一个参数是带一个整型参数返回值为字符串的函数f
    * v: Int
    * 第二个参数是一个整型参数v
    * f(v)
    * 返回值为一个函数“f(v)”。
    */
  def apply(f: (Int) => String, v: Int) = {
    //返回的函数“f(v)”，即将第二个参数作为第一个函数的参数。
    f(v)
  }

  // 定义一个方法, 参数为一个整型参数, 返回值为String
  def layout(args: (Int)): String = {
    "[" + args.toString() + "]"
  }

  def main(args: Array[String]): Unit = {
    //注意，layout传入的参数个数以及返回值类型都必须符合高阶函数apply中定义的第一个参数的函数签名。
    println(apply(layout, 150))
  }
}

//高阶有用的函数用法展示：
object HigherFunctionTest {
  def main(args: Array[String]): Unit = {
    var res1 = (1 to 10).map(_ * 0.1)
    print(s"res1 =====> $res1\n")
    println("==========我是分割线=========")
    (1 to 10).map("*" * _).foreach(println)
    println("==========我是分割线=========")
    val arr = Array[Int](1, 2, 3, 4)
    val res2 = arr.reduceLeft(_ - _)
    val res3 = arr.reduceRight(_ - _)
    println("==========我是分割线=========")
    print(s"res2 =====> $res2 \n")
    println("==========我是分割线=========")
    print(s"res3 =====> $res3 \n")
  }
}

// 五.部分参数应用函数
// 部分参数应用函数定义：如果函数传递所有预期的参数，则表示已完全应用它。如果只传递几个参数并不是全部参数，那么将返回部分应用的函数。这样就可以方便地绑定一些参数，其余的参数可稍后填写补上。
object PartialParameter {

  /**
    * 定义个输出的方法, 参数为date, message
    */
  def log(date: Date, message: String) = {
    println(s"$date, $message")
  }

  def main(args: Array[String]): Unit = {
    //定义一个日期对象
    val date = new Date()
    /**
      * 调用log 的时候, 传递了一个具体的时间参数, message为待定参数。logBoundDate成了一个新的函数,它需要传递一个String对象。
      *
      */
    val logBoundDate: (String) => Unit = {
      //我们在调用log函数时，值传递了第一个参数，第二个参数我们空出来了，并没有传递，而是指定第二个参数的类型。
      log(date, _: String)
    }

    // 调用logBoundDate 的时候, 只需要传递待传的message 参数即可
    logBoundDate("I'm Yinzhengjie!")

    //当然你想要传递两个参数，直接调用log函数也是可以的哟！
    log(date, "I'm Yinzhengjie")
  }
}

//六.柯里化函数(Currying)
//　　柯里化(Currying，以逻辑学家Haskell Brooks Curry的名字命名)指的是将原来接受两个参数的函数变成新的接受一个参数的函数的过程。新的函数返回一个以原有第二个参数为参数的函数。因此我们可以说柯里化就是高阶函数的一种，而高阶函数不一定就是柯里化函数。
//　　柯里化的好处：有时候，你想要用柯里化来把某个函数参数单拎出来，以提供更多用于类型推断的信息。

object MyCurrying {

  /**
    * 常规方法求两个参数和的函数：
    * 我们看下这个方法的定义, 求2个数的和,需要传递两个参数
    */
  def add1(x: Int, y: Int) = x + y

  /**
    * 现在我们把上面的函数变一下形 :
    * 使用柯里化(Currying)两个参数和的函数：
    */
  def add2(x: Int)(y: Int) = x + y

  /**
    * 分析下其演变过程 :
    * (y: Int) => x + y 为一个匿名函数, 也就意味着 add3 方法的返回值为一个匿名函数.
    */
  def add3(x: Int) = (y: Int) => x + y

  def main(args: Array[String]): Unit = {

    var res1 = add1(10, 20)
    println(res1)

    /**
      * 这种方式（过程）就叫柯里化。经过柯里化之后，函数的通用性有所降低，但是适用性有所提高。
      */
    var res2 = add2(10)(20)
    println(res2)

    /**
      * 调用方式需要进行两次传值操作，有点类似我们之前说的部分参数应用函数
      */
    val res3 = add3(10)
    print(res3(20))
  }
}

//七.偏函数 被包在花括号内没有 match 的一组 case 语句是一个偏函数，它是 PartialFunction[-A, +B]的一个实例，“-A” 代表参数类型，“+B” 代表返回类型，常用作输入模式匹配。
object MyPartialFunction {

  /**
    * 定义一个函数，要求传入的参数是一个String类型，而返回值类型是Int类型
    */
  def func(language: String): Int = {
    if (language.equals("Python")) 100
    else if (language.equals("Golang")) 200
    else if (language.equals("Java")) 300
    else if (language.equals("Shell")) 400
    else if (language.equals("Scala")) 500
    else -1
  }

  /**
    * 上面的函数我们也可以用关键字match+case组合来匹配用户的输入值
    */
  def func2(num: String): Int = num match {
    //case 可以匹配传进来的参数，即传入的字符串是"Python"就返回100.
    case "Python" => 100
    case "Golang" => 200
    case "Java" => 300
    case "Shell" => 400
    case "Scala" => 500
    //case _ 表示匹配默认情况，即以上条件均不满足时会走下面的这个语句哟
    case _ => -1
  }

  /**
    * 接下来我们用偏函数重写上面的func函数的功能
    * 其中PartialFunction就是偏函数的关键字，里面的第一个参数是调用者输入参数的类型(String)，而第二个参数是返回值类型(Int类型)
    */
  def func3: PartialFunction[String, Int] = {
    //case 可以匹配传进来的参数，即传入的字符串是"Python"就返回100.
    case "Python" => 100
    case "Golang" => 200
    case "Java" => 300
    case "Shell" => 400
    case "Scala" => 500
    //case _ 表示匹配默认情况，即以上条件均不满足时会走下面的这个语句哟
    case _ => -1
  }


  def func4: PartialFunction[Any, Int] = {
    //case也可以匹配传进来的类型，如果是Int类型就将这个参数乘以2并返回，如果这个参数不是Int类型的话就返回-1
    case i: Int => i * 2
    case _ => -1
  }

  def main(args: Array[String]): Unit = {

    var res1 = func("Python")
    println(res1)

    var res2 = func2("Python")
    println(res2)

    var res3 = func3("Python")
    println(res3)

    var arr = Array[Any](1, "yinzhengjie", 3, "尹正杰", 5)
    var res4 = arr.collect(func4)
    println(res4.toBuffer.toString())
  }
}