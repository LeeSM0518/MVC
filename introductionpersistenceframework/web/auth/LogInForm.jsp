<%--
  Created by IntelliJ IDEA.
  User: sangminlee
  Date: 26/09/2019
  Time: 11:30 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>로그인</title>
</head>
<body>
<h2>사용자 로그인</h2>
<%-- URL에 .do 붙임 --%>
<%-- URL, login.do로 post 요청 --%>
<form action="login.do" method="post">
    이메일: <input type="text" name="email"><br>
    암호: <input type="password" name="password"><br>
    <input type="submit" value="로그인">
</form>
</body>
</html>
