<%--
  Created by IntelliJ IDEA.
  User: sangminlee
  Date: 11/09/2019
  Time: 12:32 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div style="background-color: #00008b; color: #ffffff; height: 20px; padding: 5px;">
    SPMS(Simple Project Management System)
    <span style="float: right;">
    <a style="color: white;"
       href="<%=request.getContextPath()%>/project/list.do">프로젝트</a>
    <a style="color: white;"
       href="<%=request.getContextPath()%>/member/list.do">회원</a>
    <c:if test="${empty sessionScope.member or empty sessionScope.member.email}">
        <a style="color: white;" href="<%=request.getContextPath()%>/auth/login.do">로그인</a>
    </c:if>
    <c:if test="${!empty sessionScope.member and !empty sessionScope.member.name}">
        ${sessionScope.member.name}
        (<a style="color: white;"
        href="<%=request.getContextPath()%>/auth/logout.do">로그아웃</a>
    </c:if>
    </span>
</div>