<%--
  Created by IntelliJ IDEA.
  User: sangminlee
  Date: 10/09/2019
  Time: 12:12 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String v1 = "";
    String v2 = "";
    String result = "";
    String[] selected = {"", "", "", ""};

    if (request)

%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>계산기</title>
</head>
<body>
<h2>JSP 계산기</h2>
<form action="Calculator.jsp" method="get">
    <input type="text" name="v1" size="4" value="<%=v1%>%>">
    <select name="op">
        <option value="+" <%=selected[0]%>>+</option>
        <option value="-" <%=selected[1]%>>-</option>
        <option value="*" <%=selected[2]%>>*</option>
        <option value="/" <%=selected[3]%>>/</option>
    </select>
    <input type="text" name="v2" size="4" value="<%=v2%>">
    <input type="submit" value="=">
    <input type="text" size="8" value="<%=result%>"><br>
</form>
</body>
</html>
<%!
    private String calculate(int a, int b, String op) {
      int r = 0;

      if ("+".equals(op)) {
        r = a + b;
      } else if ("-".equals(op)) {
        r = a - b;
      } else if ("*".equals(op)) {
        r = a * b;
      } else if ("/".equals(op)) {
        r = a / b;
      }

      return Integer.toString(r);
    }
%>
