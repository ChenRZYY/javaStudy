package cn.sdrfengmi.spark._07_youhua

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD

object Graphx {

  def main(args: Array[String]): Unit = {

    val sc = new SparkContext(new SparkConf().setMaster("local[4]").setAppName("graph"))

    //1、构建点
    val vertices: RDD[(VertexId, (String, Int))] = sc.parallelize(Seq[(Long,(String,Int))](
      (1,("张三",20)),
      (2,("李四",21)),
      (3,("王五",22)),
      (4,("赵六",23)),
      (4,("王寒梅",23)),
      (5,("韩梅梅",24)),
      (6,("李雷",25)),
      (7,("小明",26)),
      (9,("tom",27)),
      (10,("jerry",28)),
      (11,("ession",29))
    ))
    //2、构建边
  val edges = sc.parallelize(Seq[Edge[Int]](
    Edge(1,136),
    Edge(2,136),
    Edge(3,136),
    Edge(4,136),
    Edge(5,136),
    Edge(5,158),
    Edge(4,158),
    Edge(6,158),
    Edge(7,158),
    Edge(9,177),
    Edge(10,177),
    Edge(11,177)
  ))
    //3、构建图
    val graph = Graph(vertices,edges)
    //查看图的所有点
     graph.vertices.foreach(println(_))
    //查看图的所有的边
    //graph.edges.foreach(println(_))
    //构建连通图
    val connected: Graph[VertexId, Int] = graph.connectedComponents()

    //connected.vertices.foreach(println(_))
    //(158,1)
    //(1,1)
    //(9,9)
    //(6,1)
    //(5,1)
    //(10,9)
    //(177,9)
    //(2,1)
    //(4,1)
    //(136,1)
    //(11,9)
    //(3,1)
    //(7,1)

    //[1,2,3,4,5,6,7,158,136]
    //[9,10,11,177]
    //得到连通图所有的点的集合
    val vers = connected.vertices

    val grouped: RDD[(VertexId, Iterable[(VertexId, VertexId)])] = vers.groupBy(_._2)

    //grouped.map(_._2.map(_._1)).foreach(println(_))

    //[(1,zhangsan,20),(2,lisi,21),(3,...),...]
    //[(9,tom,22),(10,jerry,28),(11,ession,29)]
    //(userid,aggid)  join (userid,(name,age)) => (userid,(aggid,(name,age)))
    val userGrouped = vers.join(vertices)
      .groupBy(_._2._1)
    //[(aggid,List((userid,(aggid,(name,age))))]
      .map {
      case (aggid,list)=>
        list.map(item=>(item._1,item._2._2._1,item._2._2._2))
    }

    userGrouped.foreach(println(_))
  }
}

