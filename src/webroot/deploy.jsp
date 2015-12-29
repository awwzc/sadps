<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="net.sk.deploy.util.SpringContextUtil"%>
<%@page import="net.sk.deploy.util.DeployHelper"%>
<%@page import="net.sk.deploy.support.Config"%>
<%@page import="net.sk.deploy.support.ProjectInfo"%>
<%
    Config projectConfig = SpringContextUtil.getConfig();
    Map<String,ProjectInfo> projectContext = projectConfig.getProjectsInfo();
    String hostName = request.getServerName();
    int port = request.getServerPort();
    String context = request.getContextPath();
    String wsUrl = "ws://" + hostName + ":" + port + context + "/deploy";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="refresh" content="1200">
<style>
body {
	padding: 20px;
}

#message {
	height: 530px;
	border: 1px solid;
	overflow: auto;
	background-color: black;
	color: darkcyan;
}
.details {
    display:flex;
	width: 100%;
	height: 135px;
	border: 1px solid;
	margin-bottom: 10px;
}
.details .status{
    width: 310px;
	margin-left: 15px;
}
.details .operate{
    width: 890px;
	border: 1px dotted;
	padding: 11px;
}
.config-area{
    margin-top:8px;
}
.config-area textarea{
    width: 645px;
    height: 45px;
}
.details .projectArea{
    margin-left:3px;
	border: 1px dotted;
	padding: 11px;
}
</style>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>开发联调环境自动部署</title>
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<%
	Map<String,String> userInfo = (Map<String,String>)session.getAttribute("user");
    String name = null;
    String password  = null;
    if(null == userInfo){
    	userInfo = new HashMap<String,String>();
	    name = request.getParameter("username");
	    password = request.getParameter("password");
    }else{
    	name = userInfo.get("name");
        password = userInfo.get("password");
    }
    boolean validateRes = false;
    if(null != name && null != password){
    	validateRes = DeployHelper.matchUser(name,password);
    }
    if(!validateRes){
        response.sendRedirect("login.jsp");
    }else{
        userInfo.put("name",name);
        userInfo.put("password",password);
        session.setAttribute("user", userInfo);
    }
%>

</head>
<body onload="startWebSocket();" >
    <div style="text-align: center;">
		<h2>开发联调环境自动部署</h2>
    </div>
	<div class='details'>
	   <div class="status">
		          登录状态：
	        <span id="denglu" style="color: red;">正在登录</span>
	        <br> 昵称：
	        <span id="userName"></span>
	   </div>
		<div class="operate">
			<div>
				<input type="button" value="查看War" onclick="sendMsg('listWars')">
				<input type="button" value="上传部署" onclick="sendMsg('uploadAndRestart')"> 
				<input type="button" value="清除Console" onclick="clearConsole()"> 
			</div>
		</div>
		<div class="projectArea">
	        <input type="button" value="项目构建" onclick="sendMsg('deployAndRestart')" />
	        <input type="button" value="新增配置" onclick="sendMsg('addConfig')">
            <input type="button" value="修改配置" onclick="sendMsg('modifyConfig')">
	        <input type="button" value="重新启动" onclick="sendMsg('restart')" />
	        <input type="button" value="debug模式启动" onclick="sendMsg('restartWithDebugModel')" />
	        <input type="button" value="查看存储状态" onclick="sendMsg('viewDiskUseAge')" />
	        <div>
		        <%
			        for(Entry<String,ProjectInfo> projectEntry:projectContext.entrySet()){
			        	String projectName = projectEntry.getKey();
			            out.println("<input type='checkbox' id='input-" + projectName + "' name='" + projectName + "' value='" + projectName + "'><label for='input-"+projectName+"'>" + projectName + "</label>");
			        }
		        %>
	        </div>
	        <div class="config-area">
                <textarea id="config-content" name="config-content" ></textarea>
            </div>
	   </div>
	</div>
	<div style="clear:both;"></div>
	<div id="message" >
		<div id="msg-content" style="margin-left: 5px;margin-top:5px;">
		</div>
	</div>
	<br>

	
	<script type="text/javascript">
	var userName = "<%=name%>";
	var ws = null;
	//required project command
	var projectCmd = {
		"deployAndRestart" : true,
		"restartWithDebugModel" : true,
		"restart" : true,
		"viewDiskUseAge" : true,
		"addConfig" : true,
		"modifyConfig" : true
	};
	//required single project command
	var singleProjectCmd = {
		"addConfig" : true,
		"modifyConfig" : true
	};
	var wsUrl = "<%=wsUrl %>";

	function startWebSocket() {
		if ('WebSocket' in window){
			ws = new WebSocket(wsUrl);
		}
		else if ('MozWebSocket' in window){
			ws = new MozWebSocket(wsUrl);
		}
		else{
			alert("not support");
		}
		
		ws.onmessage = function(res) {
			var jsonData = JSON.parse(res.data);
			setMessageInnerHTML(jsonData.message);
		};
		ws.onclose = function(evt) {
			$('#denglu').html("离线");
		};

		ws.onopen = function(evt) {
			$('#denglu').html("在线");
			$('#userName').html(userName);
		};
	}

	function setMessageInnerHTML(innerHTML) {
		var temp = $('#msg-content').html();
		temp += innerHTML + '<br/>';
		$('#msg-content').html(temp);
	}
	function sendMsg(command) {
		var data = {userName:userName,command:command};
		var command = command; //发给谁
		var projects = "";
		if(projectCmd[command]){
			var len = 0;
			$(":checkbox").each(function(index,e){
			    if(e.checked){
			    	projects += (e.value + ":");
			    	len ++;
			    }
	        });
			if(!projects){
				alert("请选择需要操作的项目!")
				return false;
			}
			if(singleProjectCmd[command] && len > 1){
				alert("只能选择一个需要操作的项目!")
				return false;
			}
		}
		var config = $('#config-content').val();  
		data["config"] = config;
		data["projects"] = projects;
		ws.send(JSON.stringify(data));
	}

	function clearConsole(){
        $('#msg-content').html('');
    }
</script>
	
</body>
</html>