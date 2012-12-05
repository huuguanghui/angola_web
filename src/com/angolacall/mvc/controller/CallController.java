package com.angolacall.mvc.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.angolacall.framework.ContextLoader;
import com.richitec.vos.client.VOSClient;
import com.richitec.vos.client.VOSHttpResponse;

@Controller
@RequestMapping("/callop")
public class CallController {
	private static Log log = LogFactory.getLog(CallController.class);

	private VOSClient vosClient;

	@PostConstruct
	public void init() {
		vosClient = ContextLoader.getVOSClient();
	}

	@RequestMapping("/callback")
	public void doCallBack(HttpServletResponse response,
			@RequestParam(value = "callee") String callee,
			@RequestParam(value = "username") String userName,
			@RequestParam(value = "countryCode") String countryCode, 
			@RequestParam(value = "vosPhoneNumber") String vosPhoneNumber,
			@RequestParam(value = "vosPhonePassword") String vosPhonePassword) throws IOException {
		VOSHttpResponse vosResponse = vosClient.doCallBack(callee, userName, countryCode, vosPhoneNumber, vosPhonePassword);
		if (vosResponse.getHttpStatusCode() != 200
				|| !vosResponse.isOperationSuccess()) {
			log.error("\nCannot do callback for user : " + countryCode + userName
					+ "\nVOS Http Response : "
					+ vosResponse.getHttpStatusCode()
					+ "\nVOS Status Code : "
					+ vosResponse.getVOSStatusCode()
					+ "\nVOS Response Info ："
					+ vosResponse.getVOSResponseInfo());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
}
