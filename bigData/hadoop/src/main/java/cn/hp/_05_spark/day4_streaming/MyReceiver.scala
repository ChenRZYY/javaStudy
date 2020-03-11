package cn.hp._05_spark.day4_streaming

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.net.Socket

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

/**
  * 自定义receiver
  *
  * @param host
  * @param port
  */
class MyReceiver(val host: String, val port: Int) extends Receiver[String](StorageLevel.MEMORY_AND_DISK) {
  /**
    * 接收socket数据
    */
  def receive() = {

    val socket = new Socket(host, port)
    val is: InputStream = socket.getInputStream
    val br = new BufferedReader(new InputStreamReader(is))
    var line: String = null

    //判断receiver没有关闭 并且socket中有数据就进行接收
    while ((line = br.readLine()) != null && !isStopped()) {
      //将数据保存下来 累积到一个批次之后进行处理
      store(line)
    }

    br.close()
    is.close()
    socket
  }

  /**
    * receiver启动的时候调用
    */
  override def onStart(): Unit = {

    new Thread() {
      override def run(): Unit = {
        //接收socket数据
        receive()
      }
    }.start()
  }

  /**
    * receiver停止的时候调用
    *
    */
  override def onStop(): Unit = {}
}
