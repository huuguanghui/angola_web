package com.angolacall.mvc.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alipay.client.base.PartnerConfig;
import com.alipay.client.security.RSASignature;
import com.angolacall.constants.ChargeStatus;
import com.angolacall.constants.UUTalkConfigKeys;
import com.angolacall.framework.ContextLoader;
import com.angolacall.mvc.admin.model.ChargeMoneyConfigDao;
import com.angolacall.mvc.admin.model.UUTalkConfigManager;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.util.CryptoUtil;
import com.richitec.util.MyRC4;
import com.richitec.vos.client.OrderSuiteInfo;
import com.richitec.vos.client.SuiteInfo;
import com.richitec.vos.client.VOSClient;

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
			@RequestParam(value = "charge_money_id") String chargeMoneyId,
			@RequestParam String content, @RequestParam String out_trade_no,
			@RequestParam String total_fee,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName)
			throws IOException {
		log.info("content: " + content);
		ContextLoader.getChargeDAO().addChargeRecord(out_trade_no, countryCode,
				userName, Double.valueOf(total_fee), ChargeStatus.processing,
				chargeMoneyId);
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
	public void getRegInviteDescription(HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName)
			throws JSONException, IOException {
		UUTalkConfigManager ucm = ContextLoader.getUUTalkConfigManager();
		JSONObject ret = new JSONObject();
		StringBuffer info = new StringBuffer();
		info.append(ucm.getRegGiftDescription()).append('\n')
				.append(ucm.getInviteChargeGiftDescription());
		ret.put(UUTalkConfigKeys.reg_gift_desc_text.name(), info.toString());
		response.getWriter().print(ret.toString());
	}

	@RequestMapping("/getChargeMoneyList")
	public void getChargeMoneyList(HttpServletResponse response)
			throws IOException {
		ChargeMoneyConfigDao cmcd = ContextLoader.getChargeMoneyConfigDao();
		List<Map<String, Object>> list = cmcd.getChargeMoneyList();
		JSONArray ret = new JSONArray();

		if (list != null) {
			for (Map<String, Object> item : list) {
				Integer id = (Integer) item.get("id");
				Float chargeMoney = (Float) item.get("charge_money");
				Float giftMoney = (Float) item.get("gift_money");
				String description = (String) item.get("description");
				try {
					JSONObject record = new JSONObject();
					record.put("id", id.intValue());
					record.put("charge_money", chargeMoney.floatValue());
					record.put("gift_money", giftMoney.floatValue());
					record.put("description", description);
					ret.put(record);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		response.getWriter().print(ret.toString());
	}

	/**
	 * get the ordered suites and the available suites to order
	 * 
	 * @param response
	 * @param countryCode
	 * @param userName
	 * @throws JSONException
	 * @throws IOException
	 */
	@RequestMapping("/getSuites")
	public void getSuites(HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName)
			throws JSONException, IOException {
		log.info("getSuites");
		VOSClient vosClient = ContextLoader.getVOSClient();
		List<OrderSuiteInfo> orderSuites = vosClient.getOrderSuites(countryCode
				+ userName);
		List<SuiteInfo> allSuites = vosClient.getAllSuites();

		JSONObject ret = new JSONObject();
		if (orderSuites != null) {
			JSONArray orderSuiteArray = new JSONArray();
			for (OrderSuiteInfo osi : orderSuites) {
				if ("suite0".equals(osi.getSuiteName())) {
					continue;
				}
				orderSuiteArray.put(osi.toJSONObject());
			}
			ret.put("my_suites", orderSuiteArray);
		}

		if (allSuites != null) {
			JSONArray allSuitesArray = new JSONArray();
			for (SuiteInfo si : allSuites) {
				if ("suite0".equals(si.getSuiteName())) {
					continue;
				}

				boolean isOrdered = false;
				for (OrderSuiteInfo osi : orderSuites) {
					if (si.getSuiteId().equals(osi.getSuiteId())) {
						isOrdered = true;
						break;
					}
				}

				if (!isOrdered) {
					allSuitesArray.put(si.toJSONObject());
				}
			}
			ret.put("all_suites", allSuitesArray);
		}

		response.getWriter().print(ret.toString());
	}

	@RequestMapping("/subscribeSuite")
	public void subscribeSuite(HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName,
			@RequestParam(value = "suiteId") String suiteId,
			@RequestParam(value = "open_time_type") String openTimeType) {
		
	}
}
