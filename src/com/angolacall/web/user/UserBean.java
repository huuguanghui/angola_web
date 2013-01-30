package com.angolacall.web.user;

import org.json.JSONException;
import org.json.JSONObject;

import com.angolacall.constants.UserConstants;

public class UserBean {
	public static final String SESSION_BEAN = "userbean";
	
	private String username;
	private String referrer;
	private String referrerCountryCode;
	private String userKey;
	private String password;
	private String countryCode;
	private String vosPhone;
	private String vosPhonePwd;
	private String bindPhone;
	private String bindPhoneCountryCode;
	private String status;
	private String email;
	private String emailStatus;
	private Float frozenMoney;
	
	public String getUserName() {
		return username;
	}

	public void setUserName(String name) {
		this.username = name;
	}
	
	public String getReferrer(){
		return referrer;
	}
	
	public void setReferrer(String referrer){
		this.referrer = referrer;
	}
	
	public String getReferrerCountryCode() {
		return referrerCountryCode;
	}

	public void setReferrerCountryCode(String referrerCountryCode) {
		this.referrerCountryCode = referrerCountryCode;
	}

	public String getDisplayName(){
		if (referrer == null || referrer.length()<=0){
			return username; 
		} else {
			return referrer;
		}
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getUserKey(){
		return userKey;
	}
	
	public void setUserKey(String userKey){
		this.userKey = userKey;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getVosPhone() {
		return vosPhone;
	}

	public void setVosPhone(String vosPhone) {
		this.vosPhone = vosPhone;
	}

	public String getVosPhonePwd() {
		return vosPhonePwd;
	}

	public void setVosPhonePwd(String vosPhonePwd) {
		this.vosPhonePwd = vosPhonePwd;
	}

	public String getBindPhone() {
		return bindPhone;
	}

	public void setBindPhone(String bindPhone) {
		this.bindPhone = bindPhone;
	}

	public String getBindPhoneCountryCode() {
		return bindPhoneCountryCode;
	}

	public void setBindPhoneCountryCode(String bindPhoneCountryCode) {
		this.bindPhoneCountryCode = bindPhoneCountryCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(String emailStatus) {
		this.emailStatus = emailStatus;
	}

	public Float getFrozenMoney() {
		return frozenMoney;
	}

	public void setFrozenMoney(Float frozenMoney) {
		this.frozenMoney = frozenMoney;
	}
	
	public JSONObject toJSONObject() {
		JSONObject ret = new JSONObject();
		try {
			ret.put(UserConstants.username.name(), username);
			ret.put(UserConstants.countrycode.name(), countryCode);
			ret.put(UserConstants.referrer.name(), referrer);
			ret.put(UserConstants.referrer_country_code.name(), referrerCountryCode);
			ret.put(UserConstants.userkey.name(), userKey);
			ret.put(UserConstants.vosphone.name(), vosPhone);
			ret.put(UserConstants.vosphone_pwd.name(), vosPhonePwd);
			ret.put(UserConstants.bindphone.name(), bindPhone);
			ret.put(UserConstants.bindphone_country_code.name(), bindPhoneCountryCode);
			ret.put(UserConstants.status.name(), status);
			ret.put(UserConstants.email.name(), email);
			ret.put(UserConstants.email_status.name(), emailStatus);
			ret.put(UserConstants.frozen_money.name(), frozenMoney);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
}
