<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>天下信用-部署中心</title>
    <meta charset="UTF-8">
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/user-center.css">
    <link rel="stylesheet" href="http://s.tianxiaxinyong.com/ui/css/app-center.css">
    <script src="js/jquery.js"></script>
    <script type="text/javascript">
 	 var serverMap = '<c:out escapeXml="false" value="${serverMap}"/>';
 	 var msg = '<c:out escapeXml="false" value="${message}"/>';
 	 
 	 $(function(){
 	 	$("#file-add-btn").click(function(){
 	 		$(".file-add").append('<input type="file" name="fileName" class="file">');
 	 	});
 	 	if(serverMap){
 	 		var serverJson = JSON.parse(serverMap);
 	 		$.each(serverJson,function(key,val){
 	 			$("#app-menu-list").append('<li><a href="work.do?action=toUpload&s='+key
 	 					+'" target="cframe"><i class="icon-app-s icon-app-s-QYXXCX"></i>'
 	 					+key+val.split(',')[0]+'..</a></li>');
 	 		});
 	 	}
 	 });
 	 </script>
</head>

<body>
    <div class="navbar navbar-main">
        <div class="navbar-inner">
            <a href="javascript:;" class="navbar-brand">
                <img src="images/logo.png" alt="信用天下" height="54px">
            </a>
        </div>
    </div>
    <div class="container">
        <div class="sidebar clearfix" style="min-height: 1321px;">
            <!-- 应用中心左侧导航 -->
            <div class="panel my-app">
                <div class="panel-heading">部署项目</div>
                <ul id="app-menu-list" class="nav app">
                	
                </ul>
            </div>

        </div>
		<div class="center">
		    <div class="content app-center" style="height: 785px;">
		        <iframe name="cframe" id="cframe" style="width:98%;height:98%;border: none;" src="welcome.jsp"></iframe>
		    </div>
		</div>

    </div>


    <div class="footer">
        <nav>
            <a href="#">关于我们</a>
            <span class="footer-split">｜</span>
            <a href="#">公司大事记</a>
            <span class="footer-split">｜</span>
            <a href="#">资质荣誉</a>
            <span class="footer-split">｜</span>
            <a href="#">联系我们</a>

        </nav>
        <p>Copyright© 2014 鹏元征信有限公司 <a target="_blank" href="#">粤ICP备10240340号-5</a>
        </p>
    </div>
</body>

</html>
