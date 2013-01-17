package com.richitec.util;

import java.util.HashMap;
import java.util.Map;

public class CountryManager {

	private static CountryManager instance;
	
	private Map<String, String> countryMap;
	
	private CountryManager() {
		countryMap = new HashMap<String, String>();
		init();
	}

	private void init() {
		countryMap.put("0086", "cn");
		countryMap.put("00244", "ao");
	}
	
	public synchronized static CountryManager getInstance() {
		if (instance == null) {
			instance = new CountryManager();
		}
		return instance;
	}
	
	public String getCountryABBR(String code) {
		return countryMap.get(code);
	}
}
