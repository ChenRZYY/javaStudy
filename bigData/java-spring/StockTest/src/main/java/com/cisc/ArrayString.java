package com.cisc;

import java.util.Arrays;

public class ArrayString {

	public static void main(String[] args) {
		
		String test= "胜";
//		byte[] by = new byte[];
		byte[] bytes = test.getBytes();
		System.err.println("二进制 "+Arrays.toString(bytes));
		
		String test2 = new String(bytes);
		System.err.println("数组 "+test2);
		
		
		String test1 = "{\"Action\":60,\"UpDownIndex\":\"3\",\"Grid\":\"名称|最新|幅度|涨跌|换手|量比|总额|振幅|代码|开市状态|\\r\\n1.上证指数|2770.57|0.59%|16.21|--%|2.86|628.21亿|0.88%|1A0001|0|\\r\\n2.深证成指|8524.88|0.92%|77.96|--%|3.22|1000.28亿|0.98%|2A01|0|\\r\\n3.创业板指|1425.30|0.83%|11.70|--%|3.35|279.20亿|0.81%|399006|0|\\r\\n4.平安银行|11.37|0.09%|0.01|0.13%|1.70|2.47亿|1.23%|000001|3|\\r\\n\",\"BinData\":\"CTsAAAAAAAAAAAAA/+Dg4Pz/4OD////g4OD8/+Dg////4ODg/P/g4P///+Dg4Pz/4OD//w==\",\"StockNameIndex\":\"0\",\"CommMode\":\"4352|4608|4608|4609|\",\"InstrumentType\":\"0|0|0|0|\",\"MaxCount\":\"4\",\"MarketFlag\":\"1|2|2|2|\",\"MarketStatusIndex\":\"9\",\"Direction\":\"2\",\"TotalHPerIndex\":\"5\",\"UpDownPIndex\":\"2\",\"ErrorNo\":\"5\",\"ShockRangIndex\":\"7\",\"TotalMIndex\":\"6\",\"NewPriceIndex\":\"1\",\"WarrantType\":\"0|0|0|0|\",\"area\":\"zsStateData\",\"ThroughFlag\":\"0|0|0|5|\",\"SuspFlag\":\"2|0|0|1|\",\"ProductType\":\"0|0|0|0|\",\"StockCodeIndex\":\"8\",\"Price\":\"CDHQ\",\"NewMarketNo\":\"4352|4608|4608|4609|\",\"StockProp\":\"xxx00000000000000000100000000151|xxx00000000000000000100000000251|xxx00000000000000000100000000251|xxx11000000001000000000000000201|\",\"ChangePerIndex\":\"4\"}";
		
		
	}
}
