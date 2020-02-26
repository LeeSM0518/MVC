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
  4. 페이지 컨트롤러는 화면을 만들 때 사용할 **데이터 준비.** 그리고 JSP가 사용할 수 있도록 **ServletRequest 보관소에 저장.** 프런트 컨트롤러에게 **화면 출력을 담당할 뷰 정보(JSP의 URL)를 반환**
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

* **이전의 5장에서 구현한 MemberUpdateServlet**

  ```java
  @WebServlet("/member/update")
  public class MemberUpdateServlet extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      ServletContext sc = this.getServletContext();
      Connection conn = (Connection) sc.getAttribute("conn");
  
      try {
        MemberDao memberDao = new MemberDao();
        memberDao.setConnection(conn);
        Member member = memberDao.selectOne(Integer.parseInt(req.getParameter("no")));
        req.setAttribute("updateMember", member);
  
        RequestDispatcher rd = req.getRequestDispatcher("/member/MemberUpdate.jsp");
        rd.forward(req, resp);
      } catch (Exception e) {
        e.printStackTrace();
        req.setAttribute("error", e);
        RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
        rd.forward(req, resp);
      }
    }
  
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      ServletContext sc = this.getServletContext();
      Connection conn = (Connection) sc.getAttribute("conn");
  
      try {
        Member member = new Member();
        member.setEmail(req.getParameter("email"))
            .setName(req.getParameter("name"))
            .setNo(Integer.parseInt(req.getParameter("no")));
  
        MemberDao memberDao = new MemberDao();
        memberDao.setConnection(conn);
        memberDao.update(member);
  
        resp.sendRedirect("list");
      } catch (Exception e) {
        e.printStackTrace();
        req.setAttribute("error", e);
        RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
        rd.forward(req, resp);
      }
    }
  }
  ```

* **프런트 컨트롤러를 사용한 MemberUpdateServlet**

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

> 훨씬 가독성이 좋아진 것을 확인할 수 있다.

<br>

* **src/spms/servlets/DispatcherServlet.java**

  ```java
  // 프런트 컨트롤러도 서블릿이기 때문에 HttpServlet 을 상속받는다.
  // /member/list.do, /member/add.do 와 같이 .do 로 끝나는 모든 URL 요청은 이 서블릿을 거친다.
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
          // get 요청과 post 요청을 구분하기 위한 if문 (예시를 통해 설명 필요)
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

    ```java
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

* **예시) MemberAddServlet**
  1. 처음에 신규 회원 버튼을 누르면 당연히 Member(회원) DTO 정보가 요청 정보에 담겨있지 않다. 원래는 서블릿을 상속해서 GET 메소드와 POST 메소드를 사용했으나 그럴 필요 없이 request에 저장되있는 DTO 객체의 데이터 유무를 통해 GET, POST를 구분할 수 있다.

**일반 클래스로 만들면 서블릿 기술에 종속되지 않기 때문에 재사용성이 더 높아진다.**

<br>

## 6.2.1. 프런트 컨트롤러와 페이지 컨트롤러의 호출 규칙 정의

프런트 컨트롤러가 페이지 컨트롤러를 일관성 있게 사용하려면, 호출 규칙을 정의해야 한다.

- **프런트 컨트롤러와 페이지 컨트롤러의 호출 규칙**

  <img src="../capture/스크린샷 2019-09-27 오전 1.56.00.png">

  - 호출 규칙을 정의해 두면 그 규칙에 따라 클래스를 작성하고 호출하면 되기 때문에 **일광성(객체지향의 다형성 사용)을** 확보하고 유지보수에 도움이 된다.
  - web.xml 파일에 서블릿을 등록할 필요가 없다.

<br>

## 6.2.2. 호출 규칙 정의

프런트 컨트롤러와 페이지 컨트롤러 사이의 호출 규칙을 정의하기 위해서는 **인터페이스를** 사용해야 한다.

**인터페이스는 사용자와 피사용자 사이의 일관성 있는 사용을 보장하기 위해** 만든 자바 문법이다. 즉, **서블릿의 규격을 맞춰주는 작업이다.**

<img src="../capture/스크린샷 2019-09-27 오전 11.17.04.png">

<br>

서블릿으로 작성된 모든 페이지 컨트롤러를 프런트 컨트롤러의 호출 규칙에 맞추어 일반 클래스로 전환한다.

<br>

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

### *의존 객체와의 결합도 증가에 따른 문제*

앞에서 처럼 의존 객체를 직접 생성하거나 보관소에서 꺼내는 방식에는 문제가 있다.

<br>

**코드의 잦은 변경**

<u>의존 객체를 사용하는 쪽과 의존 객체(또는 보관소) 사이의 결합도가 높아져서</u> 의존 객체나 보관소에 변경이 발생하면 바로 영향을 받는다는 것이다.

* **코드의 유지 보수가 어려움**

  <img src="../capture/스크린샷 2019-10-01 오후 10.18.41.png" width=600>

<br>

* **대체가 어렵다**

  * 만약 MySQL 데이터베이스를 사용하다가 오라클 데이터베이스를 사용해야 한다면, 일부 SQL 문을 그에 맞게끔 변경해야 한다.
  * 즉, 데이터베이스가 바뀔 대마다 DAO를 사용하는 코드도 변경해야 한다.
  * 다른 객체로 교체하기 어려움

  <img src="../capture/스크린샷 2019-10-01 오후 10.25.53.png">

<br>

## 6.3.2. 의존 객체를 외부에서 주입

초창기 객체 지향 프로그래밍에서는 의존 객체를 직접 생성하였으나, 지금은 **의존 객체를 외부에서 주입받는 방식(DI, Dependency Injection)으로** 바뀌게 된다.

* **빈 컨테이너와 의존 객체의 주입**

  <img src="../capture/스크린샷 2019-10-01 오후 10.29.22.png" width=600>

  * 의존 객체를 전문으로 관리하는 **'빈 컨테이너(Java Beans Container)'가** 등장하게 되었다.
  * **빈 컨테이너는 객체가 실행되기 전에 그 객체가 필요로 하는 의존 객체를 주입해 주는 역할을 수행한다.**
  * 이처럼 의존 객체를 관리하는 것을 **'의존성 주입(DI: Dependency Injection)'** 이라고 한다.
  * 일반적인 말로 **'역 제어(IoC: Inversion of Control)'** 라고 부른다.
  * 즉 **역제어(IoC)** 방식의 한 예가 **의존성 주입(DI)** 이다.

<br>

## 6.3.3. MemberDao 와 DataSource

DataSource 객체를 MemberDao에서 직접 생성하는 것이 아니라 외부에서 주입 받는다.

* **MemberDao에 DataSource를 주입하는 코드(src/spms/listeners/ContextLoaderListener.java)**

  ```java
  // 리스너 클래스의 메소드
  public void contextInitialzed(ServletContextEvent event) {
    try {
      ServletContext sc = event.getServletContext();
      
      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource)initialContext.lookup(
        "java:comp/env/jdbc/studydb");
      
      MemberDao memberDao = new MemberDao();
      memberDao.setDataSource(ds);
      ...
  ```
  * **contextInitialized()** : 웹 애플리케이션이 시작될 때 호출되는 메서드이다.
  * **setDataSource()** : MemberDao가 사용할 의존 객체인 'DataSource'를 주입하는 메소드

<br>

## 6.3.4. MemberListController에 MemberDao 주입

MemberListController에도 DI를 적용해 보자.

* **src/spms/controls/MemberListController.java**

  ```java
  public class MemberListController implements Controller {
  
    MemberDao memberDao;
  
    public MemberListController setMemberDao(MemberDao memberDao) {
      this.memberDao = memberDao;
      return this;
    }
  
    @Override
    public String execute(Map<String, Object> model) throws Exception {
      MemberDao memberDao = (MemberDao) model.get("memberDao");
      model.put("members", memberDao.selectList());
      return "/member/MemberList.jsp";
    }
  
  }
  ```

<br>

### *의존 객체 주입을 위한 인스턴스 변수와 셋터 메서드*

MemberListController에 MemberDao를 주입 받기 위한 인스턴스 변수와 셋터 메서드를 추가한다.

```java
public MemberListController setMemberDao(MemberDao memberDao) {
  this.memberDao = memberDao;
  return this;
}
```

<br>

# 실력 향상 과제

나머지 모든 페이지 컨트롤러도 MemberDao를 주입하기 위한 인스턴스 변수와 셋터 메서드를 추가해라.

* **LogInController**
* **MemberAddController**
* **MemberUpdateController**
* **MemberDeleteController**

<br>

## 6.3.5. 페이지 컨트롤러 객체들을 준비

페이지 컨트롤러도 MemberDao 처럼 ContextLoaderListener에 준비해보자.

* **src/spms/listeners/ContextLoaderListener.java 일부분**

  ```java
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      ServletContext sc = sce.getServletContext();
      sc.setRequestCharacterEncoding("UTF-8");
  
      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/postgresql");
  
      MemberDao memberDao = new MemberDao();
      memberDao.setDataSource(ds);
  
      //      sc.setAttribute("memberDao", memberDao);
      
      // ServletContext에 미리 MemberDao가 주입된 컨트롤러들을 저장해놓는다.
      sc.setAttribute("/auth/login.do",
                      new LogInController().setMemberDao(memberDao));
      sc.setAttribute("/auth/logout.do",
                      new LogOutController());
      sc.setAttribute("/member/list.do",
                      new MemberListController().setMemberDao(memberDao));
      sc.setAttribute("/member/add.do",
                      new MemberAddController().setMemberDao(memberDao));
      sc.setAttribute("/member/update.do",
                      new MemberUpdateController().setMemberDao(memberDao));
      sc.setAttribute("/member/delete.do",
                      new MemberDeleteController().setMemberDao(memberDao));
  
    } catch (Throwable e) {
      e.printStackTrace();
    }
}
  ```

  * MemberDao 객체는 별도로 꺼내서 사용할 일이 없기 때문에 ServletContext에 저장하지 않는다.
  
    ```java
  sc.setAttribute("memberDao", memberDao);
    ```
  
    <br>

### *페이지 컨트롤러 객체를 준비*

페이지 컨트롤러 객체를 생성하고 나서 MemberDao가 필요한 객체에 대해서는 셋터 메서드를 호출하여 주입해준다.

```java
new LogInController().setMemberDao(memberDao)
```

<br>

이렇게 생성된 페이지 컨트롤러를 ServletContext에 저장한다. 단, **저장할 때 서블릿 요청 URL을 키(key)로 하여 저장한다.**

```java
sc.setAttribute("/auth/login.do",
               new LogInController().setMemberDao(memberDao));
```

<br>

## 6.3.6. 프런트 컨트롤러의 변경

페이지 컨트롤러 객체를 ContextLoaderListener 에서 준비했기 때문에 프런트 컨트롤러를 변경해야 한다.

* **이전의 프런트 컨트롤러 DispatcherServlet.java**

  ```java
  ...
  if ("/member/list.do".equals(servletPath)) {
    pageController = new MemberListController();	// 페이지 컨트롤러를 생성해줘야만 했다
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


<br>

- **src/spms/servlets/DispatcherServlet.java의 일부분**

  ```java
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    String servletPath = req.getServletPath();
  
    try {
      ServletContext sc = this.getServletContext();
  
      HashMap<String, Object> model = new HashMap<>();
      // memberDao 객체는 더 이상 Map 객체에 담을 필요가 없다.
      // model.put("memberDao", sc.getAttribute("memberDao"));
      model.put("session", req.getSession());
  
      // 이 보관소에서 페이지 컨트롤러를 꺼낼 때는 서블릿 URL을 사용한다.
      Controller pageController = (Controller) sc.getAttribute(servletPath);
  
      // 페이지 컨트롤러가 사용할 데이터를 준비하는 부분을 제외하고는 모두 제거한다.
      if ("/member/add.do".equals(servletPath)) {
        if (req.getParameter("email") != null) {
          model.put("member", new Member()
                    .setEmail(req.getParameter("email"))
                    .setPassword(req.getParameter("password"))
                    .setName(req.getParameter("name")));
        }
      } else if ("/member/update.do".equals(servletPath)) {
        if (req.getParameter("email") != null) {
          model.put("member", new Member()
                    .setNo(Integer.parseInt(req.getParameter("no")))
                    .setEmail(req.getParameter("email"))
                    .setName(req.getParameter("name")));
        } else {
          model.put("no", req.getParameter("no"));
        }
      } else if ("/member/delete.do".equals(servletPath)) {
        model.put("no", req.getParameter("no"));
      } else if ("/auth/login.do".equals(servletPath)) {
        if (req.getParameter("email") != null) {
          model.put("member", new Member()
                    .setEmail(req.getParameter("email"))
                    .setPassword(req.getParameter("password")));
        }
      }
      ...
  ```

<br>

## 6.3.7. 인터페이스를 활용하여 공급처를 다변화 하자

다른 데이터베이스들을 사용하려면 데이터베이스에 맞추어 DAO 클래스를 준비하거나 코드를 변경해야 하기 때문에 매우 번거롭다. 이런 경우에 바로 **인터페이스를 활용하면 손쉽게 해결할 수 있다.**

<br>

* **인터페이스를 활용한 결합도 감소 - 교체 용이**

  <img src="../capture/스크린샷 2019-10-04 오후 9.10.29.png">

  * 의존 객체를 사용할 때 구체적으로 클래스 이름을 명시하는 대신에 **인터페이스를 사용하면,** 그 자리에 다양한 구현체를 놓을 수 있다.
  
  * MemberDao interface로 DAO 객체로부터 호출할 메소드들을 정의해놓는다. 그 다음 각기 다른 DBMS를 사용할 때는 그 DBMS의 해당되는 질의를 실행해서 똑같은 결과가 나오도록 MemberDao에 정의되어 있는 메서드들을 재정의하면 된다. 이로써, 캡슐화와 다형성이 구현됨.
  
    ```java
    public interface MemberDao() {
      List<Member> select();
    }
    
    public class OracleMemberDao() {
      List<Member> select() {
        ...
      }  
    }
    
    public class MySqlMemberDao() {
      List<Member> select() {
        ...
      }
    }
    
    public class MemberListController() {
      MemberDao memberDao = new OracleMemberDao();
      memberDao.select();
      memberDao = new MySqlMemberDao();
      memberDao.select();
    }
    ```

<br>

## 6.3.8. MemberDao 인터페이스 정의

* **spms/dao/MemberDao**

  ```java
  public interface MemberDao {
  
    List<Member> selectList() throws Exception;
    int insert(Member member) throws Exception;
    int delete(int no) throws Exception;
    Member selectOne(int no) throws Exception;
    int update(Member member) throws Exception;
    Member exist(String email, String password) throws Exception;
  
  }
  ```

* **spms/dao/PostgresSqlMemberDao**

  ```java
  // MemberDao 구현
  public class PostgresSqlMemberDao implements MemberDao {
  ```

* **spms/listeners/ContextLoaderListener**

  ```java
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      ServletContext sc = sce.getServletContext();
      sc.setRequestCharacterEncoding("UTF-8");
  
      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/postgresql");
  
      // 이제 MemberDao는 인터페이스이기 때문에 인스턴스를 생성할 수 없다.
      // MemberDao memberDao = new MemberDao();
      PostgresSqlMemberDao memberDao = new PostgresSqlMemberDao();
      memberDao.setDataSource(ds);
  ```

  * MemberDao 대신 PostgresSqlMemberDao 객체를 생성한다. 따라서 **페이지 컨트롤러에 주입되는 것은 PostgresSqlMemberDao 이다.**

* **모든 서블릿의 execute 안에 MemberDao를 맵객체로 부터 가져오는 코드를 제거한다(NullPointerException 방지).**

<br>

# 6.4. 리플랙션 API를 이용하여 프런트 컨트롤러 개선하기

프런트 컨트롤러의 코드의 중에서, 매개변수 값을 받아서 VO 객체를 생성하는 부분은 페이지 컨트롤러를 추가할 때마다 코드를 변경해야 되는 문제가 있다.

* **이전의 프런트 컨트롤러**

  ```java
  ...
    try {
      String pageControllerPath = null;
  
      if ("/member/list.do".equals(servletPath)) {
        pageControllerPath = "/member/list";
      } else if ("/member/add.do".equals(servletPath)) {
        pageControllerPath = "/member/add";
        if (req.getParameter("email") != null) {
          // 프런트 컨트롤러가 DTO 객체를 미리 만들어서 담아놓음.
          req.setAttribute("member", new Member()
                           .setEmail(req.getParameter("email"))
                           .setPassword(req.getParameter("password"))
                           .setName(req.getParameter("name")));
        }
      }
  ...
  ```

  * 위의 코드에서 만약 Member 클래스에서 Email 이라는 필드를 Username 으로 바꾼다면 코드를 변경해야하는 문제점이 있다.

* **리플랙션 API를 적용한 프런트 컨트롤러**

  ```java
  ...
    if (pageController instanceof DataBinding) {
      // 아래의 메서드가 페이지 컨트롤러가 필요한 데이터를 만들어준다.
      prepareRequestData(req, model, (DataBinding)pageController);
    }
  ...
  ```

이번 절에서 **리플랙션 API를 활용하여** 인스턴스를 자동 생성하고, 메서드를 자동으로 호출할 것이다.

<br>

## 6.4.1. 신규 회원 정보 추가 자동화

**프런트 컨트롤러에서 VO 객체 생성 자동화**

<img src="../capture/스크린샷 2019-10-04 오후 9.44.43.png">

1. 웹 브라우저에서 회원 등록 요청.
2. 프런트 컨트롤러는 페이지 컨트롤러에게 필요한 데이터(Member 객체)의 이름과 타입 정보를 담은 배열을 리턴받는다.
3. 프런트 컨트롤러는 ServletRequestDataBinder를 이용하여, 요청 매개변수로부터 페이지 컨트롤러가 원하는 형식의 값 객체를 만든다.
4. 프런트 컨트롤러는 ServletRequestDataBinder가 만들어 준 값 객체를 Map에 저장한다.
5. 프런트 컨트롤러는 페이지 컨트롤러의 execute()에 Map 객체를 매개변수로 하여 호출한다.

<br>

## 6.4.2. DataBinding 인터페이스 정의

프런트 컨트롤러는 페이지 컨트롤러가 실행되기 전에 필요한 **데이터를 요청하는 것에 대해 호출 규칙을 정해놓아야 한다.**

프런트 컨트롤러는 이 규칙을 준수하기 위해 페이지 컨트롤러를 호출할 때만 VO 객체를 준비하면 된다.

<br>

* **src/spms/bind/DataBinding.java**

  ```java
  public interface DataBinding {
    
    Object[] getDataBinders();
    
  }
  ```

  * 페이지 컨트롤러 중에서 클라이언트가 보낸 데이터가 필요한 경우 이 인터페이스를 구현한다.

  * getDataBinders 메소드의 반환값은 **데이터의 이름과 타입 정보를 담은 Object의 배열이다.**

    ```java
    new Object[] { "데이터이름", 데이터타입, "데이터이름", 데이터타입, ... }
    ```

  * 데이터 이름과 데이터 타입이 한 쌍으로 순서대로 오도록 작성한다.

<br>

## 6.4.3. 페이지 컨트롤러의 DataBinding 구현

클라이언트가 보낸 데이터를 사용하는 페이지 컨트롤러는 MemberAddController, MemberUpdateController, MemberDeleteController, LogInController 이다.

* **spms/controls/MemberAddController**

  ```java
  // DataBinding 인터페이스 구현 선언
  public class MemberAddController implements Controller, DataBinding {
  
    PostgresSqlMemberDao memberDao;
  
    public MemberAddController setMemberDao(PostgresSqlMemberDao memberDao) {
      this.memberDao = memberDao;
      return this;
    }
  
    // member 객체 저장 위치 변경
    @Override
    public String execute(Map<String, Object> model) throws Exception {
      Member member = (Member)model.get("member");
      if (member.getEmail() == null) {
        return "/member/MemberAdd.jsp";
      } else {
        memberDao.insert(member);
  
        return "redirect:list.do";
      }
    }
  
    // 메서드 구현
    @Override
    public Object[] getDataBinders() {
      return new Object[] {
          "member", spms.vo.Member.class
      };
    }
  
  }
  ```

  * getDataBinders() 메서드를 보면, "member" 와 spms.vo.Member.class 가 있는데 이것은 **클라이언트가 보낸 매개변수 값을 Member 인스턴스에 담아서 "member" 라는 이름으로 Map 객체에 저장해 달라는 뜻이다.**

  * 프런트 컨트롤러는 Object 배열에 지정된 대로 **Member 인스턴스를 준비하여 Map 객체에 저장하고,** execute()를 호출할 때 매개변수로 이 **Map 객체를 넘길 것이다.**

  * 기존의 execute() 와 달리 Member 존재 여부로 get, post 들 구분하면 안되고, 이메일이 존재하는지로 구분해야 한다.

    **execute() 이전 코드**

    ```java
    if (model.get("member") == null) { ... }
    else { ... }
    ```

    **execute() 현재 코드**

    ```java
    Member member = (Member) model.get("member");
    if (member.getEmail() == null) { ... }
    else { ... }
    ```

<br>

# 실력 향상 과제

모든 페이지 컨트롤러에 대해서도 DataBinding 인터페이스를 구현하라.

1. **LogInController**

   ```java
   ...
     @Override
     public String execute(Map<String, Object> model) throws Exception {
       Member member = (Member) model.get("member");
       if (member.getEmail() == null) {
         return "/auth/LogInForm.jsp";
       } else {
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
   
     @Override
     public Object[] getDataBinders() {
       // 사용자가 입력한 이메일과 암호가 필요하기 때문에 member 객체 필요
       return new Object[] {
           "member", spms.vo.Member.class
       };
     }
   ...
   ```

2. **MemberUpdateController**

   ```java
   ...
     @Override
     public String execute(Map<String, Object> model) throws Exception {
       Member member = (Member) model.get("member");
       if (member.getEmail() == null) {
         member = memberDao.selectOne(Integer.parseInt(String.valueOf(model.get("no"))));
         model.put("member", member);
         return "/member/MemberUpdate.jsp";
       } else {
         memberDao.update(member);
         return "redirect:list.do";
       }
     }
   
     @Override
     public Object[] getDataBinders() {
       // 변경폼을 출력할 때 회원 번호가 필요하고, 데이터를 변경할 때
       // Member 인스턴스가 필요하다.
       return new Object[] {
           "no", Integer.class,
           "member", spms.vo.Member.class
       };
     }
   ...
   ```

3. **MemberDeleteController**

   ```java
   ...
   	@Override
     public String execute(Map<String, Object> model) throws Exception {
       memberDao.delete(Integer.parseInt(String.valueOf(model.get("no"))));
       return "redirect:list.do";
     }
   
     @Override
     public Object[] getDataBinders() {
       // 회원 정보를 삭제할 때 회원 번호가 필요하다.
       return new Object[]{
           "no", Integer.class
       };
     }
   ...
   ```

<br>

## 6.4.4. 프런트 컨트롤러의 변경

**src/spms/servlets/DispatcherServlet.java**

```java
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    String servletPath = req.getServletPath();

    try {
      ServletContext sc = this.getServletContext();

      HashMap<String, Object> model = new HashMap<>();
      model.put("session", req.getSession());

      Controller pageController = (Controller) sc.getAttribute(servletPath);

//      if ("/member/add.do".equals(servletPath)) {
//        if (req.getParameter("email") != null) {
//          model.put("member", new Member()
//              .setEmail(req.getParameter("email"))
//              .setPassword(req.getParameter("password"))
//              .setName(req.getParameter("name")));
//        }
//      } else if ("/member/update.do".equals(servletPath)) {
//        if (req.getParameter("email") != null) {
//          model.put("member", new Member()
//              .setNo(Integer.parseInt(req.getParameter("no")))
//              .setEmail(req.getParameter("email"))
//              .setName(req.getParameter("name")));
//        } else {
//          model.put("no", req.getParameter("no"));
//        }
//      } else if ("/member/delete.do".equals(servletPath)) {
//        model.put("no", req.getParameter("no"));
//      } else if ("/auth/login.do".equals(servletPath)) {
//        if (req.getParameter("email") != null) {
//          model.put("member", new Member()
//              .setEmail(req.getParameter("email"))
//              .setPassword(req.getParameter("password")));
//        }
//      }

      if (pageController instanceof DataBinding) {
        prepareRequestData(req, model, (DataBinding)pageController);
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
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

  private void prepareRequestData(HttpServletRequest request,
                                  HashMap<String, Object> model, DataBinding dataBinding) throws Exception {
    Object[] dataBinders = dataBinding.getDataBinders();
    String dataName;
    Class<?> dataType;
    Object dataObj;
    for (int i = 0; i < dataBinders.length; i += 2) {
      dataName = (String) dataBinders[i];
      dataType = (Class<?>) dataBinders[i+1];
      dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
      model.put(dataName, dataObj);
    }
  }
}
```

- **service() 메서드**

  - 매개변수 값을 사용하는 페이지 컨트롤러를 추가하더라도 조건문을 삽입할 필요가 없다.

  - 대신 데이터 준비를 자동으로 수행하는 prepareRequestData()를 호출한다.

    ```java
    if (pageController instanceof DataBinding) {
      prepareRequestData(request, model, (DataBinding)pageController);
    }
    ```

    - prepareRequestData()를 호출하여 페이지 컨트롤러를 위한 데이터를 준비한다.

- **prepareRequestData() 메서드**

  - 페이지 컨트롤러에게 필요한 데이터가 무엇인지 가져온다.

    ```java
    Object[] dataBinders = dataBinding.getDataBinders();
    ```

  - 배열에서 꺼낸 값을 보관할 임시 변수를 준비한다.

    ```java
    String dataName = null;
    Class<?> dataType = null;
    Object dataObj = null;
    ```

  - 데이터 이름과 데이터 타입을 꺼낸다.

    ```java
    for (int i =0; i < dataBinders.length; i += 2) {
      dataName = (String)dataBinders[i];
      dataType = (Class<?>) dataBinders[i+1];
      ...
    }
    ```

  - dataName과 일치하는 요청 매개변수를 찾고 dataType을 통해 해당 클래스의 인스턴스를 생성한다. 찾은 매개변수 값을 인스턴스에 저장하며 그 인스턴스를 반환한다.

    ```java
    dataObj = servletRequestDataBinder.bind(request, dataType, dataName);
    model.put(dataName, dataObj);
    ```

    - bind() 메서드가 반환한 객체는 map 객체에 담는다. 페이지 컨트롤러가 사용할 데이터를 준비시킨다.

<br>

## 6.4.5. ServletRequestDataBinder 클래스 생성

```java
import javax.servlet.ServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

public class ServletRequestDataBinder {

  public static Object bind(ServletRequest request, Class<?> dataType, String dataName)
    throws Exception {
    if (isPrimitiveType(dataType)) {
      return createValueObject(dataType, request.getParameter(dataName));
    }

    Set<String> paramNames = request.getParameterMap().keySet();
    Object dataObject = dataType.newInstance();
    Method m;

    for (String paramName : paramNames) {
      m = findSetter(dataType, paramName);
      if (m != null) {
        m.invoke(dataObject, createValueObject(m.getParameterTypes()[0],
            request.getParameter(paramName)));
      }
    }
    return dataObject;
  }

  private static boolean isPrimitiveType(Class<?> type) {
    if (type.getName().equals("int") || type == Integer.class ||
    type.getName().equals("long") || type == Long.class ||
    type.getName().equals("float") || type == Float.class ||
    type.getName().equals("double") || type == Double.class ||
    type.getName().equals("boolean") || type == Boolean.class ||
    type == Date.class || type == String.class) {
      return true;
    }
    return false;
  }

  private static Object createValueObject(Class<?> type, String value) {
    if (type.getName().equals("int") || type == Integer.class) {
      return Integer.valueOf(value);
    } else if (type.getName().equals("float") || type == Float.class) {
      return Float.valueOf(value);
    } else if (type.getName().equals("double") || type == Double.class) {
      return Double.valueOf(value);
    } else if (type.getName().equals("long") || type == Long.class) {
      return Long.valueOf(value);
    } else if (type.getName().equals("boolean") || type == Boolean.class) {
      return Boolean.valueOf(value);
    } else if (type == Date.class) {
      return java.sql.Date.valueOf(value);
    } else {
      return value;
    }
  }

  private static Method findSetter(Class<?> type, String name) {
    Method[] methods = type.getMethods();

    String propName;
    for (Method m : methods) {
      if (!m.getName().startsWith("set")) continue;
      propName = m.getName().substring(3);
      if (propName.toLowerCase().equals(name.toLowerCase())) {
        return m;
      }
    }
    return null;
  }

}
```

- **bind() 메서드**

  - 프런트 컨트롤러에서 호출하는 메서드이다,

  - 요청 매개변수의 값과 데이터 이름, 데이터 타입을 받아서 **데이터 객체를 만드는 일을 한다.**

    ```java
    public static Object bind(ServletRequest request, Class<?> dataType, String dataName)
      throws Exception {
      ...
      return dataObject;
    }
    ```

    <br>

  - dataType이 기본 타입인지 아닌지 검사

    ```java
    if (isPrimitiveType(dataType)) {
      return createValueObject(dataType, request.getParameter(dataName));
    }
    ```

    - **isPrimitiveType()** : 기본 타입임을 확인하면 true 반환
    - **createValueObject()** : 기본 타입 객체를 생성

    <br>

  - dataType이 기본 타입이 아닌 경우 요청 매개 변수의 이름과 일치하는 셋터 메서드를 찾아서 호출

    ```java
    Set<String> paramNames = request.getParameterMap().keySet();
    ```

    - **request.getParameterMap()** : 매개변수의 이름과 값을 맵 객체에 담아서 반환. 우리가 필요한 것은 매개변수의 이름이기 때문에 Map의 keySet()을 호출하여 이름 목록만 꺼낸다.

    <br>

  - Class의 newInstance()를 사용하면 해당 클래스의 인스턴스를 얻을 수 있다.

    ```java
    Object dataObject = dataType.newInstance();
    ```

    <br>

  - 반복문을 통해서 데이터 타입 클래스에서 매개변수 이름과 일치하는 프로퍼티(셋터 메서드)를 찾는다.

    ```java
    for (String paramName : paramNames) {
      m = findSetter(dataType, paramName);
      ...
    }
    ```

    - **findSetter()** : 내부에 선언된 메서드로서, 데이터 타입(Class)과 매개변수 이름(String)을 주면 셋터 메서드를 찾아서 반환한다.

    <br>

  - 셋터 메서드를 찾았으면 이전에 생성한 dataObject에 대해 호출한다.

    ```java
    if (m != null) {
      m.invoke(dataObject, ...);
    }
    ```

    <br>

  - 셋터 메서드를 호출할 때 요청 매개변수의 값을 그 형식에 맞추어 넘긴다.

    ```java
    createValueObject(
      m.getParameterType()[0],        // 셋터 메서드의 매개변수 타입
      request.getParameter(paramName) // 요청 매개변수의 값
    )
    ```

    - 요청 매개변수의 값을 가지고 기본 타입의 객체를 만들어 준다.

    <br>

- **isPrimitiveType() 메서드**

  - 매개변수로 주어진 타입이 기본 타입인지 검사하는 메서드

  <br>

- **createValueObject() 메서드**

  - 이 메서드는 셋터로 값을 할당할 수 없는 기본 타입에 대해 객체를 생성하는 메서드이다.

  <br>

- **findSetter() 메서드**

  - 클래스(type)를 조사하여 주어진 이름(name)과 일치하는 셋터 메서드를 찾는다.

    ```java
    private static Method findSetter(Class<?> type, String name) {
    ```

    <br>

  - 제일 먼저 데이터 타입에서 메서드 목록을 얻는다.

    ```java
    Method[] methods = type.getMethods();
    ```

    <br>

  - 메서드 목록을 반복하여 셋터 메서드가 아니면 무시하도록 한다.

    ```java
    for (Method m : methods) {
      if (!m.getName().startWith("set")) continue;
    ```

    <br>

  - 셋터 메서드일 경우 요청 매개변수의 이름과 일치하는 검사

    ```java
    if (propName.toLowerCase().equals(name.toLowerCase())) {
      return m;
    }
    ```

<br>

## 6.4.6. 리플랙션 API

리플랙션 API는 **클래스나 메서드의 내부 구조를 들여다 볼 때 사용하는 도구이다.**

- **이번 절에 사용한 리플랙션 API**

  | 메서드                     | 설명                                                    |
  | -------------------------- | ------------------------------------------------------- |
  | Class.newInstance          | 주어진 클래스의 인스턴스를 생성                         |
  | Class.getName()            | 클래스의 이름을 반환                                    |
  | Class.getMethods()         | 클래스에 선언된 모든 public 메서드의 목록을 배열로 반환 |
  | Method.invoke              | 해당 메서드를 호출                                      |
  | Method.getParameterTypes() | 메서드의 매개변수 목록을 배열로 반환                    |

<br>

# 6.5. 프로퍼티를 이용한 객체 관리

페이지 컨트롤러를 추가하면 ContextLoaderListener를 변경해야 했다.

- **ContextLoaderListener에서 페이지 컨트롤러를 생성하는 코드**

  ```java
  public void contextInitialized(ServletContextEvent event) {
    try {
      ...
      PostgresSqlMemberDao memberDao = new PostgresSqlMemberDao();
      memberDao.setDataSource(ds);
      
      sc.setAttribute("/auth/login.do",
                     new LogInController().setMemberDao(memberDao));
      sc.setAttribute("/auth/logout.do", new LogOutController());
      sc.setAttribute("/member/list.do",
                     new MemberListController().setMemberDao(memberDao));
      sc.setAttribute("/member/add.do",
                     new MemberAddController().setMemberDao(memberDao));
      sc.setAttribute("/member/update.do",
                     new MemberUpdateController().setMemberDao(memberDao));
      sc.setAttribute("/member/delete.do",
                     new MemberDeleteController().setMemberDao(memberDao));
    } catch (Throwable e){
      ...
  ```

  - 페이지 컨트롤러뿐만 아니라 DAO를 추가하는 경우에도 ContextLoaderListener 클래스에 코드를 추가해야 한다.
  - **객체를 생성하고 의존 객체를 주입하는 부분을 자동화 해보자.**

<br>

## 6.5.1. 실습 시나리오

웹 애플리케이션을 시작할 때 생성해야 할 객체가 있다면 **프로퍼티 파일(application-context.properties)에** 기록한다.

ContextLoaderListener는 이 **프로퍼티 파일의 정보를 읽고 객체를 생성한다.**

- **프로퍼티 파일을 이용한 객체 자동 생성 시나리오**

  <img src="../capture/스크린샷 2019-10-07 오후 4.26.19.png">

  1. 웹 애플리케이션이 시작되면 서블릿 컨테이너는 **ContextLoaderListener의 contextInitialized() 메서드 호출**
  2. contextInitialized() 메서드에서는 **ApplicationContext를 생성.** 이때 생성자에 프로퍼티 파일의 경로를 매개변수로 넘겨준다.
  3. **ApplicationContext는 프로퍼티 파일의 내용을 읽어들인다.**
  4. 프로퍼티 파일에 선언된 대로 **객체를 생성하여 객체 테이블에 저장한다.**
  5. 객체 테이블에 저장된 각 객체에 대해 **의존 객체를 찾아서 할당해 준다.**

<br>

## 6.5.2. 프로퍼티 파일 작성

생성할 객체에 대한 정보를 담고 있는 프로퍼티 파일을 만들어보자.

<br>

### *web/WEB-INF 폴더에 application-context.properties 파일을 생성 후 편집*

- web/WEB-INF/application-context.properties

  ```properties
  jndi.dataSource=java:comp/env/jdbc/postgresql
  memberDao=spms.dao.PostgresSqlMemberDao
  /auth/login.do=spms.controls.LogInController
  /auth/logout.do=spms.controls.LogOutController
  /member/list.do=spms.controls.MemberListController
  /member/add.do=spms.controls.MemberAddController
  /member/update.do=spms.controls.MemberUpdateController
  /member/delete.do=spms.controls.MemberDeleteController
  ```

  - 이 파일은 ApplicationContext에서 객체를 준비할 때 사용한다.

<br>

객체의 종류에 따라 준비하는 방법이 다르므로 몇 가지 작성 규칙을 정의하였다. 물론 이 규칙은 우리가 만드는 미니 프레임워크에만 해당한다.

<br>

## 톰캣 서버에서 제공하는 객체

DataSource 처럼 톰캣 서버에서 제공하는 객체는 ApplicationContext에서 생성할 수 없다. 대신 **InitialContext를 통해 해당 객체를 얻어야 한다.** 다음은 이런 종류의 객체를 설정하는 규칙

```properties
jndi.{객체이름}={JNDI이름}
```

<br>

프로퍼티 **키(key)는 'jndi.' 와 객체 이름을 결합하여 작성한다.** 프로퍼티의 **값(value)은 톰캣 서버에 등록된 객체의 JNDI 이름이다.**

```properties
jndi.dataSource=java:comp/env/jdbc/studydb
```

- 이 DataSource는 MemberDao에 할당된다.

<br>

## 일반 객체

MemberDao와 같은 일반 객체 선언 규칙

```properties
{객체이름}={패키지 이름을 포함한 클래스 이름}
```

<br>

프로퍼티의 키는 객체를 알아보는 데 도움이 되는 이름을 사용한다. 중복되어서는 안된다. 프로퍼티의 값은 패키지 이름을 포함한 전체 클래스 이름이어야 한다.

```properties
memberDao=spms.dao.PostgresSqlMemberDao
```

<br>

## 페이지 컨트롤러 객체

페이지 컨트롤러는 프런트 컨트롤러에서 찾기 쉽도록 다음의 규칙으로 작성한다.

```properties
{서블릿 URL}={패키지 이름을 포함한 클래스 이름}
```

<br>

프로퍼티의 키는 서블릿 URL이다.

```properties
/auth/login.do=spms.controls.LogInController
```

<br>

## 6.5.3. ApplicationContext 클래스

- **spms/context/ApplicationContext.java**

  ```java
  public class ApplicationContext {
  
    Hashtable<String, Object> objTable = new Hashtable<>();
  
    public Object getBean(String key) {
      return objTable.get(key);
    }
  
    public ApplicationContext(String propertiesPath) throws Exception {
      Properties props = new Properties();
      props.load(new FileReader(propertiesPath));
  
      prepareObject(props);
      injectDependency();
    }
  
    private void prepareObject(Properties props) throws Exception {
      Context ctx = new InitialContext();
      String key;
      String value;
  
      for (Object item : props.keySet()) {
        key = (String)item;
        value = props.getProperty(key);
        if (key.startsWith("jndi.")) {
          objTable.put(key, ctx.lookup(value));
        } else {
          objTable.put(key, Class.forName(value).newInstance());
        }
      }
    }
  
    private void injectDependency() throws Exception {
      for (String key : objTable.keySet()) {
        if (!key.startsWith("jndi.")) {
          callSetter(objTable.get(key));
        }
      }
    }
  
    private void callSetter(Object obj) throws Exception {
      Object dependency;
      for (Method m : obj.getClass().getMethods()) {
        if (m.getName().startsWith("set")) {
          dependency = findObjectByType(m.getParameterTypes()[0]);
          if (dependency != null) {
            m.invoke(obj, dependency);
          }
        }
      }
    }
  
    private Object findObjectByType(Class<?> type) {
      for (Object obj : objTable.values()) {
        if (type.isInstance(obj)) {
          return obj;
        }
      }
      return null;
    }
  
  }
  ```

<br>

## 객체의 보관

프로퍼티에 설정된 대로 객체를 준비하려면 저장할 보관소가 필요하기 때문에, 해시 테이블을 준비한다. 또한, 해시 테이블에서 객체를 꺼낼(getter) 메서드도 정의한다.

```java
Hashtable<String, Object> objTable = new Hashtable<String, Object>();

public Object getBean(String key) { ... }
```

<br>

## 프로퍼티 파일의 로딩

ApplicationContext 생성자가 호출되면 매개변수로 지정된 프로퍼티 파일의 내용을 로딩해야 한다.

```java
Properties props = new Properties();
props.load(new FileReader(propertiesPath));
```

<br>

### *프로퍼티 파일 로딩*

- **Properties는 '이름=값' 형태로 된 파일을 다룰 때 사용하는 클래스이다.**
  - Properties의 load() 메서드는 FileReader를 통해 읽어들인 프로퍼티 내용을 키-값 형태로 내부 맵에 보관한다.

<br>

### *prepareObjects() 메서드*

프로퍼티 파일의 내용을 로딩한 후, 그에 따른 객체를 만들어주는 메서드.

먼저 JNDI 객체를 찾을 때 사용할 **InitialContext를 준비한다.**

```java
Context ctx = new InitialContext();
```

<br>

그 후, 반복문을 통해 프로퍼티에 들어있는 정보를 꺼내서 객체를 생성한다.

```java
for (Object item : props.keySet()) { ... }
```

<br>

만약 프로퍼티의 키가 **"jndi."로 시작한다면** 객체를 생성하지 않고, **IntialContext 를 통해 얻는다.**

```java
if (key.startsWith("jndi.")) {
  objTable.put(key, ctx.lookup(value));
}
```

- InitialContext의 **lookup() 메서드는 JNDI 인터페이스를 통해 톰캣 서버에 등록된 객체를 찾는다.**

<br>

그 밖의 객체는 **Class.forName()을 호출하여** 클래스를 로딩하고, **newInstance()를** 사용하여 인스턴스를 생성한다.

```java
else {
  objTable.put(key, Class.forName(value).newInstance());
}
```

<br>

### *클래스 로딩과 인스턴스 생성*

<img src="../capture/스크린샷 2019-10-08 오후 3.55.45.png">

이렇게 생성한 객체는 객체 테이블 "objTable" 에 저장된다.

<br>

### *객체 테이블에 인스턴스 저장*

<img src="../capture/스크린샷 2019-10-08 오후 4.16.52.png">

<br>

### *injectDependency() 메서드*

각 객체가 필요로 하는 의존 객체를 할당해주는 메서드이다.

```java
if (!key.startsWith("jndi.")) {
  callSetter(objTable.get(key));
}
```

- 객체 이름이 "jndi." 로 시작하는 경우 톰캣 서버에서 제공한 객체이므로 의존 객체를 주입해서는 안 된다.
- 나머지 객체에 대해서는 **셋터 메서드를 호출한다.**

<br>

### *callSetter() 메서드*

매개변수로 주어진 객체에 대해 **셋터 메서드를 찾아서 호출하는 일을** 한다.

```java
for (Method m : obj.getClass().getMethods()) {
  if (m.getName().startsWith("set")) {
```

<br>

셋터 메서드를 찾았으면 셋터 메서드의 **매개변수와 타입이 일치하는 객체를 objTable에서 찾는다.**

```java
dependency = findObjectByType(m.getParameterTypes()[0]);
```

<br>

의존 객체를 찾았으면, **셋터 메서드를 호출한다.**

```java
if (dependency != null) {
  m.invoke(obj, dependency);
}
```

<br>

### *findObjectByType() 메서드*

셋터 메서드를 호출할 때 넘겨줄 의존 객체를 찾는 일을 한다.

objTable에 들어 있는 객체를 모두 뒤진다.

```java
for (Object obj : objTable.values()) {
```

<br>

만약 셋터 메서드의 매개변수 타입과 일치하는 객체를 찾았다면 그 객체의 주소를 리턴한다. 

```java
if (type.isInstance(obj)) {
  return obj;
}
```

- Class의 **isInstance() 메서드는** 주어진 객체가 해당 클래스 또는 인터페이스의 인스턴스인지 검사한다.

<br>

## 6.5.4. ContextLoaderListener 변경

ApplicationContext를 만든 이유는 페이지 컨트롤러나 DAO가 추가되더라도 ContextLoaderListener를 변경하지 않기 위함이다.

- **spms/listeners/ContextLoaderListener.java**

  ```java
  @WebListener
  public class ContextLoaderListener implements ServletContextListener {
  
    static ApplicationContext applicationContext;
  
    public static ApplicationContext getApplicationContext() {
      return applicationContext;
    }
  
    @Override
    public void contextInitialized(ServletContextEvent sce) {
      try {
        ServletContext sc = sce.getServletContext();
        sc.setRequestCharacterEncoding("UTF-8");
  
        String propertiesPath = sc.getRealPath(
            sc.getInitParameter("contextConfigLocation"));
        applicationContext = new ApplicationContext(propertiesPath);
  
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
  
  }
  ```

  - 이제 페이지 컨트롤러나 DAO 등을 추가할 때는 프로퍼티 파일에 그 클래스에 대한 정보를 한 줄 추가하면 자동으로 그 객체가 생성된다.

<br>

### *프로퍼티 파일의 경로*

프로퍼티 파일의 이름과 경로 정보를 web.xml 파일로부터 읽어 오게 처리하였다.

```java
String propertiesPath = sc.getRealPath(
  sc.getInitParameter("contextConfigLocation"));
```

<br>

그리고 ApplicationContext 객체를 생성할 때 생성자의 매개변수로 넘겨준다.

```java
applicationContext = new ApplicationContext(propertiesPath);
```

<br>

### *getApplicationContext() 클래스 메서드*

이 메서드는 ContextLoaderListener에서 만든 ApplicationContext 객체를 얻을 때 사용한다.

클래스 이름만으로 호출할 수 있게 static으로 선언 하였다.

```java
public static ApplicationContext getApplicationContext() {
  return applicationContext;
}
```

<br>

## 6.5.5. web.xml 파일에 프로퍼티 경로 정보 설정

ContextLoaderListener가 프로퍼티 파일을 찾을 수 있도록 web.xml 파일에 프로퍼티에 대한 파일 경로 정보를 설정하자.

- **web/WEB-INF/web.xml**

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
           version="4.0">
    <display-name>Apache-Axis</display-name>
  
    <resource-ref>
      <res-ref-name>jdbc/postgresql</res-ref-name>
      <res-type>javax.sql.DataSource</res-type>
      <res-auth>Container</res-auth>
    </resource-ref>
  
    <!-- properties 파일 경로 정보 추가-->
    <context-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>/WEB-INF/application-context.properties</param-value>
    </context-param>
    ...
  ```

<br>

## 6.5.6. DispatcherServlet 변경

프런트 컨트롤러를 변경해보자.

**spms/servlets/DispatcherServlet.java**

```java
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    String servletPath = req.getServletPath();

    try {
      //      ServletContext sc = this.getServletContext();
      ApplicationContext ctx = ContextLoaderListener.getApplicationContext();

      HashMap<String, Object> model = new HashMap<>();
      model.put("session", req.getSession());

      //      Controller pageController = (Controller) sc.getAttribute(servletPath);
      Controller pageController = (Controller) ctx.getBean(servletPath);

      if (pageController == null) {
        throw new Exception("요청한 서비스를 찾을 수 없습니다.");
      }

      if (pageController instanceof DataBinding) {
        prepareRequestData(req, model, (DataBinding)pageController);
      }
      ...
```

- **ApplicationContext를 도입하면서** ServletContext로 부터 객체를 가져오는 작업이 필요없어졌다.

  ```java
  // ServletContext sc = this.getServletContext();
  ApplicationContext ctx = ContextLoaderListener.getApplicationContext();
  ```

- 페이지 컨트롤러를 찾을 때도 ServletContext에서 찾지 않기 때문에 해당 코드를 제거했다.

  ```java
  // Controller pageController = (Controller) sc.getAttribute(servletPath);
  Controller pageController = (Controller) ctx.getBean(servletPath);
  ```

- 만약 페이지 컨트롤러를 찾지 못하면 오류를 발생시킨다.

  ```java
  if (pageController == null) {
    throw new Exception("요청한 서비스를 찾을 수 없습니다.");
  }
  ```

<br>

# 6.6. 애노테이션을 이용한 객체 관리

**'애노테이션'은** 컴파일이나 배포, 실행할 때 참조할 수 있는 아주 특별한 주석이다. 애노테이션을 사용하면 클래스나 필드, 메서드에 대해 부가 정보를 등록할 수 있다. 

**애노테이션(Annotation)을 사용하여** 프로퍼티 파일에 한 줄 추가해야 하는 번거로움을 없애보자.

<br>

## 6.6.1. 애노테이션 활용

- 애노테이션이 적용된 객체 관리 시나리오

  <img src="../capture/스크린샷 2019-10-08 오후 10.13.24.png">

  1. 웹 애플리케이션이 시작되면 서블릿 컨테이너는 contextInitialized()를 호출한다.
  2. contextInitialized()는 ApplicationContext를 생성한다. 생성자의 매개변수로 프로퍼티 파일의 경로를 넘긴다.
  3. ApplicationContext 생성자는 프로퍼티 파일을 로딩하여 내부 맵에 보관한다.
  4. ApplicationContext는 맵에 저장된 정보를 꺼내 인스턴스를 생성하거나 또는 톰캣 서버에서 객체를 가져온다.
  5. 또한, 자바 classpath를 뒤져서 애노테이션이 붙은 클래스를 찾는다. 그리고 애노테이션에 지정된 정보에 따라 인스턴스를 생성한다.
  6. 객체가 모두 준비되었으면, 각 객체에 대해 의존 객체를 찾아서 할당한다.

<br>

## 6.6.2. 애노테이션 정의

### *@Component 애노테이션의 사용 예*

```java
@Component("memberDao")  // 애노테이션 선언
class MemberDao {
  ...
}
```

<br>

- **spms/annotation/Component.java**

  ```java
  package spms.annotation;
  
  import java.lang.annotation.Retention;
  import java.lang.annotation.RetentionPolicy;
  
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Component {
    String value() default "";
  }
  ```

  - 애노테이션 문법은 인터페이스 문법과 비슷하다. interface 키워드 앞에 @가 붙는다.

    ```java
    public @interface Component{
    ```

  - 객체 이름을 저장하는 용도로 사용할 'value'라는 기본 속성을 정의한다.

    ```java
    String value() default "";
    ```

    - value 속성의 값을 지정하지 않으면 default로 지정한 값이 할당된다.

<br>

### *애노테이션 유지 정책*

**'애노테이션 유지 정책' 이란** 애노테이션 정보를 언제까지 유지할 것인지 설정하는 문법이다.

```java
@Retention(RetentionPolicy.RUNTIME)
```

<br>

- **애노테이션 유지 정책**

| 정책                    | 설명                                                         |
| ----------------------- | ------------------------------------------------------------ |
| RetentionPolicy.SOURCE  | 소스 파일에서만 유지. 즉, 클래스 파일에 애노테이션 정보가 남아 있지 않는다. |
| RetentionPolicy.CLASS   | 클래스 파일에 기록됨. 즉, 실행 중에서는 클래스에 기록된 애노테이션 값을 꺼낼 수 없음(기본정책) |
| RetentionPolicy.RUNTIME | 클래스 파일에 기록됨. 즉, 실행 중에 클래스에 기록된 애노테이션 값을 참조할 수 있다. |

> 유지 정책을 지정하지 않으면 기본으로 RetentionPolicy.CLASS

<br>

## 6.6.3. 애노테이션 적용

- **spms/dao/PostgresSqlMemberDao.java**

  ```java
  @Component("memberDao")
  public class PostgresSqlMemberDao implements MemberDao {
  ```

  - ApplicationContext는 인스턴스를 보관할 때 이 객체 이름을 사용한다.

<br>

# 실력 향상 과제

PostgresSqlMemberDao처럼 페이지 컨트롤러에 대해서도 적용하세요.

1. LogInController.java

   ```java
   @Component("/auth/login.do")
   public class LogInController implements Controller, DataBinding {
   ```

   <br>

2. LogOutController.java

   ```java
   @Component("/auth/logout.do")
   public class LogOutController implements Controller {
   ```

   <br>

3. MemberAddController.java

   ```java
   @Component("/member/add.do")
   public class MemberAddController implements Controller, DataBinding {
   ```

   <br>

4. MemberDeleteController.java

   ```java
   @Component("/member/delete.do")
   public class MemberDeleteController implements Controller, DataBinding {
   ```

   <br>

5. MemberUpdateController.java

   ```java
   @Component("/member/update.do")
   public class MemberUpdateController implements Controller, DataBinding {
   ```

   <br>

6. MemberListController.java

   ```java
   @Component("/member/list.do")
   public class MemberListController implements Controller {
   ```

   <br>

## 6.6.4. 프로퍼티 파일 변경

- **web/WEB-INF/application-context.properties**

  ```properties
  jndi.dataSource=java:comp/env/jdbc/postgresql
  ```

  - DAO와 페이지 컨트롤러는 애노테이션으로 객체 정보를 관리하기 때문에 프로퍼티 파일에서 제거하였다.
  - 톰캣 서버가 관리하는 JNDI 객체나 외부 라이브러리에 들어 있는 개체는 프로퍼티 파일에 등록해놔야 한다.

<br>

## 6.6.5. ApplicationContext 변경

- **spms/context/ApplicationContext.java**

  ```java
  public class ApplicationContext {
  
    Hashtable<String, Object> objTable = new Hashtable<>();
  
    public Object getBean(String key) {
      return objTable.get(key);
    }
  
    public ApplicationContext(String propertiesPath) throws Exception {
      Properties props = new Properties();
      props.load(new FileReader(propertiesPath));
  
      prepareObject(props);
      prepareAnnotationObjects();
      injectDependency();
    }
  
    private void prepareAnnotationObjects() throws Exception {
      Reflections reflector = new Reflections("");
  
      Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
      String key;
      for (Class<?> clazz : list) {
        key = clazz.getAnnotation(Component.class).value();
        objTable.put(key, clazz.newInstance());
      }
    }
    ...
  ```

  - 애노테이션이 붙은 클래스를 찾아서 객체를 준비한다.

    ```java
    prepareAnnotationObjects();
    ```

<br>

### *prepareAnnotationObjects() 메서드*

이 메서드는 **자바 classpath를 뒤져서 @Component 애노테이션이 붙은 클래스를 찾는다.** 그리고 그 **객체를 생성하여 객체 테이블에 담는 일을 한다.**

이 메소드는 Reflections 오픈 소스를 사용하여 더 쉽게 클래스를 찾거나 클래스의 정보를 추출할 수 있다.

```java
Reflections reflector = new Reflections("");
```

<br>

**Reflections 클래스는** 우리가 원하는 클래스를 찾아 주는 도구이다. 생성자에 넘겨 주는 매개변수 값은 클래스를 찾을 때 출발하는 패키지이다.

**빈 문자열을 넘기면 자바 classpath에 있는 모든 패키지를 검색한다.**

Reflections의 **getTypesAnnotatedWith() 메서드를 사용하면 애노테이션이 붙은 클래스들을 찾을 수 있다.** 이 메서드의 **매개변수 값은 애노테이션의 클래스이다.**

```java
Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
```

<br>

**getAnnotation()을 통해 클래스로부터 애노테이션을 추출한다.** value()를 호출하면 속성값을 꺼낼 수 있다.

```java
key = clazz.getAnnotation(Component.class).value();
```

<br>

애노테이션을 통해 알아낸 객체 이름(key)으로 인스턴스를 저장한다.

```java
objTable.put(key, clazz.newInstance());
```

<br>

## 6.6.6. Reflections 라이브러리 준비

# 6.7. 실력 향상 훈련

간단한 프로젝트 관리 시스템을 만들어보자. 로그인, 로그아웃, 회원 등록/조회/변경/삭제를 구현해 보자.

<br>

## 6.7.1. 데이터베이스 모델

### 프로젝트 테이블 생성

```sql
create table projects (
  pno serial not null,
  pname varchar not null,
  content text not null,
  sta_date timestamp not null,
  end_date timestamp not null,
  state integer not null,
  cre_date timestamp not null,
  tags varchar null
);
```

- **pno 컬럼에 대해 자동적으로 증가하는 일련번호가 저장될 수 있도록 serial로 지정한다.**

<br>

### 프로젝트 멤버 테이블 생성

```java
create table prj_membs (
  pno integer not null,
  mno integer not null,
  level integer not null,
 	state integer not null,
  mod_date timestamp not null,
  PRIMARY KEY (pno, mno)
);
```

<br>

### Project 값 객체 준비

```java
package spms.vo;

import java.util.Date;

public class Project {

  protected int no;
  protected String title;
  protected String content;
  protected String startDate;
  protected Date endDate;
  protected int state;
  protected Date createdDate;
  protected String tags;

  public int getNo() {
    return no;
  }

  public Project setNo(int no) {
    this.no = no;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Project setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getContent() {
    return content;
  }

  public Project setContent(String content) {
    this.content = content;
    return this;
  }

  public String getStartDate() {
    return startDate;
  }

  public Project setStartDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  public Date getEndDate() {
    return endDate;
  }

  public Project setEndDate(Date endDate) {
    this.endDate = endDate;
    return this;
  }

  public int getState() {
    return state;
  }

  public Project setState(int state) {
    this.state = state;
    return this;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public Project setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public String getTags() {
    return tags;
  }

  public Project setTags(String tags) {
    this.tags = tags;
    return this;
  }
}
```

<br>

## 6.7.2. 프로젝트 관리 시스템

- **프로젝트 시나리오**

  <img src="../capture/스크린샷 2019-10-15 오후 5.47.15.png">

  1. 프로젝트 목록을 출력하는 기능 구현
  2. 프로젝트 등록 기능 구현
  3. 프로젝트 상세정보 출력 기능 구현
  4. 프로젝트 삭제를 수행할 기능 구현

<br>

## 6.7.3. 훈련 1 프로젝트 목록 페이지 구현

### 1) DAO 인터페이스 생성

프로젝트 데이터를 처리할 DAO의 인터페이스를 정의

```java
List<Project> selectList() thorws Exception;
```

<br>

### 2) DAO 구현체 생성

ProjectDao 인터페이스를 구현한 PostgresSqlProjectDao 클래스를 생성하세요.

- **Project 객체**
  - 프로젝트 번호(PNO)
  - 이름(PNAME)
  - 시작일(STA_DATE)
  - 종료일(END_DATE)
  - 상태(STATE)
- 목록은 최신 등록 순으로 정렬되어야 한다.

<br>

### 3) 페이지 컨트롤러 생성

ProjectListController 클래스를 생성하고, PostgresSqlProjectDao를 사용하여 목록 데이터를 준비한다.

<br>

### 4) JSP 페이지 생성

프로젝트 목록 페이지를 출력하는 'web/project/ProjectList.jsp' 를 작성한다.
<br>

## 훈련1 결과 소스

| 소스 파일                                    | 설명                                            |
| -------------------------------------------- | ----------------------------------------------- |
| src/spms/dao/ProjectDao.java                 | ProjectDao 인터페이스 정의                      |
| src/spms/dao/PostgresSqlProjectDao.java      | ProjectDao 인터페이스를 구현한 클래스           |
| src/spms/controls/ProjectListController.java | 프로젝트 목록 페이지를 처리하는 페이지 컨트롤러 |
| web/project/ProjectList.jsp                  | 프로젝트 목록 페이지를 생성하는 뷰 컴포넌트     |

<br>

### DAO 인터페이스 - ProjectDao

```java
package spms.dao;

import spms.vo.Project;
import java.util.List;

public interface ProjectDao {

  List<Project> selectList() throws Exception;

}
```

<br>

### DAO 구현체 - PostgresSqlProjectDao

```java
package spms.dao;

import spms.annotation.Component;
import spms.vo.Project;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

// 컴포넌트 어노테이션 추가
@Component("projectDao")
public class PostgresSqlProjectDao implements ProjectDao {

  private DataSource ds;

  public void setDataSource(DataSource ds) {
    this.ds = ds;
  }

  @Override
  public List<Project> selectList() throws Exception {
    String query = "select pno, pname, sta_date, end_date, state" +
        " from projects" +
        " order by pno desc";
    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
      ArrayList<Project> projects = new ArrayList<>();

      while (rs.next()) {
        projects.add(new Project()
            .setNo(rs.getInt("pno"))
            .setTitle(rs.getString("pname"))
            .setStartDate(rs.getDate("sta_date"))
            .setEndDate(rs.getDate("end_date"))
            .setState(rs.getInt("state")));
      }
      return projects;
    }
  }
}
```

<br>

### 페이지 컨트롤러 - ProjectListController

```java
package spms.controls.project;

import spms.annotation.Component;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.dao.ProjectDao;

import java.util.Map;

@Component("/project/list.do")
public class ProjectListController implements Controller {

  PostgresSqlProjectDao projectDao;

  public ProjectListController setMemberDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    model.put("projects", projectDao.selectList());
    return "/project/ProjectList.jsp";
  }

}
```

<br>

### 뷰 컴포넌트 - ProjectList.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <head>
    <meta charset="UTF-8">
    <title>프로젝트 목록</title>
  </head>
  <body>
    <jsp:include page="/Header.jsp"/>
    <h1>프로젝트 목록</h1>
    <p><a href="add.do">신규 프로젝트</a></p>
    <table border="1">
      <tr>
        <th>번호</th>
        <th>제목</th>
        <th>시작일</th>
        <th>종료일</th>
        <th>상태</th>
        <th></th>
      </tr>
      <c:forEach var="project" items="${projects}">
        <tr>
          <td>${project.no}</td>
          <td><a href="update.do?no=${project.no}">${project.title}</a></td>
          <td>${project.startDate}</td>
          <td>${project.endDate}</td>
          <td>${project.state}</td>
          <td><a href="delete.do?no=${project.no}">[삭제]</a></td>
        </tr>
      </c:forEach>
    </table>
    <jsp:include page="/Tail.jsp"/>
  </body>
</html>
```

<br>

### 실행 결과

<img src="../capture/스크린샷 2019-10-17 오후 1.08.57.png" width=500>

<br>

## 6.7.4. 훈련 2 프로젝트 등록 구현

### 1) DAO 인터페이스에 등록 메서드 추가

ProjectDao 인터페이스에 프로젝트 데이터 등록할  때 호출할 메서드 선언

```java
int insert(Project project) thorws Exception;
```

<br>

### 2) DAO 구현체에 메서드 추가

ProejctDao 인터페으스의 변경에 맞추어 PostgresSqlProjectDao 클래스도 변경한다. insert() 메서드에서는 사용자가 입력한 프로젝트 정보를 PROJECTS 테이블에 삽입한다.

<br>

### 3) 페이지 컨트롤러 생성

ProjectAddController 클래스 생성. GET 요청이 들어오면 **/project/ProjectForm.jsp** 로 보내고, POST 요청이 들어오면 **PostgresSqlProjectDao를 사용하여 프로젝트 데이터를 데이터베이스에 입력 후, '프로젝트 목록' 페이지로 리다이렉트**

<br>

### 4) JSP 페이지 생성

프로젝트 정보를 입력 받을 페이지(web/project/ProjectForm.jsp) 를 작성. 

- 입력 항목의 라벨은 \<label> 태그를 사용해라. 
- 입력 항목들은 \<ul>과 \<li> 태그를 사용하여 정렬해라.

<br>

## 훈련2 결과 소스

| 소스 파일                                   | 설명                                            |
| ------------------------------------------- | ----------------------------------------------- |
| src/spms/dao/ProjectDao.java                | ProjectDao 인터페이스. insert() 메서드 추가 됨. |
| src/spms/dao/PostgresSqlProjectDao.java     | insert() 메서드의 구현 추가                     |
| src/spms/controls/ProjectAddController.java | 신규 프로젝트의 등록을 처리하는 페이지 컨트롤러 |
| web/project/ProjectForm.jsp                 | 프로젝트 등록폼을 생성하는 뷰 컴포넌트          |

<br>

### DAO 인터페이스 - ProjectDao

```java
package spms.dao;

import spms.vo.Project;
import java.util.List;

public interface ProjectDao {

  List<Project> selectList() throws Exception;
  int insert(Project project) throws Exception;
  
}
```

<br>

### DAO 구현체 - PostgresSqlProjectDao

```java
...
    @Override
    public int insert(Project project) throws Exception {
    String query = "insert into projects"
      + " (pname, content, sta_date, end_date, state, cre_date, tags)"
      + " values (?, ?, ?, ?, 0, now(), ?)";

    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, project.getTitle());
      ps.setString(2, project.getContent());
      ps.setDate(3, new java.sql.Date(project.getStartDate().getTime()));
      ps.setDate(4, new java.sql.Date(project.getEndDate().getTime()));
      ps.setString(5, project.getTags());

      return ps.executeUpdate();
    }
	}
...
```

<br>

### 페이지 컨트롤러 - ProjectAddController

```java
package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.vo.Project;

import java.util.Map;

@Component("/project/add.do")
public class ProjectAddController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectAddController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "project", spms.vo.Project.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Project project = (Project) model.get("project");
    if (project.getTitle() == null) {
      return "/project/ProjectForm.jsp";
    } else {
      projectDao.insert(project);
      return "redirect:list.do";
    }
  }
}
```

<br>

### 뷰 컴포넌트 - ProjectForm.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>프로젝트 등록</title>
    <style>
      ul {
        padding: 0;
      }

      li {
        list-style: none;
      }

      label {
        float: left;
        text-align: right;
        width: 60px;
      }
    </style>
  </head>
  <body>
    <jsp:include page="/Header.jsp"/>
    <h1>프로젝트 등록</h1>
    <form action="add.do" method="post">
      <ul>
        <li>
          <label for="title">제목</label>
          <input id="title" type="text" name="title" size="50">
        </li>
        <li>
          <label for="content">내용</label>
          <textarea id="content" name="content" rows="5" cols="40"></textarea>
        </li>
        <li>
          <label for="sdate">시작일</label>
          <input id="sdate" type="text" name="startDate" placeholder="예)2013-01-01">
        </li>
        <li>
          <label for="edate">종료일</label>
          <input id="edate" type="text" name="endDate" placeholder="예)2013-01-01">
        </li>
        <li>
          <label for="tags">태그</label>
          <input id="tags" type="text" name="tags" placeholder="예)태그1 태그2 태그3" size="50">
        </li>
        <input type="submit" value="추가">
        <input type="reset" value="취소">
      </ul>
    </form>
    <jsp:include page="/Tail.jsp"/>
  </body>
</html>
```

<br>

### 실행 결과

<img src="../capture/스크린샷 2019-10-17 오후 1.23.05.png" width=500>

<br>

## 6.7.5. 훈련 3 프로젝트 변경 구현

### 1) DAO 인터페이스에 변경 메서드 추가

ProjectDao 인터페이스에 프로젝트 데이터를 찾거나 변경할 때 호출할 메서드를 선언한다.

```java
Project selectOne(int no) throws Exception;
int update(Project project) throws Exception;
```

<br>

### 2) DAO 구현체에 메서드 추가

ProjectDao 인터페이스의 변경에 맞추어 PostgresSqlProjectDao 클래스에 메서드를 추가한다.

- **selectOne()** : 프로젝트 번호에 해당하는 데이터를 찾아서 반환한다.
- **update()** : 기존 프로젝트 정보를 사용자가 입력한 값으로 변경한다.

<br>

### 3) 페이지 컨트롤러 생성

ProjectUpdateController 클래스를 생성한다.

- **GET** : PostgresSqlProjectDao 를 통해 프로젝트 정보를 가져온다.
- **POST** : 프로젝트 데이터를 변경하고 나서, 다시 프로젝트 변경 폼으로 리다이렉트 한다.

<br>

### 4) JSP 페이지 생성

프로젝트 목록에서 프로젝트 제목을 클릭하면 상세 정보를 출력할 ProjectUpdateForm.jsp 를 생성해라.

<br>

## 훈련 3 결과 소스

| 소스 파일                                      | 설명                                       |
| ---------------------------------------------- | ------------------------------------------ |
| src/spms/dao/ProjectDao.java                   | selectOne(), update() 추가                 |
| src/spms/dao/PostgresSqlProjectDao.java        | selectOne(), update() 메서드 구현          |
| src/spms/controls/ProjectUpdateController.java | 프로젝트의 변경을 처리하는 페이지 컨트롤러 |
| web/project/ProjectUpdateForm.jsp              | 프로젝트 변경폼을 생성하는 뷰 컴포넌트     |

<br>

### DAO 인터페이스 - ProjectDao

```java
package spms.dao;

import spms.vo.Project;
import java.util.List;

public interface ProjectDao {

  List<Project> selectList() throws Exception;
  int insert(Project project) throws Exception;
  Project selectOne(int no) throws Exception;
  int update(Project project) throws Exception;

}
```

<br>

### DAO 구현체 - PostgresSqlProjectDao

```java
...
  @Override
  public Project selectOne(int no) throws Exception {
  String query = "select pno, pname, content, sta_date, end_date, state, cre_date, tags"
    + " from projects where pno=" + no;
  try (Connection conn = ds.getConnection();
       PreparedStatement ps = conn.prepareStatement(query);
       ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
      return new Project()
        .setNo(rs.getInt("pno"))
        .setTitle(rs.getString("pname"))
        .setContent(rs.getString("content"))
        .setStartDate(rs.getDate("sta_date"))
        .setEndDate(rs.getDate("end_date"))
        .setCreatedDate(rs.getDate("cre_date"))
        .setTags(rs.getString("tags"));
    } else {
      throw new Exception("해당 번호의 프로젝트를 찾을 수 없습니다.");
    }
  }
}

@Override
public int update(Project project) throws Exception {
  String query = "update projects set " +
    " pname=?," +
    " content=?," +
    " sta_date=?," +
    " end_date=?," +
    " state=?," +
    " tags=?" +
    " where pno=?";

  try (Connection conn = ds.getConnection();
       PreparedStatement ps = conn.prepareStatement(query)) {
    ps.setString(1, project.getTitle());
    ps.setString(2, project.getContent());
    ps.setDate(3, new java.sql.Date(project.getStartDate().getTime()));
    ps.setDate(4, new java.sql.Date(project.getEndDate().getTime()));
    ps.setInt(5, project.getState());
    ps.setString(6, project.getTags());
    ps.setInt(7, project.getNo());

    return ps.executeUpdate();
  }
}
...
```

<br>

### 페이지 컨트롤러 - ProjectUpdateController

```java
package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.vo.Project;

import java.util.Map;

@Component("/project/update.do")
public class ProjectUpdateController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectUpdateController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "no", Integer.class,
        "project", spms.vo.Project.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Project project = (Project)model.get("project");

    if (project.getTitle() == null) {
      Integer no = (Integer)model.get("no");
      Project detailInfo = projectDao.selectOne(no);
      model.put("project", detailInfo);
      return "/project/ProjectUpdateForm.jsp";
    } else {
      projectDao.update(project);
      return "redirect:list.do";
    }
  }
}
```

<br>

### 뷰 컴포넌트 - ProjectUpdateForm.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
  <head>
    <meta charset="UTF-8">
    <title>프로젝트 정보</title>
    <style>
      ul {
        padding: 0;
      }

      li {
        list-style: none
      }

      label {
        float: left;
        text-align: right;
        width: 60px;
      }
    </style>
  </head>
  <body>
    <jsp:include page="/Header.jsp"/>
    <h1>프로젝트 정보</h1>
    <form action="update.do" method="post">
      <ul>
        <li>
          <label for="no">번호</label>
          <input id="no" type="text" name="no" size="5" value="${project.no}" readonly>
        </li>
        <li>
          <label for="title">제목</label>
          <input id="title" type="text" name="title" size="50" value="${project.title}">
        </li>
        <li>
          <label for="content">내용</label>
          <textarea id="content" name="content" rows="5" cols="40">${project.content}</textarea>
        </li>
        <li>
          <label for="sdate">시작일</label>
          <input id="sdate" type="text" name="startDate" placeholder="예)2013-01-01"
                 value="${project.startDate}">
        </li>
        <li>
          <label for="edate">종료일</label>
          <input id="edate" type="text" name="endDate" placeholder="예)2013-01-01"
                 value="${project.endDate}">
        </li>
        <li>
          <label for="state">상태</label>
          <select id="state" name="state">
            <option value="0" ${project.state == 0 ? "selected" : ""}>준비</option>
            <option value="1" ${project.state == 1 ? "selected" : ""}>진행</option>
            <option value="2" ${project.state == 2 ? "selected" : ""}>완료</option>
            <option value="3" ${project.state == 3 ? "selected" : ""}>취소</option>
          </select>
        </li>
        <li>
          <label for="tags">태그</label>
          <input id="tags" type="text" name="tags" placeholder="예)태그1 태그2 태그3" size="50"
                 value="${project.tags}">
        </li>
      </ul>
      <input type="submit" value="저장">
      <input type="button" value="삭제"
             onclick="location.href='delete.do?no=${project.no}';">
    </form>
    <jsp:include page="/Tail.jsp"/>
  </body>
</html>
```

<br>

## 6.7.6. 훈련 4 프로젝트 삭제 구현

### 1) DAO 인터페이스에 삭제 메서드 추가

ProjectDao 인터페이스에 프로젝트 데이터를 삭제할 때 호출할 메서드 선언

```java
int delete(int no) throws Exception;
```

<br>

### 2) DAO 구현체에 메서드 추가

delete() 메서드는 프로젝트 번호를 매개변수로 받는다. 그리고 프로젝트 번호에 해당하는 데이터를 찾아서 삭제한다.

<br>

### 3) 페이지 컨트롤러 생성

ProjectDeleteController 클래스 생성. 삭제 요청이 들어오면 해당 번호의 프로젝트를 삭제하고 리다이렉트

<br>

### 4) JSP 페이지 생성

프로젝트 데이터를 삭제한 후 프로젝트 목륵으로 리다이렉트 하기 때문에 JSP를 만들지 않는다.

<br>

## 훈련4. 결과 소스

| 소스 파일                                              | 설명                                       |
| ------------------------------------------------------ | ------------------------------------------ |
| src/spms/dao/ProjectDao.java                           | ProjectDao 인터페이스에 delete() 추가      |
| src/spms/dao/PostgresSqlProjectDao.java                | delete() 메서드 구현                       |
| src/spms/controls/project/ProjectDeleteController.java | 프로젝트의 삭제를 처리하는 페이지 컨트롤러 |

<br>

### DAO 인터페이스 - ProjectDao

```java
package spms.dao;

import spms.vo.Project;
import java.util.List;

public interface ProjectDao {

  List<Project> selectList() throws Exception;
  int insert(Project project) throws Exception;
  Project selectOne(int no) throws Exception;
  int update(Project project) throws Exception;
  int delete(int no) throws Exception;

}
```

<br>

### DAO 구현체 - PostgresSqlProjectDao

```java
...
@Override
public int delete(int no) throws Exception {
  String query = "delete from projects where pno=" + no;
  try (Connection conn = ds.getConnection();
       PreparedStatement ps = conn.prepareStatement(query)) {
    return ps.executeUpdate();
  }
}
...
```

<br>

### 페이지 컨트롤러 - ProjectDeleteController

```java
package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;

import java.util.Map;

@Component("/project/delete.do")
public class ProjectDeleteController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectDeleteController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "no", Integer.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Integer no = (Integer) model.get("no");
    projectDao.delete(no);
    return "redirect:list.do";
  }
}
```

<br>

## 6.7.7. 메뉴의 추가

이제 회원 관리 외에 프로젝트 관리가 추가되었다. 이 기능들 사이로 자유롭게 이동할 수 있도록 화면 상단에 메뉴를 추가해보자.

<br>

**web/Header.jsp**

```jsp
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
```

**실행 결과**

<img src="../capture/스크린샷 2019-10-17 오후 2.10.50.png" width=500>

