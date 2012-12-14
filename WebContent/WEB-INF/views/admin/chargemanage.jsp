<%@page import="com.angolacall.constants.UUTalkConfigKeys"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh">
<head>
<title>充值管理</title>
<jsp:include page="common/_head.jsp"></jsp:include>
</head>
<body>
	<jsp:include page="common/afterlogin_navibar.jsp"></jsp:include>
	
	<%
	%>
	
	<div class="container">
		<div class="row">
			<div class="span8 offset3 tabbable tabs-left">
				<ul class="nav nav-tabs">
					<li class="active">
						<a data-toggle="tab" href="#pane-charge-amount-config">充值金额设置</a>
					</li>
				</ul>
				<div class="tab-content">	
                    <div id="pane-charge-amount-config" class="tab-pane active">
                        <h3>充值金额设置</h3>
                        <hr>
					</div>  
                    
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