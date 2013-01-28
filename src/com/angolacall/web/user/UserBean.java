package com.angolacall.web.user;

public class UserBean {
	public static final String SESSION_BEAN = "userbean";
	
	private String username;
	private String referrer;
	private String userKey;
	private String password;
	private String countryCode;
	private String vosPhone;
	private String vosPhonePwd;
	private String bindPhone;
	private String bindPhoneCountryCode;
	private String status;
	private String email;
	
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
}
