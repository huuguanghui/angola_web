package com.angolacall.mvc.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.angolacall.constants.WebConstants;
import com.angolacall.framework.Configuration;
import com.angolacall.framework.ContextLoader;
import com.angolacall.web.user.UserBean;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.util.CryptoUtil;

@Controller
@RequestMapping(value="/setting")
public class ProfileController {
	public static final String NicknameRetCode = "NicknameRetCode";
	
	private static Log log = LogFactory.getLog(ProfileController.class);
	
	private Configuration config;
	private UserDAO userDao;
	
	@PostConstruct
	public void init(){
		config = ContextLoader.getConfiguration();
		userDao = ContextLoader.getUserDAO();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView view = new ModelAndView();
		view.setViewName("setting");
		view.addObject(WebConstants.page_name.name(), "setting");
		return view;
	}
	
	@RequestMapping(value="/changepassword", method=RequestMethod.POST)
	public @ResponseBody String changePassword(
			HttpSession session,
			HttpServletResponse response,
			@RequestParam(value="oldPwd") String oldPwd, 
			@RequestParam(value="newPwd") String newPwd,
			@RequestParam(value="newPwdConfirm") String newPwdConfirm) throws IOException{
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		if (!oldPwd.equals(user.getPassword())){
			return "400";
		}
		
		if (newPwd.isEmpty() || !newPwd.equals(newPwdConfirm)){
			return "403";
		}
		
		String md5Password = CryptoUtil.md5(newPwd);
		if (userDao.changePassword(user.getUserName(), md5Password, user.getCountryCode())<=0){
			return "500";
		}
		
		user.setPassword(md5Password);
		return "200";
	}
	
	
}
