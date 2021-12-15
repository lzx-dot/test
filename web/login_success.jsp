<%--
  Created by IntelliJ IDEA.
  User: ZX
  Date: 2021/5/11
  Time: 15:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录成功</title>
</head>
<body>

    <div style="display: flex ">登录成功!!</div>
    <div>用户名:${requestScope.username}</div>
    <div>密  码：${requestScope.password}</div>
     <div>预约区域：${sessionScope.area}</div>
    <div> segment: ${sessionScope.segment}</div>

</body>
</html>
