package com.angolacall.mvc.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.angolacall.constants.UserAccountStatus;
import com.angolacall.framework.Configuration;
import com.angolacall.framework.ContextLoader;
import com.angolacall.web.user.UserBean;
import com.richitec.sms.client.SMSClient;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.util.MD5Util;
import com.richitec.util.RandomString;
import com.richitec.vos.client.VOSClient;
import com.richitec.vos.client.VOSHttpResponse;

@Controller
@RequestMapping("/user")
public class UserController extends ExceptionController {

	private static Log log = LogFactory.getLog(UserController.class);

	private UserDAO userDao;
	private VOSClient vosClient;
	private Configuration config;
	private SMSClient smsClient;

	public static final String ErrorCode = "error_code";
	public static final String PhoneNumberError = "phone_number_error";
	public static final String PhoneCodeError = "phone_code_error";
	public static final String PasswordError = "password_error";
	public static final String ConfirmPasswordError = "confirm_password_error";
	public static final String NicknameError = "nickname_error";

	@PostConstruct
	public void init() {
		userDao = ContextLoader.getUserDAO();
		vosClient = ContextLoader.getVOSClient();
		config = ContextLoader.getConfiguration();
		smsClient = ContextLoader.getSMSClient();
	}

	@RequestMapping("/login")
	public void login(
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "loginName") String loginName,
			@RequestParam(value = "loginPwd") String loginPwd,
			@RequestParam(value = "brand", required = false, defaultValue = "") String brand,
			@RequestParam(value = "model", required = false, defaultValue = "") String model,
			@RequestParam(value = "release", required = false, defaultValue = "") String release,
			@RequestParam(value = "sdk", required = false, defaultValue = "") String sdk,
			@RequestParam(value = "width", required = false, defaultValue = "0") String width,
			@RequestParam(value = "height", required = false, defaultValue = "0") String height,
			HttpServletResponse response, HttpSession session) throws Exception {
		JSONObject json = new JSONObject();
		try {
			UserBean user = userDao.getUserBean(countryCode, loginName,
					loginPwd);
			if (null != user) {
				json.put("result", "0");
				json.put("userkey", user.getUserKey());
				user.setUserName(loginName);
				user.setPassword(loginPwd);
				session.setAttribute(UserBean.SESSION_BEAN, user);
				userDao.recordDeviceInfo(loginName, countryCode, brand, model,
						release, sdk, width, height);
			} else {
				json.put("result", "1");
			}
		} catch (DataAccessException e) {
			json.put("result", "1");
		}
		response.getWriter().print(json.toString());
	}

	/**
	 * 此接口用于 forgetpwd.jsp 页面获取手机验证码请求。
	 * 
	 * @param session
	 * @param phoneNumber
	 * @return
	 */
	@RequestMapping("/validatePhoneNumber")
	public @ResponseBody
	String validatePhoneNumber(HttpSession session,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "phone") String phoneNumber) {
		String result = userDao.checkRegisterPhone(countryCode, phoneNumber);
		if ("3".equals(result)) {
			userDao.getPhoneCode(session, phoneNumber, countryCode);
			return "200";
		} else {
			return "404";
		}
	}

	/**
	 * 用户忘记密码后重新设置密码
	 * 
	 * @param session
	 * @param phoneNumber
	 * @param phoneCode
	 * @param newPassword
	 * @param newPasswordConfirm
	 * @return
	 */
	@RequestMapping("/resetPassword")
	public @ResponseBody
	String resetPassword(HttpSession session,
			@RequestParam(value = "phone") String phoneNumber,
			@RequestParam(value = "code") String phoneCode,
			@RequestParam(value = "newPwd") String newPassword,
			@RequestParam(value = "newPwdConfirm") String newPasswordConfirm) {
		if (phoneNumber.isEmpty() || phoneCode.isEmpty()
				|| newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
			return "400";
		}

		String sessionPhoneNumber = (String) session
				.getAttribute("phonenumber");
		String countryCode = (String) session.getAttribute("countrycode");
		String sessionPhoneCode = (String) session.getAttribute("phonecode");
		if (null == sessionPhoneCode || null == sessionPhoneNumber
				|| null == countryCode) {
			return "410";
		}

		if (!phoneNumber.equals(sessionPhoneNumber)
				|| !phoneCode.equals(sessionPhoneCode)) {
			return "401";
		}

		if (!newPassword.equals(newPasswordConfirm)) {
			return "403";
		}

		String md5Password = MD5Util.md5(newPassword);
		if (userDao.changePassword(phoneNumber, md5Password, countryCode) <= 0) {
			return "500";
		}

		session.removeAttribute("phonenumber");
		session.removeAttribute("phonecode");
		session.removeAttribute("countrycode");
		return "200";
	}

	@RequestMapping(value = "/websignup", method = RequestMethod.POST)
	public ModelAndView webSignup(
			HttpSession session,
			@RequestParam(value = "countryCode", defaultValue = "") String countryCode,
			@RequestParam(value = "phoneNumber") String phoneNumber,
			@RequestParam(value = "phoneCode") String phoneCode,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "confirmPassword") String confirmPassword)
			throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("signup");

		String sessionPhoneNumber = (String) session
				.getAttribute("phonenumber");
		String sessionCountryCode = (String) session
				.getAttribute("countrycode");
		String sessionPhoneCode = (String) session.getAttribute("phonecode");

		if (phoneNumber.isEmpty() || phoneCode.isEmpty() || password.isEmpty()
				|| confirmPassword.isEmpty()) {
			mv.addObject(ErrorCode, HttpServletResponse.SC_BAD_REQUEST);
			if (phoneNumber.isEmpty()) {
				mv.addObject(PhoneNumberError,
						HttpServletResponse.SC_BAD_REQUEST);
			}
			if (phoneCode.isEmpty()) {
				mv.addObject(PhoneCodeError, HttpServletResponse.SC_BAD_REQUEST);
			}
			if (password.isEmpty()) {
				mv.addObject(PasswordError, HttpServletResponse.SC_BAD_REQUEST);
			}
			if (confirmPassword.isEmpty()) {
				mv.addObject(ConfirmPasswordError,
						HttpServletResponse.SC_BAD_REQUEST);
			}
			return mv;
		}

		if (!phoneNumber.equals(sessionPhoneNumber)
				|| !phoneCode.equals(sessionPhoneCode)
				|| !countryCode.equals(sessionCountryCode)) {
			mv.addObject(ErrorCode, HttpServletResponse.SC_UNAUTHORIZED);
			mv.addObject(PhoneCodeError, HttpServletResponse.SC_UNAUTHORIZED);
			return mv;
		}

		if (!password.equals(confirmPassword)) {
			mv.addObject(ErrorCode, HttpServletResponse.SC_FORBIDDEN);
			mv.addObject(ConfirmPasswordError, HttpServletResponse.SC_FORBIDDEN);
			return mv;
		}

		String result = userDao.regUser(countryCode, phoneNumber, "", "",
				password, confirmPassword);

		if ("0".equals(result)) { // insert success
			Map<String, Object> vosPhoneInfoMap = userDao.getVOSPhone(countryCode,
					phoneNumber);
			Long vosPhoneNumber = (Long) vosPhoneInfoMap.get("vosphone");
			String vosPhonePwd = (String) vosPhoneInfoMap.get("vosphone_pwd");
			result = addUserToVOS(countryCode + phoneNumber,
					vosPhoneNumber.toString(), vosPhonePwd);

			if ("0".equals(result)) {
				int affectedRows = userDao.updateUserAccountStatus(countryCode,
						phoneNumber, UserAccountStatus.success);
				if (affectedRows > 0) {
					result = "0";
				} else {
					result = "1";
				}
			}

			else if ("2001".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phoneNumber,
						UserAccountStatus.vos_account_error);
			} else if ("2002".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phoneNumber,
						UserAccountStatus.vos_phone_error);
			} else if ("2003".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phoneNumber,
						UserAccountStatus.vos_suite_error);
			}
		}

		if ("0".equals(result)) {
			Double money = config.getSignupGift();
			if (money != null && money > 0) {
				VOSHttpResponse depositeResp = vosClient.deposite(countryCode
						+ phoneNumber, money);
				if (depositeResp.getHttpStatusCode() != 200
						|| !depositeResp.isOperationSuccess()) {
					log.error("\nCannot deposite gift for user : "
							+ phoneNumber + "\nVOS Http Response : "
							+ depositeResp.getHttpStatusCode()
							+ "\nVOS Status Code : "
							+ depositeResp.getVOSStatusCode()
							+ "\nVOS Response Info ："
							+ depositeResp.getVOSResponseInfo());
				}
			}
		}

		if ("0".equals(result)) {
			mv.addObject(ErrorCode, HttpServletResponse.SC_OK);
		} else {
			mv.addObject(ErrorCode,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return mv;
	}

	@RequestMapping("/regViaInvite")
	public void regViaInvite(
			HttpServletResponse response,
			HttpSession session,
			@RequestParam(value = "referrer") String referrer,
			@RequestParam(value = "referrerCountryCode") String referrerCountryCode,
			@RequestParam(value = "countryCode", defaultValue = "") String countryCode,
			@RequestParam(value = "phoneNumber") String phoneNumber,
			@RequestParam(value = "phoneCode") String phoneCode,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "confirmPassword") String confirmPassword)
			throws IOException {
		String sessionPhoneNumber = (String) session
				.getAttribute("phonenumber");
		String sessionCountryCode = (String) session
				.getAttribute("countrycode");
		String sessionPhoneCode = (String) session.getAttribute("phonecode");

		if (phoneNumber.isEmpty() || phoneCode.isEmpty() || password.isEmpty()
				|| confirmPassword.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (!phoneNumber.equals(sessionPhoneNumber)
				|| !phoneCode.equals(sessionPhoneCode)
				|| !countryCode.equals(sessionCountryCode)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		if (!password.equals(confirmPassword)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		String result = userDao.regUser(countryCode, phoneNumber,
				referrerCountryCode, referrer, password, confirmPassword);

		if ("0".equals(result)) { // insert success
			Map<String, Object> vosPhoneInfoMap = userDao.getVOSPhone(countryCode,
					phoneNumber);
			Long vosPhoneNumber = (Long) vosPhoneInfoMap.get("vosphone");
			String vosPhonePwd = (String) vosPhoneInfoMap.get("vosphone_pwd");
			result = addUserToVOS(countryCode + phoneNumber,
					vosPhoneNumber.toString(), vosPhonePwd);

			if ("0".equals(result)) {
				int affectedRows = userDao.updateUserAccountStatus(countryCode,
						phoneNumber, UserAccountStatus.success);
				if (affectedRows > 0) {
					result = "0";
				} else {
					result = "1";
				}
			} else if ("2001".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phoneNumber,
						UserAccountStatus.vos_account_error);
			} else if ("2002".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phoneNumber,
						UserAccountStatus.vos_phone_error);
			} else if ("2003".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phoneNumber,
						UserAccountStatus.vos_suite_error);
			}
		}

		if ("0".equals(result)) {
			Double money = config.getSignupGift();
			if (money != null && money > 0) {
				VOSHttpResponse depositeResp = vosClient.deposite(countryCode
						+ phoneNumber, money);
				if (depositeResp.getHttpStatusCode() != 200
						|| !depositeResp.isOperationSuccess()) {
					log.error("\nCannot deposite gift for user : "
							+ phoneNumber + "\nVOS Http Response : "
							+ depositeResp.getHttpStatusCode()
							+ "\nVOS Status Code : "
							+ depositeResp.getVOSStatusCode()
							+ "\nVOS Response Info ："
							+ depositeResp.getVOSResponseInfo());
				}
			}
		}

		if (!"0".equals(result)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

	}

	/**
	 * 用户从手机注册获取验证码请求。
	 * 
	 * @param phone
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/getPhoneCode")
	public void getPhoneCode(
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "phone") String phone,
			HttpServletResponse response, HttpSession session) throws Exception {
		JSONObject jsonUser = new JSONObject();
		try {
			String result = "0";
			result = userDao.checkRegisterPhone(countryCode, phone);
			if (result.equals("0")) {
				result = userDao.getPhoneCode(session, phone, countryCode);
			}
			jsonUser.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.getWriter().print(jsonUser.toString());
	}

	/**
	 * 验证手机客户端发送来的验证码
	 * 
	 * @param code
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/checkPhoneCode")
	public void checkPhoneCode(@RequestParam(value = "code") String code,
			HttpServletResponse response, HttpSession session) throws Exception {
		JSONObject jsonUser = new JSONObject();
		try {
			String result = "0";
			if (session.getAttribute("phonecode") != null) {
				result = userDao.checkPhoneCode(session, code);
			} else {
				result = "6"; // session timeout
			}
			jsonUser.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.getWriter().print(jsonUser.toString());
	}

	@RequestMapping("/regUser")
	public void regUser(@RequestParam(value = "password") String password,
			@RequestParam(value = "password1") String password1,
			HttpServletResponse response, HttpSession session) throws Exception {
		log.info("regUser");

		String result = "";
		String phone = "";
		String countryCode = "";
		if (null == session.getAttribute("phonenumber")) {
			result = "6"; // session过期
		} else {
			phone = (String) session.getAttribute("phonenumber");
			countryCode = (String) session.getAttribute("countrycode");
			result = userDao.regUser(countryCode, phone, "", "", password,
					password1);
		}

		if ("0".equals(result)) { // insert success
			Map<String, Object> vosPhoneInfoMap = userDao.getVOSPhone(countryCode,
					phone);
			Long vosPhoneNumber = (Long) vosPhoneInfoMap.get("vosphone");
			String vosPhonePwd = (String) vosPhoneInfoMap.get("vosphone_pwd");
			result = addUserToVOS(countryCode + phone,
					vosPhoneNumber.toString(), vosPhonePwd);

			if ("0".equals(result)) {
				int affectedRows = userDao.updateUserAccountStatus(countryCode,
						phone, UserAccountStatus.success);
				if (affectedRows > 0) {
					result = "0";
				} else {
					result = "1";
				}
			} else if ("2001".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phone,
						UserAccountStatus.vos_account_error);
			} else if ("2002".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phone,
						UserAccountStatus.vos_phone_error);
			} else if ("2003".equals(result)) {
				userDao.updateUserAccountStatus(countryCode, phone,
						UserAccountStatus.vos_suite_error);
			}
		}

		if ("0".equals(result)) {
			Double money = config.getSignupGift();
			if (money != null && money > 0) {
				VOSHttpResponse depositeResp = vosClient.deposite(countryCode
						+ phone, money);
				if (depositeResp.getHttpStatusCode() != 200
						|| !depositeResp.isOperationSuccess()) {
					log.error("\nCannot deposite gift for user : " + phone
							+ "\nVOS Http Response : "
							+ depositeResp.getHttpStatusCode()
							+ "\nVOS Status Code : "
							+ depositeResp.getVOSStatusCode()
							+ "\nVOS Response Info ："
							+ depositeResp.getVOSResponseInfo());
				} else {
					// try {
					// smsClient
					// .sendTextMessage(
					// phone,
					// "欢迎您成为智会用户，"
					// + "您的账户已获赠10元。更多信息请访问 http://www.wetalking.net/help");
					// } catch (Exception e) {
					// log.error("Cannot send SMS to new user: " + phone);
					// }
				}
			}
		}

		JSONObject jsonUser = new JSONObject();
		try {
			jsonUser.put("result", result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.getWriter().print(jsonUser.toString());
	}

	private String addUserToVOS(String fullUserName, String vosPhoneNumber, String vosPhonePwd) {
		// create new account in VOS
		VOSHttpResponse addAccountResp = vosClient.addAccount(fullUserName);
		if (addAccountResp.getHttpStatusCode() != 200
				|| !addAccountResp.isOperationSuccess()) {
			log.error("\nCannot create VOS accont for user : " + fullUserName
					+ "\nVOS Http Response : "
					+ addAccountResp.getHttpStatusCode()
					+ "\nVOS Status Code : "
					+ addAccountResp.getVOSStatusCode()
					+ "\nVOS Response Info ："
					+ addAccountResp.getVOSResponseInfo());
			return "2001";
		}

		// create new phone in VOS
		VOSHttpResponse addPhoneResp = vosClient.addPhoneToAccount(
				fullUserName, vosPhoneNumber, vosPhonePwd);
		if (addPhoneResp.getHttpStatusCode() != 200
				|| !addPhoneResp.isOperationSuccess()) {
			log.error("\nCannot create VOS phone <" + vosPhoneNumber
					+ "> for user : " + fullUserName + "\nVOS Http Response : "
					+ addPhoneResp.getHttpStatusCode() + "\nVOS Status Code : "
					+ addPhoneResp.getVOSStatusCode() + "\nVOS Response Info ："
					+ addPhoneResp.getVOSResponseInfo());
			return "2002";
		}

		// add suite to account
		VOSHttpResponse addSuiteResp = vosClient.addSuiteToAccount(
				fullUserName, config.getSuite0Id());
		if (addSuiteResp.getHttpStatusCode() != 200
				|| !addSuiteResp.isOperationSuccess()) {
			log.error("\nCannot add VOS suite <" + config.getSuite0Id()
					+ "> for user : " + fullUserName + "\nVOS Http Response : "
					+ addSuiteResp.getHttpStatusCode() + "\nVOS Status Code : "
					+ addSuiteResp.getVOSStatusCode() + "\nVOS Response Info ："
					+ addSuiteResp.getVOSResponseInfo());
			return "2003";
		}

		return "0";
	}

	@RequestMapping("/checkUserExist")
	public void checkUserExist(
			@RequestParam(value = "countryCode", required = true) String countryCode,
			@RequestParam(value = "username", required = true) String userName,
			HttpServletResponse response) throws JSONException, SQLException,
			IOException {
		JSONObject ret = new JSONObject();
		boolean isExist = userDao.isExistsLoginName(countryCode, userName);
		ret.put("result", isExist);
		response.getWriter().print(ret.toString());
	}

	@RequestMapping("/getUserPwd")
	public void getUserPassword(HttpServletResponse response,
			@RequestParam(value = "countryCode") String countryCode,
			@RequestParam(value = "username") String userName)
			throws IOException {
		try {
			Map<String, Object> user = userDao.getUser(countryCode, userName);
			if (user == null) {
				log.info("user is null");
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			String newPwd = RandomString.getPassword();
			int rows = userDao.changePassword(userName, MD5Util.md5(newPwd),
					countryCode);
			if (rows > 0) {
				String msg = String.format(
						"您的新密码是%s，请登录后及时修改您的密码。[AngolaCall]", newPwd);
				String bindPhone = (String) user.get("bindphone");
				smsClient.sendTextMessage(bindPhone, msg);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (DataAccessException e) {
			// e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
