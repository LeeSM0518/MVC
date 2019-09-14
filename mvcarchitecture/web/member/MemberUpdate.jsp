<%--
  Created by IntelliJ IDEA.
  User: sangminlee
  Date: 11/09/2019
  Time: 11:16 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>회원정보</title>
</head>
<body>
<h1>회원정보</h1>
<form action="/member/update" method="post">
    번호: <input type="text" name="no" value="${updateMember.no}" readonly><br>
    이름: <input type="text" name="name" value="${updateMember.name}"><br>
    이메일: <input type="text" name="email" value="${updateMember.email}"><br>
    가입일: ${updateMember.createDate}<br>
    <input type="submit" value="저장">
    <input type="button" value="삭제" onclick="location.href='delete?no=${updateMember.no}'">
    <input type="button" value="취소" onclick="location.href='list'">
</form>
</body>
</html>
