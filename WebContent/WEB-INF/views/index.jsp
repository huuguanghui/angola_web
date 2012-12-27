<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>UU-Talk</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
	<jsp:include page="common/beforelogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div class="row">
    		<div class="span4 offset2">
    			<div class="row">
    				<div class="span3 offset1">
    					<div class="app_demo_view">
		    				<img alt="uu-talk" src="./img/uutalk-app-capture.png"/>
	    				</div>
    				</div>
    			</div>
    		</div>
    		<div class="span4">
    			<h1>UU-Talk&nbsp;<small>最好用的网络电话</small></h1>
    			<ul>
    				<li><h2><small>免费下载,免费注册,即可获赠话费</small></h2></li>
	    			<li><h2><small>高清语音质量</small></h2></li>
	    			<li><h2><small>0月租0漫游0功能费0负担放心使用</h2></li>
	    			<li><h2><small>操作快捷方便</small></h2></li>
    			</ul>
    			<hr>
    			<div>
    				<a class="btn" href="/appvcenter/downloadapp/2/android">
    					<div>
    						<img class="pull-left" alt="app store" src="./img/android.png">
    						<div class="pull-right">
    							<p><strong>&nbsp;UU-Talk Android 版</strong><br>下载 apk 文件</p>
    						</div>
    					</div>
    				</a>    				
    			</div>
    		</div>
    	</div>
    	
    	<jsp:include page="common/_footer.jsp"></jsp:include>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>
  </body>
</html>
