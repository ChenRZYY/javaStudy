package cn.hp._06_flink.stream_base.utils

import java.net.InetSocketAddress
import java.util

//import com.itheima.Entity.Logs
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests

object ElasticSearchUtil {
  /*val userConfig = new util.HashMap[String,String]()
  val transportAddresses = new util.ArrayList[InetSocketAddress]()

  transportAddresses.add(new InetSocketAddress("node01",9200))
  transportAddresses.add(new InetSocketAddress("node02",9200))
  transportAddresses.add(new InetSocketAddress("node03",9200))

  def getElasticsearchSink:ElasticsearchSink[Logs] = {
    /*val es:ElasticsearchSink[Logs] = new ElasticsearchSink[Logs](userConfig,transportAddresses,new myElasticsearchSinkFunction)
*/
  }

  class myElasticsearchSinkFunction extends ElasticsearchSinkFunction[Logs]{
    override def process(logs: Logs, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
      implicit val formats = org.json4s.DefaultFormats
      val jsonStr: String = org.json4s.native.Serialization.write(logs)

      val request: IndexRequest = Requests.indexRequest().index("").`type`("_doc").source(jsonStr)

        requestIndexer.add(request)

    }
  }*/
}
