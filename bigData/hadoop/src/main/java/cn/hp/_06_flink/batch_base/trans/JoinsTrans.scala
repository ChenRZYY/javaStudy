package cn.hp._06_flink.batch_base.trans

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

object JoinsTrans {
  case class score(id:Int,name:String,cid:Int,scores:Double)
  case class subject(cid:Int,className:String)
  def main(args: Array[String]): Unit = {
    //1.创建本地执行环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    //2.加载本地文件数据
    val scores: DataSet[score] = env.readCsvFile[score]("D:\\flink_project\\flink-base-project\\flink-batch-base\\src\\main\\resources\\score.csv")
    val subject: DataSet[subject] = env.readCsvFile[subject]("D:\\flink_project\\flink-base-project\\flink-batch-base\\src\\main\\resources\\subject.csv")
    //3.join操作
    val result: JoinDataSet[score, subject] = scores.join[subject](subject).where(2).equalTo(0)
    val finalResult: DataSet[(Int, String, String, Double)] = result.map(new RichMapFunction[(score, subject), (Int, String, String, Double)] {
      override def map(value: (score, subject)): (Int, String, String, Double) = {
        (value._1.id, value._1.name, value._2.className, value._1.scores)
      }
    })

    //4.打印数据
    finalResult.print()
  }
}
