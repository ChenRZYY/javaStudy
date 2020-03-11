package cn.hp._06_flink.batch_base.batch

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala._

object BatchFromCsvFile {


  def main(args: Array[String]): Unit = {

    /*// env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 加载CSV文件, csv是以,分割的文本内容
    case class Subject(id:Long,name:String)
    val caseClassDataSet: DataSet[Subject] = env.readCsvFile[Subject]("D:\\flink_project\\flink-base-project\\flink-batch-base\\src\\main\\resources\\subject.csv")

    caseClassDataSet.print()*/


    //设置环境变量
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    //创建样例类
    case class subject(id: String, subjectName: String)
    var filePath: String = "D:\\flink_project\\flink-base-project\\flink-batch-base\\src\\main\\resources\\subject.csv"

    //加载csv文件
    //val csvDataSet: DataSet[subject] = env.readCsvFile[subject](filePath)
    val csvDataSet: DataSet[(String, String)] = env.readCsvFile[(String,String)](filePath)
    val sortDataset = csvDataSet.sortPartition(0, Order.ASCENDING).setParallelism(1)
    sortDataset.print()


  }

}
