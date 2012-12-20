package com.richitec.vos.client;

import org.json.JSONException;
import org.json.JSONObject;

public class SuiteInfo {
	private String suiteId = "";
	private String suiteName = "";
	private String rentMoney = "";
	private String comment = "";
	
	public SuiteInfo(String suiteInfo) {
		String[] list = suiteInfo.split(";");
		if (null == list)
			return;
		
		if (list.length > 0) suiteId = list[0];
		if (list.length > 1) suiteName = list[1];
		if (list.length > 4) rentMoney = list[4];
		if (list.length > 6) comment = list[6];
	}
	
	public String getSuiteId() {
		return suiteId;
	}
	public void setSuiteId(String suiteId) {
		this.suiteId = suiteId;
	}
	public String getSuiteName() {
		return suiteName;
	}
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}
	public String getRentMoney() {
		return rentMoney;
	}
	public void setRentMoney(String rentMoney) {
		this.rentMoney = rentMoney;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("suiteId", suiteId);
			obj.put("suiteName", suiteName);
			obj.put("rentMoney", rentMoney);
			obj.put("comment", comment);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
}
