<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>登录</title>
<style>
.title{
    text-align: center;
}
.login {
	margin: 0 auto;
	border: 1.5px solid;
	width: 380px;
}
.row{
    margin-top: 10px;
    margin-left:5px;
    margin-right:5px;
    display:flex;
}
.row .label{
    width: 80px;
}
.btn{
    margin: 10px 5px 15px 5px;
    text-align: center;
}
</style>
</head>
<body>
    <div class="title">
            <h2>开发联调环境自动部署</h2>
    </div>
	<div class="login">
		<form action="index.jsp" method="post">
			<div class="row">
				<div class="label">用户名：</div>
				<input type="text" name="username">
			</div>
			<div class="row">
				<div class="label">密码：</div>
				<input type="password" name="password">
			</div>
			<div class="btn">
				<input type="submit" value='登录'>
			</div>
		</form>
	</div>
</body>
</html>