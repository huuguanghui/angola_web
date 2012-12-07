package com.angolacall.framework;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.angolacall.mvc.admin.model.UUTalkConfigManager;
import com.angolacall.mvc.model.charge.ChargeDAO;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.richitec.donkey.client.DonkeyClient;
import com.richitec.notify.Notifier;
import com.richitec.sms.client.SMSClient;
import com.richitec.ucenter.model.AdminUserDao;
import com.richitec.ucenter.model.CommonConfigDao;
import com.richitec.ucenter.model.RegLinkTagDao;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.vos.client.VOSClient;

public class ContextLoader extends ContextLoaderListener {

	public static ApplicationContext appContext;
	public static String appAbsolutePath;

	public void contextDestroyed(ServletContextEvent event) {
		try {
			ComboPooledDataSource ds = (ComboPooledDataSource) appContext
					.getBean("dataSource_mysql_c3p0");
			if (null != ds) {
				ds.close();
			}
		} finally {
			super.contextDestroyed(event);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		ServletContext context = event.getServletContext();
		appAbsolutePath = context.getRealPath("/");
		appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(context);
	}

	public static Configuration getConfiguration() {
		return (Configuration) appContext.getBean("angola_config");
	}

	public static SMSClient getSMSClient() {
		return (SMSClient) appContext.getBean("sms_client");
	}

	public static DonkeyClient getDonkeyClient() {
		return (DonkeyClient) appContext.getBean("donkey_client");
	}

	public static Notifier getNotifier() {
		return (Notifier) appContext.getBean("notifier");
	}

	public static UserDAO getUserDAO() {
		return (UserDAO) appContext.getBean("user_dao");
	}

	public static ChargeDAO getChargeDAO() {
		return (ChargeDAO) appContext.getBean("charge_dao");
	}
	
	public static VOSClient getVOSClient() {
		return (VOSClient) appContext.getBean("vos_client");
	}
	
	public static RegLinkTagDao getRegLinkTagDao() {
		return (RegLinkTagDao) appContext.getBean("reg_link_tag_dao");
	}
	
	public static AdminUserDao getAdminUserDao() {
		return (AdminUserDao) appContext.getBean("admin_user_dao");
	}
	
	public static CommonConfigDao getCommonConfigDao() {
		return (CommonConfigDao) appContext.getBean("common_config_dao");
	}
	
	public static UUTalkConfigManager getUUTalkConfigManager() {
		return (UUTalkConfigManager) appContext.getBean("uutalk_config_manager");
	}
}
