package com.cisc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StockRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cancelAreas;// 取消推送区域 1,2,3

	private String message;

	private ConcurrentHashMap<String, HashMap<String, String>> params;

	public String getCancelAreas() {
		return cancelAreas;
	}

	public void setCancelAreas(String cancelAreas) {
		this.cancelAreas = cancelAreas;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ConcurrentHashMap<String, HashMap<String, String>> getParams() {
		return params;
	}

	public void setParams(ConcurrentHashMap<String, HashMap<String, String>> params) {
		this.params = params;
	}

	
}
