<%--
  Created by IntelliJ IDEA.
  User: sangminlee
  Date: 11/09/2019
  Time: 12:32 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="spms.vo.Member"%>
<jsp:useBean id="member"
             scope="session"
             class="spms.vo.Member"/>
<div style="background-color: #00008b; color: #ffffff; height: 20px; padding: 5px;">
    SPMS(Simple Project Management System)
    <span style="float: right;">
    <%=member.getName()%>
    <a style="color: white;"
       href="<%=request.getContextPath()%>/auth/logout">로그아웃</a>
    </span>
</div>