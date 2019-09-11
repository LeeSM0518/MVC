<%--
  Created by IntelliJ IDEA.
  User: sangminlee
  Date: 10/09/2019
  Time: 11:02 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="spms.vo.Member" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="members"
             scope="request"
             class="java.util.ArrayList"
             type="java.util.ArrayList<spms.vo.Member>"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>회원 목록</title>
</head>
<body>
<jsp:include page="/Header.jsp"/>
<h1>회원 목록</h1>
<p><a href="/member/add">신규 회원</a></p>
<%
//    ArrayList<Member> members = (ArrayList<Member>) request.getAttribute("members");
    for (Member member : members) {
%>
<%=member.getNo()%>,
<a href="update?no=<%=member.getNo()%>"><%=member.getName()%>
</a>,
<%=member.getEmail()%>,
<%=member.getCreateDate()%>
<a href="delete?no=<%=member.getNo()%>">[삭제]</a><br>
<%} %>
<jsp:include page="/Tail.jsp"/>
</body>
</html>
