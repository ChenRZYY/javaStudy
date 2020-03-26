package cn.sdrfengmi._05_spark._03_flume_kafka

import org.apache.log4j.{Level, Logger}
import org.apache.spark.internal.Logging
//import org.apache._05_spark.Logging //TODO 1.6 版本到2.1版本以后改变

object LoggerLevels extends Logging {

  def setStreamingLogLevels() {
    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
    if (!log4jInitialized) {
      logInfo("Setting log level to [WARN] for streaming example." +
        " To override add a custom log4j.properties to the classpath.")
      Logger.getRootLogger.setLevel(Level.WARN)
    }
  }
}