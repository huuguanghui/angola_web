package com.angolacall.mvc.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.angolacall.constants.Pages;
import com.angolacall.constants.WebConstants;
import com.angolacall.framework.ContextLoader;
import com.angolacall.web.user.AdminUserBean;
import com.richitec.ucenter.model.AdminUserDao;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private static Log log = LogFactory.getLog(AdminController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("admin/index");
		return view;
	}
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index_() {
		ModelAndView view = new ModelAndView("admin/index");
 		return view;
	}
	
	@RequestMapping("/login")
	public ModelAndView login(HttpSession session,
			@RequestParam String loginName, @RequestParam String loginPwd) {
		log.info("admin login - loginname: " + loginName + " login pwd: " + loginPwd);
		
		AdminUserDao aud = ContextLoader.getAdminUserDao();
		
		if (aud.validateUser(loginName, loginPwd)) {
			AdminUserBean user = new AdminUserBean();
			user.setUserName(loginName);
			user.setPassword(loginPwd);
			session.setAttribute(AdminUserBean.SESSION_BEAN, user);

			ModelAndView view = new ModelAndView();
			view.setViewName("redirect:giftmanage");
			return view;
		} else {
			ModelAndView view = new ModelAndView();
			view.setViewName("index");
			view.addObject("LoginRetCode", HttpServletResponse.SC_FORBIDDEN);
			return view;
		}
	}
	
	@RequestMapping(value = "/giftmanage", method = RequestMethod.GET)
	public ModelAndView giftManage() {
		ModelAndView view = new ModelAndView("admin/giftmanage");
		view.addObject(WebConstants.page_name.name(), Pages.gift_manage.name());
		return view;
	}
	
}
