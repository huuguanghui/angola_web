<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.angolacall.constants.UUTalkConfigKeys"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh">
<head>
<title>注册管理</title>
<jsp:include page="common/_head.jsp"></jsp:include>
</head>
<body>
	<jsp:include page="common/afterlogin_navibar.jsp"></jsp:include>

	<%
		String defaultRegMoney = (String) request.getAttribute(UUTalkConfigKeys.default_register_money.name());
		Map<String, Object> regActivityMap = (Map<String, Object>) request.getAttribute("register_activity");
	%>
	<div class="container">
		<div class="row">
			<div class="span6 offset3">
				<div>
					<h4>注册默认赠送金额</h4>
					<div class="input-append float-left">
						<input id="default_register_money_ipt" class="span2" type="text"
							value="<%=defaultRegMoney %>" pattern="(\d+\.\d{2})|(\d+)" />
						<button type="button" class="btn" id="edit_reg_money_btn">保存</button>
					</div>
					<span class="help-inline" id="reg_money_edit_info"></span>
				</div>
				<hr />
				
				<div>
					<h4>注册送话费活动管理</h4>
					<table class="table table-hover">
						<thead>
							<tr>
								<th>开始时间</th>
								<th>结束内容</th>
								<th>赠送金额</th>
								<th>状态</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody id="reg_activity_body">
							<%
								if (regActivityMap != null) {
									String startDate = String.valueOf(regActivityMap.get("start_date"));
									String endDate = String.valueOf(regActivityMap.get("end_date"));
									Double giftMoney = (Double) regActivityMap.get("gift_money");
									String status = (String) regActivityMap.get("status");
								}
							%>
						</tbody>
					</table>
				</div>
			</div>
		</div>


	</div>
	<jsp:include page="common/_footer.jsp"></jsp:include>


	<script src="/angola/js/lib/jquery-1.8.0.min.js"></script>
	<script src="/angola/js/lib/bootstrap.min.js"></script>
	<script type="text/javascript">
		
	</script>
</body>
</html>