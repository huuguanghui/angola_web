package com.angolacall.mvc.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alipay.client.base.PartnerConfig;
import com.alipay.client.security.RSASignature;
import com.angolacall.constants.UUTalkConfigKeys;
import com.angolacall.framework.ContextLoader;
import com.angolacall.mvc.admin.model.UUTalkConfigManager;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.util.CryptoUtil;
import com.richitec.util.MyRC4;

@Controller
@RequestMapping("/profile")
public class ProfileApiController {
	private static Log log = LogFactory.getLog(ProfileController.class);
	private UserDAO userDao;

	@PostConstruct
	public void init() {
		userDao = ContextLoader.getUserDAO();
	}

	@RequestMapping(value = "/changePwd")
	public void changePwdAPI(HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName,
			@RequestParam(value = "oldPwd") String oldPwd,
			@RequestParam(value = "newPwd") String newPwd,
			@RequestParam(value = "newPwdConfirm") String newPwdConfirm)
			throws IOException, JSONException {
		log.info("countryCode: " + countryCode + " username: " + userName
				+ " oldPwd: " + oldPwd + " newpwd: " + newPwd + " confirmpwd: "
				+ newPwdConfirm);
		Map<String, Object> user = userDao.getUser(countryCode, userName);
		String pwd = (String) user.get("password");

		if (!oldPwd.equals(pwd)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		if (newPwd.isEmpty() || !newPwd.equals(newPwdConfirm)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String md5Password = CryptoUtil.md5(newPwd);
		if (userDao.changePassword(userName, md5Password, countryCode) <= 0) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		Map<String, Object> userMap = userDao.getUser(countryCode, userName);
		String userkey = (String) userMap.get("userkey");
		JSONObject ret = new JSONObject();
		ret.put("userkey", userkey);
		response.getWriter().print(ret.toString());
	}

	// === following code is used for alipay client request
	@RequestMapping("/alipayinfo")
	public void alipayInfo(HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName)
			throws JSONException, IOException {
		JSONObject info = new JSONObject();
		info.put("partner_id", PartnerConfig.PARTNER);
		info.put("seller", PartnerConfig.SELLER);

		Map<String, Object> user = ContextLoader.getUserDAO().getUser(
				countryCode, userName);
		String userkey = (String) user.get("userkey");
		String cryptMsg = MyRC4.encryptPro(info.toString(), userkey);
		response.getWriter().print(cryptMsg);
	}

	@RequestMapping("/alipaysign")
	public void alipayClientParamSign(HttpServletResponse response,
			@RequestParam String content, @RequestParam String out_trade_no,
			@RequestParam String total_fee,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName)
			throws IOException {
		log.info("content: " + content);
		ContextLoader.getChargeDAO().addChargeRecord(out_trade_no, countryCode,
				userName, Double.valueOf(total_fee));
		response.getWriter().print(
				RSASignature.sign(content, PartnerConfig.RSA_PRIVATE));
	}

	@RequestMapping("/getRegInviteLink")
	public void getRegInviteLink(HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName)
			throws IOException {
		String inviterId = ContextLoader.getRegLinkTagDao().getRegLinkTag(
				countryCode, userName);
		if (inviterId == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		String inviteRegUrl = ContextLoader.getConfiguration().getServerUrl()
				+ "/invitejoin/" + inviterId;
		response.getWriter().print(inviteRegUrl);
	}

	@RequestMapping("/getBindPhone")
	public void getBindPhone(HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName)
			throws JSONException, IOException {
		Map<String, Object> user = userDao.getUser(countryCode, userName);
		String bindPhone = (String) user.get("bindphone");
		String bindPhoneCountryCode = (String) user
				.get("bindphone_country_code");
		JSONObject ret = new JSONObject();
		ret.put("bindphone", bindPhone);
		ret.put("bindphone_country_code", bindPhoneCountryCode);
		response.getWriter().print(ret.toString());
	}

	@RequestMapping("/setBindPhone")
	public void setBindPhone(
			HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName,
			@RequestParam(value = "bindphone_country_code") String bindPhoneCountryCode,
			@RequestParam(value = "bindphone") String bindPhone) {
		userDao.setBindPhone(countryCode, userName, bindPhoneCountryCode,
				bindPhone);
	}
	
	@RequestMapping("/regInviteDesc")
	public void getRegInviteDescription(HttpServletResponse response, @RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName) throws JSONException, IOException {
		UUTalkConfigManager ucm = ContextLoader.getUUTalkConfigManager();
		JSONObject ret = new JSONObject();
		ret.put(UUTalkConfigKeys.reg_gift_desc_text.name(), ucm.getRegGiftDescription());
		response.getWriter().print(ret.toString());
	}
}
