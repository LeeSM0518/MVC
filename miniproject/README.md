# 06. 미니 MVC 프레임워크 만들기

디자인 패턴과 라이브러리를 적용하여 MVC 프레임워크를 직접 만들어보자. 프레임워크를 만드는 과정 속에서 디자인 패턴의 응용과 오픈 소스 라이브러리의 사용, 자바 리플렉션 API와 애노테이션을 사용하는 방법을 익히게 될 것이다.

<br>

# 6.1 프런트 컨트롤러의 도입

**'프런트 컨트롤러**' 를 통해 요청 데이터를 처리하는 코드나 모델과 뷰를 제어하는 **코드가 중복되는 경우를 해결해보자.**

<br>

## 6.1.1. 프런트 컨트롤러 패턴

* **프런트 컨트롤러를 적용한 MVC 구조도와 클라이언트 요청 처리 절차**

  <img src="../capture/스크린샷 2019-09-25 오후 9.34.50.png">

  1. 웹 브라우저의 요청을 프런트 컨트롤러에서 받는다. 프런트 컨트롤러는 **VO 객체를 생성하여** 데이터를 담는다. 그리고 **ServletRequest 보관함에 VO 객체를 저장한다.** 요청 URL에 따라 **페이지 컨트롤러를 선택하여 실행 위임.**
  2. 페이지 컨트롤러는 **DAO를 사용하여 프런트 컨트롤러부터 받은 VO 객체를 처리**
  3. **DAO는** 페이지 컨트롤러로부터 받은 **데이터 처리**
  4. 페이지 컨트롤러는 화면을 만들 때 사용할 **데이터 준비.** 그리고 JSP가 사용할 수 있도록 **ServletRequest 보관소에 저장.** 프턴트 컨트롤러에게 **화면 출력을 담당할 뷰 정보(JSP의 URL)를 반환**
  5. 프런트 컨트롤러는 페이지 컨트롤러가 알려준 **JSP로 실행을 위임. 오류 발생시에는 '/Error.jsp'로 실행 위임.**
  6. **JSP는** 페이지 컨트롤러에서 준비한 데이터를 가지고 **화면을 생성하여 출력.** 프런트 컨트롤러는 웹 브라우저의 요청에 대한 응답을 완료.

  <br>

기존에 서블릿이 단독으로 하던 작업을 **프런트 컨트롤러와 페이지 컨트롤러로** 이렇게 두 개의 역할로 나누어 수행한다.

* **프런트 컨트롤러**
  * VO 객체의 준비
  * 뷰 컴포넌트로의 위임
  * 오류 처리
* **페이지 컨트롤러**
  * 요청 페이지만을 위한 작업 수행

<br>

### *디자인 패턴*

개발자는 늘 **인스턴스의 생성과 소멸에** 대해 관심을 가지고 **시스템 성능을 높일 수 있는 방향으로** 구현해야 한다. 또한 **중복 작업을 최소화하여 유지보수를 좋게** 만드는 방법을 찾아야 한다.

**디자인 패턴은** 이미 실무에서 사용되고 검증된 방법이기 때문에 시스템 개발에 디자인 패턴을 활용하면 시행착오를 최소화할 수 있다.

<br>

### *프레임워크*

**프레임워크(Framework)는** 디자인 패턴을 적용해 만든 시스템 중에서 우수 사례(Best Practice)를 모아 하나의 개발 툴로 표쥰화시킨 것이다.

* **스프링 프레임워크** : 범용 시스템 개발에 사용
* **MyBatis** : 데이터베이스 연동을 쉽게 해주는 프레임워크

<br>

## 6.1.2. 프런트 컨트롤러를 통해 회원 목록 출력

**프런트 컨트롤러가 적용된 MVC 구동 시나리오**

<img src="../capture/스크린샷 2019-09-25 오후 9.56.26.png">

1. 웹 브라우저는 '/member/list.do'를 **요청한다.**
2. 프런트 컨트롤러 'DispatcherServlet'은 클라이언트가 요청한 **서블릿으로 실행 위임**
3. 페이지 컨트롤러 'MemberListServlet'은 **MemberDao에게 회원 목록 요청**
4. MemberDao는 데이터베이스로부터 회원 정보를 가져와서 **Member 객체에 담는다. 그리고 Member 목록 반환**
5. 페이지 컨트롤러는 **Member 목록을** JSP에서 사용할 수 있도록 **ServletRequest 보관소에 저장**
6. 페이지 컨트롤러는 **URL 정보를 보관소에 저장**
7. 프런트 컨트롤러는 **보관소에서 URL 정보를 꺼내 해당 JSP로 실행 위임**
8. MemberList.jsp는 **ServletRequest 보관소에 저장된 Member 목록을 꺼낸다.**
9. MemberList.jsp는 Member 객체들의 데이터를 이용하여 **회원 목록 화면을 출력한다.**
10. 응답 완료

  <br>

## 6.1.3. 프런트 컨트롤러 만들기

* **src/spms/servlets/DispatcherServlet.java**

  ```java
  // 프런트 컨트롤러도 서블릿이기 때문에 HttpServlet 을 상속받는다.
  @WebServlet("*.do")
  public class DispatcherServlet extends HttpServlet {
  
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.setContentType("text/html; charset=UTF-8");
      String servletPath = req.getServletPath();
  
      try {
        String pageControllerPath = null;
  
        if ("/member/list.do".equals(servletPath)) {
          pageControllerPath = "/member/list";
        } else if ("/member/add.do".equals(servletPath)) {
          pageControllerPath = "/member/add";
          if (req.getParameter("email") != null) {
            req.setAttribute("member", new Member()
                .setEmail(req.getParameter("email"))
                .setPassword(req.getParameter("password"))
                .setName(req.getParameter("name")));
          }
        } else if ("/member/update.do".equals(servletPath)) {
          pageControllerPath = "/member/update";
          if (req.getParameter("email") != null) {
            req.setAttribute("member", new Member()
                .setNo(Integer.parseInt(req.getParameter("no")))
                .setEmail(req.getParameter("email"))
                .setName(req.getParameter("name")));
          }
        } else if ("/member/delete.do".equals(servletPath)) {
          pageControllerPath = "/member/delete";
        } else if ("/auth/login.do".equals(servletPath)) {
          pageControllerPath = "/auth/login";
        } else if ("/auth/logout.do".equals(servletPath)) {
          pageControllerPath = "/auth/logout";
        }
  
        RequestDispatcher rd = req.getRequestDispatcher(pageControllerPath);
  
        rd.include(req, resp);
        String viewUrl = (String) req.getAttribute("viewUrl");
  
        if (viewUrl.startsWith("redirect:")) {
          resp.sendRedirect(viewUrl.substring(9));
        } else {
          rd = req.getRequestDispatcher(viewUrl);
          rd.include(req, resp);
        }
      } catch (Exception e) {
        e.printStackTrace();
        req.setAttribute("error", e);
        RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
        rd.forward(req, resp);
      }
    }
  }
  ```

* doGet(), doPost() 가 아니라 service() 메서드를 오버라이딩 한다. 이 메서드의 매개 변수는 ServletRequest와 ServletResponse 가 아니라 HttpServletRequest 와 HttpServletResponse 이다.

  ```java
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { ... }
  ```

* **HttpServlet에 추가된 service() 메서드**

  <img src="../capture/스크린샷 2019-09-26 오후 2.31.12.png">

  1. 클라이언트로부터 요청이 들어오면 서블릿 컨테이너는 규칙에 따라 Servlet 인터페이스에 선언된 service() 메서드를 호출한다.
  2. DispatcherServlet은 service(HttpServletRequest, HttpServletResponse) 메서드는 HTTP 요청 프로토콜을 분석하여 다시 doGet(), doPost() 등을 호출한다.

<br>

### *요청 URL에서 서블릿 경로 알아내기*

프런트 컨트롤러의 역할은 클라이언트 요청을 적절한 페이지 컨트롤러에게 전달하는 것이다. 그러려면 클라이언트가 요청한 서블릿의 URL을 알아내야 한다.

* **URL 예시**

  ```url
  http://localhost:9999/web06/member/list.do?pageNo=1&pageSize=10
  ```

* **URL에서 특정 정보만 추출하는 HttpServletRequest의 메서드들**

  | 메서드           | 설명                   | 반환값                                     |
  | ---------------- | ---------------------- | ------------------------------------------ |
  | getRequestURL()  | 요청 URL 리턴          | http://localhost:9999/web06/member/list.do |
  | getRequestURI()  | 서버 주소를 제외한 URL | /web06/member/list.do                      |
  | getContextPath() | 웹 애플리케이션 경로   | /web06                                     |
  | getServletPath() | 서블릿 경로            | /member/list.do                            |
  | getQueryString() | 요청 매개변수 정보     | pageNo=1&pageSize=10                       |

* 클라이언트가 요청한 **서블릿의 경로를 알고 싶으면 getServletPath()를 호출하면 된다.** DispatcherServlet 클래스에서도 서블릿의 경로를 알아내고자 이 메서드를 사용하였다.

  ```java
  String servletPath = request.getServletPath();
  ```

<br>

### *페이지 컨트롤러로 위임*

서블릿 경로에 따라 조건문을 사용하여 적절한 페이지 컨트롤러를 인클루딩 한다.

```java
String pageControllerPath = null;

if ("/member/list.do".equals(servletPath)) {
  pageControllerPath = "/member/list";
} else if ("/member/add.do".equals(servletPath)) {
  pageControllerPath = "/member/add";
  if (req.getParameter("email") != null) {
    req.setAttribute("member", new Member()
                     .setEmail(req.getParameter("email"))
                     .setPassword(req.getParameter("password"))
                     .setName(req.getParameter("name")));
  }
} else if ("/member/update.do".equals(servletPath)) {
  pageControllerPath = "/member/update";
  if (req.getParameter("email") != null) {
    req.setAttribute("member", new Member()
                     .setNo(Integer.parseInt(req.getParameter("no")))
                     .setEmail(req.getParameter("email"))
                     .setName(req.getParameter("name")));
  }
} else if ("/member/delete.do".equals(servletPath)) {
  pageControllerPath = "/member/delete";
} else if ("/auth/login.do".equals(servletPath)) {
  pageControllerPath = "/auth/login";
} else if ("/auth/logout.do".equals(servletPath)) {
  pageControllerPath = "/auth/logout";
}

RequestDispatcher rd = req.getRequestDispatcher(pageControllerPath);

rd.include(req, resp);
```

<br>

### *요청 매개변수로부터 VO 객체 준비*

프런트 컨트롤러의 역할 중 하나는 페이지 컨트롤러가 필요한 데이터를 미리 준비하는 것이다.

회원 정보 등록과 변경은 사용자가 입력한 데이터를 페이지 컨트롤러에게 전달하기 위해, 요청 **매개변수의 값을 꺼내서 VO 객체에 담고, "member" 라는 키로 ServletRequest에 보관하였다.**

```java
req.setAttribute("member", new Member()
                 .setEmail(req.getParameter("email"))
                 .setPassword(req.getParameter("password"))
                 .setName(req.getParameter("name")));
```

> 쓸데없는 임시 변수의 사용을 최소화하기 위해 Member 객체를 생성한 후, 바로 값을 할당하고 ,ServletRequest에 보관한다.

<br>

### *JSP로 위임*

페이지 컨트롤러의 실행이 끝나면, 화면 출력을 위해 **ServletRequest에 보관된 뷰 URL로 실행을 위임한다.** 

단 뷰 URL이 "redirect:" 로 시작한다면, 인클루딩 하는 대신에 sendRedirect()를 호출한다.

```java
String viewUrl = (String) req.getAttribute("viewUrl");
if (viewUrl.startsWith("redirect:")) {
  resp.sendRedirect(viewUrl.substring(9));
} else {
  rd = req.getRequestDispatcher(viewUrl);
  rd.include(req, resp);
}
```

<br>

### *오류 처리*

서블릿을 만들 때 매번 작성한 것 중의 하나가 오류 처리다. 이제 **프런트 컨트롤러에서 오류 처리를 담당하기 때문에, 페이지 컨트롤러를 작성할 때는 오류 처리 코드를 넣을 필요가 없다.**

```java
try {
  ...
} catch (Exception e) {
  e.printStackTrace();
  req.setAttribute("error", e);
  RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
  rd.forward(req, resp);
}
```

<br>

### *프런트 컨트롤러의 배치*

**@WebServlet 애노테이션을 사용하여 프런트 컨트롤러의 배치 URL을 "*.do" 로 지정한다.** 

즉 클라이언트의 요청 중에서 서블릿 경로 이름이 .do로 끝나는 경우는 DispatcherServlet이 처리하겠다는 의미이다.

```java
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
```

<br>

## 6.1.4. MemberListServlet을 페이지 컨트롤러로 만들기

* **src/spms/servlets/MemberListServlet.java**

  ```java
  @WebServlet("/member/list")
  public class MemberListServlet extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
        ServletContext sc = this.getServletContext();
        MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
        req.setAttribute("members", memberDao.selectList());
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }
    
  }
  ```

  * 응답 데이터의 문자 집합은 프런트 컨트롤러에서 설정했으므로 설정 코드 제거

    ```java
    // response.setContentType("text/html; charset=UTF-8");
    ```

  * JSP 실행 위임 코드도 제거

    ```java
    // RequestDispatcher rd = request.getRequestDispatcher(
    //   "/member/MemberList.jsp");
    // rd.include(req, resp);
    ```

    대신 JSP URL 정보를 프런트 컨트롤러에게 알려주고자 **ServletRequest 보관소에 저장**

    ```java
    request.setAttribute("viewUrl", "/member/MemberList.jsp");
    ```

  * 오류가 발생했을 때 오류 처리 페이지(/Error.jsp)로 실행을 위임하는 작업도 프런트 컨트롤러가 하기 때문에 다음 코드를 제거한다.

    ```java
    // e.printStackTrace();
    // req.setAttribute("error", e);
    // RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
    // rd.forward(req, resp);
    ```

    대신 Dao를 실행하다가 오류가 발생한다면, 기존의 오류를 ServletException 객체에 담아서 던지도록 하였다.

    ```
    throw new ServletException(e);
    ```

    > service() 메서드는 ServletException을 던지도록 선언되어 있기 때문에 기존의 예외 객체를 그냥 던질 수 없다. 그래서 ServletException 객체를 생성해야 한다.

  <br>

  ## 6.1.5. 프런트 컨트롤러를 통한 회원 목록 페이지 요청

  1. **web/WEB-INF에 lib를 넣어 오류가 안나도록 한다.**

  2. **http://localhost:8080/member/list.do 로 접속한다.**

  3. **실행 결과**

     <img src="../capture/스크린샷 2019-09-26 오후 4.29.32.png" width=500>

  <br>

### **.do 요청*

/member/list.do 요청을 처리할 페이지 컨트롤러(MemberListServlet)을 찾아 실행을 위임한다.

* ***.do 요청을 처리하는 프런트 컨트롤러**

  <img src="../capture/스크린샷 2019-09-26 오후 4.41.38.png">

<br>

### *회원 목록 페이지에 있는 링크의 URL에 .do 접미사 붙이기*

회원 목록 페이지에 있는 **URL에 .do 를 붙이자**

* **/web/member/MemberList.jsp**

  ```jsp
  <h1>회원 목록</h1>
  <p><a href="add.do">신규 회원</a></p>
  <c:forEach var="member" items="${members}">
    ${member.no},
    <a href="update.do?no=${member.no}">${member.name}</a>,
    ${member.email},
    ${member.createDate}
    <a href="delete.do?no=${member.no}">[삭제]</a><br>
  </c:forEach>
  <jsp:include page="/Tail.jsp"/>
  </body>
  </html>
  ```

<br>

## 6.1.6. MemberAddServlet을 페이지 컨트롤러로 만들기

회원 등록 서블릿에 대해 프런트 컨트롤러를 적용해 보자.

* **/src/spms/servlets/MemberAddServlet.java**

  ```java
  @WebServlet("/member/add")
  public class MemberAddServlet extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      req.setAttribute("viewUrl", "/member/MemberAdd.jsp");
    }
  
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
        ServletContext sc = this.getServletContext();
        MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
  
        Member member = (Member) req.getAttribute("member");
        memberDao.insert(member);
        
        req.setAttribute("viewUrl", "redirect:list.do");
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }
    
  }
  ```

<br>

### *뷰를 포워딩하는 코드를 제거*

GET 요청을 처리하는 doGet() 메서드에서 MemberForm.jsp로 포워딩하는 코드를 제거한다. 대신 **MemberForm.jsp의 URL을 ServletRequest에 저장한다.**

```java
// RequestDispatcher rd = request.getRequestDispatcher("/member/MemberFrom.jsp");
// rd.forward(request, response);
request.setAttribute("viewUrl", "/member/MemberAdd.jsp");
```

<br>

### *요청 매개변수의 값을 꺼내는 코드를 제거*

클라이언트가 보낸 회원 정보를 꺼내기 위해 getParameter()를 호출하는 대신, **프런트 컨트롤러가 준비해 놓은 Member 객체를 ServletRequest 보관소에서 꺼내도록 변경한다.**

```java
// memberDao.insert(new Member()
//                 .setEmail(request.getParameter("email"))
//                 .setPassword(request.getParameter("password"))
//                 .setName(request.getParameter("name")));
Member member = (Member) request.getAttribute("member");
memberDao.insert(member);
```

<br>

### *리다이렉트를 위한 뷰 URL 설정*

기존 코드를 제거하고 대신에 ServletRequest에 리다이렉트 URL을 저장한다.

```java
// response.sendRedirect("list");
request.setAttribute("viewUrl", "redirect:list.do");
```

> 뷰 URL이 "redirect:" 문자열로 시작할 경우, 프런트 컨트롤러는 그 URL로 리다이렉트 한다.

<br>

### *오류 처리 코드 제거*

오류 처리 JSP로 실행을 위임하는 코드를 제거하고, 그 자리에 ServletException을 던지는 코드를 넣는다.

```java
// e.printStackTrace();
// request.setAttribute("error", e);
// RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
// rd.forward(req, resp);
throw new ServletException(e);
```

<br>

### *회원 등록 폼의 action URL에 .do 붙이기*

- **web/member/MemberAdd.jsp**

  ```jsp
  <h1>회원 등록</h1>
  <form action="add.do" method="post">
    이름: <input type="text" name="name"><br>
    이메일: <input type="text" name="email"><br>
    암호: <input type="password" name="password"><br>
    <input type="submit" value="추가">
    <input type="reset" value="취소">
  </form>
  </body>
  </html>
  ```

<br>

## 6.1.7. 회원 등록 테스트

1. **localhost:8080/member/list.do 에 접속**

<img src="../capture/스크린샷 2019-09-26 오후 5.14.05.png" width=500>

2. **"신규 회원" 클릭**

<img src="../capture/스크린샷 2019-09-26 오후 5.14.47.png" width=500>

3. **회원 등록 확인**

<img src="../capture/스크린샷 2019-09-26 오후 5.15.48.png" width=500>

<br>

# 실력 향상 과제

1. **spms.servlets.MemberUpdateServlet을 페이지 컨트롤러로 변경하세요.**

   ```java
   @WebServlet("/member/update")
   public class MemberUpdateServlet extends HttpServlet {
   
     @Override
     protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       ServletContext sc = this.getServletContext();
       MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
       try {
         // 프런트 컨트롤러가 저장한 VO 객체를 이용
         req.setAttribute("member", memberDao.selectOne(
           Integer.parseInt(req.getParameter("no"))));
         // 프런트 컨트롤러에게 뷰 정보를 전달
         req.setAttribute("viewUrl", "/member/MemberUpdate.jsp");
       } catch (Exception e) {
         throw new ServletException(e);
       }
     }
   
     @Override
     protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       ServletContext sc = this.getServletContext();
       try {
         Member member = (Member) req.getAttribute("member");
         MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
         memberDao.update(member);
   
         // 프런트 컨트롤러에게 list로 리다이렉트 요청 url 전달
         req.setAttribute("viewUrl", "redirect:list.do");
       } catch (Exception e) {
         throw new ServletException(e);
       }
     }
   
   }
   ```

   <br>

2. **Member/MemberUpdate.jsp 페이지에 있는 링크 또는 폼의 URL에 .do를 붙이세요.**

   ```jsp
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
   ```

   <br>

3. **spms.servlets.MemberDeleteServlet을 페이지 컨트롤러로 변경하세요.**

   ```java
   @WebServlet("/member/delete")
   public class MemberDeleteServlet extends HttpServlet {
   
     @Override
     protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       ServletContext sc = this.getServletContext();
       try {
         MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
         // 프론트 컨트롤러가 저장한 "no"을 불러온다.
         memberDao.delete(Integer.parseInt(req.getParameter("no")));
         // list 로 리다이렉트 요청 URL 저장
         req.setAttribute("viewUrl", "redirect:list.do");
       } catch (Exception e) {
         throw new ServletException(e);
       }
     }
   
   }
   ```

   <br>

4. **spms.servlets.LogInServlet을 페이지 컨트롤러로 변경하세요.**

   ```java
   @WebServlet("/auth/login")
   public class LogInServlet extends HttpServlet {
   
     @Override
     protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       // 로그인 URL 저장
       req.setAttribute("viewUrl", "/auth/LogInForm.jsp");
     }
   
     @Override
     protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       ServletContext sc = this.getServletContext();
   
       try {
         MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
         Member member = memberDao.exist(req.getParameter("email"), req.getParameter("password"));
         if (member != null) {
           req.setAttribute("member", member);
           // 로그인 정보 세션에 저장
           HttpSession session = req.getSession();
           session.setAttribute("member", member);
           // list 로 리다이렉트 요청 저장
           req.setAttribute("viewUrl", "redirect:/member/list.do");
         } else {
           // 로그인 실패로 URL 저장
           req.setAttribute("viewUrl", "/auth/LogInFail.jsp");
         }
       } catch (Exception e) {
         throw new ServletException(e);
       }
     }
   
   }
   ```

   <br>

5. **auth/LogInForm.jsp 페이지에 있는 링크 또는 폼의 URL에 .do를 붙이세요.**

   ```jsp
   <%@ page contentType="text/html;charset=UTF-8" language="java" %>
   <html>
     <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
       <title>로그인</title>
     </head>
     <body>
       <h2>사용자 로그인</h2>
       <%-- URL에 .do 붙임 --%>
       <form action="login.do" method="post">
         이메일: <input type="text" name="email"><br>
         암호: <input type="password" name="password"><br>
         <input type="submit" value="로그인">
       </form>
     </body>
   </html>
   ```

   <br>

6. **Auth/LogInFail.jsp 페이지에 있는 링크 또는 폼의 URLdp .do를 붙이세요.**

   ```jsp
   <%@ page contentType="text/html;charset=UTF-8" language="java" %>
   <html>
     <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
       <meta http-equiv="Refresh" content="1;url=login.do">
       <title>로그인 실패</title>
     </head>
     <body>
       <p>로그인 실패입니다. 이메일 또는 암호가 맞지 않습니다!<br>
         잠시 후에 다시 로그인 화면으로 갑니다.</p>
     </body>
   </html>
   ```

   <br>

7. **spms.servlets.LogOutServlet을 페이지 컨트롤러로 변경하세요.**

   ```java
   @WebServlet("/auth/logout")
   public class LogOutServlet extends HttpServlet {
   
     @Override
     protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       // 회원 정보 삭제를 위한 세션 초기화
       HttpSession session = req.getSession();
       session.invalidate();
   
       // 로그인 url로 리다이렉트 요청 저장
       req.setAttribute("viewUrl", "redirect:login.do");
     }
     
   }
   ```

   <br>

8. **Header.jsp 페이지에 있는 링크 또는 폼의 URL에 .do를 붙이세요. 또한 기존에 \<jsp:useBean> 및 \<% %>, \<%= %> 태그를 EL 태그로 교체하세요.**

   ```jsp
   <%@ page contentType="text/html;charset=UTF-8" language="java" %>
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
   <div style="background-color: #00008b; color: #ffffff; height: 20px; padding: 5px;">
     SPMS(Simple Project Management System)
     <span style="float: right;">
       ${member.name}
       <a style="color: white;"
          href="${request.getContextPath()}/auth/logout.do">로그아웃</a>
     </span>
   </div>
   ```

<br>

# 6.2. 페이지 컨트롤러의 진화

프런트 컨트롤러를 도입하면 페이지 컨트롤러를 굳이 서블릿으로 만들어야 할 이유가 없다.

**일반 클래스로 만들면 서블릿 기술에 종속되지 않기 때문에 재사용성이 더 높아진다.**

<br>

## 6.2.1. 프런트 컨트롤러와 페이지 컨트롤러의 호출 규칙 정의

프런트 컨트롤러가 페이지 컨트롤러를 일관성 있게 사용하려면, 호출 규칙을 정의해야 한다.

- **프런트 컨트롤러와 페이지 컨트롤러의 호출 규칙**

  <img src="../capture/스크린샷 2019-09-27 오전 1.56.00.png">

  - 호출 규칙을 정의해 두면 그 규칙에 따라 클래스를 작성하고 호출하면 되기 때문에 일광성을 확보하고 유지보수에 도움이 된다.
  - web.xml 파일에 등록할 필요가 없다.

<br>

## 6.2.2. 호출 규칙 정의

프런트 컨트롤러와 페이지 컨트롤러 사이의 호출 규칙을 정의하기 위해서는 **인터페이스를** 사용해야 한다.

**인터페이스는 사용자와 피사용자 사이의 일관성 있는 사용을 보장하기 위해** 만든 자바 문법이다.

<img src="../capture/스크린샷 2019-09-27 오전 11.17.04.png">

<br>

서블릿으로 작성된 모든 페이지 컨트롤러를 프런트 컨트롤러의 호출 규칙에 맞추어 일반 클래스로 전환한다.

### *일반 클래스로 만든 페이지 컨트롤러의 사용 시나리오*

<img src="../capture/스크린샷 2019-09-28 오후 3.54.42.png">



1. 웹 브라우저가 회원 목록 페이지 요청
2. **프런트 컨트롤러는 회원 목록 요청 처리를 담당하는 페이지 컨트롤러를 호출. 이때 데이터 Map 객체 넘김**
3. 페이지 컨트롤러는 Dao에게 회원 목록 데이터를 요청
4. Dao는 데이터베이스로부터 회원 목록 데이터를 가져와, Member 객체에 담아 반환
5. 페이지 컨트롤러는 Dao가 반환한 회원 목록 데이터를 Map 객체에 저장. 그 후, 프런트 컨트롤러에게 뷰 URL 반환
6. 프런트 컨트롤러는 Map 객체에 저장된 페이지 컨트롤러의 작업 결과물을 JSP가 사용할 수 있도록 ServletRequest로 옮김
7. 인클루딩

<br>

## 6.2.3. 페이지 컨트롤러를 위한 인터페이스 정의

* **src/spms/Controller.java**

  ```java
  public interface Controller {
  
    String execute(Map<String, Object> model) throws Exception;
  
  }
  ```

  * **execute()** : 프런트 컨트롤러가 페이지 컨트롤러에게 일을 시키기 위해 호출하는 메서드이다.

<br>

## 6.2.4. 페이지 컨트롤러 MemberListServlet을 일반 클래스로 전환

회원 목록을 처리하는 페이지 컨트롤러를 일반 클래스로 전환해보자.

* **src/spms/controls/MemberListController.java**

  ```java
  public class MemberListController implements Controller {
  
    @Override
    public String execute(Map<String, Object> model) throws Exception {
      MemberDao memberDao = (MemberDao) model.get("memberDao");
      model.put("members", memberDao.selectList());
      return "/member/MemberList.jsp";
    }
    
  }
  ```

<br>

### *Controller 인터페이스의 구현*

페이지 컨트롤러가 되려면 Controller 규칙에 따라 클래스를 작성해야 한다.

```java
public class MemberListController implements Controller {
  public String execute(Map<String, Object> model) throws Exception {
```

* 예외가 발생했을 때 호출자인 프런트 컨트롤러에게 던지면 된다.

<br>

### *페이지 컨트롤러에게 사용할 객체를 Map에서 꺼내기*

MemberDao 객체는 프런트 컨트롤러가 넘겨준 **'Map 객체(model)'** 에 들어 있다.

```java
MemberDao memberDao = (MemberDao)model.get("memberDao");
```

<br>

### *페이지 컨트롤러가 작업한 결과물을 Map에 담기*

**Map 객체 'model' 매개변수는** 페이지 컨트롤러가 작업한 **결과를 담을 때도 사용한다.**

```java
model.put("members", memberDao.selectList());
```

* Map 객체에 저장된 값은 프런트 컨트롤러가 꺼내서 **ServletReqeust 보관소로 옮긴다.**

<br>

### *뷰 URL 반환*

페이지 컨트롤러의 반환값은 화면을 출력할 JSP의 URL이다.

```java
return "/member/MemberList.jsp";
```

<br>

## 6.2.5. 프런트 컨트롤러 변경

Controller 규칙에 따라 페이지 컨트롤러를 호출하도록 프런트 컨트롤러를 변경하자.

* **spms/servlets/DispatcherServlet.java**

  ```java
  ...
  try {
    ServletContext sc = this.getServletContext();
  
    HashMap<String, Object> model = new HashMap<>();
    model.put("memberDao", sc.getAttribute("memberDao"));
  
    String pageControllerPath = null;
    Controller pageController = null;
  
    if ("/member/list.do".equals(servletPath)) {
      pageController = new MemberListController();
    } else if ("/member/add.do".equals(servletPath)) {
      ...
    } else if ("/member/update.do".equals(servletPath)) {
      ...
    } else if ("/member/delete.do".equals(servletPath)) {
      ...
    } else if ("/auth/login.do".equals(servletPath)) {
      ...
    } else if ("/auth/logout.do".equals(servletPath)) {
      ...
    }
  
    String viewUrl = pageController.execute(model);
  
    for (String key : model.keySet())
      req.setAttribute(key, model.get(key));
  
    if (viewUrl.startsWith("redirect:")) {
      resp.sendRedirect(viewUrl.substring(9));
    } else {
      RequestDispatcher rd = req.getRequestDispatcher(viewUrl);
      rd.include(req, resp);
    }
  } catch (Exception e) { ...
  ```

<br>

### *Map 객체 준비*

프런트 컨트롤러와 페이지 컨트롤러 사이에 데이터나 객체를 주고 받을 때 사용할 **Map 객체를 준비한다.**

```java
ServletContext sc = this.getServletContext();
HashMap<String, Object> model = new HashMap<>();
model.put("memberDao", sc.getAttribute("memberDao"));
```

* 회원 목록을 가져오기 위해 **MemberDao를 ServletContext 보관소에서 꺼내서 Map 객체에 담았다.**

<br>

### *회원 목록을 처리할 페이지 컨트롤러 준비*

Controller 인터페이스 타입의 **참조 변수를 선언한다.**

```java
Controller pageController = null;
```

<br>

회원 목록 요청을 처리할 페이지 **컨트롤러를 준비한다.**

```java
if ("/member/list.do".equals(servletPath)) {
  pageController = new MemberListController();
}
```

<br>

### *페이지 컨트롤러의 실행*

이제는 페이지 컨트롤러가 일반 클래스이기 때문에 **메서드를 호출하기만 하면** 된다.

```java
String viewUrl = pageController.execute(model);
```

* execute()를 호출할 때 페이지 컨트롤러를 위해 준비한 **Map 객체를 매개변수로** 넘김.

<br>

### *Map 객체에 저장된 값을 ServletRequest에 복사*

Map 객체는 페이지 컨트롤러의 실행 결과물을 받을 때도 사용한다. 따라서 **Map 객체에 보관되어 있는 데이터나 객체를 JSP가 사용할 수 있도록 ServletRequest에 복사한다.**

```java
for (String key : model.keySet()) {
  req.setAttribute(key, model.get(key));
}
```

<br>

### *뷰로 실행 위임*

나머지 코드는 이전과 같이 JSP로 실행을 위임하거나 리다이렉트 코드이다.

<br>

## 6.2.6. 회원 등록 페이지 컨트롤러에 Controller 규칙 적용하기

* **spms/controls/MemberAddController.java**

  ```java
  public class MemberAddController implements Controller {
    
    @Override
    public String execute(Map<String, Object> model) throws Exception {
      // 입력폼을 요청할 때
      if (model.get("member") == null) {
        return "/member/MemberAdd.jsp";
      } else {  // 회원 등록을 요청할 때
        MemberDao memberDao = (MemberDao) model.get("memberDao");
  
        Member member = (Member) model.get("member");
        memberDao.insert(member);
        
        // 데이터 저장 후 회원 목록 페이지로 리다이렉트 할 수 있도록 
        // 반환 URL 앞에 "redirect:" 를 붙인다.
        return "redirect:list.do";
      }
    }
    
  }
  ```

  * Map 객체에 VO 객체 **"Member"가 들어 있으면 POST** 요청으로 간주하고, **그렇지 않으면 GET** 요청으로 간주한다.

<br>

## 6.2.7. 회원 등록 요청을 처리하기 위해 DispatcherServlet 변경

* **spms/src/servlets/DispatcherServlet.java**

  ```java
  ...
  if ("/member/list.do".equals(servletPath)) {
    pageController = new MemberListController();
  } else if ("/member/add.do".equals(servletPath)) {
    pageController = new MemberAddController();
    if (req.getParameter("email") != null) {
      model.put("member", new Member()
                .setEmail(req.getParameter("email"))
                .setPassword(req.getParameter("password"))
                .setName(req.getParameter("name")));
    }
  }
  ...
  ```

  * 사용자가 입력한 데이터에 대해 Member 객체를 만든 후 Map 객체에 담는다.

<br>

# 실력 향상 과제

### *1.MemberUpdateController를 작성하세요.*

spms/controls/MemberUpdateController.java

```java
public class MemberUpdateController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    MemberDao memberDao = (MemberDao) model.get("memberDao");
    Member member;
    if (model.get("member") == null) {
      // model로 부터 수정할 멤버 객체 번호를 가져와서 데이터베이스에서 조회
      member = memberDao.selectOne(Integer.parseInt(String.valueOf(model.get("no"))));
      // 맵 객체에 저장
      model.put("member", member);
      // 업데이트 URL 반환
      return "/member/MemberUpdate.jsp";
    } else {
      // member 객체 가져와서 Dao를 통해 업데이트 요청
      member = (Member) model.get("member");
      memberDao.update(member);

      // 회원 목록 URL로 리다이렉트 요청 URL 반환
      return "redirect:list.do";
    }
  }

}
```

<br>

### *2.MemberDeleteController를 작성하세요.*

spms/controls/MemberDeleteController.java

```java
public class MemberDeleteController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    // 맵 객체로 부터 DAO 와 no 를 가져와서 DAO의 메소드를 호출해
    // 회원 삭제
    MemberDao memberDao = (MemberDao) model.get("memberDao");
    memberDao.delete(Integer.parseInt(String.valueOf(model.get("no"))));
    // 회원 목록으로 리다이렉트 URL 반환
    return "redirect:list.do";
  }

}
```

<br>

### *3.LogInController를 작성하세요.*

spms/controls/LogInController.java

```java
public class LogInController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    // 맵 객체에 member가 존재하지 않을시, 로그인 URL 반환
    if (model.get("member") == null) {
      return "/auth/LogInForm.jsp";
    } 
    // 존재하면, 맵 객체로부터 DAO, member, Session 객체를 가져와서
    // 멤버가 존재하는지 확인한 뒤, Session에 보관하고 회원 목록으로 리다이렉트 하는 URL을 반환하거나
    // 로그인 실패 URL을 반환한다.
    else {
      MemberDao memberDao = (MemberDao) model.get("memberDao");
      Member member = (Member) model.get("member");
      Member existMember = memberDao.exist(member.getEmail(), member.getPassword());
      if (existMember != null) {
        ((HttpSession) model.get("session"))
            .setAttribute("member", existMember);
        return "redirect:/member/list.do";
      } else {
        return "/auth/LogInFail.jsp";
      }
    }
  }

}
```

<br>

### *4.LogOutController를 작성하세요.*

spms/controls/LogOutController.java

```java
public class LogOutController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    // 맵 객체로 부터 Session 객체를 가져와서 초기화 해준뒤
    // 로그인 화면으로 리다이렉트 하는 URL 반환
    HttpSession session = (HttpSession) model.get("session");
    session.invalidate();
    return "redirect:login.do";
  }

}
```

<br>

### *5.DispatcherServlet을 변경하세요.*

spms/servlets/DispatcherServlet.java

```java
...
} else if ("/member/update.do".equals(servletPath)) {
  pageController = new MemberUpdateController();
  // "no" 정보를 맵 객체에 담는다.
  model.put("no", req.getParameter("no"));
  if (req.getParameter("email") != null) {
    // 맵 객체에 member 객체를 담는다.
    model.put("member", new Member()
              .setNo(Integer.parseInt(req.getParameter("no")))
              .setEmail(req.getParameter("email"))
              .setName(req.getParameter("name")));
  }
} else if ("/member/delete.do".equals(servletPath)) {
  pageController = new MemberDeleteController();
  // 맵 객체에 "no" 정보를 담는다.
  model.put("no", req.getParameter("no"));
} else if ("/auth/login.do".equals(servletPath)) {
  pageController = new LogInController();
  // 맵 객체에 member 객체 정보와 session 객체를 담는다.
  if (req.getParameter("email") != null) {
    model.put("member", new Member()
              .setEmail(req.getParameter("email"))
              .setPassword(req.getParameter("password")));
    model.put("session", req.getSession());
  }
} else if ("/auth/logout.do".equals(servletPath)) {
  pageController = new LogOutController();
  // 맵 객체에 session 객체를 담는다.
  model.put("session", req.getSession());
}
...
```

<br>

# 6.3. DI를 이용한 빈 의존성 관리

MemberListController가 작업을 수행하려면 데이터베이스로부터 회원 정보를 가져다줄 MemberDao가 필요하다. 이렇게 **특정 작업을 수행할 때 사용하는 객체를 '의존 객체' 라고 하고, 이런 관계를 '의존 관계(dependency)' 라고 한다.**

- **의존 관계**

  <img src="../capture/스크린샷 2019-09-30 오전 12.56.40.png">

<br>

## 6.3.1. 의존 객체의 관리

### *의존 객체가 필요하면 즉시 생성*

고전적인 방법은 의존 객체를 사용하는 쪽에서 직접 그 객체를 생성하고 관리하는 것이다.

- **MemberListServlet의 일부 코드**

  ```java
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      ServletContext sc = this.getServletContext();
      Connection conn = (Connection) sc.getAttribute("conn");
      
      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(conn);
      
      request.setAttribute("members", memberDao.selectList());
  ...
  ```

  - 이 방식은 doGet()이 호출될 때마다 MemberDao 객체를 생성하기 때문에 비효율적이다.

<br>

### *의존 객체를 미리 생성해 두었다가 필요할 때 사용*

사용할 객체를 미리 생성해 두고 필요할 때마다 꺼내 쓰는 방식입니다. 

웹 애플리케이션이 **시작될 때 MemberDao 객체를 미리 생성하여 ServletContext에 보관해둔다.**

- **MemberListServlet의 일부 코드**

  ```java
  public void doGet{
    HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
      try {
        ServletContext sc = this.getServletContext();
        MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
        memberDao.setConnection(conn);
        
        request.setAttribute("members", memberDao.selectList());
        ...
  ```

- **MemberListController의 일부 코드**

  ```java
  public String execute(Map<String, Object> model) throws Exception {
    MemberDao memberDao = (MemberDao) model.get("memberDao");
    model.put("members", memberDao.selectList());
    return "/member/MemberList.jsp";
  }
  ```

<br>

