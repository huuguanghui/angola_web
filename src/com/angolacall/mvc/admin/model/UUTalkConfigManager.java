package com.angolacall.mvc.admin.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.angolacall.constants.UUTalkConfigKeys;
import com.richitec.ucenter.model.CommonConfigDao;

public class UUTalkConfigManager {
	private static Log log = LogFactory.getLog(UUTalkConfigManager.class);
	private CommonConfigDao commonConfigDao;

	public void setCommonConfigDao(CommonConfigDao ccd) {
		this.commonConfigDao = ccd;
	}

	public String getRegGiftValue() {
		String giftValue = commonConfigDao
				.getValue(UUTalkConfigKeys.reg_gift_value.name());
		log.info("giftValue: " + giftValue);
		if (giftValue == null) {
			giftValue = "0";
		}
		return giftValue;
	}

	public void setRegGiftValue(String regGiftValue) {
		commonConfigDao.setValue(UUTalkConfigKeys.reg_gift_value.name(),
				regGiftValue);
	}

	public void setRegGiftDescription(String desc) {
		commonConfigDao.setValue(UUTalkConfigKeys.reg_gift_desc_text.name(),
				desc);
	}

	public String getRegGiftDescription() {
		String desc = commonConfigDao
				.getValue(UUTalkConfigKeys.reg_gift_desc_text.name());
		log.info("desc: " + desc);
		if (desc == null) {
			desc = "";
		}
		return desc;
	}
}
