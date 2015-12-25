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
<div>txt:<c:out value="${bean.txt}"/></div>
<div>waitfor:<c:out value="${bean.code}"/></div>
<div>exit:<c:out value="${bean.exit}"/></div>

</form>

</body>
</html>