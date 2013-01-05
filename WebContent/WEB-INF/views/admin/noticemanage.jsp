<%@page import="com.angolacall.constants.NoticeStatus"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>消息管理</title>
<jsp:include page="common/_head.jsp"></jsp:include>
</head>
<body>
	<jsp:include page="common/afterlogin_navibar.jsp"></jsp:include>

	<%
		List<Map<String, Object>> list = (List<Map<String, Object>>) request.getAttribute("notices");
	%>

	<div class="container">
		<div class="row">
			<div class="span8 offset2">
				<div class="clearfix">
					<h4 class="pull-left">消息列表</h4>
					<a type="button" class="btn btn-primary pull-right"
						data-toggle="modal" href="#new_notice_dlg"><i class="icon-pencil icon-white"></i> 创建新消息</a>
				</div>
				<hr>
				<table class="table table-hover">
					<thead>
						<tr>
							<th>创建时间</th>
							<th>消息内容</th>
							<th>状态</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody id="notice_list">
						<%
							if (list != null) {
								for (Map<String, Object> item : list) {
									Integer id = (Integer) item.get("id");
									String content = (String) item.get("content");
									Long time = (Long)item.get("time");
									Calendar cal = Calendar.getInstance();
									cal.setTimeInMillis(time * 1000);
									SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									String createTime = df.format(cal.getTime());
									
									String status = (String) item.get("status");
									String statusText = "";
									if (status.equals(NoticeStatus.publish.name())) {
										statusText = "发布";
									} else if (status.equals(NoticeStatus.draft.name())) {
										statusText = "草稿";
									}
									
						%>
									<tr id="<%=id %>">
										<td><%=createTime %></td>
										<td class="notice_content"><%=content %></td>
										<td><%=statusText %></td>
										<td>
										<% 
											if (NoticeStatus.draft.name().equals(status)) {
										%>
											<a title="发布" class="op_publish_bt" href="#"><i class="icon-share"></i></a>
											<a title="编辑" class="op_edit_bt" href="#"><i class="icon-edit"></i></a>
										<%
											} 
										%>
											<a title="删除" class="op_delete_bt" href="#"><i
												class="icon-trash"></i></a>
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
	<div id="new_notice_dlg" class="modal hide fade">
		<div class="modal-header">
			<h4>创建新消息</h4>
		</div>
		<div class="modal-body">
			<form id="new_notice_form" class="span4 offset1" method="post"
				action="">
				<div>
					<textarea id="content_ta" placeholder="在此填写消息内容" cols="20" rows="5"
						wrap="virtual" name="content"></textarea>
					<div id="new_notice_form_alert" class="alert alert-error hidden">
						<label id="new_notice_form_alert_content"></label>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal" id="new_notice_close">关闭</a>
			<a id="new_notice_save" href="#" class="btn btn-primary">保存</a>
		</div>
	</div>
	
	<div id="edit_notice_dlg" class="modal hide fade">
		<div class="modal-header">
			<h4>编辑消息</h4>
		</div>
		<div class="modal-body">
			<form id="edit_notice_form" class="span4 offset1" method="post" action="">
				<div>
					<input id="edit_notice_id_ipt" type="hidden"/>
					<textarea id="edit_content_ta" placeholder="在此填写消息内容" cols="20" rows="5"
						wrap="virtual" name="content"></textarea>
					<div id="edit_notice_form_alert" class="alert alert-error hidden">
						<label id="edit_notice_form_alert_content"></label>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal" id="edit_notice_close">关闭</a> <a
				id="edit_notice_save" href="#" class="btn btn-primary">保存</a>
		</div>
	</div>
	
	<script type="text/javascript" src="/angola/js/lib/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="/angola/js/lib/bootstrap.min.js"></script>
	<script type="text/javascript">
		$("#new_notice_save").click(
				function() {
					$("#new_notice_form_alert").addClass("hidden");
					var content = $("#content_ta").val();

					if (content == null || content == "") {
						$("#new_notice_form_alert").removeClass("hidden");
						$("#new_notice_form_alert_content").html("请填写消息内容");
						return false;
					}

					$.ajax({
						type : "post",
						url : "/angola/admin/noticemanage/addNotice",
						dataType : "json",
						data : {
							"content" : content
						},
						success : function(data, textStatus, jqxhr) {
							location = "/angola/admin/noticemanage";

						},
						error : function(jqXHR, textStatus) {
							$("#new_notice_form_alert").removeClass("hidden");
							$("#new_notice_form_alert_content")
									.html(
											"系统内部错误（STATUS CODE: "
													+ jqXHR.status + ")");
						}
					});

					return false;
				});

		$("#new_notice_close").click(function() {
			$("#content_ta").val("");
			$("#new_notice_form_alert").addClass("hidden");
			return true;
		});
		
		$("#notice_list").delegate(".op_delete_bt", "click", function() {
			var $tr = $(this).parent().parent();
			var id = $tr.attr('id');
			var content = ($tr.find(".notice_content")).html();
			if (confirm("确定要删除'" + content + "'这条消息？")) {
				$.ajax({
					type : "post",
					url : "/angola/admin/noticemanage/delNotice",
					dataType : 'json',
					data : {
						"noticeId" : id
					},
					success : function() {
						location = "/angola/admin/noticemanage";
					},
					error : function(jqXHR, textStatus) {
						alert("系统内部错误（STATUS CODE: "
								+ jqXHR.status + ")");
					}
				});
			}
		});
		
		$("#notice_list").delegate(".op_publish_bt", "click", function() {
			var $tr = $(this).parent().parent();
			var id = $tr.attr('id');
			var content = ($tr.find(".notice_content")).html();
			if (confirm("确定要发布'" + content + "'这条消息？")) {
				$.ajax({
					type : "post",
					url : "/angola/admin/noticemanage/pubNotice",
					dataType : 'json',
					data : {
						"noticeId" : id
					},
					success : function() {
						location = "/angola/admin/noticemanage";
					},
					error : function(jqXHR, textStatus) {
						alert("系统内部错误（STATUS CODE: "
								+ jqXHR.status + ")");
					}
				});
			}
		});
		
		$("#notice_list").delegate(".op_edit_bt", "click", function() {
			var $tr = $(this).parent().parent();
			var id = $tr.attr('id');
			var content = ($tr.find(".notice_content")).html();
			
			$("#edit_notice_id_ipt").val(id);
			$("#edit_content_ta").val(content);
			
			$("#edit_notice_dlg").modal();
		});
		
		$("#edit_notice_close").click(function() {
			$("#edit_content_ta").val("");
			$("#edit_notice_form_alert").addClass("hidden");
			return true;
		});
		
		$("#edit_notice_save").click(function() {
			$("#edit_notice_form_alert").addClass("hidden");
			var content = $("#edit_content_ta").val();
			var id = $("#edit_notice_id_ipt").val();
			
			if (content == null || content == "") {
				$("#edit_notice_form_alert").removeClass("hidden");
				$("#edit_notice_form_alert_content").html("请填写消息内容");
				return false;
			}

			$.ajax({
				type : "post",
				url : "/angola/admin/noticemanage/editNotice",
				dataType : "json",
				data : {
					"noticeId" : id,
					"content" : content
				},
				success : function(data, textStatus, jqxhr) {
					location = "/angola/admin/noticemanage";

				},
				error : function(jqXHR, textStatus) {
					$("#edit_notice_form_alert").removeClass("hidden");
					$("#edit_notice_form_alert_content")
							.html(
									"系统内部错误（STATUS CODE: "
											+ jqXHR.status + ")");
				}
			});

			return false;
		});
	</script>
</body>
</html>