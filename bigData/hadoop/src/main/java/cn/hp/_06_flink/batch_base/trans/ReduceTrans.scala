package cn.hp._06_flink.batch_base.trans

import org.apache.flink.api.scala._

/**
  * 请将以下元组数据，使用`reduce`操作聚合成一个最终结果
  *
  * ("java" , 1) , ("java", 1) ,("java" , 1)
  *
  * 将上传元素数据转换为`("java",3)`
  */
object ReduceTrans {

  def main(args: Array[String]): Unit = {

    // env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // load list
    val list: DataSet[(String, Int,Int)] = env.fromCollection(List(("java", 1,2), ("java", 1,3), ("java", 1,4)))

    // reduce
    // p1 ("java" , 1) p2 ("java", 1) ("java",2)
    // p1 ("java",2) p2 ("java" , 1) ("java",3)
    val reduceDataSet: DataSet[(String, Int, Int)] = list.reduce{
      (a,b)=>(a._1,a._2+b._3,a._3+b._3)
    }
    
    //list.reduceByKey(_+_)

    // print
    reduceDataSet.print()

  }

}
