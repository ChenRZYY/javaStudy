package cn.itcast.up.bean

/**
  * 元数据样例类,表功能
  *
  * @param inType
  * @param zkHosts
  * @param zkPort
  * @param hbaseTable
  * @param family
  * @param selectFields
  * @param rowKey
  */
case class HBaseMeta(
                      inType: String,
                      zkHosts: String,
                      zkPort: String,
                      hbaseTable: String,
                      family: String,
                      selectFields: String,
                      rowKey: String
                    )

object HBaseMeta {
  val INTYPE = "inType"
  val ZKHOSTS = "zkHosts"
  val ZKPORT = "zkPort"
  val HBASETABLE = "hbaseTable"
  val FAMILY = "family"
  val SELECTFIELDS = "selectFields"
  val ROWKEY = "rowKey"
}