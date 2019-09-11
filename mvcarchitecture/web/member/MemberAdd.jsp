<%--
  Created by IntelliJ IDEA.
  User: sangminlee
  Date: 11/09/2019
  Time: 9:09 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>회원 등록</title>
</head>
<body>
<h1>회원 등록</h1>
<form action="/member/add" method="post">
    이름: <label>
    <input type="text" name="name">
</label><br>
    이메일: <label>
    <input type="text" name="email">
</label><br>
    암호: <label>
    <input type="password" name="password">
</label><br>
    <input type="submit" value="추가">
    <input type="reset" value="취소">
</form>
</body>
</html>
