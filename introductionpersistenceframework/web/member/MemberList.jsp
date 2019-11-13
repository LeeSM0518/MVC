<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>회원 목록</title>
</head>
<body>
<jsp:include page="/Header.jsp"/>
<h1>회원 목록</h1>
<p><a href="add.do">신규 회원</a></p>
<table border="1">
    <%-- 정렬 링크가 걸린 칼럼 헤더--%>
    <tr>
        <th>
            <%-- orderCond 값에 따라 MNO 정렬 방식을 결정한다--%>
            <c:choose>
                <c:when test="${orderCond == 'MNO_ASC'}">
                    <a href="list.do?orderCond=MNO_DESC">번호▲</a>
                </c:when>
                <c:when test="${orderCond == 'MNO_DESC'}">
                    <a href="list.do?orderCond=MNO_ASC">번호▼</a>
                </c:when>
                <c:otherwise>
                    <a href="list.do?orderCond=MNO_ASC">번호</a>
                </c:otherwise>
            </c:choose>
        </th>
        <th>
            <c:choose>
                <c:when test="${orderCond == 'NAME_ASC'}">
                    <a href="list.do?orderCond=NAME_DESC">이름▲</a>
                </c:when>
                <c:when test="${orderCond == 'NAME_DESC'}">
                    <a href="list.do?orderCond=NAME_ASC">이름▼</a>
                </c:when>
                <c:otherwise>
                    <a href="list.do?orderCond=NAME_ASC">이름</a>
                </c:otherwise>
            </c:choose>
        </th>
        <th>
            <c:choose>
                <c:when test="${orderCond == 'EMAIL_ASC'}">
                    <a href="list.do?orderCond=EMAIL_DESC">이메일▲</a>
                </c:when>
                <c:when test="${orderCond == 'EMAIL_DESC'}">
                    <a href="list.do?orderCond=EMAIL_ASC">이메일▼</a>
                </c:when>
                <c:otherwise>
                    <a href="list.do?orderCond=EMAIL_ASC">이메일</a>
                </c:otherwise>
            </c:choose>
        </th>
        <th>
            <c:choose>
                <c:when test="${orderCond == 'CREDATE_ASC'}">
                    <a href="list.do?orderCond=CREDATE_DESC">등록일▲</a>
                </c:when>
                <c:when test="${orderCond == 'CREDATE_DESC'}">
                    <a href="list.do?orderCond=CREDATE_ASC">등록일▼</a>
                </c:when>
                <c:otherwise>
                    <a href="list.do?orderCond=CREDATE_ASC">등록일</a>
                </c:otherwise>
            </c:choose>
        </th>
    </tr>
    <%-- members 리스트를 가져와서 member 객체에 넣고 for문 돌림--%>
    <c:forEach var="member" items="${members}">
        <tr>
            <td>${member.no}</td>
            <td><a href="update.do?no=${member.no}">${member.name}</a></td>
            <td>${member.email}</td>
            <td>${member.createDate}</td>
            <td><a href="delete.do?no=${member.no}">[삭제]</a></td>
        </tr>
    </c:forEach>
</table>
<jsp:include page="/Tail.jsp"/>
</body>
</html>
