package cn.sdrfengmi._04_scala.foundation

import org.junit.Test


/*class ApplyDemo {
  def main(args: Array[String]) {
    //调用了Array伴生对象的apply方法
    // def apply(x: Int, xs: Int*): Array[Int]
    //arr1中只有一个元素5
    val arr1 = Array(5)
    println(arr1.toBuffer)

    //new了一个长度为5的array，数组里面包含5个null
    var arr2 = new Array(5)

    def plus(x: Int) = 3 + x

    val ints: Array[Int] = Array(1, 2, 3, 4).map(plus(_))
    println(ints.mkString(","))

    val a = Array("Hello", "World")
    val b = Array("hello", "world")
    println(a.corresponds(b)(_.equalsIgnoreCase(_)))


    val str100 = "\"action\": \"104\",\n\"reqNo\": \"20190001\",\n\"token\": \"8838cf058c4d4afd8cd8f49fb8922146\",\n\"area\": \"\",\n\"errorNo\": \"\",\n\"errorMessage\": \"\",\n\"userCode\": \"18119772\",\n\"userName\": \"月红\",\n\"fundAccount\": \"76877046\",\n\"maxCount\": \"100\",\n\"khBranch\": \"8063\",\n\"title\": \"5901|8888118000048118|\\\\n\",\n\"bankMoney\": \"人民币|\",\n\"accountList\":\"市场类别|股东代码|市场类别名称|\u0003SZACCOUNT|0600612006|深A|\u0003SHACCOUNT|E006107402|沪A|\u0003\",\n\"grid\": \"外部机构|外部帐户|外部子帐户|币种|资产账户|\\\\n5901|8888118000048118||人民币|76877046|\\\\n\",\n\"custCert\": \"0123456789\",\n\"isHKAccount\": \"\",\n\"isHKSZAccount\": \"\""
    //    str.split(",").map(x => x.substring(1)).foreach(x=>{
    //      print(x.substring(0,x.indexOf("\"")))
    //    })

    val str104_r = "\"action\": \"104\",\n\"reqNo\": \"{{$randomInt}}\",\n\"accountType\": \"RZRQZJACCOUNT\",\n\"account\": \"96997622\",\n\"password\": \"{{modulus_id}}\",\n\"maxCount\": \"100\",\n\"accountList\": \"1\",\n\"macAddr\": \"11-22-33-AA-BB-CC@192168001001\",\n\"macAddr1\": \"11-22-33-AA-BB-CC@192168001001@W\",\n\"hdId\": \"hdid-abcd1234\",\n\"terminalCode\": \"kpt\",\n\"checkCode\": \"{{checkCode}}\",\n\"checkToken\": \"{{checkToken}}\""

    val str112_ = "\"action\": \"112\",\n\"area\":\"{{$randomInt}}\",\n\"reqNo\": \"{{$randomInt}}\",\n\"token\": \"{{26900609_token}}\",\n\"accountType\": \"KHBH\",\n\"macAddr\": \"11-22-33-AA-BB-CC@192168001001\",\n\"fundAccount\": \"7899943\",\n\"newPassword\": \"{{modulus_id}}\",\n\"password\": \"{{modulus_id}}\",\n\"passwordType\": \"2\",\n\"accountToken\": \"26900609_token\""
    val str112 = "\"action\": \"112\",\n\"reqNo\": \"476\",\n\"token\": \"32329544868549929518b41c55c91c4a\",\n\"area\": \"113\",\n\"errorNo\": \"0\",\n\"errorMessage\": \"密码修改成功!\""

    val str400_ = "\"action\": \"400\",\n\"reqNo\": \"{{$randomInt}}\",\n\"token\": \"{{8193110004_token}}\",\n\"accountToken\": \"8193110004_token\",\n\"fundAccount\": \"8193110004\",\n\"wtAccountType\": \"SZACCOUNT\",\n\"wtaccount\": \"0604704116\",\n\"stockCode\": \"000001\",\n\"volume\": \"100\",\n\"direction\": \"B\",\n\"priceType\": \"0\",\n\"price\": \"16.28\",\n\"creditType\": \"1\",\n\"macAddr\": \"11-22-33-AA-BB-CC@192168001001\",\n\"hdId\": \"H12345678901234567890123456789012345\",\n\"terminalCode\": \"kpt\""
    val str400 = "\"action\": \"400\",\n\"reqNo\": \"275\",\n\"token\": \"8d8f068c7d5a4031bdd106f75cd254f2\",\n\"area\": \"\",\n\"errorNo\": \"H1006323\",\n\"errorMessage\": \"委托已提交,合同号为:H1006323\",\n\"answerNo\": \"H1006323\""


    val str401_ = "\"action\": \"401\",\n\"reqNo\": \"{{$randomInt}}\",\n\"token\": \"{{8193110004_token}}\",\n\"accountToken\": \"8193110004_token\",\n\"fundAccount\": \"8193110004\",\n\"wtAccountType\": \"SZACCOUNT\",\n\"wtaccount\": \"0600612006\",\n\"contactID\": \"H1006323\",\n\"macAddr\": \"11-22-33-AA-BB-CC@192168001001\",\n\"hdId\": \"H12345678901234567890123456789012345\",\n\"terminalCode\": \"kpt\""
    val str401 = "\"action\": \"401\",\n\"reqNo\": \"288\",\n\"token\": \"787f2338ccb743f09ec0cd0e8a27c28f\",\n\"area\": \"\",\n\"errorNo\": \"100\",\n\"errorMessage\": \"委托已提交!\",\n\"market\": \"\",\n\"board\": \"\",\n\"orderId\": \"\",\n\"status\": \"\""

    val str402 = "\"action\": \"402\",\n\"reqNo\": \"10000001122\",\n\"token\": \"e625a2497887417f94d10bad2197e378\",\n\"area\": \"402\",\n\"errorNo\": \"\",\n\"errorMessage\": \"\",\n\"grid\": \"资产账户|币种|资金余额|资金可用金额|可提现金金额|股票市值|资产总值|沪港通资金台账可用|货币代码|银行代码|银行名称|密码标识|银行账号|\\n7899943|人民币|12000139.77|14103982.10|9415432.00|7015526.00|23121378.46||0|6401|安银行|42|6223226020633250|\\n\",\n\"hideSegmentIndex\": \"8\",\n\"bankIndex\": \"9\",\n\"bankNameIndex\": \"10\",\n\"passwordIdentifyIndex\": \"11\",\n\"balanceIndex\": \"2\",\n\"usableIndex\": \"3\",\n\"availableIndex\": \"4\",\n\"moneyTypeIndex\": \"8\",\n\"moneyNameIndex\": \"1\",\n\"totalAssetsIndex\": \"6\",\n\"fundAccountIndex\": \"0\",\n\"bankAccountIndex\": \"12\",\n\"bankCodeIndex\": \"9\""

    val str403 = "\"action\": \"403\",\n\"reqNo\": \"838\",\n\"token\": \"d45d2b246eff4ddd9e0607124e94ceaa\",\n\"area\": \"\",\n\"errorNo\": \"\",\n\"errorMessage\": \"\",\n\"maxCount\": \"\",\n\"grid\": \"证券名称|证券代码|证券数量|可卖数量|在途数量|在途可用数量|币种|当前价|成本价|当前成本|冻结数量|异常冻结数量|资金账号|股东代码|市场板块|股份可用|货币代码|浮动盈亏|盈亏率%|市场类别|股票市场|市值|证券内码|买入成本价|交易席位|单位|市场编号|证券类别编码|\\n平安银行|000001|10000|10000|0|0|人民币|16.950|15.701|157007.15|2100|0|7899943|0605197526|深圳A|7900|0|31992.85|20.3767|SZACCOUNT|股票市场|189000.00|1|15.701|200701|股|00|0|\\n万  科Ａ|000002|10000|10000|0|0|人民币|31.420|13.785|137848.58|0|0|7899943|0605197526|深圳A|10000|0|214751.42|155.7879|SZACCOUNT|股票市场|352600.00|2|13.785|200701|股|00|0|\\n德赛电池|000049|10000|10000|0|0|人民币|43.060|55.889|558887.56|0|0|7899943|0605197526|深圳A|10000|0|-100887.56|-18.0515|SZACCOUNT|股票市场|458000.00|49|55.889|200701|股|00|0|\\n创业慧康|300451|76800|13250|0|0|人民币|17.800|14.894|1143879.00|63550|0|7899943|0605197526|深圳A|13250|0|298425.00|26.0889|SZACCOUNT|股票市场|1442304.00|300451|14.833|200701|股|00|F|\\n浦发银行|600000|160100|160000|100|0|人民币|12.450|0.091|14550.58|0|0|7899943|E052139017|上海A|160000|0|1980295.42|13609.7353|SHACCOUNT|股票市场|1994846.00|1600000|0.091|25242|股|10|0|\\n首创股份|600008|10000|10000|0|0|人民币|3.330|1.917|19170.88|0|0|7899943|E052139017|上海A|10000|0|14029.12|73.1793|SHACCOUNT|股票市场|33200.00|1600008|1.917|25242|股|10|0|\\n中国联通|600050|10000|10000|0|0|人民币|5.980|0.146|1460.03|0|0|7899943|E052139017|上海A|10000|0|58239.97|3988.9571|SHACCOUNT|股票市场|59700.00|1600050|0.146|25242|股|10|0|\\n同方股份|600100|10000|10000|0|0|人民币|9.030|62.458|624576.38|0|0|7899943|E052139017|上海A|10000|0|-534376.38|-85.5582|SHACCOUNT|股票市场|90200.00|1600100|62.458|25242|股|10|0|\\n振华重工|600320|10000|10000|0|0|人民币|3.700|0.051|512.01|600|0|7899943|E052139017|上海A|9400|0|36487.99|7126.4214|SHACCOUNT|股票市场|37000.00|1600320|0.051|25242|股|10|0|\\n龙头股份|600630|10000|10000|0|0|人民币|7.430|0.345|3450.07|0|0|7899943|E052139017|上海A|10000|0|70849.93|2053.5795|SHACCOUNT|股票市场|74300.00|1600630|0.345|25242|股|10|0|\\n中国化学|601117|10000|10000|0|0|人民币|6.560|33.572|335721.84|0|0|7899943|E052139017|上海A|10000|0|-270321.84|-80.5196|SHACCOUNT|股票市场|65400.00|1601117|33.572|25242|股|10|0|\\n华兴源创|688001|50000|50000|0|0|人民币|46.500|47.270|2363505.25|50000|0|7899943|E052139017|上海A|0|0|-28505.25|-1.2061|SHACCOUNT|股票市场|2335000.00|1688001|47.270|25242|股|10|h|\",\n\"grid2\": \"货币|资金余额|资金可用金额|市值|资产总值|可提现金金额|\\n人民币|12000139.77|14103982.10|7131550.00|23237402.46|9415432.00|\",\n\"totalYk\": \"1770980.67\",\n\"totalYk_RMB\": \"1770980.67\",\n\"totalYk_HK\": \"0.00\",\n\"totalYk_USD\": \"0.00\",\n\"mktVal_RMB\": \"7131550.00\",\n\"mktVal_HK\": \"0.00\",\n\"mktVal_USD\": \"0.00\",\n\"totalAsset_RMB\": \"23237402.46\",\n\"totalAsset_HK\": \"0.00\",\n\"totalAsset_USD\": \"0.00\",\n\"hideSegmentIndex\": \"15\",\n\"stockNameIndex\": \"0\",\n\"stockIndex\": \"1\",\n\"stockNumIndex\": \"2\",\n\"stockAvailableIndex\": \"3\",\n\"kyIndex\": \"15\",\n\"accountIndex\": \"13\",\n\"keepIndex\": \"23\",\n\"priceIndex\": \"7\",\n\"ykPriceIndex\": \"7\",\n\"marketNameIndex\": \"14\",\n\"marketIndex\": \"19\",\n\"stockValueIndex\": \"21\",\n\"ykIndex\": \"17\",\n\"yklIndex\": \"18\",\n\"stockCodeIndex\": \"1\",\n\"moneyTypeIndex\": \"16\",\n\"currCostIndex\": \"9\",\n\"lastPriceIndex\": \"7\",\n\"freezingQuantityIndex\": \"10\",\n\"costPriceIndex\": \"8\",\n\"wtaccounttypeindex\": \"19\",\n\"fundAccountIndex\": \"12\",\n\"wtaccountindex\": \"13\",\n\"seatNoIndex\": \"24\",\n\"wtAccountTypeNameIndex\": \"14\",\n\"stockvalueindex\": \"21\",\n\"secuTypeNo\": \"27\",\n\"currencyIndex_2\": \"0\",\n\"balanceIndex_2\": \"1\",\n\"usableIndex_2\": \"2\",\n\"availableIndex_2\": \"5\",\n\"moneyTypeIndex_2\": \"0\",\n\"moneyTypeCodeIndex_2\": \"5\",\n\"totalAssetsIndex_2\": \"4\""

    val str404 = "\"action\": \"404\",\n\"reqNo\": \"941\",\n\"token\": \"d45d2b246eff4ddd9e0607124e94ceaa\",\n\"area\": \"\",\n\"errorNo\": \"\",\n\"errorMessage\": \"\",\n\"maxCount\": \"\",\n\"grid\": \"证券名称|证券代码|交易日期|委托日期|委托类别|委托编号|股东代码|已撤单数量|成交价格|状态说明|资金账号|币种|市场板块|可撤单标志|委托价格|委托数量|成交数量|已撤单标志|发送标志|合法标志|委托类型|市场类别|操作渠道|撤单标志|交易类别|交易类型|成交金额|\\n平安银行|000001|2020-01-06|2020-01-06 14:58:32|普通买入|G9401163|0605197526|0|0.000|未成交|7899943|人民币|深圳A|1|17.050|100|0|0|1|1|00|SZACCOUNT||0|限价买入|0B|0.00|\",\n\"hideSegmentIndex\": \"13\",\n\"entrusttypename\": \"12\",\n\"reportTime\": \"3\",\n\"contactIndex\": \"5\",\n\"entrustPrice\": \"14\",\n\"amountIndex\": \"15\",\n\"businessAmount\": \"16\",\n\"stockNameIndex\": \"0\",\n\"stockCodeIndex\": \"1\",\n\"dateIndex\": \"2\",\n\"buydirectionIndex\": \"25\",\n\"drawIndex\": \"13\",\n\"businessPrice\": \"8\",\n\"moneyTypeIndex\": \"11\",\n\"accountIndex\": \"6\",\n\"buydirectionNameIndex\": \"4\",\n\"drawsIndex\": \"9\",\n\"wtAccountTypeNameIndex\": \"12\",\n\"wtAccountTypeIndex\": \"21\",\n\"validIndex\": \"19\",\n\"dealTypeNameIndex\": \"24\",\n\"secuTypeNo\": \"25\"\t"

    val str405 = "\"action\": \"12121\",\n\"reqNo\": \"604\",\n\"token\": \"d45d2b246eff4ddd9e0607124e94ceaa\",\n\"area\": \"23\",\n\"errorNo\": \"\",\n\"errorMessage\": \"\",\n\"maxCount\": \"\",\n\"grid\": \"证券名称|证券代码|委托日期|成交日期|成交时间|显示状态|资金账号|币种|股东代码|委托编号|市场板块|撤单标志|成交数量|成交价格|成交金额|数量|委托类型|成交编号|交易类型|市场类别|显示状态|交易类别|证券类别编码|\\n浦发银行|600000|2019-12-19 16:12:15|19-12月-19|16:13:12|普通买入|7899943|人民币|E052139017|25207922|上海A||100|13.343|1334.30|100|00|251|VB|SHACCOUNT|普通买入|最优成交剩撤买|0|\",\n\"hideSegmentIndex\": \"11\",\n\"stockNameIndex\": \"0\",\n\"businessPrice\": \"13\",\n\"businessAmount\": \"12\",\n\"businessBalance\": \"14\",\n\"stockCodeIndex\": \"1\",\n\"reportTime\": \"2\",\n\"buyDirectionIndex\": \"18\",\n\"entrustPrice\": \"4\",\n\"reportDate\": \"2\",\n\"wtAccountIndex\": \"8\",\n\"contactIndex\": \"9\",\n\"drawIndex\": \"11\",\n\"amountIndex\": \"15\",\n\"buyDirectionNameIndex\": \"20\",\n\"businessDate\": \"3\",\n\"wtaccountTypeIndex\": \"19\",\n\"dealTypeNameIndex\": \"21\",\n\"secuTypeNo\": \"22\""

    val str5811 = "\"action\": \"12121\",\n        \"reqNo\": \"604\",\n        \"token\": \"d45d2b246eff4ddd9e0607124e94ceaa\",\n        \"area\": \"23\",\n        \"errorNo\": \"\",\n        \"errorMessage\": \"\",\n        \"maxCount\": \"\",\n        \"grid\": \"证券名称|证券代码|委托日期|成交日期|成交时间|显示状态|资金账号|币种|股东代码|委托编号|市场板块|撤单标志|成交数量|成交价格|成交金额|数量|委托类型|成交编号|交易类型|市场类别|显示状态|交易类别|证券类别编码|\\n浦发银行|600000|2019-12-19 16:12:15|19-12月-19|16:13:12|普通买入|7899943|人民币|E052139017|25207922|上海A||100|13.343|1334.30|100|00|251|VB|SHACCOUNT|普通买入|最优成交剩撤买|0|\",\n        \"hideSegmentIndex\": \"11\",\n        \"stockNameIndex\": \"0\",\n        \"businessPrice\": \"13\",\n        \"businessAmount\": \"12\",\n        \"businessBalance\": \"14\",\n        \"stockCodeIndex\": \"1\",\n        \"reportTime\": \"2\",\n        \"buyDirectionIndex\": \"18\",\n        \"entrustPrice\": \"4\",\n        \"reportDate\": \"2\",\n        \"wtAccountIndex\": \"8\",\n        \"contactIndex\": \"9\",\n        \"drawIndex\": \"11\",\n        \"amountIndex\": \"15\",\n        \"buyDirectionNameIndex\": \"20\",\n        \"businessDate\": \"3\",\n        \"wtaccountTypeIndex\": \"19\",\n        \"dealTypeNameIndex\": \"21\",\n        \"secuTypeNo\": \"22\""


    val str406 = "\"action\": \"406\",\n\"reqNo\": \"137\",\n\"token\": \"d45d2b246eff4ddd9e0607124e94ceaa\",\n\"area\": \"167\",\n\"errorNo\": \"\",\n\"errorMessage\": \"\",\n\"maxCount\": \"\",\n\"grid\": \"资金帐号|币种|资金余额|可用资金|可用信用额度|已用信用额度|保证金可用|保证金已用|融资负债|融资费用|融资利息|融券负债|融券费用|融券利息|最新市值|担保品维持率|可用融资额度|已用融资额度|可用融券额度|已用融券额度|融资市值|融券市值|融资合约盈亏|融券合约盈亏|融资已使用保证金额|融券已使用保证金额|总盈亏|可还款金额|维持担保比例线需追加的资金|资产类别|分支机构|未还融资合约金额|未还融券合约金额|未还融资合约利息|未还融券合约利息|净资产|用户代码|货币代码|\\n7899943|人民币|12000139.77|14102272.07|8013228.08|3986771.92|9334418.39|6015544.25|5058.69|15.11|0.00|2683822.61|0.00|0.00|7136650.00|6.5572|5021951.35|1278048.65|2991276.73|2708723.27|6513.50|2260476.67|1454.81|423345.94|1889746.69|4125797.56|424800.75|9415431.90|0.00|0|8025|5043.58|2683822.61|0.00|0.00|20976967.10|18326752|0|\",\n\"hideSegmentIndex\": \"29\",\n\"fzzjeIndex\": \"31\",\n\"assureEnbuyBalance\": \"3\",\n\"fzjeIndex\": \"8\",\n\"zjhkIndex\": \"27\",\n\"moneyTypeIndex\": \"37\",\n\"moneyNameIndex\": \"1\",\n\"net_AssetIndex\": \"35\",\n\"stockValueIndex\": \"14\",\n\"creditBalanceIndex\": \"4\",\n\"fi_ContractIndex\": \"8\",\n\"sl_Contract\": \"11\",\n\"fiContractPLIndex\": \"22\",\n\"slContractPLIndex\": \"23\",\n\"maint_RatioIndex\": \"15\",\n\"finCompactBalanceIndex\": \"8\",\n\"finCompactAmounIndex\": \"9\",\n\"finCompactInterestIndex\": \"10\",\n\"finUsedQuota\": \"5\",\n\"finAvialQuota\": \"6\""

    val str411 = "\"action\": \"411\",\n\"reqNo\": \"545\",\n\"token\": \"9b704f22a45647ebbde8433a5762ae2d\",\n\"area\": \"\",\n\"errorNo\": \"\",\n\"errorMessage\": \"\",\n\"htmlTitle\": \"\",\n\"hideSegmentIndex\": \"15\",\n\"businessBalance\": \"7\",\n\"remarkIndex\": \"0\",\n\"dateFormIndex\": \"18\",\n\"timeFormIndex\": \"20\",\n\"moneyNameIndex\": \"15\",\n\"occurBalance\": \"16\",\n\"postBalance\": \"21\",\n\"businessDate\": \"5\",\n\"newIndex\": \"1\""

    val str413 = "\"stockNumIndex\": \"3\",\n\"entrustPrice\": \"8\",\n\"buyDirectionIndex\": \"2\",\n\"dateIndex\": \"12\",\n\"contactIndex\": \"11\",\n\"accountIndex\": \"13\",\n\"drawIndex\": \"16\",\n\"marketIndex\": \"15\",\n\"wtAccountTypeIndex\": \"17\",\n\"stockCodeIndex\": \"6\",\n\"stockNameIndex\": \"0\",\n\"wtAccountTypeNameIndex\": \"2\""

    val str414 = "\"hideSegmentIndex\": \"6\",\n\"stockCodeIndex\": \"1\",\n\"stockIndex\": \"1\",\n\"stockNameIndex\": \"0\",\n\"collateralRateIndex\": \"3\",\n\"rzMarginRatioIndex\": \"4\",\n\"rqMarginRatioIndex\": \"5\",\n\"isrzIndex\": \"6\",\n\"isrqIndex\": \"7\""

    val str417 = "\"hideSegmentIndex\": \"17\",\n\"proMoneyIndex\": \"19\",\n\"stockNameIndex\": \"0\",\n\"stockCodeIndex\": \"1\",\n\"openingDateIndex\": \"3\",\n\"expirationDateIndex\": \"4\",\n\"contractTypeIndex\": \"18\",\n\"stockNumIndex\": \"6\",\n\"orderFrzAmtindex\": \"7\",\n\"buyDirectionIndex\": \"17\",\n\"buydirectionNameIndex\": \"5\",\n\"compactStatusIndex\": \"15\",\n\"entrustprice\": \"16\",\n\"openDateIndex\": \"3\",\n\"contractQtyIndex\": \"20\",\n\"contractAmtIndex\": \"21\",\n\"debitAmtIndex\": \"22\""

    val str418 = "\"hideSegmentIndex\": \"13\",\n\"entrustTypeName\": \"12\",\n\"reportTime\": \"3\",\n\"contactIndex\": \"5\",\n\"entrustPrice\": \"14\",\n\"amountIndex\": \"15\",\n\"businessAmount\": \"16\",\n\"stockNameIndex\": \"0\",\n\"stockCodeIndex\": \"1\",\n\"dateIndex\": \"2\",\n\"buyDirectionIndex\": \"25\",\n\"drawIndex\": \"13\",\n\"businessPrice\": \"8\",\n\"moneyTypeIndex\": \"11\",\n\"accountIndex\": \"6\",\n\"buydirectionNameIndex\": \"4\",\n\"drawsIndex\": \"9\",\n\"wTAccountTypeNameIndex\": \"12\",\n\"wTAccountTypeIndex\": \"21\",\n\"validIndex\": \"19\",\n\"dealTypeNameIndex\": \"24\",\n\"secuTypeNo\": \"25\",\n\"wtaccountTypeNameIndex\": \"12\",\n\"wtaccountTypeIndex\": \"21\""

    val str426 = "\"businessDate\": \"1\",\n\"moneyTypeIndex\": \"4\",\n\"occurBalance\": \"5\",\n\"remark\": \"6\",\n\"dateFormIndex\": \"0\",\n\"isJerg\": \"11\",\n\"zzpIndex\": \"5\",\n\"entrustTypeName\": \"3\",\n\"buyDirectionIndex\": \"3\",\n\"cptlFlowIndex\": \"12\",\n\"extFuncNoIndex\": \"13\",\n\"extMsgCode\": \"14\",\n\"msgCode\": \"15\",\n\"returnMsg\": \"8\""

    val str433 = "\"hideSegmentIndex\": \"11\",\n\"stockNameIndex\": \"0\",\n\"businessPrice\": \"13\",\n\"businessAmount\": \"12\",\n\"businessBalance\": \"14\",\n\"stockCodeIndex\": \"1\",\n\"reportTime\": \"2\",\n\"buyDirectionIndex\": \"18\",\n\"entrustPrice\": \"4\",\n\"reportDate\": \"2\",\n\"wtAccountIndex\": \"8\",\n\"contactIndex\": \"9\",\n\"drawIndex\": \"11\",\n\"amountIndex\": \"15\",\n\"buydirectionNameIndex\": \"20\",\n\"businessDate\": \"3\",\n\"wtAccountTypeIndex\": \"19\",\n\"wtAccountTypeNameIndex\": \"10\",\n\"dealTypeNameIndex\": \"21\",\n\"secuTypeNo\": \"22\",\n\"newIndex\": \"1\""

    val str439 = "\"hideSegmentIndex\": \"16\",\n\"proMoneyIndex\": \"15\",\n\"stockNameIndex\": \"0\",\n\"stockCodeIndex\": \"1\",\n\"opening_DateIndex\": \"3\",\n\"expiration_DateIndex\": \"4\",\n\"contract_TypeIndex\": \"17\",\n\"stockNumIndex\": \"6\",\n\"order_Frz_AmtIndex\": \"7\",\n\"buyDirectionIndex\": \"16\",\n\"buyDirectionNameIndex\": \"5\",\n\"contractQtyIndex\": \"18\",\n\"contractAmtIndex\": \"19\",\n\"manageFeeIndex\": \"20\",\n\"debitAmtIndex\": \"21\""

    val str446 = "\"hideSegmentIndex\": \"13\",\n\"contactIndex\": \"11\",\n\"reportTime\": \"3\",\n\"entrustTypeName\": \"7\",\n\"entrustPrice\": \"15\",\n\"amountIndex\": \"16\",\n\"businessAmount\": \"17\",\n\"stockNameIndex\": \"0\",\n\"stockCodeIndex\": \"1\",\n\"dateIndex\": \"3\",\n\"buyDirectionIndex\": \"22\",\n\"drawIndex\": \"13\",\n\"businessPrice\": \"6\",\n\"moneyTypeIndex\": \"9\",\n\"accountIndex\": \"10\",\n\"wtAccountTypeIndex\": \"24\",\n\"buyDirectionNameIndex\": \"4\",\n\"drawsIndex\": \"7\",\n\"wtAccountTypeNameIndex\": \"12\",\n\"dealTypeNameIndex\": \"23\",\n\"secuTypeNo\": \"25\""

    val str7019 = "\"businessDate\": \"1\",\n\"moneyTypeIndex\": \"4\",\n\"occurBalance\": \"5\",\n\"remark\": \"6\",\n\"dateFormIndex\": \"0\",\n\"isJerg\": \"11\",\n\"zzpIndex\": \"5\",\n\"entrustTypeName\": \"3\",\n\"buyDirectionIndex\": \"3\",\n\"cptlFlowIndex\": \"12\",\n\"extFuncNoIndex\": \"13\",\n\"extMsgCode\": \"14\",\n\"msgCode\": \"15\",\n\"returnMsg\": \"8\""

    val str7112 = "\"stockCodeIndex\": \"6\",\n\"stockNameIndex\": \"7\",\n\"marketIndex\": \"3\",\n\"stockNumIndex\": \"13\",\n\"moneyTypeIndex\": \"12\",\n\"fundAccountIndex\": \"1\",\n\"kyIndex\": \"14\",\n\"wtAccountTypeNameIndex\": \"2\",\n\"accountIndex\": \"4\""

    val str611 = "\"wtAccountTypeIndex\": \"0\",\n\"stockNameIndex\": \"1\",\n\"stockCodeIndex\": \"2\",\n\"lastPriceIndex\": \"9\",\n\"maxRiseValueIndex\": \"5\",\n\"maxDownValueIndex\": \"6\",\n\"pointIndex\": \"7\",\n\"newIndex\": \"1\""

    val str5061 = "\"wtAccountTypeIndex\": \"0\",\n\"stockNameIndex\": \"1\",\n\"stockCodeIndex\": \"2\",\n\"lastPriceIndex\": \"9\",\n\"maxRiseValueIndex\": \"5\",\n\"maxDownValueIndex\": \"6\",\n\"pointIndex\": \"7\",\n\"newIndex\": \"1\""


    val str5839 = "\"stockCodeIndex\": \"0\",\n\"orderQtyIndex\": \"1\",\n\"assignDate\": \"2\",\n\"orderDateIndex\": \"3\",\n\"takeQtyIndex\": \"4\",\n\"applyStateIndex\": \"5\",\n\"stockNameIndex\": \"6\",\n\"priceIndex\": \"7\",\n\"assignQty\": \"8\",\n\"lotradeDateIndex\": \"9\",\n\"assignSnIndex\": \"10\",\n\"luckyAmountIndex\": \"11\",\n\"tipIndex\": \"12\",\n\"applyResultType\": \"13\",\n\"secType\": \"14\",\n\"orderIdIndex\": \"15\",\n\"remarkIndex\": \"16\"\t\t"

    val str5856 = "\"stockCodeIndex\": \"0\",\n\"stockNameIndex\": \"1\",\n\"payDateIndex\": \"2\",\n\"payNumberIndex\": \"3\",\n\"payAmountIndex\": \"4\",\n\"tradeDateIndex\": \"5\",\n\"orderIdIndex\": \"6\",\n\"luckyNumINdex\": \"7\",\n\"buyPriceIndex\": \"8\",\n\"luckyDateIndex\": \"9\",\n\"luckyAmountIndex\": \"10\",\n\"marketCodeIndex\": \"11\",\n\"remarkIndex\": \"12\",\n\"unitIndex\": \"13\"\t\t"


    val str48003 = "\"smarketIndex\": \"0\",\n\"sstockCodeIndex\": \"3\",\n\"sstockNameIndex\": \"5\",\n\"sdateIndex\": \"9\",\n\"spriceIndex\": \"12\",\n\"supIndex\": \"6\",\n\"sdownIndex\": \"7\",\n\"sbaseIndex\": \"8\",\n\"ssecType\": \"17\""

    val str7101 = "\"amountIndex\": \"6\",\n\"fundaccountIndex\": \"2\",\n\"wtAccountTypeIndex\": \"13\",\n\"OrderDateIndex\": \"0\",\n\"mainBoardAmount\": \"14\",\n\"tecBordAmount\": \"15\",\n\"orderDateIndex\": \"0\""

    val str7103 = "\"hideSegmentIndex\": \"7\",\n\"stockNameIndex\": \"0\",\n\"businessPrice\": \"17\",\n\"stockCodeIndex\": \"1\",\n\"PurchaseDateIndex\": \"7\",\n\"ballotNumberIndex\": \"16\",\n\"purchasePriceIndex\": \"17\",\n\"paidAmountIndex\": \"3\",\n\"paidDateIndex\": \"2\",\n\"paidPriceIndex\": \"4\",\n\"wtAccountTypeIndex\": \"21\",\n\"dateformIndex\": \"7\",\n\"paymentAmountIndex\": \"22\",\n\"luckyDateIndex\": \"18\",\n\"freezeAmountIndex\": \"23\",\n\"marketCodeIndex\": \"24\",\n\"boardCodeIndex\": \"25\",\n\"orderIdIndex\": \"15\",\n\"remarkIndex\": \"26\",\n\"purchaseDateIndex\": \"7\""

    val str7115 = "\"hideSegmentIndex\": \"8\",\n\"bankIndex\": \"9\",\n\"bankNameIndex\": \"10\",\n\"passwordIdentifyIndex\": \"11\",\n\"balanceIndex\": \"2\",\n\"usableIndex\": \"3\",\n\"availableIndex\": \"4\",\n\"moneyTypeIndex\": \"8\",\n\"moneyNameIndex\": \"1\",\n\"totalAssetsIndex\": \"6\",\n\"fundAccountIndex\": \"0\",\n\"bankAccountIndex\": \"12\",\n\"bankCodeIndex\": \"9\"\t\t"

    split(str7115)
    //    split(str42)

  }

  def split(str: String) {
    str.split(",").map(x => x.substring(2)).map(v => v.substring(0, v.indexOf("\""))).foreach(v => println(v))
  }

}*/

class ApplyDemo {

  @Test
  def get613(): Unit = {
    val HsReq_613: String = "USER_CODE|MARKET|SECU_ACC|ACCOUNT|SECU_CODE|TRD_ID|PRICE|EXT_INST|ORDER_TYPE|CAL_QTY_FLAG|LINK_SECU_ACC|SEAT|REMARK|VID||CommBatchEntrustInfo|"
    val HsAns_613: String = "TRD_DATE|CURRENCY|EXT_INST|EXT_FUNC|USER_CODE|USER_NAME|ACCOUNT|TRD_ACC|EXT_ACC|CPTL_FLOW|INITIATOR|CPTL_AMT|EXT_PROCESS_TIME|CHANNEL|SERIAL_NO|EXT_MSG_TEXT|REMARK|"
    val ReturnGrid_613: String = "交易日期|币种|银行代码|外部功能号|客户代码|客户姓名|资产帐户|交易帐户1|交易帐户|资金流向|发起方向|转账资金|成交时间|渠道|流水号|返回信息|备注|"
    val IndexParamNew_613: String = "businessdate=1&moneytypeindex=4&occurbalance=5&remark=6&dateFormIndex=0&ISJERG=11&ZZPINDEX=5&ENTRUSTTYPENAME=3&BuydirectionIndex=3&CPTL_FLOWIndex=12&extFuncIndex=13&"
    val DisplayFormat_613: String = "交易日期|成交时间|银行代码|发起方向|币种|转账资金|返回信息|渠道|备注|流水号|资产帐户|交易帐户|资金流向|外部功能号|"

    hsReq(HsReq_613)
    indexParamNew(IndexParamNew_613)
    responseMapping(HsAns_613, ReturnGrid_613, DisplayFormat_613)
  }

  //  val HsReq_613 =
  //  val IndexParamNew_613 =
  //  val HsAns_613 =
  //  val ReturnGrid_613 =
  //  val DisplayFormat_613 =
  @Test
  def get345(): Unit = {
    val HsReq_345: String = "TRD_DATE|MARKET|BOARD|SEAT|CUST_CODE|ACCOUNT|SECU_ACC|BRANCH|SECU_INTL|SECU_CODE|ORDER_ID|TAKE_DATE|R_LAST_SN|R_COUNT|K_TRD_DATE|K_MARKET|K_BOARD|K_ORDER_ID|DATE_BGN|DATE_END|"
    val HsAns_345: String = "TRD_DATE|MARKET|BOARD|SEAT|CUST_CODE|ACCOUNT|SECU_ACC|BRANCH|SECU_INTL|SECU_CODE|SECU_NAME|ORDER_ID|TAKE_DATE|TAKE_QTY|ORDER_PRICE|PAY_DATE|PAY_QTY|PAY_AMT|GIVE_UP_QTY|MKT_CFM_QTY|PREPAY_AMT|REMARK|MARKET|({13}*{14})|MARKET|BOARD|SECU_CLS|SECU_CLS|"
    val Request_345: String = "DEALDATE|WTACCOUNTTYPE|||USERCODE|FUNDACCOUNT|WTACCOUNT|KHBranch||STOCKCODE||LUCKYDATE||R_COUNT|||||begindate|enddate|"
    val ReturnGrid_345: String = "交易日期|市场|板块|席位|客户号|资产账号|股东账户|分支机构|证券内码|证券代码|证券名称|委托序号|中签日期|中签数量|申购价格|缴款日期|缴款数量|缴款金额|放弃数量|交易所确认数量|应缴款金额|备注|市场类别|中签金额|市场编码|板块编码|证券类别编码|单位|"
    val IndexParamNew_345: String = "HideSegmentIndex=7&STOCKNAMEINDEX=0&BUSINESSPRICE=17&STOCKCODEINDEX=1&PurchasedateIndex=7&ballotnumberIndex=16&PurchasepriceIndex=17&PaidamountIndex=3&PaidDateIndex=2&PaidPriceIndex=4&WTACCOUNTTYPEINDEX=21&DATEFORMINDEX=7&PAYMENTAMOUNTINDEX=22&luckydateIndex=18&marketcodeindex=23&boardcodeindex=24&orderIdIndex=15&remarkIndex=25&"
    val DisplayFormat_345: String = "证券名称|证券代码|缴款日期|缴款数量|缴款金额|放弃数量|市场|交易日期|板块|席位|客户号|资产账号|股东账户|分支机构|证券内码|委托序号|中签数量|申购价格|中签日期|交易所确认数量|应缴款金额|市场类别|中签金额|市场编码|板块编码|备注|证券类别编码|单位|"

    hsReq(HsReq_345)
    indexParamNew(IndexParamNew_345)
    request(Request_345)
    responseMapping(HsAns_345, ReturnGrid_345, DisplayFormat_345)
  }

  @Test
  def get617(): Unit = {
    val HsReq: String = "CUST_CODE|ACC_PWD|IN_ACCOUNT|CURRENCY|OP_REMARK|"
    val HsAns: String = "SERIAL_NO|ACCOUNT|BALANCE|AVAILABLE|USER_CODE|BIZ_NO|"
    val Request: String = "usercode|ComPassword|FUNDACCOUNT|MONEYTYPE||"
    val ReturnGrid: String = "流水号|资产账户|余额|可用数|客户代码|业务序号|"
    val IndexParamNew: String = ""
    val DisplayFormat: String = "流水号|资产账户|余额|可用数|客户代码|业务序号|"

    hsReq(HsReq)
    request(Request)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get618(): Unit = {

    val HsReq: String = "CUST_CODE|ACCOUNT|CURRENCY|FLAG|BRANCHES|R_LAST_SN|R_COUNT|K_CUST_CODE|K_ACCOUNT|AUTHORITY_FLAG|"
    val HsAns: String = "CUST_CODE|CUST_NAME|OPEN_BRANCH|ACCOUNT|CURRENCY|EXT_ACC|TRDINAMT|OTHERAMT|FLAG|ISMAIN|REMARK|"
    val Request: String = "USERCODE||MONEYTYPE||||999|||1|"
    val ReturnGrid: String = "客户代码|客户姓名|开户分支|账户|货币代码|外部账户|本行转入资金|他行转入资金|是否开通内转|是否主资金账户|备注|"
    val IndexParamNew: String = "fundaccountIndex=0&bankIndex=6&banknameIndex=7&CurrencyCodeIndex=2&CurrencyIndex=3&UsableIndex=5&AvailableIndex=4&passwordidentifyindex=8&"
    val DisplayFormat: String = "客户代码|客户姓名|开户分支|账户|货币代码|外部账户|本行转入资金|他行转入资金|是否开通内转|是否主资金账户|备注|"

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get122(): Unit = {

    val HsReq: String = "USER_CODE|MARKET|SECU_ACC|"
    val HsAns: String = "USER_CODE|MARKET|SECU_ACC|SECU_ACC_NAME|DFT_ACC|MAIN_FLAG|BIND_SEAT|BIND_STATUS|STATUS|EXT_CLS|MARKET|MARKET|"
    val Request: String = "USERCODE|WTACCOUNTTYPE|WTACCOUNT|"
    val ReturnGrid: String = "客户代码|市场类别|股东代码|股东姓名|缺省资产账户|主副标志|指定席位|指定交易状态|帐户状态|外部类型|市场名称|市场代码|"
    val IndexParamNew: String = "WtAccountTypeIndex=0&ACCOUNTINDEX=1&mainflag=2&holderstatus=3&SeatNoIndex=5&marketCodeIndex=6&bindStatusIndex=7&"
    val DisplayFormat: String = "市场类别|股东代码|主副标志|帐户状态|市场名称|指定席位|市场代码|指定交易状态|"

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get5846(): Unit = {

    val HsReq: String = "INT_ORG|CUST_CODE|PRV_SERIAL_NO|BIZ_TYPE|TRUST_TYPE|STK_CODE|TRDACCT|STKPBU|CRP_ACT_CODE|ETTLM_TYPE|ETTLM_NO|ANN_NO|PRXY_NO|APPROVE_QTY|OPPOSE_QTY|ABANDON_QTY|S_STKPBU|LIST_STATUS|SALE_FLAG|EXE_ORG|SHARE_ATTR|FREEZE_MON|CASE_NO|ORG_FREEZE_SN|LINK_DEPTH|DECLARE_QTY|NOTE_TYPE|CUST_TYPE|REMARK|"
    val HsAns: String = "SERIAL_NO|"
    val Request: String = "BRANCH|usercode|tradeNo|sgtTradeId|declareType|secuIntl|WTACCOUNT||bhNo|||meetingSeq|vid|agreeNum|rejectNum|giveUpNum||||||||||qualtity|noteType|||"
    val ReturnGrid: String = "业务序号|"
    val IndexParamNew: String = ""
    val DisplayFormat: String = "业务序号|"

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get5850(): Unit = {
    val HsReq: String = "SETTNO|STK_CODE|ISIN_NO|PUB_NO|BILL_NO|UP_NO|DOWN_NO|FREE_NO|TRDACCT|INT_ORG|CUST_CODE|STKEX|SERVICE_NAME|SERVICE_TYPE|STKBD|DECLARE_TYPE|HK_TYPE|F_FUNCTION|F_RUNTIME|"
    val HsAns: String = "SERIAL_NO|"
    val Request: String = "JS990|secuIntl||pubNo|billNo|agreeNo|rejectNo|giveUpNo|wtaccount|KHBranch|UserCode|||||||||"
    val ReturnGrid: String = "流水序号|"
    val DisplayFormat: String = "流水序号|"
    val IndexParamNew: String = "SerialNoIndex=0&"

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get5028(): Unit = {
    val HsReq: String = "CUST_CODE|AGENT_BIZ_TYPE|BRANCH|"
    val HsAns: String = "CUSTOMER|AGENT_BIZ_TYPE|BEGIN_DATE|EXPIRY_DATE|"
    val Request: String = "USERCODE|ContractType|KHBranch|"
    val ReturnGrid: String = "客户代码|签约类型|协议生效日|协议到期日|"
    val DisplayFormat: String = "客户代码|签约类型|协议生效日|协议到期日|"
    val IndexParamNew: String = "startDateIndex=2&endDateIndex=3&"

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get6513(): Unit = {
    val HsReq: String = "BEGIN_DATE|END_DATE|USER_CODE|ACCOUNT|USE_DZD|CURRENCY|"
    val HsAns: String = "SETT_DATE|TRD_DATE|OCCUR_DATE|USER_CODE|CUST_NAME|SERIAL_NO|CUST_CLS|ACCOUNT|CURRENCY|ACC_CLS|BRANCH|SECU_ACC|BIZ_CODE|TRD_ID|ORDER_ID|MARKET|BOARD|BOARD|SEAT|SECU_CODE|SECU_NAME|SECU_CLS|PRICE|QTY|ORDER_FRZ_AMT|MATCHED_PRICE|MATCHED_QTY|MATCHED_AMT|MATCHED_TIME|MATCHED_SN|SETT_AMT|SETT_QTY|CPTL_AMT|BALANCE|SHARE_BLN|OP_BRH|OP_USER|OP_NAME|OP_ROLE|EXT_INST|CHANNELS|COMMISSION|STAMP_DUTY|TRADE_FEE|ADMIN_FEE|HANDLE_FEE|TRANS_FEE|XTRANS_FEE|CLEAR_FEE|GZLX|OTHER_FEE|FEE_10C|ORDER_TYPE|REMARK|BIZ_NAME|NET_COMMISSION|HK_RATE|"
    val Request: String = "BEGINDATE|ENDDATE|USERCODE||||"
    val ReturnGrid: String = "清算日期|交易日期|发生日期|客户代码|客户姓名|用户流水号|客户分类|资产账户|货币|资产账户分类|分支机构|股东代码|业务代码|交易行为|合同序号|交易市场|板块|板块A|席位|证券代码|证券名称|证券类别|委托价格|委托数量|委托冻结金额|成交价格|成交数量|成交金额|成交时间|成交编号|清算过户金额|清算股份数量|发生金额|资金余额|股份余额|操作分支|操作用户|操作用户名|操作角色|外部机构|交易渠道|手续费|印花税|交易规费|证管费|经手费|过户费|交易所过户费|清算费|国债利息|其它费|限售所得税|委托类型|备注信息|业务名称|净手续费|沪港通汇率|"
    val DisplayFormat: String = "清算日期|交易日期|发生日期|客户代码|客户姓名|用户流水号|客户分类|资产账户|货币|股东代码|业务代码|交易行为|合同序号|交易市场|证券代码|证券名称|证券类别|委托价格|委托数量|委托冻结金额|成交价格|成交数量|成交金额|成交时间|成交编号|清算过户金额|清算股份数量|发生金额|资金余额|股份余额|手续费|印花税|交易规费|证管费|经手费|过户费|交易所过户费|清算费|国债利息|其它费|限售所得税|委托类型|备注信息|业务名称|净手续费|沪港通汇率|板块A|"
    val IndexParamNew: String = "InitDateIndex=1&OrderDateIndex=1&UserCodeIndex=3&UserNameIndex=4&FundAccountIndex=7&WtAccountIndex=9&WtAccountIndex=9&refbusicodeindex=10&StockCodeIndex=14&StockNameIndex=15&Stocktypeindex=16&OrderPriceIndex=17&OrderQtyIndex=18&MatchPriceIndex=20&MatchQtyIndex=21&DealMoneyIndex=22&OrderTimeIndex=23&ReportCodeindex=24&MoneyIndex=27&OccurBalanceIndex=27&FareIndex=30&StampDutyIndex=31&TransFeeIndex=35&Otherchargesindex=39&BusinessNameIndex=43&"

    //    HsReq=BEGIN_DATE|END_DATE|USER_CODE|ACCOUNT|USE_DZD|CURRENCY|
    //      HsAns=SETT_DATE|TRD_DATE|OCCUR_DATE|USER_CODE|CUST_NAME|SERIAL_NO|CUST_CLS|ACCOUNT|CURRENCY|ACC_CLS|BRANCH|SECU_ACC|BIZ_CODE|TRD_ID|ORDER_ID|MARKET|BOARD|BOARD|SEAT|SECU_CODE|SECU_NAME|SECU_CLS|PRICE|QTY|ORDER_FRZ_AMT|MATCHED_PRICE|MATCHED_QTY|MATCHED_AMT|MATCHED_TIME|MATCHED_SN|SETT_AMT|SETT_QTY|CPTL_AMT|BALANCE|SHARE_BLN|OP_BRH|OP_USER|OP_NAME|OP_ROLE|EXT_INST|CHANNELS|COMMISSION|STAMP_DUTY|TRADE_FEE|ADMIN_FEE|HANDLE_FEE|TRANS_FEE|XTRANS_FEE|CLEAR_FEE|GZLX|OTHER_FEE|FEE_10C|ORDER_TYPE|REMARK|BIZ_NAME|NET_COMMISSION|HK_RATE|
    //      Request=BEGINDATE|ENDDATE|USERCODE||||
    //      ReturnGrid=清算日期|交易日期|发生日期|客户代码|客户姓名|用户流水号|客户分类|资产账户|货币|资产账户分类|分支机构|股东代码|业务代码|交易行为|合同序号|交易市场|板块|板块A|席位|证券代码|证券名称|证券类别|委托价格|委托数量|委托冻结金额|成交价格|成交数量|成交金额|成交时间|成交编号|清算过户金额|清算股份数量|发生金额|资金余额|股份余额|操作分支|操作用户|操作用户名|操作角色|外部机构|交易渠道|手续费|印花税|交易规费|证管费|经手费|过户费|交易所过户费|清算费|国债利息|其它费|限售所得税|委托类型|备注信息|业务名称|净手续费|沪港通汇率|
    //      DisplayFormat=清算日期|交易日期|发生日期|客户代码|客户姓名|用户流水号|客户分类|资产账户|货币|股东代码|业务代码|交易行为|合同序号|交易市场|证券代码|证券名称|证券类别|委托价格|委托数量|委托冻结金额|成交价格|成交数量|成交金额|成交时间|成交编号|清算过户金额|清算股份数量|发生金额|资金余额|股份余额|手续费|印花税|交易规费|证管费|经手费|过户费|交易所过户费|清算费|国债利息|其它费|限售所得税|委托类型|备注信息|业务名称|净手续费|沪港通汇率|板块A|
    //      IndexParamNew=InitDateIndex=1&OrderDateIndex=1&UserCodeIndex=3&UserNameIndex=4&FundAccountIndex=7&WtAccountIndex=9&WtAccountIndex=9&refbusicodeindex=10&StockCodeIndex=14&StockNameIndex=15&Stocktypeindex=16&OrderPriceIndex=17&OrderQtyIndex=18&MatchPriceIndex=20&MatchQtyIndex=21&DealMoneyIndex=22&OrderTimeIndex=23&ReportCodeindex=24&MoneyIndex=27&OccurBalanceIndex=27&FareIndex=30&StampDutyIndex=31&TransFeeIndex=35&Otherchargesindex=39&BusinessNameIndex=43&


    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get5847(): Unit = {
    val HsReq: String = ""
    val HsAns: String = "serialNoIndex|occurDateIndex|occurTimeIndex|intOrgIndex|trustSnIndex|usercodeIndex|stockCodeIndex|annNoIndex|agreeNumIndex|rejectNumIndex|giveUpNumIndex|tradeDateIndex|trustStateIndex|bizTypeIndex|trustTypeIndex|cpnActCodeIndex|rightTypeIndex|rightNoIndex|proxyNoIndex|declareNumIndex|stockNameIndex|bizTypeNameIndex|trustStateNameIndex|"
    val Request: String = ""
    val ReturnGrid: String = "流水序号|发生日期|发生时间|内部机构|委托序号|客户代码|证券代码|公告编号|赞成数量|反对数量|弃权数量|委托日期|深港通投票及公司行为委托状态|业务类别|委托类型|公司行为代码|权益类别|权益编号|议案编号|申报数量|证券名称|深港通回报业务类型F|深港通投票及公司行为委托状态F|"
    val DisplayFormat: String = "流水序号|发生日期|发生时间|内部机构|委托序号|客户代码|证券代码|公告编号|赞成数量|反对数量|弃权数量|委托日期|深港通投票及公司行为委托状态|业务类别|委托类型|公司行为代码|权益类别|权益编号|议案编号|申报数量|证券名称|深港通回报业务类型F|深港通投票及公司行为委托状态F|"
    val IndexParamNew: String = ""

    //    HsReq=BEGIN_DATE|END_DATE|USER_CODE|ACCOUNT|USE_DZD|CURRENCY|
    //      HsAns=SETT_DATE|TRD_DATE|OCCUR_DATE|USER_CODE|CUST_NAME|SERIAL_NO|CUST_CLS|ACCOUNT|CURRENCY|ACC_CLS|BRANCH|SECU_ACC|BIZ_CODE|TRD_ID|ORDER_ID|MARKET|BOARD|BOARD|SEAT|SECU_CODE|SECU_NAME|SECU_CLS|PRICE|QTY|ORDER_FRZ_AMT|MATCHED_PRICE|MATCHED_QTY|MATCHED_AMT|MATCHED_TIME|MATCHED_SN|SETT_AMT|SETT_QTY|CPTL_AMT|BALANCE|SHARE_BLN|OP_BRH|OP_USER|OP_NAME|OP_ROLE|EXT_INST|CHANNELS|COMMISSION|STAMP_DUTY|TRADE_FEE|ADMIN_FEE|HANDLE_FEE|TRANS_FEE|XTRANS_FEE|CLEAR_FEE|GZLX|OTHER_FEE|FEE_10C|ORDER_TYPE|REMARK|BIZ_NAME|NET_COMMISSION|HK_RATE|
    //      Request=BEGINDATE|ENDDATE|USERCODE||||
    //      ReturnGrid=清算日期|交易日期|发生日期|客户代码|客户姓名|用户流水号|客户分类|资产账户|货币|资产账户分类|分支机构|股东代码|业务代码|交易行为|合同序号|交易市场|板块|板块A|席位|证券代码|证券名称|证券类别|委托价格|委托数量|委托冻结金额|成交价格|成交数量|成交金额|成交时间|成交编号|清算过户金额|清算股份数量|发生金额|资金余额|股份余额|操作分支|操作用户|操作用户名|操作角色|外部机构|交易渠道|手续费|印花税|交易规费|证管费|经手费|过户费|交易所过户费|清算费|国债利息|其它费|限售所得税|委托类型|备注信息|业务名称|净手续费|沪港通汇率|
    //      DisplayFormat=清算日期|交易日期|发生日期|客户代码|客户姓名|用户流水号|客户分类|资产账户|货币|股东代码|业务代码|交易行为|合同序号|交易市场|证券代码|证券名称|证券类别|委托价格|委托数量|委托冻结金额|成交价格|成交数量|成交金额|成交时间|成交编号|清算过户金额|清算股份数量|发生金额|资金余额|股份余额|手续费|印花税|交易规费|证管费|经手费|过户费|交易所过户费|清算费|国债利息|其它费|限售所得税|委托类型|备注信息|业务名称|净手续费|沪港通汇率|板块A|
    //      IndexParamNew=InitDateIndex=1&OrderDateIndex=1&UserCodeIndex=3&UserNameIndex=4&FundAccountIndex=7&WtAccountIndex=9&WtAccountIndex=9&refbusicodeindex=10&StockCodeIndex=14&StockNameIndex=15&Stocktypeindex=16&OrderPriceIndex=17&OrderQtyIndex=18&MatchPriceIndex=20&MatchQtyIndex=21&DealMoneyIndex=22&OrderTimeIndex=23&ReportCodeindex=24&MoneyIndex=27&OccurBalanceIndex=27&FareIndex=30&StampDutyIndex=31&TransFeeIndex=35&Otherchargesindex=39&BusinessNameIndex=43&

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get5845(): Unit = {
    val HsReq: String = ""
    val HsAns: String = "TradeDateIndex|InterCfgIndex|recSnIndex|marketNoIndex|NoteTypeIndex|noteTimeIndex|StockTypeIndex|StockCodeIndex|IsListIndex|RightTypeIndex|RightCodeIndex|Account1Index|Account2Index|Date1Index|Date2Index|Date3Index|Amount1Index|Amount2Index|Amount3Index|Price1Index|Price2Index|MoneyTypeIndex|Ratio|Ratio1Index|Ratio2Index|Quality1Index|Quality2Index|type1Index|type2Index|AssistCode1Index|AssistCode2Index|Extra1Index|Extra2Index|PramStateIndex|BackupIndex|StockNameIndex|MoneyTypeNameIndex|NoteNameIndex|RightTypeNameIndex|StockTypeNameIndex|meetingDateIndex|"
    val Request: String = ""
    val ReturnGrid: String = "交易日期|接口配置序号|记录序号|市场代码|通知类别|通知日期|股份性质|证券代码|上市状态|权益类别|权益编号|账户1|账户2|日期1|日期2|日期3|金额1|金额2|金额3|价格1|价格2|深港信息币种|汇率|比率1|比率2|数量1|数量2|类型1|类型2|辅助代码1|辅助代码2|附加说明1|附加说明2|备用|参数状态|证券名称|深港信息币种F|深港信息通知类别F|深港信息权益类别F|深港信息流通类型说明F|会议时间|"
    val DisplayFormat: String = "交易日期|接口配置序号|记录序号|市场代码|通知类别|通知日期|股份性质|证券代码|上市状态|权益类别|权益编号|账户1|账户2|日期1|日期2|日期3|金额1|金额2|金额3|价格1|价格2|深港信息币种|汇率|比率1|比率2|数量1|数量2|类型1|类型2|辅助代码1|辅助代码2|附加说明1|附加说明2|备用|参数状态|证券名称|深港信息币种F|深港信息通知类别F|深港信息权益类别F|深港信息流通类型说明F|会议时间|"
    val IndexParamNew: String = ""

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get5849(): Unit = {
    val HsReq: String = ""
    val HsAns: String = "TradeDateIndex|InterCfgIndex|recSnIndex|marketNoIndex|NoteTypeIndex|noteTimeIndex|settleOrgNoIndex|StockCodeIndex|rightTypeIndex|flowTypeIndex|listYearIndex|rightTimesIndex|Account1Index|Account2Index|Date1Index|Date2Index|Date3Index|Amount1Index|Amount2Index|Amount3Index|Price1Index|Price2Index|MoneyTypeIndex|Ratio|Ratio1Index|Ratio2Index|Quality1Index|Quality2Index|type1Index|type2Index|AssistCode1Index|AssistCode2Index|Extra1Index|Extra2Index|backupIndex|noteTypeNameIndex|stockNameIndex|meetingDateIndex|"
    val Request: String = ""
    val ReturnGrid: String = "交易日期|接口配置序号|记录序号|市场代码|通知类别|通知日期|结算参与机构的清算编号|证券代码|权益类别|流通类别|挂牌年份|权益次数|账户1|账户2|日期1|日期2|日期3|金额1|金额2|金额3|价格1|价格2|沪港通币种|汇率|比率1|比率2|数量1|数量2|类型1|类型2|辅助代码1|辅助代码2|附加说明1|附加说明2|备用|沪港通通知类别F|证券名称|会议时间|"
    val DisplayFormat: String = "交易日期|接口配置序号|记录序号|市场代码|通知类别|通知日期|结算参与机构的清算编号|证券代码|权益类别|流通类别|挂牌年份|权益次数|账户1|账户2|日期1|日期2|日期3|金额1|金额2|金额3|价格1|价格2|沪港通币种|汇率|比率1|比率2|数量1|数量2|类型1|类型2|辅助代码1|辅助代码2|附加说明1|附加说明2|备用|沪港通通知类别F|证券名称|会议时间|"
    val IndexParamNew: String = ""

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }

  @Test
  def get5812(): Unit = {
    val HsReq: String = "CUSTOMER|AGENT_BIZ_TYPE|"
    val HsAns: String = "CUSTOMER|AGENT_BIZ_TYPE|BEGIN_DATE|EXPIRY_DATE|"
    val Request: String = "USERCODE|h|"
    val ReturnGrid: String = "CUSTOMER|AGENT_BIZ_TYPE|BEGIN_DATE|EXPIRY_DATE|"
    val DisplayFormat: String = "EXPIRY_DATE|"
    val IndexParamNew: String = ""

    //    hsReq(HsReq)
    request(Request)
    requestMapping(Request, HsReq)
    indexParamNew(IndexParamNew)
    responseMapping(HsAns, ReturnGrid, DisplayFormat)
  }


  def requestMapping(request: String, hsReq: String): Array[Unit] = {
    System.err.println("++++++++++++++++++++++++requestMapping++++++++++++++++++++++++++++")
    val requests: Array[String] = request.split("\\|")
    val hsReqs: Array[String] = hsReq.split("\\|")
    var i: Int = -1;
    requests.map((req: String) => {
      i = i + 1
      //req.setTrdacct(requests.getWtAccount());
      if (req != "") println(s"req.set${hsReqs(i).charAt(0) + hsReqs(i).substring(1).toLowerCase}(requests.get${req.charAt(0).toString.toUpperCase() + req.substring(1)}());")
    })
  }

  def hsReq(attribute: String): Unit = {
    val func: String => String = (param: String) => {
      var upperCase: String = param.toLowerCase()
      while (upperCase.contains("_")) {
        upperCase = upperCase.substring(0, upperCase.lastIndexOf("_")) + upperCase.charAt(upperCase.lastIndexOf("_") + 1).toString.toUpperCase + upperCase.substring(upperCase.lastIndexOf("_") + 2)
      }
      upperCase
    }
    val hsReq: Array[String] = attribute.split("\\|").map(func)
    System.err.println("++++++++++++++++++++++++hsReq++++++++++++++++++++++++++++")
    hsReq.map(println(_: String))
  }


  def indexParamNew(str: String): Unit = {
    System.err.println("++++++++++++++++++++++++indexParamNew++++++++++++++++++++++++++++")
    //    System.err.println(s"public  String grid = ${str}")
    if (str == "") return
    str.split("&").map((x: String) => {
      val y: String = x.charAt(0).toString.toLowerCase + x.substring(1)
      "public  String " + y.substring(0, y.lastIndexOf("=") + 1) + "\"" + y.substring(y.lastIndexOf("=") + 1) + "\"" + ";"
    }).foreach(println(_: String))
  }

  def request(str: String): Unit = {
    System.err.println("++++++++++++++++++++++++request++++++++++++++++++++++++++++")
    str.split("\\|").map((x: String) => {
      val y: String = x.toLowerCase
      //      "public  String " + y.substring(0, y.lastIndexOf("=") + 1) + "\"" + y.substring(y.lastIndexOf("=") + 1) + "\"" + ";"
      val str: String = if (y != "") "public  String " + y + "=\"\";" else ""
      str
    }).foreach((str: String) => if (str != "") println(str))
  }

  def responseMapping(hsAns: String, grid: String, format: String): Unit = {
    System.err.println()
    System.err.println("++++++++++++++++++++++++responseMapping++++++++++++++++++++++++++++")
    val hsAnss: Array[String] = hsAns.split("\\|")
    val grids: Array[String] = grid.split("\\|")
    val formats: Array[String] = format.split("\\|")
    import scala.collection.mutable.Map

    val map: Map[String, String] = Map()

    for (i <- 0 to hsAnss.length - 1) {
      map += grids(i) -> hsAnss(i).toLowerCase
    }
    val combinations: Array[(String, String, String)] = formats.map((x: String) => {
      if (map.contains(x)) {
        println((x, x, map(x)))
        (x: String, x: String, map(x): String)
      } else {
        print("====================================================")
        ("": String, "": String, "": String)
      }
    })
    combinations.foreach(a => println(s"row.get${a._3.charAt(0).toString.toUpperCase() + a._3.substring(1)}(), // ${a._2}"))

    //    val array: Array[(String, String)] = Array()
    //    for (i <- 0 to hsAnss.length) {
    //      array(i) = (grids(i), hsAnss(i))
    //    }

  }


}