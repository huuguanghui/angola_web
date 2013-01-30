<%@page import="java.util.Map"%>
<%@page import="com.angolacall.constants.WebConstants"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>安中通领取话费</title>
<jsp:include page="../common/_head.jsp"></jsp:include>
</head>
<body>
	<div class="container">
		<div class="pay_result_center">
			<div>
			<%
				String result = (String) request.getAttribute("result");
				String countryCode = (String) request.getAttribute("countryCode");
				String userName = (String) request.getAttribute("userName");
			
				if ("money_get_ok".equals(result)) {
					Float money = (Float) request.getAttribute("money"); 
					%>
						<p>用户<%=countryCode + userName %>您好,</p>
						<br/>
						<p>您已成功领取系统赠送的<%=String.format("%.2f", money.floatValue()) %>元。</p>
					<%
						
				} else if ("money_get_failed".equals(result)){
			%>
					<p>用户<%=countryCode + userName %>您好,</p>
					<br/>
					<p>由于系统故障，话费领取失败，请稍后重试！</p>
			<%
				} else if ("already_get_money".equals(result)){
			%>
					<p>用户<%=countryCode + userName %>您好,</p>
					<br/>
					<p>您之前已经领取了话费，不可重复领取！</p>
			<%
				} else if ("email_verified_ok".equals(result)) {
			%>
					<p>用户<%=countryCode + userName %>您好,</p>
					<br/>
					<p>您的邮箱已通过验证，感谢您对安中通的支持！</p>
			<%		
				}
			%>
			<div class="link_region">
				<a href="http://www.00244dh.com">返回首页</a>(安中通)
			</div>
			</div>
		</div>
	</div>

</body>
</html>