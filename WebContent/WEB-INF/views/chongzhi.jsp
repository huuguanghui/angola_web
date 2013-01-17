<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.angolacall.web.user.UserBean"%>
<%@page import="com.richitec.vos.client.VOSHttpResponse"%>
<%
	List<Map<String, Object>> chargeMoneyList = 
		(List<Map<String, Object>>) request.getAttribute("charge_money_list");

	String accountName = (String)request.getParameter("accountName");
	String accountError = (String)request.getAttribute("accountError");

	String depositeType = (String)request.getParameter("depositeType");
	if (null == depositeType){
		depositeType = "alipay";
	}
	
	String alipayError = (String)request.getAttribute("alipayError");
	String uutalkError = (String)request.getAttribute("uutalkError");
	VOSHttpResponse vosHttpError = (VOSHttpResponse)request.getAttribute("vosHttpError");
	VOSHttpResponse vosError = (VOSHttpResponse)request.getAttribute("vosError");
	
	String vosErrorInfo = null;
	if (null!=vosError){
		if (vosError.getVOSStatusCode() == -10079) {
			vosErrorInfo = "充值卡卡号或密码错误！";
		} else if (vosError.getVOSStatusCode() == -10078) {
			vosErrorInfo = "此充值卡已被使用！";
		} else {
			vosErrorInfo = vosError.getVOSResponseInfo() + vosError.getVOSStatusCode();
		}
	}
	
	String cardNumber = (String)request.getParameter("cardNumber");

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset={dede:global.cfg_soft_lang/}" />
<title>充值中心</title>
<link href="http://www.uu-talk.com/templets/uutalk2/style/uutalk.css" rel="stylesheet" media="screen" type="text/css" />
</head>
<body>

<div class="header w960">
<!-- 
	<div class="logo"></div>
 -->	
	<div class="nav-bar">
		<ul>
		<li><a href="http://www.uu-talk.com">首页</a></li>
		<li><a href="#">充值中心</a></li>
		<li><a href="http://www.uu-talk.com/a/zifeishuoming/">资费说明</a></li>
		<li><a href="http://www.uu-talk.com/a/bangzhu/">帮助</a></li>
		</ul>
	</div>
</div>

<div class="content w960">
	<div class="chongzhi-banner">
		<div class="chongzhi-gift"></div>
		<a class="chongzhi50" href=""></a>
		<a class="chongzhi100" href=""></a>
		<a class="chongzhi200" href=""></a>
		<a class="chongzhi500" href=""></a>
		<a class="download-btn" href=""></a>
	</div>
	<form id="depositeForm" action="chongzhi" method="post">
		<div id="divAccountInfo" class="chongzhi-form-field">
			<label>选择国家代码</label>
			<select id="iptCountryCodeSelect" name="countryCode">
				<jsp:include page="common/countrycode_options.jsp"></jsp:include>
			</select>
			<label>输入充值账户</label>
			<input id="iptAccountName" name="accountName" type="text" 
				pattern="\d{9}|\d{11}" maxlength="11" 
				<%if(null!=accountName) %>value="<%=accountName %>" />
			<%if(null!=accountError) { %>
			<span class="red">账户不存在</span>
			<% } %>
		</div>
  		
		<div id="divDepositeType" class="chongzhi-form-field">
			<label>请选择充值方式</label>
			<div id="divDepositeTypeList">
				<input type="radio" name="depositeType" value="alipay" 
					<% if("alipay".equals(depositeType)){ %>checked="checked"<% } %> /><span>支付宝充值</span>
				<input type="radio" name="depositeType" value="uutalk" 
					<% if("uutalk".equals(depositeType)){ %>checked="checked"<% } %>/><span>UU-Talk充值卡充值</span>
			</div>
			<div id="divAlipayPanel" <% if(!"alipay".equals(depositeType)){ %>class="hidden"<% } %>>
				<%if(null!=alipayError) { %>
				<span class="red">请选择充值金额</span>
				<% } %>
				<h3>充值大优惠</h3>
				<%
					if (chargeMoneyList != null) {
						boolean checked = false;
						for (Map<String, Object> item : chargeMoneyList) {
							Integer id = (Integer) item.get("id");
							Float chargeMoney = (Float) item.get("charge_money");
							String description = (String) item.get("description");
							
				%>				
				<div class="youhui-item">
					<input type="radio" name="depositeId" value="<%=id %>" 
						<%if(!checked) {%>checked="checked"<% checked=true;} %>/>
					<span><%=chargeMoney.toString() + "(" + description + ")" %></span>				
				</div>
				<%
						}
					}
				%> 
				<p>说明：使用UU-Talk充值卡充值享受另外优惠，以上优惠限支付宝充值。</p>				
			</div>
			<div id="divUUTalkPanel" <% if(!"uutalk".equals(depositeType)){ %>class="hidden"<% } %>>	
				<h3>充值卡卡号</h3>
				<input id="iptCardNumber" name="cardNumber" type="text"
					value="<%=(null!=cardNumber)?cardNumber:""%>" />
				<%if(null!=uutalkError) { %>
				<span class="red">无效的充值卡号</span>
				<% } %>					
				<h3>充值卡密码</h3>
				<input id="iptCardPwd" name="cardPwd" type="text"></input>
				
				<%if(null!=vosHttpError) { %>
				<div>
					<span class="red">"网络请求错误："<%=vosHttpError.getHttpStatusCode() %></span>
				</div>
				<% } %>	
				<%if(null!=vosErrorInfo) { %>
				<div>
					<span class="red">充值错误：<%=vosErrorInfo %></span>
				</div>
				<% } %>		
			</div>			
		</div>
 
		<div id="divDepositeSubmit" class="chongzhi-form-field">
			<input id="iptDepositeSubmit" type="submit" value="" />
		</div>
	</form>

</div>

<div class="footer w960">
	<p class="copyright">Copyright © (HongKong) UuTalk Technology Limited. （香港）寰宇通科技有限公司</p>
	<p class="powered">    
		Powered by 
		<a href="http://www.dedecms.com" title="织梦内容管理系统(DedeCms)" target="_blank">DedeCMS_V57_UTF8_SP1</a>
		 © 2004-2011 
		<a href="http://www.desdev.cn/" target="_blank">DesDev</a> 
		Inc.
	</p>
</div>

<script src="/angola/js/lib/jquery-1.8.0.min.js"></script>
<script type="text/javascript">
$(function(){
    var hidden = "hidden";
	var $divAlipayPanel = $("#divAlipayPanel");
	var $divUUTalkPanel = $("#divUUTalkPanel");
	$("#divDepositeTypeList :radio").change(function(){
		var value = $(this).val();
		if (value == 'alipay'){
			//show alipay div
			$divAlipayPanel.removeClass(hidden);
			$divUUTalkPanel.addClass(hidden);
		} else 
		if (value == 'uutalk') {
			//show uutalk div
			$divAlipayPanel.addClass(hidden);
			$divUUTalkPanel.removeClass(hidden);			
		} else {
			//do nothing;
		}		
	});
});
</script>

</body>
</html>