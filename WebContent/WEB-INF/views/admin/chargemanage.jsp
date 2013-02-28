<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
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
		List<Map<String, Object>> list = (List<Map<String, Object>>) request.getAttribute("charge_money_list");
	%>
	
	<div class="container">
		<div class="row">
			<div class="span8 offset2">
				<div class="clearfix">
					<h4 class="pull-left">充值金额设置</h4>
					<a type="button" class="btn btn-primary pull-right avc-button" data-toggle="modal" href="#new_charge_money_dlg">添加新金额</a>
				</div>
				<hr>
				<table class="table table-hover">
					<thead>
							<tr>
								<th>充值金额</th>
								<th>赠送金额</th>
								<th>内容描述</th>
								<th>操作</th>
							</tr>
					</thead>
					<tbody id="charge_money_list">
						<%
							if (list != null) {
								for (Map<String, Object> item : list) {
									Integer id = (Integer) item.get("id");
									Float chargeMoney = (Float) item.get("charge_money");
									Float giftMoney = (Float) item.get("gift_money");
									String description = (String) item.get("description");
									
						%>
									<tr id="<%=id %>">
										<td class="charge_money_td"><%=String.format("%.2f", chargeMoney.floatValue()) %></td>
										<td class="gift_money_td"><%=String.format("%.2f", giftMoney.floatValue()) %></td>
										<td class="description_td"><%=description %></td>
										<td>
											<div class="btn-group">
												<button data-toggle="dropdown" class="btn dropdown-toggle">
													选择操作 <span class="caret"></span>
												</button>
												<ul class="dropdown-menu">
													<li class="op_eidt_li"><a href="#"><i
															class="icon-wrench"></i>&nbsp;修改</a></li>
													<li class="op_delete_li"><a href="#"><i
															class="icon-trash"></i>&nbsp;删除</a></li>
												</ul>
											</div>
										</td>
									</tr>
						<%
								}
							}
						%>
					</tbody>
				</table>
			</div>
		</div>


	</div>
	<jsp:include page="common/_footer.jsp"></jsp:include>

	<!-- dialog  -->
	<div id="new_charge_money_dlg" class="modal hide fade">
		<div class="modal-header">
			<h4>添加新金额</h4>
		</div>
		<div class="modal-body">
			<form id="new_money_form" class="span3 offset1" method="post" action="">
				<div>
					<label>充值金额</label>
					<input id="charge_money_ipt" type="text" class="input-medium span3"
						name="charge_money" />
					<label>赠送金额</label>
					<input id="gift_money_ipt" type="text" class="input-medium span3" name="gift_money" />
					<label>内容描述</label>
					<input id="description_ipt" type="text" class="input-medium span3" name="description" />
					<div id="money_form_alert" class="alert alert-error hidden">
						<label id="money_form_alert_content"></label>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal" id="new_money_close">关闭</a> <a
				id="new_money_save" href="#" class="btn btn-primary">保存</a>
		</div>
	</div>

	<div id="edit_charge_money_dlg" class="modal hide fade">
		<div class="modal-header">
			<h4>修改金额</h4>
		</div>
		<div class="modal-body">
			<form id="edit_money_form" class="span3 offset1" method="post" action="">
				<div>
					<input id="edit_charge_money_id_ipt" type="hidden"/>
					<label>充值金额</label>
					<input id="edit_charge_money_ipt" type="text" class="input-medium span3"
						name="charge_money" />
					<label>赠送金额</label>
					<input id="edit_gift_money_ipt" type="text" class="input-medium span3" name="gift_money" />
					<label>内容描述</label>
					<input id="edit_description_ipt" type="text" class="input-medium span3" name="description" />
					<div id="edit_money_form_alert" class="alert alert-error hidden">
						<label id="edit_money_form_alert_content"></label>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal" id="edit_money_close">关闭</a> <a
				id="edit_money_save" href="#" class="btn btn-primary">保存</a>
		</div>
	</div>

	<script src="/uutalk/js/lib/jquery-1.8.0.min.js"></script>
	<script src="/uutalk/js/lib/bootstrap.min.js"></script>
	<script type="text/javascript">
		$("#new_money_save").click(function() {
			$("#money_form_alert").addClass("hidden");
			
			var chargeMoney = $("#charge_money_ipt").val();
			var giftMoney = $("#gift_money_ipt").val();
			var desc = $("#description_ipt").val();
			
			if (chargeMoney == null || chargeMoney == "") {
				$("#money_form_alert").removeClass("hidden");
				$("#money_form_alert_content").html("请输入充值金额！");
				return false;
			}
			
			if (giftMoney == null || giftMoney == "") {
				$("#money_form_alert").removeClass("hidden");
				$("#money_form_alert_content").html("请输入赠送金额！");
				return false;
			}
			
			if (desc == null || desc == "") {
				$("#money_form_alert").removeClass("hidden");
				$("#money_form_alert_content").html("请输入内容描述！");
				return false;
			}
			
			$.ajax({
				type : "post",
				url : "/uutalk/admin/chargemanage/addChargeMoney",
				dataType : "json",
				data : {
					charge_money : chargeMoney,
					gift_money : giftMoney,
					description : desc
				},
				success : function(data, textStatus, jqxhr) {
					var result = data.result;
					switch (result) {
					case "invalid_charge_money":
						$("#money_form_alert").removeClass("hidden");
						$("#money_form_alert_content").html("充值金额不是有效的输入(有效输入格式为00.00)！");
						break;
					case "invalid_gift_money":
						$("#money_form_alert").removeClass("hidden");
						$("#money_form_alert_content").html("赠送金额不是有效的输入(有效输入格式为00.00)！");
						break;
						
					case "ok":
						location = "/uutalk/admin/chargemanage";
						break;
					}
					
				},
				error : function(jqXHR, textStatus) {
					$("#money_form_alert").removeClass("hidden");
					$("#money_form_alert_content").html("系统内部错误（STATUS CODE: " + jqXHR.status + ")");
				}
			});
			
			return false;
		});
	
		$("#new_money_close").click(function() {
			$("#charge_money_ipt").val("");
			$("#gift_money_ipt").val("");
			$("#description_ipt").val("");
			$("#money_form_alert").addClass("hidden");
			return true;
		});
		
		$("#charge_money_list").delegate(".op_eidt_li", "click", function() {
			var $tr = $(this).parent().parent().parent().parent();
			var id = $tr.attr("id");
			var chargeMoney = $tr.find(".charge_money_td").html();
			var giftMoney = $tr.find(".gift_money_td").html();
			var description = $tr.find(".description_td").html();
	
			$("#edit_charge_money_id_ipt").val(id);
			$("#edit_charge_money_ipt").val(chargeMoney);
			$("#edit_gift_money_ipt").val(giftMoney);
			$("#edit_description_ipt").val(description);
			$("#edit_charge_money_dlg").modal();
		
		});
		
		$("#edit_money_close").click(function() {
			$("#edit_money_form_alert").addClass("hidden");
			return true;
		});
		
		$("#edit_money_save").click(function() {
			$("#edit_money_form_alert").addClass("hidden");
			
			var chargeMoneyId = $("#edit_charge_money_id_ipt").val();
			var chargeMoney = $("#edit_charge_money_ipt").val();
			var giftMoney = $("#edit_gift_money_ipt").val();
			var desc = $("#edit_description_ipt").val();
			
			if (chargeMoney == null || chargeMoney == "") {
				$("#edit_money_form_alert").removeClass("hidden");
				$("#edit_money_form_alert_content").html("请输入充值金额！");
				return false;
			}
			
			if (giftMoney == null || giftMoney == "") {
				$("#edit_money_form_alert").removeClass("hidden");
				$("#edit_money_form_alert_content").html("请输入赠送金额！");
				return false;
			}
			
			if (desc == null || desc == "") {
				$("#edit_money_form_alert").removeClass("hidden");
				$("#edit_money_form_alert_content").html("请输入内容描述！");
				return false;
			}
			
			$.ajax({
				type : "post",
				url : "/uutalk/admin/chargemanage/editChargeMoney",
				dataType : "json",
				data : {
					id : chargeMoneyId,
					charge_money : chargeMoney,
					gift_money : giftMoney,
					description : desc
				},
				success : function(data, textStatus, jqxhr) {
					var result = data.result;
					switch (result) {
					case "invalid_charge_money":
						$("#edit_money_form_alert").removeClass("hidden");
						$("#edit_money_form_alert_content").html("充值金额不是有效的输入(有效输入格式为00.00)！");
						break;
					case "invalid_gift_money":
						$("#edit_money_form_alert").removeClass("hidden");
						$("#edit_money_form_alert_content").html("赠送金额不是有效的输入(有效输入格式为00.00)！");
						break;
						
					case "ok":
						location = "/uutalk/admin/chargemanage";
						break;
					}
					
				},
				error : function(jqXHR, textStatus) {
					$("#edit_money_form_alert").removeClass("hidden");
					$("#edit_money_form_alert_content").html("系统内部错误（STATUS CODE: " + jqXHR.status + ")");
				}
			});
			
			return false;
		});
		
		$("#charge_money_list").delegate(".op_delete_li", "click", function() {
			var $tr = $(this).parent().parent().parent().parent();
			var chargeMoneyId = $tr.attr("id");
			var description = $tr.find(".description_td").html();
			
			if (confirm("确定要删除'" + description + "'这条记录？")) {
				$.ajax({
					type : "post",
					url : "/uutalk/admin/chargemanage/deleteChargeMoney",
					dataType : "json",
					data : {
						id : chargeMoneyId,
					},
					success : function(data, textStatus, jqxhr) {
						location = "/uutalk/admin/chargemanage";
					},
					error : function(jqXHR, textStatus) {
						alert("系统内部错误（STATUS CODE: " + jqXHR.status + ")");
					}
				});
				
			}
		});
	</script>
</body>
</html>