package com.cisc;

import java.util.HashMap;
import java.util.Map;

public class TradeClient {
    /**
	 * Greet server. If provided, the first element of {@code args} is the name to
	 * use in the greeting.
	 */
	public static boolean test() throws ClassNotFoundException {

		long start = System.currentTimeMillis();
		TradeClient client = null;
//		Class clz = Class.forName("com.cisc.util.LimitUtil");
//		for (int i = 0; i < clz.getFields().length; i++) {
//			System.out.println("Methods==" + clz.getFields()[i]);
//		}
//		try {
//			if(client==null){
//				//client = new TradeClient("10.137.37.93", 50052, buildSslContext("ca.crt", "client.crt", "client.pem"));
//				client = new TradeClient("10.137.37.93", 50052, buildSslContext("ca.crt", null, null));
//			}
//			client = new TradeClient("kptfg.china-invs.cn", 563, buildSslContext(
//					"ca.crt",
//					null,
//					null));
			//System.out.println("client=="+client);
			long ran=(long) ((Math.random()*9+1)*100000000000L);
			String mac = String.valueOf(ran);
			System.out.println("MAC="+mac);

			//*****************************************************************
			//压力测试
//			Map<String, String> data254 = new HashMap<String, String>();
//			data254.put("Action","254");
//			client.callRmote("254", data254, mac);

			//获取验证码
			Map<String, String> data0 = new HashMap<String, String>();
			data0.put("Action","41092");
			data0.put("reqlinktype","1");
			data0.put("newindex","1");
			data0.put("mobilecode",mac);
			data0.put("terminalcode","kpt");

//			//模拟长耗时功能或柜台响应慢
//			Map<String, String> data60000 = new HashMap<String, String>();
//			data60000.put("Action","60000");
//			client.callRmote("60000", data60000, mac);

			//获取公钥
			Map<String, String> data00 = new HashMap<String, String>();
			data00.put("Action","40141");
			data00.put("reqlinktype","1");
			data00.put("newindex","1");
			data00.put("mobilecode",mac);
			data00.put("terminalcode","kpt");
//			client.callRmote("40141", data00, mac);

//			System.out.println("CheckCode="+CheckCode);
//			System.out.println("CheckToken="+CheckToken);
//			System.out.println("Modulus_id="+Modulus_id);

			//登录
			Map<String, String> data = new HashMap<String, String>();
            data.put("Action","100");
            data.put("Reqno",mac);
            data.put("MobileCode",mac);
			data.put("YybCode","14080");
            data.put("accounttype","KHBH");
            data.put("MaxCount","100");
            data.put("AccountList","1");
            data.put("TFrom", "kpt");
            data.put("newindex", "1");
			data.put("OS", "Win2008 R2_压力测试");
//			data.put("CheckCode",CheckCode);
//			data.put("CheckToken", CheckToken);
			//********** 可变入参 **********
//			data.put("Modulus_id", "2");
//			data.put("Password","2359d78691b719b65766e11c86bd8aa7fb45fce997a9f827a2c6b54300bc37a590678855b58d23a76e7d2875bd97f9a6dac1e435be65345777339255ad11487c9ba9eb52cfc3ecfcf510040b5024126db5033c19492b1d1e23764fb69510ee761b277f552930be8f389c99d33644c8fe6e3fd2005e65dd4dfe7cf55ab390ad80");
			//data.put("Modulus_id", "1");
			//data.put("Password","0b873ce382080bede79d44aa990d1dc60a1d7c4be21d129bd8503bc0a1e201982c304425b50e4311f942d7df6aa79ddc71871790f43c45aef71714559875fc344138f3712175bbd7e8a43cbd3c2aa53f54bd205a6ec8fa026118fdf01194e166248eb7ba24681f6197595d02f481c673157009ab43a13ca992525695b4b45526");
			//data.put("Account","24512072");
//			data.put("Account","10109967");
			//******************************
//			client.callRmote("100", data,mac);
            return false;

//			System.out.println("token="+token);
//			System.out.println("IntactToServer="+IntactToServer);

//			//340 多账户银行账户查询
//			Map<String, String> data340 = new HashMap<String, String>();
//			data340.put("Action","340");
//			data340.put("Reqno","1212");
//			data340.put("UserCode","34300271");
//			data340.put("Token",token);
//			data340.put("IntactToServer",IntactToServer);
//			data340.put("reqlinktype","1");
//			data340.put("newindex","1");
//			data340.put("mobilecode",mac);
//			data340.put("terminalcode","kpt");
//			client.callRmote("340", data340, mac);

//-------------------------------------------------------------
            //委托买入
//			Map<String, String> data1 = new HashMap<String, String>();
//			data1.put("Action","110");
//			data1.put("Token",token);
//			data1.put("IntactToServer",IntactToServer);
//			data1.put("Reqno",mac);
//			data1.put("MobileCode",mac);
//			data1.put("YybCode","14080");
//			//**** 可变入参，与登录账户对应 ***
//			data1.put("Account","82000942");
//			data1.put("WTAccount", "A292934239");
//			//*******************************
//			data1.put("AccountType","ZJACCOUNT");
//			data1.put("WTAccountType","SHACCOUNT");
//			data1.put("StockCode","601258");
//			data1.put("Price","1.30");
//			data1.put("Volume","100");
//			data1.put("Direction","B");
//			client.callRmote("110", data1,mac);

//-------------------------------------------------------------
//			//查交割单
//			Map<String, String> data121 = new HashMap<String, String>();
//			data121.put("Action","121");
//			data121.put("Token",token);
//			data121.put("IntactToServer",IntactToServer);
//			data121.put("Reqno",mac);
//			data121.put("MobileCode",mac);
//			data121.put("Direction","0");
//			data121.put("BeginDate","20190512");
//			data121.put("EndDate","20190518");
//			data121.put("Usercode","24512072");
//
//			client.callRmote("121", data121,mac);

//-------------------------------------------------------------

//			Map<String, String> data3 = new HashMap<String, String>();
//			//修改密码
//			data3.put("Action","112");
//			data3.put("Token",token);
//			data3.put("IntactToServer",IntactToServer);
//			data3.put("Reqno","201808080002");
//			data3.put("MobileCode","13000000000");
//			data3.put("password","123555");
//			data3.put("newPassword","123444");
//			data3.put("passwordtype","2");
//			data3.put("NewIndex","1");
//			client.callRmote("112", data3,mac);

//*****************************************************************************

	}
    
    public Map<String, String> getCheckCode(String mac) {
        
        //        long ran=(long) ((Math.random()*9+1)*100000000000L);
        //        String mac = String.valueOf(ran);
        //        System.out.println("MAC="+mac);
        
        //获取验证码
        Map<String, String> codeMap = new HashMap<String, String>();
        codeMap.put("Action", "41092");
        codeMap.put("reqlinktype", "1");
        codeMap.put("newindex", "1");
        codeMap.put("mobilecode", mac);
        codeMap.put("terminalcode", "kpt");
        
        return codeMap;
    }
    
    public Map<String, String> getPublicKey(String mac) {
        //获取公钥
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("Action", "40141");
        keyMap.put("reqlinktype", "1");
        keyMap.put("newindex", "1");
        keyMap.put("mobilecode", mac);
        keyMap.put("terminalcode", "kpt");
        //          client.callRmote("40141", data00, mac);
        
        return keyMap;
    }
    
    public Map<String, String> login(String mac) {
        //登录
        Map<String, String> data = new HashMap<String, String>();
        data.put("Action", "100");
        data.put("Reqno", mac);
        data.put("MobileCode", mac);
        data.put("YybCode", "14080");
        data.put("accounttype", "KHBH");
        data.put("MaxCount", "100");
        data.put("AccountList", "1");
        data.put("TFrom", "kpt");
        data.put("newindex", "1");
        data.put("OS", "Win2008 R2_压力测试");
        data.put("CheckCode", getCheckCode(mac).get("CheckCode"));
        data.put("CheckToken", getCheckCode(mac).get("CheckToken"));
        //********** 可变入参 **********
        data.put("Modulus_id", "2");
        data.put("Password", "2359d78691b719b65766e11c86bd8aa7fb45fce997a9f827a2c6b54300bc37a590678855b58d23a76e7d2875bd97f9a6dac1e435be65345777339255ad11487c9ba9eb52cfc3ecfcf510040b5024126db5033c19492b1d1e23764fb69510ee761b277f552930be8f389c99d33644c8fe6e3fd2005e65dd4dfe7cf55ab390ad80");
        
        //data.put("Modulus_id", "1");
        //data.put("Password","0b873ce382080bede79d44aa990d1dc60a1d7c4be21d129bd8503bc0a1e201982c304425b50e4311f942d7df6aa79ddc71871790f43c45aef71714559875fc344138f3712175bbd7e8a43cbd3c2aa53f54bd205a6ec8fa026118fdf01194e166248eb7ba24681f6197595d02f481c673157009ab43a13ca992525695b4b45526");
        //data.put("Account","24512072");
        data.put("Account", "10109967");
        //******************************
        //            client.callRmote("100", data,mac);
        
        return data;
    }
    
    public static void main(String[] args) throws ClassNotFoundException {
        //PropertiesUtil.initLog4jConfig();
        test();
        //System.out.println(test());
    }
}