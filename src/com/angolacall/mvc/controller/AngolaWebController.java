package com.angolacall.mvc.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.angolacall.constants.AuthConstant;
import com.angolacall.constants.ChargeType;
import com.angolacall.constants.EmailStatus;
import com.angolacall.constants.UserAccountStatus;
import com.angolacall.constants.WebConstants;
import com.angolacall.framework.Configuration;
import com.angolacall.framework.ContextLoader;
import com.angolacall.mvc.model.charge.ChargeUtil;
import com.angolacall.web.user.UserBean;
import com.richitec.sms.client.SMSHttpResponse;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.vos.client.AccountInfo;

@Controller
public class AngolaWebController {
	private static Log log = LogFactory.getLog(AngolaWebController.class);

	@RequestMapping("/")
	public ModelAndView index(HttpSession session, HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.setViewName("index");
		// view.addObject(WebConstants.page_name.name(), "index");
		return view;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView signup() {
		ModelAndView view = new ModelAndView();
		view.setViewName("signup");
		view.addObject(WebConstants.page_name.name(), "signup");
		return view;
	}

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public ModelAndView signin() {
		ModelAndView view = new ModelAndView();
		view.setViewName("signin");
		view.addObject(WebConstants.page_name.name(), "signin");
		return view;
	}

	@RequestMapping(value = "/invitejoin/{inviterId}", method = RequestMethod.GET)
	public ModelAndView inviteJoin(HttpServletResponse response,
			@PathVariable String inviterId) throws IOException {
		ModelAndView view = new ModelAndView();
		view.setViewName("invitejoin_directreg");

		Map<String, Object> inviterMap = null;
		try {
			inviterMap = ContextLoader.getRegLinkTagDao().getInviterMap(
					inviterId);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		if (inviterMap == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return view;
		}

		String inviterName = (String) inviterMap.get("username");
		String inviterCountryCode = (String) inviterMap.get("country_code");

		view.addObject(AuthConstant.username.name(), inviterName);
		view.addObject(AuthConstant.countryCode.name(), inviterCountryCode);
		return view;
	}

	@RequestMapping(value = "/forgetpwd", method = RequestMethod.GET)
	public ModelAndView forgetpwd() {
		ModelAndView view = new ModelAndView();
		view.setViewName("forgetpwd");
		view.addObject(WebConstants.page_name.name(), "forgetpwd");
		return view;
	}

	@RequestMapping(value = "/signout", method = RequestMethod.GET)
	public String signout(HttpSession session) {
		session.removeAttribute(UserBean.SESSION_BEAN);
		return "redirect:/";
	}

	@RequestMapping(value = "/404")
	public String page404() {
		return "error/404";
	}

	@RequestMapping(value = "/500")
	public String page500() {
		return "error/500";
	}

	@RequestMapping("/getDownloadPageUrl")
	public void getDownloadPageUrl(HttpServletResponse response,
			@RequestParam String phoneNumber) throws JSONException, IOException {
		String url = ContextLoader.getConfiguration().getAppDonwloadPageUrl();
		String msgContent = "智会客户端下载地址：" + url;
		SMSHttpResponse resp = ContextLoader.getSMSClient().sendTextMessage(
				phoneNumber, msgContent);
		JSONObject ret = new JSONObject();
		log.info("status code: " + resp.getStatusCode() + " code: "
				+ resp.getCode());
		if (resp.getCode() == 3) {
			ret.put("result", "ok");
		} else {
			ret.put("result", "fail");
		}
		response.getWriter().print(ret.toString());
	}

	@RequestMapping("/appdownload")
	public String appDownloadPage() {
		return "app_download";
	}

	@ResponseStatus(HttpStatus.MOVED_TEMPORARILY)
	@RequestMapping("/downloadAppClient/{device}")
	public void downloadAppClient(HttpServletResponse response,
			@PathVariable String device) {
		Configuration config = ContextLoader.getConfiguration();
		String downloadUrl = config.getAppDownloadUrl();
		String appId = config.getAppId();
		downloadUrl = downloadUrl + "/" + appId + "/" + device;
		response.addHeader("Location", downloadUrl);
	}

	@ResponseStatus(HttpStatus.MOVED_TEMPORARILY)
	@RequestMapping("/appVersion/{device}")
	public void appVersion(HttpServletResponse response,
			@PathVariable String device) {
		Configuration config = ContextLoader.getConfiguration();
		String versionUrl = config.getAppVersionUrl();
		String appId = config.getAppId();
		versionUrl = versionUrl + "/" + appId + "/" + device;
		response.addHeader("Location", versionUrl);
	}

	@RequestMapping("/getNewNotice")
	public void getNewNotice(
			HttpServletResponse response,
			@RequestParam(value = "maxId") String maxId,
			@RequestParam(value = "username", defaultValue = "") String userName,
			@RequestParam(value = "countryCode", defaultValue = "") String countryCode)
			throws JSONException, IOException {
		List<Map<String, Object>> noticeList = ContextLoader.getNoticeDao()
				.getNewPublishedNotices(Integer.parseInt(maxId), countryCode,
						userName);
		JSONArray ret = new JSONArray();
		if (noticeList != null) {
			for (Map<String, Object> map : noticeList) {
				Integer id = (Integer) map.get("id");
				String content = (String) map.get("content");
				Long time = (Long) map.get("time");

				JSONObject notice = new JSONObject();
				notice.put("id", id.intValue());
				notice.put("content", content);
				notice.put("create_time", time.longValue());
				ret.put(notice);
			}
		}
		response.getWriter().print(ret.toString());
	}

	/**
	 * 安中卡余额查询
	 */
	@RequestMapping(value = "azcardbalance", method = RequestMethod.POST)
	public @ResponseBody
	String azcardPost(
			@RequestParam(value = "countryCode", required = false, defaultValue = "") String countryCode,
			@RequestParam(value = "phoneNumber") String phoneNumber) {
		UserDAO userDao = ContextLoader.getUserDAO();
		AccountInfo accountInfo = ContextLoader.getVOSClient().getAccountInfo(
				userDao.genVosAccountName(countryCode, phoneNumber), 2);
		if (accountInfo != null) {
			return "{\"status\":0, \"balance\":"
					+ String.format("%,.2f", accountInfo.getBalance()) + "}";
		} else {
			return "{\"status\":1}";
		}
	}

	@RequestMapping(value = "azcard", method = RequestMethod.GET)
	public String azcardGet() {
		return "azcard";
	}

	@RequestMapping(value = "/getFrozenMoneyViaEmailLink/{randomId}")
	public ModelAndView getFrozenMoneyViaEmailLink(
			HttpServletResponse response, @PathVariable String randomId)
			throws IOException {
		ModelAndView view = new ModelAndView(
				"other/get_activation_money_result");
		UserDAO userDao = ContextLoader.getUserDAO();
		Map<String, Object> user = null;
		try {
			user = userDao.getUserByRandomId(randomId);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		if (user == null) {
			view.addObject("result", "money_gain_user_not_found");
			return view;
		}

		String countryCode = (String) user.get("countrycode");
		String userName = (String) user.get("username");
		Float frozenMoney = (Float) user.get("frozen_money");

		userDao.updateEmailStatus(countryCode, userName, EmailStatus.verified);

		if (frozenMoney > 0) {
			boolean ret = ChargeUtil.chargeUser(ChargeType.sysgift,
					countryCode, userName, frozenMoney.doubleValue());
			if (ret) {
				userDao.clearFrozenMoney(countryCode, userName);
				view.addObject("result", "money_get_ok");
			} else {
				view.addObject("result", "money_get_failed");
			}
		} else {
			view.addObject("result", "already_get_money");
		}

		view.addObject("countryCode", countryCode);
		view.addObject("userName", userName);
		view.addObject("money", frozenMoney);
		return view;
	}

	@RequestMapping(value = "/verifyEmailAddress/{randomId}")
	public ModelAndView verifyEmailAddress(HttpServletResponse response,
			@PathVariable String randomId) throws IOException {
		ModelAndView view = new ModelAndView(
				"other/get_activation_money_result");
		UserDAO userDao = ContextLoader.getUserDAO();
		Map<String, Object> user = null;
		try {
			user = userDao.getUserByRandomId(randomId);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		if (user == null) {
			view.addObject("result", "verify_email_user_not_found");
			return view;
		}

		String countryCode = (String) user.get("countrycode");
		String userName = (String) user.get("username");

		userDao.updateEmailStatus(countryCode, userName, EmailStatus.verified);

		view.addObject("countryCode", countryCode);
		view.addObject("userName", userName);
		view.addObject("result", "email_verified_ok");
		return view;
	}
}
