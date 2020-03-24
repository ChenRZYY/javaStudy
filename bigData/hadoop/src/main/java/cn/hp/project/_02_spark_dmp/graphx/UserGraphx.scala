package cn.hp.project._02_spark_dmp.graphx

import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD

/**
  * @Author 陈振东
  * @create 2020/3/24 15:55
  */
object UserGraphx {

  def graph(currentTags: RDD[(String, (List[String], Map[String, Double]))]) = {

    //1、创建点
    val vertices: RDD[(Long, (List[String], Map[String, Double]))] = currentTags.map {
      case (userId, (allUserIds, tags)) =>
        (userId.hashCode.toLong, (allUserIds, tags))
    }
    //2、创建边
    val edges = currentTags.flatMap {
      case (userId, (allUserIds, tags)) =>
        allUserIds.map(item => Edge[Int](userId.hashCode.toLong, item.hashCode.toLong))
    }
    //3、构建图
    val graph = Graph(vertices, edges)
    //4、构建连通图
    val connected = graph.connectedComponents()
    //5、获取连通图的点的集合 (userid.hashcode.toLong, aggid)
    val vers = connected.vertices
    //6、将连通图的点的集合与创建的点的集合进行join，补全用户信息[用户所有标识，用户的所有的标签]
    //(userid,aggid)  join (userid,(alluserids,tags)) => (userid,(aggid,(alluserids,tags)))
    val result = vers.join(vertices)

    result
  }
}
