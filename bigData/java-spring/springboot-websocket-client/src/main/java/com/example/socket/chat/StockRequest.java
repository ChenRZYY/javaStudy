package com.example.socket.chat;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
public class StockRequest implements Serializable {
	   private static final long serialVersionUID = 1L; 
//	   private String CUACCT_CODE;                         //信用资金账号
//	   private String STK_BIZ;				    		   //唯一标识
//	   private String STK_BIZ_ACTION;                      //委托方式
//	   private String ORDER_BSN;                           //委托批号
//	   private String BASKET_AMOUNT;                       //委托篮子数
//	   private String ACTION; 					           //前端界面请求那个接口
//	   private List<HashMap<String, String>> params;       //接口请求参数
	   public HashMap<String, HashMap<String,String>> params;       //接口请求参数
//	   private HashMap<String,String> requests;       //接口请求参数

}
