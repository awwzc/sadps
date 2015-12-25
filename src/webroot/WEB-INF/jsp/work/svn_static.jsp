<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
	
%>

<!DOCTYPE html>
<html ng-app="tscApp">
<head>
    <meta charset="UTF-8">
    <title>天下信用服务器控制台</title>
    
    <link rel="stylesheet" href="<%=basePath%>/lib/bootstrap/3.0.0/bootstrap.css">
    <link rel="stylesheet" href="<%=basePath%>/css/index.css">

    <script src="<%=basePath%>/lib/angular/1.2.3/angular.js"></script>

</head>
<body ng-controller="mainCtrl" class="ng-cloak">
    
    <header id="header" class="nav navbar navbar-default navbar">
        <div class="container">
            <a href="" class="navbar-brand" title="TXXY Server Console">
                <i class="glyphicon glyphicon-hdd"></i> TSC
            </a>
        </div>
    </header>

    <main class="container">
        <ul class="list-group">
            
            <li class="list-group-item" ng-controller="staticUpdateCtrl">
                <button class="btn btn-primary" 
                    ng-disabled="isLoading" 
                    ng-click="update('work.do?action=svnUIUp')">
                    <!-- 配置 update('your-update-url') 中的 your-update-url 为更新命令请求地址 -->

                    <i class="glyphicon glyphicon-refresh"></i>
                    更新UI静态资源
                </button>

                <div class="alert mat" 
                    ng-show="isSuccess || isError" 
                    ng-class="{ 'alert-success': isSuccess, 'alert-warning': isError }">

                    <button class="close" ng-click="clear()">&times;</button>
                    <strong ng-bind-html-unsafe="updateResultText"></strong>
                </div>

                <blockquote ng-show="message" ng-bind-html-unsafe="message" class="error-box"></blockquote>
            </li>
            
            <li class="list-group-item" ng-controller="staticUpdateCtrl">
                <button class="btn btn-primary" 
                    ng-disabled="isLoading" 
                    ng-click="update('work.do?action=svnWXUp')">
                    <!-- 配置 update('your-update-url') 中的 your-update-url 为更新命令请求地址 -->

                    <i class="glyphicon glyphicon-refresh"></i>
                    更新WX静态资源
                </button>

                <div class="alert mat" 
                    ng-show="isSuccess || isError" 
                    ng-class="{ 'alert-success': isSuccess, 'alert-warning': isError }">

                    <button class="close" ng-click="clear()">&times;</button>
                    <strong ng-bind-html-unsafe="updateResultText"></strong>
                </div>

                <blockquote ng-show="message" ng-bind-html-unsafe="message" class="error-box"></blockquote>
            </li>

        </ul>
    </main>

    <script src="<%=basePath%>/js/app.js"></script>
</body>
</html>