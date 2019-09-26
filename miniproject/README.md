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



