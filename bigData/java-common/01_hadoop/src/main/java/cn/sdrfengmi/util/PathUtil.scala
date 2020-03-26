package cn.sdrfengmi.util

object PathUtil {

  def getPath(clazz: Class[_], sourceName: String): String = {
    "src/main/java/" + clazz.getPackage.getName.replace(".", "/") + "/" + sourceName
  }

}
