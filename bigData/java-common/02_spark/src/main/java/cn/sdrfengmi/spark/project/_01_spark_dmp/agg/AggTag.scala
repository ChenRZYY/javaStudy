package cn.sdrfengmi.spark.project._01_spark_dmp.agg

import org.apache.spark.graphx.VertexId
import org.apache.spark.rdd.RDD

/**
  * @Author 陈振东
  * @create 2020/3/24 15:57
  *         标签聚合
  */
object AggTag {

  def agg(graph: RDD[(VertexId, (VertexId, (List[String], Map[String, Double])))]) = {
    //(userid,(aggid,(alluserids,tags)))
    //1、根据聚集点的id进行分组
    //(52:54:00:4f:be:d5,(List(52:54:00:4f:be:d5, 019635834566264),Map(ISP_D00030001 -> 1.0, DX_Nova 2 Plus -> 1.0, CN_123556 -> 1.0, KW_美文 -> 1.0, NW_D00020004 -> 1.0, RN_上海市 -> 1.0, BA_颛桥 -> 1.0, CT_上海市 -> 1.0, APP_饭本 -> 1.0, AGE_28 -> 1.0, DT_D00010003 -> 1.0, SEX_男 -> 1.0)))
    //(52:54:00:4f:be:d5,(List(52:54:00:4f:be:d5, 431155434772903),Map(RN_重庆市 -> 1.0, NW_D00030004 -> 1.0, ISP_D00030001 -> 1.0, BA_大溪沟 -> 1.0, KW_美文 -> 1.0, BA_华新街 -> 1.0, CN_123547 -> 1.0, DT_D00010004 -> 1.0, BA_五里店 -> 1.0, APP_觅食 -> 1.0, CT_重庆市 -> 1.0, AGE_28 -> 1.0, SEX_男 -> 1.0, DX_BLACK BARRY -> 1.0, KW_睡眠 -> 1.0)))
    //(YGHAOTJHXBNKFEDJ,(List(YGHAOTJHXBNKFEDJ, 52:54:00:4f:be:d5),Map(NW_D00030004 -> 1.0, KW_美文 -> 1.0, DX_IPHONE7_PLUS -> 1.0, DT_D00010002 -> 1.0, APP_IT桔子 -> 1.0, ISP_D00030002 -> 1.0, CN_123518 -> 1.0, BA_北海 -> 1.0, CT_临汾市 -> 1.0, BA_沙滩 -> 1.0, RN_山西省 -> 1.0, AGE_28 -> 1.0, SEX_男 -> 1.0)))
    //(aggid,Iterable((userid,(aggid,(alluserids,tags)))))
    //(-1243429280,[
    // (-145530244,(-1243429280,(List(52:54:00:4f:be:d5, 019635834566264),Map(ISP_D00030001 -> 1.0, DX_Nova 2 Plus -> 1.0, CN_123556 -> 1.0, KW_美文 -> 1.0, NW_D00020004 -> 1.0, RN_上海市 -> 1.0, BA_颛桥 -> 1.0, CT_上海市 -> 1.0, APP_饭本 -> 1.0, AGE_28 -> 1.0, DT_D00010003 -> 1.0, SEX_男 -> 1.0)))),
    //(-145530244,(-1243429280,(List(52:54:00:4f:be:d5, 431155434772903),Map(RN_重庆市 -> 1.0, NW_D00030004 -> 1.0, ISP_D00030001 -> 1.0, BA_大溪沟 -> 1.0, KW_美文 -> 1.0, BA_华新街 -> 1.0, CN_123547 -> 1.0, DT_D00010004 -> 1.0, BA_五里店 -> 1.0, APP_觅食 -> 1.0, CT_重庆市 -> 1.0, AGE_28 -> 1.0, SEX_男 -> 1.0, DX_BLACK BARRY -> 1.0, KW_睡眠 -> 1.0)))),
    //(-626326250,(-1243429280,(List(YGHAOTJHXBNKFEDJ, 52:54:00:4f:be:d5),Map(NW_D00030004 -> 1.0, KW_美文 -> 1.0, DX_IPHONE7_PLUS -> 1.0, DT_D00010002 -> 1.0, APP_IT桔子 -> 1.0, ISP_D00030002 -> 1.0, CN_123518 -> 1.0, BA_北海 -> 1.0, CT_临汾市 -> 1.0, BA_沙滩 -> 1.0, RN_山西省 -> 1.0, AGE_28 -> 1.0, SEX_男 -> 1.0))))
    // ])
    val grouped: RDD[(VertexId, Iterable[(VertexId, (VertexId, (List[String], Map[String, Double])))])] = graph.groupBy(_._2._1)
    //2、对数据进行聚合，将一个用户的多条数据合并为一条
    val userTags: RDD[(VertexId, (VertexId, (List[String], Map[String, Double])))] = grouped.map {
      case (aggid, it) =>
        it.reduce((first, last) => {
          val firstUserIds: List[String] = first._2._2._1
          val lastUserIds: List[String] = last._2._2._1
          val firstTags: Map[String, Double] = first._2._2._2
          val lastTags: Map[String, Double] = last._2._2._2
          //合并用户标识，并去重
          val allUserIds = (firstUserIds ++ lastUserIds).distinct
          //["KW_美文"->1,"KW_美文"->1,"KW_美文"->1]
          //groupBy(_._1) => ["KW_美文"->List["KW_美文"->1,"KW_美文"->1,"KW_美文"->1]]
          //["KW_美文"->3]
          val groupedTags: Map[String, List[(String, Double)]] = (firstTags.toList ++ lastTags.toList).groupBy(_._1)

          val tags: Map[String, Double] = groupedTags.map(item => {
            val keyword = item._1
            val attr = item._2.map(_._2).sum
            (keyword, attr)
          })
          (first._1, (first._2._1, (allUserIds, tags)))
        })
    }
    //3、数据返回
    userTags.map {
      case (userid, (aggid, (allUserIds, tags))) =>
        (allUserIds.head, (allUserIds, tags))
    }
  }
}
