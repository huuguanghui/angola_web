package com.richitec.vos.client;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderSuiteInfo {
	private String orderId = "";
	private String availableTime = "";
	private String expireTime = "";
	private String suiteId = "";
	private String suiteName = "";
	private String rentMoney = "";
	private String comment = "";

	public OrderSuiteInfo(String suiteInfo) {
		String[] list = suiteInfo.split(";");
		if (null == list)
			return;

		if (list.length > 0)
			orderId = list[0];
		if (list.length > 1) {
			availableTime = list[1];
			if (availableTime.length() > 11) {
				availableTime = availableTime.substring(0, 11);
			}
		}
		if (list.length > 2) {
			expireTime = list[2];
			if (expireTime.indexOf("-") == 4 && expireTime.length() > 11) {
				expireTime = expireTime.substring(0, 11);
			} else {
				expireTime = "never";
			}
		}
		if (list.length > 3)
			suiteId = list[3];
		if (list.length > 4)
			suiteName = list[4];
		if (list.length > 7)
			rentMoney = list[7];
		if (list.length > 9)
			comment = list[9];
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAvailableTime() {
		return availableTime;
	}

	public void setAvailableTime(String availableTime) {
		this.availableTime = availableTime;
	}

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
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
			obj.put("orderId", orderId);
			obj.put("availableTime", availableTime);
			obj.put("expireTime", expireTime);
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
