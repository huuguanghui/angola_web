package com.angolacall.framework;

/**
 * Manage the configuration of phone conference server, including IP info
 * 
 * @author sk
 * 
 */
public class Configuration {

	private String suite0Id;
	private String suite5Id;
	private String suite10Id;
	private String appDonwloadPageUrl;
	private Double signupGift;
	private String appvcenterUrl;
	private String appId;
	private String serverUrl;
	private String callbackPrefix;
	
	public String getSuite0Id() {
		return this.suite0Id;
	}

	public void setSuite0Id(String id) {
		this.suite0Id = id;
	}

	public String getSuite5Id() {
		return this.suite5Id;
	}

	public void setSuite5Id(String id) {
		this.suite5Id = id;
	}

	public String getSuite10Id() {
		return this.suite10Id;
	}

	public void setSuite10Id(String id) {
		this.suite10Id = id;
	}

	public String getAppDonwloadPageUrl() {
		return appDonwloadPageUrl;
	}

	public void setAppDonwloadPageUrl(String appDonwloadPageUrl) {
		this.appDonwloadPageUrl = appDonwloadPageUrl;
	}

	public void setSignupGift(Double value) {
		this.signupGift = value;
	}

	public Double getSignupGift() {
		return signupGift;
	}

	public String getAppvcenterUrl() {
		return appvcenterUrl;
	}

	public void setAppvcenterUrl(String appvcenterUrl) {
		this.appvcenterUrl = appvcenterUrl;
	}

	public String getAppDownloadUrl() {
		return this.appvcenterUrl + "/downloadapp";
	}

	public String getAppVersionUrl() {
		return this.appvcenterUrl + "/version";
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getCallbackPrefix() {
		return callbackPrefix;
	}

	public void setCallbackPrefix(String callbackPrefix) {
		this.callbackPrefix = callbackPrefix;
	}
}
