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
<%-- URL에 .do 붙이기--%>
<form action="update.do" method="post">
    번호: <input type="text" name="no" value="${member.no}" readonly><br>
    이름: <input type="text" name="name" value="${member.name}"><br>
    이메일: <input type="text" name="email" value="${member.email}"><br>
    가입일: ${member.createDate}<br>
    <input type="submit" value="저장">
    <%-- URL에 .do 붙이기 --%>
    <input type="button" value="삭제" onclick="location.href='delete.do?no=${member.no}'">
    <input type="button" value="취소" onclick="location.href='list.do'">
</form>
</body>
</html>
