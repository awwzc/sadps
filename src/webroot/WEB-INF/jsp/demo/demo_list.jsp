<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ include file="../common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>

<script type="text/javascript">

</script>
</head>

<body>
<div>一个小小的测试例子。。。</div>
<div>提示信息:<c:out value="${bean.message}"/></div>
<form action="<%=path %>/demo/demo.do?action=list" method="post">
<input type="text" name="keyword" value="<c:out value="${keyword}"/>"/>
<input type="submit" value="查询" />

<input type="text" name="keyword2" value="<c:out value="${bean.description}"/>"/>

<a href="https://wangking:8443/cas2/logout" >注销</a>
</form>

</body>
</html>