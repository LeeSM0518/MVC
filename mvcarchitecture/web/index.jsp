<%--&lt;%&ndash;--%>
<%--  Created by IntelliJ IDEA.--%>
<%--  User: sangminlee--%>
<%--  Date: 09/09/2019--%>
<%--  Time: 10:33 오후--%>
<%--  To change this template use File | Settings | File Templates.--%>
<%--&ndash;%&gt;--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java"%>--%>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%--<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>--%>
<%--<%!--%>
<%--  public static class MyMember {--%>
<%--    int no;--%>
<%--    String name;--%>

<%--    public int getNo() {--%>
<%--      return no;--%>
<%--    }--%>

<%--    public void setNo(int no) {--%>
<%--      this.no = no;--%>
<%--    }--%>

<%--    public String getName() {--%>
<%--      return name;--%>
<%--    }--%>

<%--    public void setName(String name) {--%>
<%--      this.name = name;--%>
<%--    }--%>
<%--  }--%>
<%--%>--%>
<%--<%--%>
<%--  MyMember member = new MyMember();--%>
<%--  member.setNo(100);--%>
<%--  member.setName("홍길동");--%>
<%--  session.setAttribute("member", member);--%>
<%--%>--%>
<%--<html>--%>
<%--  <head>--%>
<%--    <title>Test</title>--%>
<%--  </head>--%>
<%--  <body>--%>
<%--  <h3>객체의 프로퍼티 값 변경</h3>--%>
<%--  ${member.name}<br>--%>
<%--  <c:set target="${member}" property="name" value="임꺽정"/>--%>
<%--  ${member.name}<br>--%>
<%--  <h3>보관소에 저장된 값 제거</h3>--%>
<%--  <% request.setAttribute("username1", "홍길동"); %>--%>
<%--  \${username1} = ${username1}<br>--%>
<%--  <c:remove var="username1"/>--%>
<%--  \${username1} = ${username1}<br>--%>
<%--  <h3>c:if 태그</h3>--%>
<%--  <c:if test="${10 > 20}" var="result1">--%>
<%--    10은 20보다 크다.<br>--%>
<%--  </c:if>--%>
<%--  ${result1}<br>--%>
<%--  <c:if test="${10 < 20}" var="result2">--%>
<%--    10은 20보다 작다.<br>--%>
<%--  </c:if>--%>
<%--  ${result2}<br>--%>
<%--  <h3>c:choose 태그</h3>--%>
<%--  <c:set var="userid" value="admin"/>--%>
<%--  <c:choose>--%>
<%--    <c:when test="${userid == 'hong'}">--%>
<%--      홍길동님 반값습니다.--%>
<%--    </c:when>--%>
<%--    <c:when test="${userid == 'leem'}">--%>
<%--      임꺽정님 반값습니다.--%>
<%--    </c:when>--%>
<%--    <c:when test="${userid == 'admin'}">--%>
<%--      관리자 전용 페이지 입니다.--%>
<%--    </c:when>--%>
<%--    <c:otherwise>--%>
<%--      등록되지 않은 사용자입니다.--%>
<%--    </c:otherwise>--%>
<%--  </c:choose>--%>
<%--  <h3>c:forEach 태그</h3>--%>
<%--  <%--%>
<%--    session.setAttribute("nameList",--%>
<%--            new String[]{"홍길동", "임꺽정", "일지매"});--%>
<%--  %>--%>
<%--  <ul>--%>
<%--    <c:forEach var="name" items="${nameList}">--%>
<%--      <li>${name}</li>--%>
<%--    </c:forEach>--%>
<%--  </ul>--%>
<%--  <ul>--%>
<%--    <c:forEach var="no" begin="1" end="6">--%>
<%--      <li><a href="jstl0${no}.jsp">JSTL 예제 ${no}</a></li>--%>
<%--    </c:forEach>--%>
<%--  </ul>--%>
<%--  <h3>c:forTokens 태그</h3>--%>
<%--  <%--%>
<%--    request.setAttribute("tokens", "v1=20&v2=30&op=+");--%>
<%--  %>--%>
<%--  <ul>--%>
<%--    <c:forTokens var="item" items="${tokens}" delims="&">--%>
<%--      <li>${item}</li>--%>
<%--    </c:forTokens>--%>
<%--  </ul>--%>
<%--  <h3>c:url 태그</h3>--%>
<%--  <c:url var="calcUrl" value="http://localhost:8080/calc/Calculator.jsp">--%>
<%--    <c:param name="v1" value="20"/>--%>
<%--    <c:param name="v2" value="30"/>--%>
<%--    <c:param name="op" value="+"/>--%>
<%--  </c:url>--%>
<%--  <a href="${calcUrl}">계산하기</a>--%>
<%--  <h3>c:import 태그</h3>--%>
<%--  <textarea rows="10" cols="80">--%>
<%--    <c:import url="http://www.zdnet.co.kr/Include2/ZDNetKorea_News.xml"/>--%>
<%--  </textarea>--%>
<%--  <h3>fmt:parseDate 태그</h3>--%>
<%--  <fmt:parseDate var="date1" value="2013-11-16" pattern="yyyy-MM-dd"/>--%>
<%--  날짜: ${date1}<br>--%>
<%--  <h3>fmt:formatDate 태그</h3>--%>
<%--  <fmt:formatDate var="date2" value="${date1}" pattern="MM/dd/yy"/>--%>
<%--  날짜: ${date2}--%>
<%--  </body>--%>
<%--</html>--%>
