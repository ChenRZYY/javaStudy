package cn.sdrfengmi.spark._02_sql.taxi

import org.json4s.jackson.Serialization

object JsonTest {

  def main(args: Array[String]): Unit = {
    import org.json4s._
    import org.json4s.jackson.JsonMethods._
    import org.json4s.jackson.Serialization.{read, write}

    val product =
      """
        |{"name":"Toy","price":35.35}
      """.stripMargin

    // 隐士转换的形式提供格式工具, 例如 如何解析时间字符串
    implicit val formats = Serialization.formats(NoTypeHints)

    // 具体的解析为某一个对象 TODO 在依赖的spark本地包中删除 json4s-jackson_2.11-3.2.11.jar  json4s-ast_2.11-3.2.11.jar json4s-core_2.11-3.2.11.jar 因为和导入json4s冲突,下面parse不能够使用
    val productObj1: Product = parse(product).extract[Product]
//    println(productObj1)

    // 可以通过一个方法, 直接将 JSON 字符串转为对象, 但是这种方式就无法进行搜索了
    val productObj2 = read[Product](product)

    // 将对象转为 JSON 字符串
    val productObj3 = Product("电视", 10.5)
    //val jsonStr1 = compact(render(Extraction.decompose(productObj3)))
    val jsonStr = write(productObj3)

    println(jsonStr)
  }
}

case class Product(name: String, price: Double)
