package com.angolacall.mvc.admin.model;

import com.angolacall.constants.UUTalkConfigKeys;
import com.richitec.ucenter.model.CommonConfigDao;

public class UUTalkConfigManager {

	private CommonConfigDao commonConfigDao;
	
	public void setCommonConfigDao(CommonConfigDao ccd) {
		this.commonConfigDao = ccd;
	}

	public String getRegGiftValue() {
		String giftValue = commonConfigDao.getValue(UUTalkConfigKeys.reg_gift_value.name());
		if (giftValue == null) {
			giftValue = "0";
		}
		return giftValue;
	}
	
	public void setRegGiftValue(String regGiftValue) {
		commonConfigDao.setValue(UUTalkConfigKeys.reg_gift_value.name(), regGiftValue);
	}
}
