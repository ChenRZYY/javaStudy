package cn.hp._06_flink._01_batch_base

import org.apache.flink.api.common.functions.{Partitioner, RichMapFunction}
import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.common.operators.base.JoinOperatorBase.JoinHint
import org.apache.flink.api.java.aggregation.Aggregations
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.util.Collector
import org.junit.Test
import org.apache.log4j.{Level, Logger}

//TODO 样例类必须放到 类外面 否则不能被序列化 报错,main方法的时候可以放到里面
case class Person(id: String, name: String)

case class Score1(id: String, name: String, subjecid: String, score: String)

case class score(id: Int, name: String, cid: Int, scores: Double)

case class subject1(cid: Int, className: String)

case class Subject2(id: String, name: String)

class TransformationDemo {
  val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
  Logger.getLogger("org").setLevel(Level.ERROR)

  @Test
  def map(): Unit = {
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
    personDataSet.print()
  }


  @Test
  def flatmap(): Unit = {
    // 去加载本地数据
    val listDataSet: DataSet[String] = env.fromCollection(List("张三,中国,江西省,南昌市", "李四,中国,河北省,石家庄市", "Tom,America,NewYork,Manhattan"))
    // 使用flatmap进行转换
    val dataSet: DataSet[Product] = listDataSet.flatMap {
      text => {
        var array = text.split(",")
        List((array(0), array(1)), (array(0), array(1), array(2)), (array(0), array(1), array(2), array(3)))
      }
    }
    dataSet.print()
  }

  @Test
  //函数处理包含一个分区所有数据的“迭代器”，可以生成任意数量的结果值。每个分区中的元素数量取决于并行度和先前的算子操作。
  //根据给定的 key 对数据集做 hash 分区。可以是 position keys，expression keys 或者 key selector functions。
  def mapPartition(): Unit = {
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

    //Hash-Partition 根据给定的 key 对数据集做 hash 分区。可以是 position keys，expression keys 或者 key selector functions。
    val ByHash = listDataSet.partitionByHash(0).mapPartition(iter => List(""))
    //Range-Partition 根据给定的 key 对一个数据集进行 Range 分区。可以是 position keys，expression keys 或者 key selector functions。
    val ByRange = listDataSet.partitionByRange(0).mapPartition(iter => List(""))
    //Custom Partitioning 手动指定数据分区。此方法仅适用于单个字段的 key。
    val custom = listDataSet.partitionCustom(new Partitioner[String] {
      override def partition(key: String, numPartitions: Int): Int = ???
    }, 0)


  }

  @Test
  def filter(): Unit = {
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

  //作用于整个 DataSet，合并该数据集的元素。
  @Test
  def reduce(): Unit = {
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

  //  对一组数据求聚合值，聚合可以应用于完整数据集或分组数据集。聚合转换只能应用于元组（Tuple）数据集，并且仅支持字段位置键进行分组。
  //  有一些常用的聚合算子，提供以下内置聚合函数（Aggregations）：
  //  Sum Min Max
  //  Grouped DataSet 方法 groupBy() 用来将数据分组，有多种数据分组方法：
  //  对于 Pojo 类型，可以根据 KeyExpression 或 KeySelector 分区
  @Test
  def groupBy_sortGroup(): Unit = {
    // 2 加载source
    val words: DataSet[(String, Int)] = env.fromCollection(List(("hadoop", 1), ("hadoop", 2), ("hadoop", 2), ("hive", 3), ("spark", 1), ("flink", 1)))
    // 先分组,在reduce TODO _._1 为什么不行???
    val groupDataSet: GroupedDataSet[(String, Int)] = words.groupBy(_._1) //按照字段名称进行分组
    //    groupDataSet.sum(0).first(2).print() TODO ??? 报错
    //    val groupDataSet1: GroupedDataSet[(String, Int)] = words.groupBy(0) //按照字段名称进行分组
    //    groupDataSet1.first(4).print()
    //
    //    val aggDataSet: AggregateDataSet[(String, Int)] = groupDataSet1.aggregate(Aggregations.SUM, 1)
    //    aggDataSet.print()

    // Grouped by Key Expression
    val wordCounts1: GroupedDataSet[(String, Int)] = words.groupBy(0)
    val agg1: AggregateDataSet[(String, Int)] = wordCounts1.sum(1)
    println(s"agg1: ${agg1.print()}")

    // Grouped by KeySelector Function
    //    val wordCounts2 = words.groupBy { _._1 }

    //对于元组（Tuple）类型，可以根据字段位置分组  根据元组的第一和第二个字段分组
    val group1: GroupedDataSet[(String, Int)] = words.groupBy(0, 1)
    println(s"group1: ${group1.sum(1).print()}")

    val group2: GroupedDataSet[(String, Int)] = words.groupBy(0).sortGroup(1, Order.ASCENDING)
    println(s"group2: ${group2.first(100).print()}")

    val agg2: AggregateDataSet[(String, Int)] = words.groupBy(0).sum(1).max(1) //TODO 为什么max后 (flink,5) 不是(hadoop,5)
    println(s"agg2: ${agg2.print()}")

  }

  @Test
  def aggregate(): Unit = {
    // 2 加载source
    val words: DataSet[(String, Int)] = env.fromCollection(List(("hadoop", 1), ("hadoop", 2), ("hive", 3), ("spark", 1), ("flink", 1)))
    // 先分组,在reduce TODO _._1 为什么不行? ???
    //    val groupDataSet: GroupedDataSet[(String, Int)] = words.groupBy(_._1) //按照字段名称进行分组
    //    val groupDataSet1: GroupedDataSet[(String, Int)] = words.groupBy(0) //按照字段名称进行分组
    //    groupDataSet1.first(100).print()
    //    val aggDataSet: AggregateDataSet[(String, Int)] = groupDataSet1.aggregate(Aggregations.SUM, 1)
    //    aggDataSet.print()
    env.setParallelism(1)
    val aggValue: AggregateDataSet[(String, Int)] = words.aggregate(Aggregations.SUM, 1)
    aggValue.print()
    val aggMin: AggregateDataSet[(String, Int)] = aggValue.aggregate(Aggregations.MAX, 1)
    aggMin.print()

    val input: DataSet[(Int, String, Double)] = env.fromElements((1, "a", 10d), (1, "b", 20d), (2, "a", 30d), (3, "c", 50d))
    val output1: DataSet[(Int, String, Double)] = input.aggregate(Aggregations.SUM, 0).aggregate(Aggregations.MIN, 2)
    output1.print()

    // 简化语法
    val output2: DataSet[(Int, String, Double)] = input.sum(0).min(2)
    output2.print()
  }

  //取元组数据集中指定一个或多个字段的值最小（最大）的元组，可以应用于完整数据集或分组数据集。
  // 用于比较的字段必须可比较的。如果多个元组具有最小（最大）字段值，则返回这些元组的任意元组。
  @Test
  def minBy_maxBy(): Unit = {
    val input: DataSet[(Int, String, Double)] = env.fromElements((1, "b", 20d), (1, "a", 10d), (2, "a", 30d))
    // 比较元组的第一个字段 输出 (1,b,20.0)
    val output1 = input.minBy(0).print()
    // 比较元组的第一、三个字段 输出 (1,a,10d)
    input.minBy(0, 2).print()

    input.min(1).print() //最小输出最后面一个
    input.max(1).print() //最大输出最后面一个

//    input.sum(0)
//    input.sum("key")
//    input.min(0)
//    input.min("key")
//    input.max(0)
//    input.max("key")
//    input.minBy(0)
//    input.minBy("key")
//    input.maxBy(0)
//    input.maxBy("key")
  }

  @Test
  def distinct(): Unit = {
    // load list
    val list: DataSet[(String, Int)] = env.fromCollection(List(("java", 1), ("java", 1), ("java", 2), ("scala", 1)))
    // distinct 0: 根据元组的第一个元素进行去重
    val distinctDataSet1: DataSet[(String, Int)] = list.distinct(0)
    distinctDataSet1.print()

    val distinctDataSet2: DataSet[(String, Int)] = list.distinct(0, 1)
    distinctDataSet2.print()
    //    val output = list.distinct { (x, y) => Math.abs(y) }
  }

  @Test
  def union(): Unit = {
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

  //Cross 是一个计算密集型操作，对大型数据集会带来挑战。建议使用 crossWithTiny() 和 crossWithHuge() 优化。
  @Test
  def cross(): Unit = {
    val scores: DataSet[score] = env.readCsvFile[score]("dataset/score.csv")
    val subject: DataSet[subject1] = env.readCsvFile[subject1]("dataset/subject.csv")
    val result: CrossDataSet[score, subject1] = scores.cross[subject1](subject)
    result.print()
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
    hashDataSet.writeAsText("datasetOut/patitions3",FileSystem.WriteMode.OVERWRITE)
    // 6. 打印输出
    hashDataSet.print()
  }

  @Test
  def partitionByHash2(): Unit = {
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

  //本地以指定的顺序在指定的字段上对数据集的所有分区进行排序。可以指定 field position 或 filed expression。
  @Test
  def sortPartition(): Unit = {
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
  def join_apply(): Unit = {
    // readCsvFile 为什么Score1 不行 ????  虚拟机运行时找不到类
    val scoreDataSet: DataSet[Score1] = env.readCsvFile[Score1]("dataset/score.csv")
    val subjectDataSet: DataSet[Subject2] = env.readCsvFile[Subject2]("dataset/subject.csv")

    // join
    // A.join(B).where(A的哪一个元素).equalTo(和B的哪个元素相等)
    val joinDataSet: JoinDataSet[Score1, Subject2] = scoreDataSet.join(subjectDataSet).where(2).equalTo(0)

    val function = (sc: Score1, su: Subject2) => (sc.id, sc.name, sc.subjecid, sc.score, su.id, sc.name);
    //    val map = joinDataSet.map()

    val applyDataSet: DataSet[(String, String, String, String, String, String)] = joinDataSet.apply((sc, su) => (sc.id, sc.name, sc.subjecid, sc.score, su.id, sc.name))
    applyDataSet.print()

  }

  @Test
  def join_map() = {
    //2.加载本地文件数据
    val scores: DataSet[score] = env.readCsvFile[score]("dataset/score.csv")
    val subject: DataSet[subject1] = env.readCsvFile[subject1]("dataset/subject.csv")
    //3.join操作 从0开始按照元组的形式
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

  // 可以通过 JoinHint 参数来指定运行时执行连接的方式。参数描述了 join 是通过分区（partitioning）还是广播（broadcasting）发生的，
  // 以及使用算法是基于排序（sort-based）的还是基于哈希（hash-based）的。如果没有指定 JoinHint，系统将尝试对输入大小进行评估，并根据这些评估选择最佳策略。
  @Test
  def joinHint() = {
    // JoinHint 可选项：
    // OPTIMIZER_CHOOSES，由系统判断选择
    // BROADCAST_HASH_FIRST，第一个数据集构建哈希表并广播，由第二个表扫描。适用于第一个数据集较小的情况
    // BROADCAST_HASH_SECOND，适用于第二个数据集较小的情况
    // REPARTITION_HASH_FIRST，对两个数据同时进行分区，并从第一个输入构建哈希表。如果第一个输入小于第二个输入，则此策略很好。
    // REPARTITION_HASH_SECOND，适用于第二个输入小于第一个输入。
    // REPARTITION_SORT_MERGE，对两个数据同时进行分区，并对每个输入进行排序（除非数据已经分区或排序）。输入通过已排序输入的流合并来连接。如果已经对一个或两个输入进行过分区排序的情况，则此策略很好。
    // REPARTITION_HASH_FIRST 是系统使用的默认回退策略，如果不能进行大小估计，并且不能重新使用预先存在的分区和排序顺序。

    val scores: DataSet[score] = env.readCsvFile[score]("dataset/score.csv")
    val subject: DataSet[subject1] = env.readCsvFile[subject1]("dataset/subject.csv")
    // 广播input1，并使用 hash table 的方式
    val joinDataSet: JoinDataSet[score, subject1] = scores.join[subject1](subject, JoinHint.BROADCAST_HASH_FIRST).where(2).equalTo(0)
    joinDataSet.print()

    //为了引导优化器选择正确的执行策略，可以提示要关联的 DataSet 的大小：
    // 表示第二个数据集 subject 特别小
    val join2 = scores.joinWithTiny[subject1](subject).where(0).equalTo(0)

    // 表示第二个数据集 input2 特别大
    val join3 = scores.joinWithHuge(subject).where(0).equalTo(0)

    val join4: JoinDataSet[score, subject1] = scores.join[subject1](subject).where(2).equalTo(0)
    //对join后的数据转换  利用apply方法
    val value1: DataSet[(Int, String, Int, Double, Int, String)] = join4 { (sc, su) => (sc.id, sc.name, sc.cid, sc.scores, su.cid, su.className) }

    //对join后的数据转换  利用apply方法  apply 有4个方法
    val value2: DataSet[(Int, String, Int, Double, Int, String)] = join4 {
      (sc, su, out: Collector[(Int, String, Int, Double, Int, String)]) =>
        if (sc.id > 2) out.collect(sc.id, sc.name, sc.cid, sc.scores, su.cid, su.className)
    }
    //    join4.map{ (sc, su) => (sc.id, sc.name, su.cid) }

  }

  // 多两个数据集执行左连接（leftOuterJoin）、右连接（rightOuterJoin）或全外连接（fullOuterJoin）。
  // 与 Join（inner join）的区别在于，如果在另一侧没有找到匹配的数据，则保存左侧（或右侧、两侧）的记录。
  @Test
  def leftOuterJoin_rightOuterJoin_fullOuterJoin() = {
    env.setParallelism(1) //有的必须指定并行度,因为是多线程 sort排序 不能正常
    val scores: DataSet[score] = env.readCsvFile[score]("dataset/score.csv")
    val subject: DataSet[subject1] = env.readCsvFile[subject1]("dataset/subject.csv")
    val joinFunc1: JoinFunctionAssigner[score, subject1] = scores.leftOuterJoin[subject1](subject).where(2).equalTo(0)
    val joinFunc2: JoinFunctionAssigner[score, subject1] = scores.rightOuterJoin[subject1](subject).where(2).equalTo(0)
    val joinFunc3: JoinFunctionAssigner[score, subject1] = scores.fullOuterJoin[subject1](subject).where(2).equalTo(0)
    val dataset: DataSet[(Int, String, Int, Double, Int, String)] = joinFunc1.apply((sc, su) => (sc.id, sc.name, sc.cid, sc.scores, su.cid, su.className))
    dataset.sortPartition((x) => x._1, Order.ASCENDING).print()

  }

  @Test
  def coGroup() = {
    val iVals: DataSet[(String, Int)] = env.fromElements(("a", 10), ("b", 20), ("a", 30))
    val dVals: DataSet[(String, Double)] = env.fromElements(("a", 1.0), ("b", 2.0), ("c", 3.0))

    // iVals 第一个字段与 dVals 第一个字段连接
    val output: DataSet[Double] = iVals.coGroup(dVals).where(0).equalTo(0) {
      (iVals, dVals, out: Collector[Double]) =>
        // iVals [("a",10),("a",30)] 空格也是执行
        val ints: Set[Int] = iVals map {
          _._2
        } toSet

        // dVals [("a", 1.0)]
        for (dVal <- dVals) {
          for (i <- ints) {
            out.collect(dVal._2 * i)
          }
        }
    }
    output.print()
    // 输出 10.0,30.0,40.0
  }

  //Java API 支持，Scala API 不支持，作用于元组的转换，从元组中选择字段的子集。
  def project = {
    val iVals: DataSet[(String, Int)] = env.fromElements(("a", 10), ("b", 20), ("a", 30))
    //DataSet<Tuple3<Integer, Double, String>> in = // [...]
    // converts Tuple3<Integer, Double, String> into Tuple2<String, Integer>
    //DataSet<Tuple2<String, Integer>> out = in.project(2,0);
  }

  //Iteration
  //迭代在 Flink 程序中实现循环。迭代运算符封装了程序的一部分并重复执行，将一次迭代的结果（部分结果）反馈到下一次迭代中。Flink 两种迭代类型：==BulkIteration== 和 ==DeltaIteration==。
  //批量迭代（Bulk Iteration）
  //调用 DataSet 的 iterate(int) 方法创建一个 BulkIteration，迭代以此为起点，返回一个 IterativeDataSet，可以用常规运算符进行转换。迭代调用的参数 int 指定最大迭代次数。
  //IterativeDataSet 调用 closeWith(DataSet) 方法来指定哪个转换应该反馈到下一个迭代，可以选择使用 closeWith(DataSet，DataSet) 指定终止条件。如果该 DataSet 为空，则它将评估第二个 DataSet 并终止迭代。如果没有指定终止条件，则迭代在给定的最大次数迭代后终止。
  //以下示例迭代地估计数量Pi。目标是计算落入单位圆的随机点数。在每次迭代中，挑选一个随机点。如果此点位于单位圆内，增加计数。然后估计 Pi 作为结果计数除以迭代次数乘以4。

  @Test
  def iterate: Unit = {
    val initial = env.fromElements(0)

    val function: (DataSet[Int] => DataSet[Int]) => DataSet[Int] = initial.iterate(10000)

    val count = initial.iterate(10000) { iterationInput: DataSet[Int] =>
      val result: DataSet[Int] = iterationInput.map { i =>
        val x = Math.random()
        val y = Math.random()
        i + (if (x * x + y * y < 1) 1 else 0)
      }
      result
    }
    val result: DataSet[Double] = count map { c => c / 10000.0 * 4 }
    result.print()
  }

  //增量迭代（Delta Iteration）
  //DeltaIteration 利用了某些算法在每次迭代中不会更改解的每个数据点的特点。
  //除了在每次迭代中反馈的部分解决方案之外，还在迭代中维护状态，可以通过增量更新。迭代计算的结果是最后一次迭代之后的状态。参考 迭代的基本原理。
  //定义 DeltaIteration 类似于定义 BulkIteration。两个数据集构成每次迭代的输入（工作集和解集），并且在每次迭代中生成两个数据集作为结果（新工作集，增量解集）。
  //调用初始解决方案集的 iterateDelta(initialWorkset, maxIterations, key) 方法创建一个 DeltaIteration：

  @Test
  def iterateDelta: Unit = {
    //    val initialSolutionSet: DataSet[(Long, Double)] = env.fromElements((1, 1D), (2, 2D))
    //    val initialWorkset: DataSet[(Long, Double)] = env.fromElements((3, 3D), (3, 3D))
    //    val maxIterations = 100
    //    val keyPosition = 0
    //
    //    val result = initialSolutionSet.iterateDelta(initialWorkset, maxIterations, Array(keyPosition)) {
    //      (solution, workset) =>
    //        val candidateUpdates = workset.groupBy(1).reduceGroup(new ComputeCandidateChanges())
    //        val deltas = candidateUpdates.join(solution).where(0).equalTo(0)(new CompareChangesToCurrent())
    //        val nextWorkset = deltas.filter(new FilterByThreshold())
    //        (deltas, nextWorkset)
    //    }
    //    result.writeAsCsv("")
    //    env.execute()
  }


}

