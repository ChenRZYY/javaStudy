package cn.hp._06_flink.batch_base

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.java.aggregation.Aggregations
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, _}
import org.junit.Test

//TODO 样例类必须放到 类外面 否则不能被序列化 报错,main方法的时候可以放到里面
case class Person(id: String, name: String)

case class Score1(id: String, name: String, subjecid: String, score: String)

case class score(id: Int, name: String, cid: Int, scores: Double)

case class subject1(cid: Int, className: String)

case class Subject(id: String, name: String)

class TransformationDemo {


  @Test
  def map(): Unit = {
    // env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 加载本地集合
    val listDataSet: DataSet[String] = env.fromCollection(List("1,张三", "2,李四", "3,王五", "4,赵六"))
    val personDataSet: DataSet[Person] = listDataSet.map {
      text: String =>
        val arr: Array[String] = text.split(",")
        Person(arr(0), arr(1))
    }

    //实现的 MapFunction
    /*listDataSet.map(new RichMapFunction[String,Person] {
      override def map(value: String): Person = {
        val arr: Array[String] = value.split(",")
        Person(arr(0),arr(1))
      }
    })*/

    // 打印
    personDataSet.print()
  }


  @Test
  def flatmap(): Unit = {
    // 批处理环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 去加载本地数据
    val listDataSet: DataSet[String] = env.fromCollection(List(
      "张三,中国,江西省,南昌市",
      "李四,中国,河北省,石家庄市",
      "Tom,America,NewYork,Manhattan"
    ))

    // 使用flatmap进行转换
    val dataSet: DataSet[Product] = listDataSet.flatMap {
      text => {
        var array = text.split(",")
        List(
          (array(0), array(1)),
          (array(0), array(1), array(2)),
          (array(0), array(1), array(2), array(3)))
      }
    }
    dataSet.print()
  }

  @Test
  def mapPartition(): Unit = {
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    val listDataSet: DataSet[String] = env.fromCollection(List("1,张三", "2,李四", "3,王五", "4,赵六"))
    // mapPartition
    val mapDataSet: DataSet[Person] = listDataSet.mapPartition {
      // 开启redis或者mysql的链接
      iterable => {
        val persons: Iterator[Person] = iterable.map {
          text =>
            val arrs: Array[String] = text.split(",")
            Person(arrs(0), arrs(1))
        }
        persons
      }
      // 关闭redis或者mysql的链接
    }
    mapDataSet.print()
  }

  @Test
  def filter(): Unit = {
    //1 创建环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 2 加载source
    val words: DataSet[String] = env.fromCollection(List("hadoop", "hive", "spark", "flink"))
    val value: DataSet[String] = words.filter(_.startsWith("h"))
    value.print()
    /*val filterDatasets: DataSet[String] = words.filter(new RichFilterFunction[String] {
      override def filter(value: String): Boolean = {
        value.startsWith("h")
      }
    })*/
  }

  @Test
  def reduce(): Unit = {
    //1 创建环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 2 加载source
    val words: DataSet[(String, Int)] = env.fromCollection(List(("hadoop", 1), ("hadoop", 1), ("hive", 1), ("spark", 1), ("flink", 1)))
    //reduce
    val reduce = words.reduce {
      (p1, p2) => (p1._1, p1._2 + p2._2)
    }
    reduce.print()

  }

  @Test
  def group(): Unit = {
    //1 创建环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 2 加载source
    val words: DataSet[(String, Int)] = env.fromCollection(List(("hadoop", 1), ("hadoop", 1), ("hive", 1), ("spark", 1), ("flink", 1)))
    // 先分组,在reduce
    val groupDataSet: GroupedDataSet[(String, Int)] = words.groupBy(_._1)
    val reduce: DataSet[(String, Int)] = groupDataSet.reduce {
      (p1, p2) => (p1._1, p1._2 + p2._2)
    }
    reduce.print()

  }

  @Test
  def reduceGroup(): Unit = {
    //1 创建环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 2 加载source
    val words: DataSet[(String, Int)] = env.fromCollection(List(("hadoop", 1), ("hadoop", 1), ("hive", 1), ("spark", 1), ("flink", 1)))
    // 先分组,在reduce
    val groupedDataSet1: DataSet[(String, Int)] = words.reduceGroup {
      iter => iter.reduce((p1, p2) => (p1._1, p1._2 + p2._2))
    }
    groupedDataSet1.print() //(hadoop,5)

    val groupDataSet2: GroupedDataSet[(String, Int)] = words.groupBy(_._1)
    val groupedDataSet2: DataSet[(String, Int)] = groupDataSet2.reduceGroup {
      iter => iter.reduce((p1, p2) => (p1._1, p1._2 + p2._2))
    }

    //(hadoop,2)
    //(hive,1)
    //(flink,1)
    //(spark,1)
    groupedDataSet2.print()

  }

  @Test
  def aggregate(): Unit = {
    //1 创建环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    // 2 加载source
    val words: DataSet[(String, Int)] = env.fromCollection(List(("hadoop", 1), ("hadoop", 2), ("hive", 3), ("spark", 1), ("flink", 1)))
    // 先分组,在reduce TODO _._1 为什么不行?
    //    val groupDataSet: GroupedDataSet[(String, Int)] = words.groupBy(_._1) //按照字段名称进行分组
    val groupDataSet1: GroupedDataSet[(String, Int)] = words.groupBy(0) //按照字段名称进行分组
    groupDataSet1.first(4).print()

    val aggDataSet: AggregateDataSet[(String, Int)] = groupDataSet1.aggregate(Aggregations.SUM, 1)
    aggDataSet.print()
  }


  @Test
  def distinctTrans(): Unit = {
    // env
    val env = ExecutionEnvironment.getExecutionEnvironment
    // load list
    val list: DataSet[(String, Int)] = env.fromCollection(List(("java", 1), ("java", 1), ("java", 2), ("scala", 1)))
    // distinct 0: 根据元组的第一个元素进行去重
    val distinctDataSet1: DataSet[(String, Int)] = list.distinct(0)
    distinctDataSet1.print()

    val distinctDataSet2: DataSet[(String, Int)] = list.distinct(0, 1)
    distinctDataSet2.print()

  }

  @Test
  def union(): Unit = {
    // env
    val env = ExecutionEnvironment.getExecutionEnvironment

    // load list
    val dataset1: DataSet[String] = env.fromCollection(List("hadoop", "hive", "flume"))
    val dataset2: DataSet[String] = env.fromCollection(List("hadoop", "hive", "spark"))

    // union不去重
    val newDataSet1: DataSet[String] = dataset1.union(dataset2)
    newDataSet1.print()

    //去重
    val newDataSet2: DataSet[String] = dataset1.union(dataset2).distinct()
    newDataSet2.print()
  }

  @Test
  def rebanlanceTrans(): Unit = {
    // 1. 构建批处理运行环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    //   2. 使用`env.generateSequence`创建0-100的并行数据
    val seqDataSet: DataSet[Long] = env.generateSequence(0, 100)

    //   3. 使用`fiter`过滤出来`大于8`的数字
    // 使用rebanlance均匀分布数据
    val filterDataSet: DataSet[Long] = seqDataSet.filter(_ > 8).rebalance()
    //   4. 使用map操作传入`RichMapFunction`，将当前子任务的ID和数字构建成一个元组
    // 在RichMapFunction中可以使用`getRuntimeContext.getIndexOfThisSubtask`获取子任务序号
    val mapDataSet: DataSet[(Int, Long)] = filterDataSet.map(new RichMapFunction[Long, (Int, Long)] {
      // value: 是每次遍历的数子
      override def map(value: Long): (Int, Long) = {
        (getRuntimeContext.getIndexOfThisSubtask, value)
      }
    })

    // 5. 打印测试
    mapDataSet.print()
  }

  @Test
  def partitionByHash(): Unit = {
    // 1. 创建批处理环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 2. 设置并行度为2 TODO 并行度 是不是 分区个数 reduce个数???
    //  分区数<reduce 有空文件
    //  分区数=reduce 均匀分配
    //  分区数>reduce 会丢失数据
    env.setParallelism(5)
    // 3. 加载本地集合
    val listDataSet: DataSet[Int] = env.fromCollection(List(1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2))
    // 4. 进行分区 总共有3个方法 按照第一参数的toString方法分区  如果是元祖用下标,其他数据输入名称
    val hashDataSet: DataSet[Int] = listDataSet.partitionByHash(_.toString)
    // 5. 写入文件
    hashDataSet.writeAsText("datasetOut/patitions3")
    // 6. 打印输出
    hashDataSet.print()
  }

  @Test
  def partitionByHash2(): Unit = {
    // 1. 创建批处理环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 2. 设置并行度为2
    env.setParallelism(2)
    // 3. 加载本地集合
    val listDataSet = env.fromCollection(List(("flink", 1), ("spark", 2), ("flume", 3)))
    // 4. 进行分区
    val hashDataSet: DataSet[(String, Int)] = listDataSet.partitionByHash(0)
    // 5. 写入文件
    hashDataSet.writeAsText("datasetOut/patitions" + Math.random)
    // 6. 打印输出
    hashDataSet.print()
  }

  @Test
  def sortPartition(): Unit = {
    // 1. 创建批处理环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    env.setParallelism(2) //1 和5都一样

    // 2. 加载本地集合
    val listDataSet: DataSet[String] = env.fromCollection(List("hadoop", "hadoop", "hadoop", "hive", "hive", "spark", "spark", "flink"))
    // 3. 排序
    val sortDataSet: DataSet[String] = listDataSet.sortPartition((x: String) => x, Order.ASCENDING)
    val value: DataSet[String] = sortDataSet.partitionByHash(_.toString)
    // 4. 写入文件
    value.writeAsText("datasetOut/sort_output" + Math.random)
    // 5. 打印输出
    value.print()
  }

  @Test
  def join(): Unit = {
    // env
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    // readCsvFile 为什么Score1 不行 ????  虚拟机运行时找不到类
    val scoreDataSet: DataSet[Score1] = env.readCsvFile[Score1]("dataset/score.csv")
    val subjectDataSet: DataSet[Subject] = env.readCsvFile[Subject]("dataset/subject.csv")

    // join
    // A.join(B).where(A的哪一个元素).equalTo(和B的哪个元素相等)
    val joinDataSet: JoinDataSet[Score1, Subject] = scoreDataSet.join(subjectDataSet).where(2).equalTo(0)

    val applyDataSet: DataSet[(String, String, String, String, String, String)] = joinDataSet.apply((sc, su) => (sc.id, sc.name, sc.subjecid, sc.score, su.id, sc.name))
    applyDataSet.print()
    //        val map = joinDataSet.map ((a: Score, b: Subject) =>"" ) TODO 为什么不能map

  }

  @Test
  def joinMap() = {
    //1.创建本地执行环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    //2.加载本地文件数据
    val scores: DataSet[score] = env.readCsvFile[score]("dataset/score.csv")
    val subject: DataSet[subject1] = env.readCsvFile[subject1]("dataset/subject.csv")
    //3.join操作
    val result: JoinDataSet[score, subject1] = scores.join[subject1](subject).where(2).equalTo(0)
    //平常的map怎么不行
    val finalResult: DataSet[(Int, String, String, Double)] = result.map(new RichMapFunction[(score, subject1), (Int, String, String, Double)] {
      override def map(value: (score, subject1)): (Int, String, String, Double) = {
        (value._1.id, value._1.name, value._2.className, value._1.scores)
      }
    })
    //4.打印数据
    finalResult.print()
  }
}

