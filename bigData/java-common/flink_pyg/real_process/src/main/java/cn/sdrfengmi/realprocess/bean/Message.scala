package cn.sdrfengmi.realprocess.bean

case class Message(
                    var clickLog: ClickLog,
                    var count: Long,
                    var timeStamp: Long
                  )