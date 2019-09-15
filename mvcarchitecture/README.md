# 05. MVC 아키텍처

서블릿의 단점을 보완하기 위해 등장한 **JSP(JavaServer Page)** 라는 기술을 배워 보자.

여러 기법들이 다년간 다양한 업무 시스템에 적용되고 검증되어 좋은 방법이라고 증명되면, 우리는 이것을 **'최선의 관행(best practice)'** 이라고 부른다.

이번 장에서는 실무 웹 애플리케이션 개발에 있어 최선의 관행으로 알려진 **'MVC 아키텍처(Architecture)'** 에 대해 배워 보자.

<br>

# 5.1. MVC 이해하기

**MVC(model-view-controller)** 아키텍처의 구성 요소는 **모델(model), 뷰(view), 컨트롤러(controller)** 이다.

<br>

## 5.1.1. 올인원 All-in-one 방식과 문제점

이전 까지는 클라이언트의 요청 처리를 서블릿 홀로 담당하는 올인원 방식이였다. 즉, 서블릿이 혼자서 너무 많은 기능을 하는 방식이였다.

* **혼자서 너무 많은 기능을 하는 서블릿**

<img src="../capture/스크린샷 2019-09-09 오후 10.43.01.png">

<br>

이러한 올인원의 장점은 하나에 모든 기능이 들어가 있어서 크기를 작게 만들 수 있으나, 단점은 특정 한 곳이 고장이 나면 전체를 고쳐야 되므로 불편합니다.

그래서, 올인원 방식은 규모가 작거나 업무 변경이 많지 않은 경우에 적합하지만, 규모가 크거나 업무 변경이 잦은 경우에는 오히려 유지 보수가 어려워 운영 비용이 증가하게 됩니다.

요즘처럼 글로벌 비즈니스 환경에서는 더더욱 올인원 방식은 적합하지 않습니다.

<br>

## 5.1.2. 글로벌 환경과 MVC 아키텍처

시스템 변경이 잦은 상황에서 유지 보수를 보다 쉽게 하려면, **중복 코드의 작성을 최소화 하고, 코드 변경이 쉬워야 한다.** 이를 위해 기존 코드의 **재사용성을 높이는 방향으로** 설계해야 한다. 특히 객체지향의 특성을 십분 활용하여 좀 더 **역할을 세분화하고 역할 간 의존성을 최소화한다면,** 변화무쌍한 업무 환경에 대응하기 쉬운 시스템을 개발할 수 있다.

* **현재 대부분의 기업용 애플리케이션 개발에 적용되는 MVC 아키텍처 패턴**

<img src="../capture/스크린샷 2019-09-09 오후 11.00.21.png">

> MVC 구조에서는 클라이언트의 요청 처리를 서블릿 혼자서 담당하지 않고 세 개의 컴포넌트가 나누어 처리한다.

<br>

### MVC의 각 컴포넌트 역할

* **컨트롤러(controller) 컴포넌트의 역할** 
  * 클라이언트의 요청을 받았을 때 그 요청에 대해 실제 업무를 수행하는 <u>모델 컴포넌트를 호출하는 일</u>
  * 모델을 호출할 때 전달하기 쉽게 <u>데이터를 적절히 가공하는 일</u> 
  * 모델이 업무 수행을 완료하면, 그 결과를 가지고 화면을 생성하도록 <u>뷰에게 전달하는 일</u>

<br>

* **모델(model) 컴포넌트의 역할**
  * 데이터 저장소(예: 데이터베이스, 디렉터리 서비스 등)와  연동하여 <u>사용자가 입력한 데이터나 사용자에게 출력할 데이터를 다루는 일</u>
  * 여러 개의 데이터 변경 작업을 하나의 작업으로 묶은 <u>트랜잭션을 다루는 일</u>

<br>

* **뷰(view) 컴포넌트의 역할**
  * 모델이 처리한 데이터나 그 작업 결과를 가지고 사용자에게 <u>출력할 화면을 만드는 일</u>
  * HTML 과 CSS, JavaScript 를 사용하여 웹 브라우저가 출력할 <u>UI를 만드는 일</u>

<br>

### 높은 재사용성, 넓은 융통성

1. **룩앤필(look and feel)을** 쉽게 교체할 수 있다. 즉 화면 생성 부분을 별도의 컴포넌트로 분리하였기 때문에, 뷰 교체만으로 **사용자 화면을 손쉽게 바꿀 수 있다.**
2. **원 소스 멀티 유즈(one source multi use)를** 구현할 수 있다. 모델 컴포넌트가 작업한 결과를 다양한 뷰 컴포넌트를 통하여 PDF 나 HTML, XML, JSON(JavaScript Object) 등 **클라이언트가 원하는 형식으로 출력할 수 있다.**
3. **코드를 재사용할 수 있다.** 화면을 바꾸거나 데이터 형식을 바꾸더라도 모델 컴포넌트는 그대로 재사용할 수 있다.

<br>

### 빠른 개발, 저렴한 비용

1. 모델 컴포넌트를 재사용할 수 있기 때문에 **개발 속도가 빨리진다.** 
2. 컴포넌트를 쪼개게 되면, 그 컴포넌트의 난이도에 따라 좀 더 낮은 수준의 개발자를 투입할 수 있어서 **전체적인 개발 및 유지보수 비용을 줄일 수 있다.**

<br>

## 5.1.4. MVC 구동 원리

* **MVC 아키텍처를 적용한 전형적인 웹 애플리케이션의 예 (MVC 구조의 실행 흐름)**

  <img src="../capture/스크린샷 2019-09-09 오후 11.37.39.png">

  1. 웹 브라우저가 웹 애플리케이션 실행을 요청하면, **웹 서버가 그 요청을 받아서** 서블린 컨테이너(예: 톰켓 서버)에 넘겨 준다. **서블릿 컨테이너는 URL을 확인하여 그 요청을 처리할 서블릿을 찾아서 실행한다.**
  2. **서블릿은 실제 업무를 처리하는 모델 자바 객체의 메서드를 호출한다.** 만약 웹 브라우저가 보낸 데이터를 저장하거나 변경해야 한다면 그 **데이터를 가공하여 값 객체(VO; Value Object)를 생성하고, 모델 객체의 메서드를 호출할 때 인자값으로 넘긴다. **
  3. 모델 객체는 **JDBC를 사용하여 매개변수로 넘어온 값 객체를 데이터베이스에 저장하거나, 데이터베이스로부터 질의 결과를 가져와서 값 객체로 만들어 반환한다.** 이렇게 **값 객체는** 객체와 객체 사이에 데이터를 전달하는 용도로 사용하기 때문에 **'데이터 전송 객체(DTO; Data Transfer Object)'** 라도고 부른다.
  4. 서블릿은 모델 객체로부터 반환받은 값을 **JSP에 전달한다.**
  5. **JSP는** 서블릿으로부터 전달받은 **값 객체를 참조하여 웹 브라우저가 출력할 결과 화면을 만든다.** 그리고 **웹 브라우저에 출력함으로써 요청 처리를 완료한다.**
  6. 웹 브라우저는 서버로부터 받은 응답 내용을 화면에 출력한다.

  <br>

이처럼 MVC 아키텍처는 **서로 오고 가는 절차(트레이드오프: trade-off)** 가 많아서 복잡해 보일 수 있다. 하지만 코드들이 **역할 단위로 쪼개져 있어 유지 보수하기가 쉽다.**

<br>

# 5.2. 뷰 컴포넌트와 JSP

MVC 아키텍처에서 뷰 컴포넌트를 만들 때 보통 JSP를 사용한다. 뷰 컴포넌트의 역할은 웹 브라우저가 출력할 화면을 만드는 일이다. 그렇다면 **JSP는 화면 생성을 쉽게 해주는 기술이라고** 볼 수 있다.

<br>

## 5.2.1. JSP를 사용하는 이유

* **회원 등록 양식 화면의 일부**

  ```java
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    PrintWriter out = resp.getWriter();
    out.println("<html><head><title>회원 등록</title></head>");
    out.println("<body><h1>회원 등록</h1>");
    out.println("<form action='add' method='post'>");
    out.println("이름: <input type='text' name='name'><br>");
    out.println("이메일: <input type='text' name='email'><br>");
    out.println("암호: <input type='password' name='password'><br>");
    out.println("<input type='submit' value='추가'>");
    out.println("<input type='reset' value='취소'>");
    out.println("</form>");
    out.println("</body></html>");
  }
  ```

  * HTML을 출력하려고 out.println() 를 호출하고 있다. 출력하는 문자열에 태그 속성이 포함되어 있는데 인용 부호(' ')로 인해 코드가 더 복잡해 보인다.

<br>

그래서 많은 양의 HTML을 out.println()을 사용하여 출력한다면 거의 재앙이라 할 수 있다. 그래서 이런 부분을 해소하고자 나온 기술이 **JSP** 이다.

<br>

## 5.2.2. JSP 구동 원리

JSP 기술의 가장 중요한 목적은 콘텐츠를 출력하는 **코딩을 단순화하는 것이다.**

* **JSP의 실행**

  <img src="../capture/스크린샷 2019-09-10 오전 12.01.23.png">

  1. 개발자는 **서버에 JSP 파일을 작성해 둔다.** 클라이언트가 JSP를 실행해 달라고 요청하면, **서블릿 컨테이너는 JSP 파일에 대응하는 자바 서블릿을 찾아서 실행한다.**
  2. 만약 JSP에 대응하는 서블릿이 없거나 JSP 파일이 변경되었다면, **JSP 엔진을 통해 JSP 파일을 해석하여 서블릿 자바 소스를 생성한다.**
  3. **서블릿 자바 소스는 자바 컴파일러를 통해 서블릿 클래스 파일로 컴파일된다.** JSP 파일을 바꿀 때 마다 이 과정을 반복한다.
  4. JSP 로부터 생성된 서블릿은 서블릿 구동 방식에 따라 실행된다. 즉 **서블릿의 service() 메서드가 호출되고, 출력 메서드를 통해 서블릿이 생성한 HTML 화면을 웹 브라우저로 보낸다.**

<br>

JSP를 사용하면 HTML을 작성하기 쉬워지므로 **뷰 컴포넌트를 만들 때 주로 사용된다.** 즉, **JSP는 서블릿 자바 파일을 만들기 위한 템플릿으로 사용된다.**

<br>

## 5.2.3. JSP의 구동 과정 확인

* **web/WEB-INF/web.xml**

  ```xml
  ...
  <!-- welcomefile 추가 -->
  <welcome-file-list>
    <welcome-file>hello.jsp</welcome-file>
  </welcome-file-list>
  ...
  ```

* **web/hello.jsp**

  ```jsp
  <%--
    Created by IntelliJ IDEA.
    User: sangminlee
    Date: 10/09/2019
      Time: 12:12 오전
    To change this template use File | Settings | File Templates.
        --%>
  <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <html>
    <head>
      <title>Hello</title>
    </head>
    <body>
      <p>안녕하세요</p>
    </body>
  </html>
  ```

* **결과확인**

  <img src="../capture/스크린샷 2019-09-10 오전 12.16.28.png" width=500>

**JSP를 실행할 때 비로소 JSP 엔진은 서블릿을 만든다.** 즉, JSP가 직접 실행되는 것이 아니라 JSP로부터 만들어진 서블릿이 실행된다.

<br>

## 5.2.4. HttpJspPage 인터페이스

JSP 엔진은 JSP 파일로부터 서블릿 클래스를 생성할 때 HttpJspPage 인터페이스를 구현한 클래스를 만든다.

* **HttpJspPage 인터페이스의 상속 관계**

  <img src="../capture/스크린샷 2019-09-10 오전 8.34.35.png" width=500>

  * **HttpJspPage를** 구현한다는 것은 결국 **Servlet 인터페이스도** 구현한다는 것이기 때문에 해당 클래스는 서블릿이 될 수밖에 없다.

<br>

### jspInit()

JspPage에 선언된 **jspInit()는 JSP 객체(JSP로부터 만들어진 서블릿 객체)가 생성될 때 호출된다.** 만약 JSP 페이지에서 init()를 오버라이딩할 일이 있다면 init() 대신 jspInit()를 오버라이딩 하세요.

<br>

### jspDestroy()

이 메소드는 **JSP 객체(JSP로부터 만들어진 서블릿 객체)가 언로드(Unload) 될 때 호출된다.** 만약 JSP 페이지에서 destroy() 오버라이딩 할 일이 있다면 destroy() 대신 jspDestroy()를 오버라이딩 해야 한다.

<br>

### _jspService()

HttpJspPage 에 선언된 **_jspService() 는 JSP 페이지가 해야 할 작업이 들어 있는 메서드이다.** 서블릿 컨테이너가 service() 를 호출하면 service() 메서드 내부에서는 바로 이 메서드를 호출함으로써 JSP 페이지에 작성했던 코드들이 실행되는 것이다.

<br>

## 5.2.5. JSP 객체의 실체 분석

### HttpJspBase 클래스

HttpJspBase는 톰캣 서버에서 제공하는 클래스이다. 이 클래스를 분석해 보면 결국 HttpServlet 클래스를 상속받았고 HttpJspPage 인터페이스를 구현하였다. 즉 **HttpJspBase를 상속받은 JSP 객체는 서블릿이다.**

<br>

### JSP 내장 객체

**_jspService()의** 매개변수는 HttpServletRequest 와 HttpServletResponse 객체이다. doGet() 과 doPost() 의 매개변수와 같다. 다만, **매개변수의 이름은 반드시 request, response로 해야 한다.**

```java
public void _jspService(
	final javax.servlet.http.HttpServletRequest request,
  final javax.servlet.http.HttpServletResponse response)
```

<br>

그리고 _jspServlet()에 선언된 로컬 변수 중에서 다음의 참조 변수들은 반드시 이 이름으로 존재해야 한다. 물론 이 변수들에는 해당 객체의 주소가 할당된다.

```java
final javax.servlet.jsp.PageContext pageContext;
javax.servlet.http.HttpSession session = null;
final javax.servlet.ServletContext application;
final javax.servlet.ServletConfig config;
javax.servlet.jsp.JspWriter out = null;
final java.lang.Object page = this;
```

이렇게 _jspService()에 선언된 request, response, pageContext, session, application, config, out, page, exception 객체들은 이 메서드가 호출될 때 반드시 준비되는 객체들이기 때문에, 이 객체들을 가리켜 **'JSP 내장 객체(Implicit Objects)'** 라 부른다. 이 객체들은 별도의 선언 없이 JSP 페이지에서 마음껏 사용할 수 있다.

<br>

### JSP 출력문

HTML을 출력하고 싶으면 JSP에서는 그냥 출력할 내용을 작성하면 된다. 

<br>

## 5.2.6. HttpJspBase 클래스의 소스

톰캣 서버의 JSP 엔진은 서블릿 소스를 생성할 때 슈퍼 클래스로서 HttpJspBase를 사용한다. 당연히 이 클래스는 톰캣 서버에 포함된 클래스이다.

* **HttpServlet.java 클래스의 일부분**

  ```java
  public abstract class HttpJspBase extends HttpServlet implements HttpJspPage {
    ...
    @Override
    public final void init(ServletConfig config) throws ServletException {
      super.init(config);
      jspInit();
      _jspInit();
    }
    
    @Override
    public final void destroy() {
      jspDestroy();
      _jspDestroy();
    }
    
    @Override
    public final void service(
      HttpServletRequest request, HttpServletResponse response) throws ServletException,
    IOException {
      _jspService(request, response);
    }
    
    @Override
    public void jspInit() {}
    
    @Override
    public void jspDestroy() {}
    
    @Override
    public abstract void _jspService(
    	HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    )
  }
  ```

  * HttpJspBase는 HttpServlet을 상속받기 때문에 이 클래스의 자식 클래스는 당연히 서블릿 클래스가 되는 것이다.
  * init()가 호출되면 init()에서는 jspInit()를 호출
  * destroy()에서는 jspDestroy()를 호출
  * service()도 마찬가지로 _jspService()를 호출

<br>

## 5.2.7. JSP 프리컴파일

 **미리 자바 서블릿을 만들게 되면,** JSP를 실행할 때마다 JSP 파일이 변경되었는지, JSP 파일에 대해 서블릿 파일이 있는지 **매번 검사할 필요가 없다.** 또한 JSP 파일에 대해 자바 서블릿 소스를 생성하고 **컴파일하는 과정이 없어서 실행 속도를 높일 수 있다.**

이 방식의 문제는 JSP를 편집하면 서버를 다시 시작해야 한다. 그래야만 변경된 JSP에 대해 서블릿 코드가 다시 생성된다. **어느정도 안정화된 이후에 JSP 프리컴파일(Precompile)을 고려해라.**

<br>

# 5.3. JSP의 주요 구성 요소

**JSP를 구성하는 요소**

* **템플릿 데이터**
  * 클라이언트로 출력되는 콘텐츠(ex: HTML, 자바스크립트, 스타일 시트, JSON 형식 문자열, XML, 일반 텍스트 등)
* **JSP 전용 태그**
  * 특정 자바 명령문으로 바뀌는 태그

<br>

## 5.3.1. JSP로 만드는 계산기 실습

* **web/calc/Calculator.jsp**

  ```jsp
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
  
      if (request.getParameter("v1") != null) {
          v1 = request.getParameter("v1");
          v2 = request.getParameter("v2");
          String op = request.getParameter("op");
  
          result = calculate(
                  Integer.parseInt(v1),
                  Integer.parseInt(v2),
                  op);
  
          if ("+".equals(op)) {
              selected[0] = "selected";
          } else if ("-".equals(op)) {
              selected[1] = "selected";
          } else if ("*".equals(op)) {
              selected[2] = "selected";
          } else if ("/".equals(op)) {
              selected[3] = "selected";
          }
      }
  %>
  <html>
  <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>계산기</title>
  </head>
  <body>
  <h2>JSP 계산기</h2>
  <form action="Calculator.jsp" method="get">
      <input type="text" name="v1" size="4" value="<%=v1%>">
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
  ```

* **웹 브라우저에서 localhost:8080/calc/Calculator.jsp 를 주소창에 입력한다.**

<img src="../capture/스크린샷 2019-09-10 오후 9.14.17.png" width=500>

* **계산기 화면에서 값을 입력하고 <=> 버튼을 누르면, 계산기 입력 화면이 출력되면서 계산 결과가 출력된다.**

  <img src="../capture/스크린샷 2019-09-10 오후 9.16.06.png" width=500>

  * \<form> 태그의 action 속성값을 Calculator.jsp 로 설정했기 때문에 계산 결과도 이 JSP가 처리한다. 즉 값을 계산하기 위해 '=' 버튼을 누르면 다시 Calculator.jsp 를 요청하도록 했다.

    ```jsp
    <form action="Calculator.jsp" method="get">
    ```

<br>

## 5.3.2. 템플릿 데이터

**템플릿 데이터는 클라이언트로 출력되는 콘텐츠이다.** 즉 HTML이나 XML, 자바스크립트, 스타일 시트, JSON 문자열, 일반 텍스트 등을 말한다.

템플릿 데이터는 서블릿 코드를 생성할 때 출력문으로 바뀐다. 클라이언트로 출력할 내용은 일반 문서 작성하듯이 적어 넣는다. 이렇게 작성한 텍스트를 템플릿 데이터라 부른다.

- **web/calc/Calculator.jsp**

  ```jsp
  <html>
  <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>계산기</title>
  </head>
  <body>
  <h2>JSP 계산기</h2>
  <form action="Calculator.jsp" method="get">
    ...
  </form>
  </body>
  </html>
  ```

<br>

### 5.3.3. JSP 전용 태그 - 지시자

**\<%@ 지시자 ... %>** 는 JSP 전용 태그로 '지시자(Directives)' 나 '속성'에 따라 특별한 자바 코드를 생성한다. JSP 지시자에는 **page, taglib, include** 가 있다.

- **지시자 선언**

  ```jsp
  <%@ 지시자 속성="값" 속성="값" ... %>
  ```

<br>

### page 지시자

page 지시자는 **JSP 페이지와 관련된 속성을 정의할 때 사용하는 태그이다.** 

- **Calculator.jsp 의 page 지시자**

  ```jsp
  <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  ```

  - **language 속성** : 스크립트릿(Scriptlet)이나 표현식(Expression element), 선언부(Declaration element)를 작성할 때 사용할 프로그래밍 언어를 지정한다. language 속성을 생략하면 기본으로 'java' 입니다.
  - **contentType 속성** : 출력할 데이터의 MIME 타입과 문자 집합을 지정한다. 예제 코드에서 값 'text/html'은 출력할 데이터가 HTML 텍스트임을 가리킨다. 'charset=UTF-8'은 출력할 데이터를 UTF-8로 변환(encoding)할 것을 지시한다.
  - **pageEncoding 속성** : 출력할 데이터의 문자 집합을 지정한다. 기본값은 'ISO-8859-1' 이다.

<br>

## 5.3.4. JSP 전용 태그 - 스크립트릿

JSP 페이지 안에 자바 코드를 넣을 때는 스크립트릿(Scriptlet Elements) 태그\<% %> 안에 작성한다.

- **스크립트릿**

  ```jsp
  <% 자바 코드 %>
  ```

- **web/calc/Calculator.jsp 의 스크립트릿**

  ```jsp
  <%
  String v1 = "";
  String v2 = "";
  String result = "";
  String[] selected = {"", "", "", ""};
  
  // 값이 있을 때만 꺼낸다.
  if (request.getParameter("v1") != null) {
    v1 = request.getParameter("v1");
    v2 = request.getParameter("v2");
    String op = request.getParameter("op");
    
    result = calculate(Integer.parseInt(v1), Integer.parseInt(v2), op);
  }
  %>
  ```

  - **\<% %> 안에는 클라이언트가 보낸 매개변수 값을 임시 변수에 저장하고 그 계산 결과도 임시 변수에 저장하는** 자바 코드가 들어 있다.

<br>

## 5.3.5. JSP 내장 객체

JSP 페이지에서 스크립트릿 \<% %> 이나 표현식 \<%=%> 을 작성할 때 별도의 선언 없이 사용하는 자바 객체가 있다. 이런 객체를 **JSP 내장 객체(Implicit Objects)라 한다.**

- **request 객체**

  ```java
  v1 = reqeust.getParameter("v1");
  v2 = request.getParameter("v2");
  String op = request.getParameter("op");
  ```

<br>

## 5.3.6. JSP 전용 태그 - 선언문

**JSP 선언문(Declarations) \<%! %>** 은 서블릿 클래스의 멤버(변수나 메서드)를 선언할 때 사용하는 태그이다. 선언문을 작성하는 위치는 위, 아래, 가운데 어디든 상관없다.

- **선언문**

  ```jsp
  <%! 멤버 변수 및 메서드 선언 %>
  ```

- **web/calc/Calculator.jsp 의 JSP 선언문**

  ```jsp
  <%!
  private String calculate(int a, int b, String op) {
    int r = 0;
    
    if ("+".equals(op)) {
      r = a + b;
    }
    ...
    return Integer.toString(r);
  }
  %>
  ```

  - 계산을 수행하는 calculate() 메서드를 선언문 태그 \<%! %> 에 작성하였다. **calculate()는 클라이언트가 보낸 매개변수 값을 계산하여 그 결과를 문자열로 바꾸어 반환한다.**

<br>

## 5.3.7. JSP 전용 태그 - 표현식

**표현식(Expressions) 태그는 문자열을 출력할 때 사용한다.** 따라서 표현식 **\<%= %>** 안에는 결과를 반환하는 자바 코드가 와야 한다.

- **표현식 태그**

  ```jsp
  <%= 결과를 반환하는 자바 표현식 %>
  ```

- **web/calc/Calculator.jsp 의 JSP 표현식**

  ```jsp
  <input type="text" name="v1" size="4" value="<%=v1%>">
  <select name="op">
    <option value="+" <%=selected[0]%>>+</option>
    <option value="-" <%=selected[1]%>>-</option>
    <option value="*" <%=selected[2]%>>*</option>
    <option value="/" <%=selected[3]%>>/</option>
  </select>
  <input type="text" name="v2" size="4" value="<%=v2%>">
  <input type="submit" value="=">
  <input type="text" size="8" value="<%=result%>"><br>
  ```

  - \<input> 태그에 value 속성을 추가하였다. value 속성의 값은 표현식\<%= %> 태그를 사용하여 출력한다.
  - 계산 요청이 들어오면 op 연산자 값이 무엇이냐에 따라 selected[] 배열에서 그 연산자에 해당하는 항목을 찾아 'selected' 문자열을 넣는다.

<br>

서블릿 소스를 보면 JSP 표현식 안에 있던 자바 코드가 out.println() 출력의 인자값(argument)으로 복사된다. 즉 **JSP 엔진은 표현식 태그에 작성한 자바 코드를 출력문의 인자값으로 복사한다.**

<br>

# 5.4. 서블릿에서 뷰 분리하기

- **뷰 컴포넌트 도입**

  <img src="../capture/스크린샷 2019-09-10 오후 10.20.05.png">

  - 클라이언트로부터 요청이 들어오면 서블릿은 **데이터 준비(모델 역할)하여 JSP에 전달(컨트롤러 역할)한다.** JSP는 서블릿이 준비한 데이터를 가지고 웹 브라우저로 출력할 화면을 만든다.

<br>

## 5.4.1. 값 객체(VO) = 데이터 수송 객체(DTO)

**'값 객체(value object)' 란** 데이터베이스에서 가져온 JSP 페이지에 전달하기 위한 정보를 담는 객체이다. 값 객체는 계층 간 또는 객체 간에 데이터 전달하는데 이용하므로 **'데이터 수송 객체(data transfer object)'** 라고도 부른다. 또한 값 객체는 업무영역(business domain)의 데이터를 표현하기 때문에 객체지향 분석 및 설계 분야에서는 **'도메인 객체(domain obejct)'** 라고도 한다.

- **값 객체**

  <img src="../capture/스크린샷 2019-09-10 오후 10.25.36.png">

<br>

## 5.4.2. 뷰 분리하기 실습

- **Model-View-Controller 올인원 방식 (이전의 회원 목록 서블릿)**

  <img src="../capture/스크린샷 2019-09-10 오후 10.27.48.png">

<br>

- **회원 목록 서블릿으로 부터 출력 부분을 분리하여 JSP 페이지를 만듬**

  <img src="../capture/스크린샷 2019-09-10 오후 10.31.08.png">

  - 출력은 JSP에서 맡게 될 것이며 서블릿에서 준비한 데이터를 JSP 전달할 것이다.

<br>

## 5.4.3. 값 객체 생성

값 객체 역할을 수행할 Member 클래스를 생성

- **src/spms/vo/Member.java**

  ```java
  package spms.vo;
  
  import java.util.Date;
  
  public class Member {
  
    protected int no;
    protected String name;
    protected String email;
    protected String password;
    protected Date createDate;
    protected Date modifiedDate;
  
    public int getNo() {
      return no;
    }
  
    public Member setNo(int no) {
      this.no = no;
      return this;
    }
  
    public String getName() {
      return name;
    }
  
    public Member setName(String name) {
      this.name = name;
      return this;
    }
  
    public String getEmail() {
      return email;
    }
  
    public Member setEmail(String email) {
      this.email = email;
      return this;
    }
  
    public String getPassword() {
      return password;
    }
  
    public Member setPassword(String password) {
      this.password = password;
      return this;
    }
  
    public Date getCreateDate() {
      return createDate;
    }
  
    public Member setCreateDate(Date createDate) {
      this.createDate = createDate;
      return this;
    }
  
    public Date getModifiedDate() {
      return modifiedDate;
    }
  
    public Member setModifiedDate(Date modifiedDate) {
      this.modifiedDate = modifiedDate;
      return this;
    }
  }
  ```

  - 보통 데이터베이스 테이블에 대응하여 값 객체를 정의한다.

  -  **주의할 점은 셋터(Setter) 메서드의 리턴값이 void가 아니라 Member 이다.** 그 이유는 셋터 메서드를 연속으로 호출하여 값을 할당하게 하기 위함이다.

    ```java
    new Member().setNo(1).setName("홍길동").setEmail("hong@test.com");
    ```

<br>

## 5.4.4. 서블릿에서 뷰 관련 코드 제거

MemberListServlet 클래스에서 뷰 역할을 분리하기 위해 출력 코드를 제거한다.

- **src/spms/servlets/MemberListServlet.java**

  ```java
  package spms.servlets;
  
  import spms.vo.Member;
  
  import javax.servlet.*;
  import javax.servlet.annotation.WebServlet;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  import java.io.PrintWriter;
  import java.sql.*;
  import java.util.ArrayList;
  
  // 서블릿을 만들고자 서블릿 어노테이션을 쓰고
  // GenericServlet 을 상속받는다.
  @WebServlet("/member/list")
  public class MemberListServlet extends HttpServlet {
  
    private ServletContext sc;
  
    static {
      try {
        Class.forName("org.postgresql.Driver");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  
    @Override
    public void init() throws ServletException {
      super.init();
      sc = this.getServletContext();
    }
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String query = "select mno, mname, email, cre_date" +
          " from members" +
          " order by mno";
      
      // 데이터 베이스 연결 및 쿼리 실행
      try (Connection connection = DriverManager.getConnection(
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));
           PreparedStatement statement = connection.prepareStatement(query);
           ResultSet resultSet = statement.executeQuery()) {
        resp.setContentType("text/html; charset=UTF-8");
        ArrayList<Member> members = new ArrayList<>();
  
        // 데이터베이스에서 회원 정보를 가져와 Member 에 담는다.
        // 그리고 Member 객체를 ArrayList 에 추가한다.
        while (resultSet.next()) {
          members.add(new Member()
          .setNo(resultSet.getInt("mno"))
          .setName(resultSet.getString("mname"))
          .setEmail(resultSet.getString("email"))
          .setCreateDate(resultSet.getDate("cre_date")));
        }
  
        // request 에 회원 목록 데이터 보관한다.
        req.setAttribute("members", members);
  
        // JSP 로 출력을 위임한다.
        RequestDispatcher rd = req.getRequestDispatcher(
            "/member/MemberList.jsp");
        rd.include(req, resp);
  
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }
  }
  ```

  - HTML 출력 코드 제거 (ex: out.println())

  - JSP에 전달할 회원 목록 데이터를 준비한다.

    ```java
    ArrayList<Member> members = new ArrayList<Member>();
    while(rs.next()) {
      members.add(new Member()
                 .setNo(rs.getInt("mno"))
                 .setName(rs.getString("mname"))
                 .setEmail(rs.getString("email"))
                 .setCreatedDate(rs.getDate("cre_date")));
    }
    ```

  - RequestDispatcher를 이용한 forward, include 사용, 회원 목록 데이터가 준비되었으면, 화면 생성을 위해 JSP로 작업을 위임해야 한다.

    ```java
    ReqeustDispatcher rd = request.getRequestDispatcher(
                                      "/member/MemberList.jsp");
    rd.include(req, resp);
    ```

    > RequestDispatcher를 얻을 때, 반드시 어떤 서블릿(또는 JSP)으로 위임할 것인지 알려 줘야 한다.

  - ServletRequest(HttpServletRequest)를 통한 데이터를 전달한다. setAttribute()를 호출하여 값을 보관할 수 있고, getAttribute()를 호출하여 보관된 값을 꺼낼 수 있다.

    ```java
    request.setAttribute("members", members);
    ```

    > MemberListServlet의 request 객체는 MemberList.jsp 와 공유하기 때문에, request에 값을 담아 두면 MemberList.jsp 에서 써내 쓸 수 있다.

<br>

## 5.4.5. 뷰 컴포넌트 만들기

MemberListServlet으로부터 받은 회원 목록 데이터를 가지고 화면을 생성하는 JSP를 만들어 보자.

- **web/member/MemberList.jsp**

  ```jsp
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
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>회원 목록</title>
    </head>
    <body>
      <h1>회원 목록</h1>
      <p><a href="add">신규 회원</a></p>
      <%
      ArrayList<Member> members = (ArrayList<Member>) request.getAttribute("members");
      for (Member member : members) {
        %>
      <%=member.getNo()%>,
      <a href="update?no=<%=member.getNo()%>"><%=member.getName()%>
      </a>,
      <%=member.getEmail()%>,
      <%=member.getCreateDate()%>
      <a href="delete?no=<%=member.getNo()%>">[삭제]</a><br>
      <%} %>
    </body>
  </html>
  ```

  - Member 클래스와 ArrayList 클래스를 사용해야 하므로 **import를 처리하는 page 지시자 추가**

    ```jsp
    <%@ page import="spmps.vo.Member"%>
    <%@ page import="java.util.ArrayList"%>
    ```

  - MemberListServlet이 넘겨준 회원 목록 데이터를 꺼내고자 스크립트릿 태그를 사용하여 getAttribute()를 호출하였다.

    ```jsp
    <%
    ArrayList<Member> members = (ArrayList<Memeber>)request.getAttribute("members");
    for(Member member : members) {
    %>
    ```

    > request로부터 회원 목록을 꺼내고 나서 반복문을 사용하여 회원 정보를 출력한다.

  - 번호와 이름, 이메일, 등록일을 출력하기 위해 **JSP 표현식 \<%= %>을 사용한다.**

    ```jsp
    <%=member.getNo()%>,
    <a href="update?no=<%=member.getNo()%>"><%=member.getName()%></a>,
    <%=member.getEmail()%>,
    <%=member.getCreatedDate()%>
    <a href="delete?no=<%=member.getNo()%>">[삭제]</a><br>
    ```

  <br>

## 5.4.6. 회원 목록 테스트

- http://localhost:8080/member/list 웹 브라우저로 테스트

  <img src="../capture/스크린샷 2019-09-10 오후 11.15.38.png" width=300>

  - 이처럼 서블릿에서 출력 작업을 분리하여 JSP에 위임하는 구조로 만들면 출력문을 작성하기도 쉽고, 소스 코드를 유지 보수하기도 편리하다.

<br>

# 5.5. 포워딩과 인클루딩

서블릿끼리 작업을 위임하는 방법은 **포워딩(Forwarding)과 인클루딩(Including)** 이렇게 두 가지 방법이 있다.

- **포워드 위임 방식**

  <img src="../capture/스크린샷 2019-09-10 오후 11.31.07.png" width=500>

  1. 웹 브라우저가 '서블릿 A' 를 요청하면, 서블릿 A는 작업을 수행한다.
  2. 서블릿 A에서 '서블릿 B'로 실행을 위임한다.
  3. 서블릿 B는 작업을 수행하고 나서 응답을 완료한다.

  > 포워드 방식은 작업을 한 번 위임하면 **다시 이전 서블릿으로 제어권이 돌아오지 않는다.**

<br>

- **인클루드 위임 방식**

  <img src="../capture/스크린샷 2019-09-10 오후 11.33.50.png" width=500>

  1. 웹 브라우저가 '서블릿 A' 를 요청하면, 서블릿 A는 작업을 수행한다.
  2. 서블릿 A 에서 '서블릿 B' 로 실행을 위임한다.
  3. 서블릿 B 는 작업을 수행하고 나서 다시 서블릿 A로 제어권을 넘긴다.
  4. 서블릿 A는 나머지 작업을 수행한 후 응답을 완료한다.

  > 인클루드 방식은 다른 서블릿으로 작업을 위임한 후, 그 서블릿의 실행이 끝나면 **다시 이전 서블릿으로 제어권이 넘어온다.**

<br>

## 5.5.1. 포워딩과 인클루딩 실습

서블릿을 실행하다가 오류가 발생하면, 이 부분을 포워딩으로 오류 정보를 출력하는 페이지로 위임해보자.

- **포워딩 실습 시나리오**

  <img src="../capture/스크린샷 2019-09-11 오전 12.13.31.png">

<br>

- **인클루딩 실습 시나리오**

  <img src="../capture/스크린샷 2019-09-11 오전 12.17.57.png">

  1. MemebrListServlet 요청
  2. MemberListServlet 는 데이터베이스에서 회원 정보를 가져온다. 그리고 출력을 위해 MemberList.jsp로 실행 위임(인클루딩 방식)
  3. Header.jsp 는 화면 상단 내용에 출력, 다시 MemberList.jps 로 제어권 넘김
  4. MemberList.jsp 는 화면의 하단 내용 출력을 위해 Tail.jsp 로 실행을 위임(인클루딩 방식)
  5. Tail.jsp 는 하단 내용을 출력하고, 다시 MemberList.jsp 로 제어권을 넘김
  6. MemberList.jsp 는 마무리 출력을 수행한 다음, 제어권을 MemberListServlet 으로 넘김
  7. MemberListServlet은 응답을 완료

<br>

## 5.5.2. 포워딩

MemberListServlet 소스를 보면, 데이터베이스와 연동하여 작업하다가 문제가 발생하면, **ServletException 객체를 던지게 되어 있다.**

```java
} catch (Exception e) {
  throw new ServletException(e);
}
```

톰캣 서버가 서블릿을 호출하는 것이기 때문에, **서블릿이 던진 ServletException 객체는 톰캣 서버가 받는다.**

<br>

### 예외 발생 테스트

MySQL 데이터베이스 연결을 실패해본다.

- **예외가 발생한 경우의 화면**

  <img src="../capture/스크린샷 2019-09-11 오전 12.24.23.png" width=500>

  - 에러 발생시 서블릿을 호출한 톰캣 서버는 서블릿이 던진 예외 객체를 받게 되고, 앞의 그림과 같은 결과 화면을 웹 브라우저에게 보낸다.

<br>

### 포워딩을 이용한 예외 처리

예외가 발생하면 앞의 화면 내용보다는 좀 더 부드러운 안내 문구를 출력하는 JSP로 위임해보자.

- **web/Error.jsp**

  ```jsp
  <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>시스템 오류!</title>
    </head>
    <body>
      <p>요청을 처리하는 중에 문제가 발생하였습니다. 잠시 후에 다시 요청하시기 바랍니다.
        만약 계속해서 이 문제가 발생한다면 시스템 운영팀(사내번호: 8282)에 연락하기 바랍니다.</p>
    </body>
  </html>
  ```

- **src/spms/servlets/MemberListServlet.java**

  ```java
  ...
  } catch (Exception e) {
    //      throw new ServletException(e);
    req.setAttribute("error", e);
    RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
    rd.forward(req, resp);
  }
  ...
  ```

  - 예외를 던지는 코드를 제거하고, **Error.jsp로 포워딩하는 코드를 추가한다.** 나중에 오류에 대한 상세 내용을 출력할 때 사용하기 위해 예외 객체를 request에 보관해 둔다.

- **실행 결과**

  <img src="../capture/스크린샷 2019-09-11 오전 12.28.24.png">

<br>

## 5.5.3. 인클루딩

MemberList.jsp 에서 인클루딩을 이용하여 상단 내용을 출력하는 Header.jsp 와 하단 내용을 출력하는 Tail.jsp 를 포함해보자.

<br>

### Header.jsp 만들기

- **web/Header.jsp**

  ```jsp
  <%@ page language="java" contentType="text/html; charset=UTF-8"%>
  <div style="background-color: #00008b; color: #ffffff; height: 20px; 
              padding: 5px;">
    SPMS(Simple Project Management System)
  </div>
  ```

  - **\<div> 태그**
    - **style 속성** : 출력되는 내용의 모양을 정의

<br>

### Tail.jsp 만들기

- **web/Tail.jsp**

  ```jsp
  <%$ page language="java" contentType="text/html; charset=UTF-8"%>
  <div style="background-color: #f0fff0; height: 20px; padding: 5px; margin-top: 10px">
    SPMS &copy; 2013
  </div>
  ```

<br>

### MemberList.jsp 에서 Header.jsp 와 Tail.jsp를 포함하기

- **web/member/MemberList.jsp**

  ```jsp
  <%@ page import="spms.vo.Member" %>
  <%@ page import="java.util.ArrayList" %>
  <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>회원 목록</title>
    </head>
    <body>
      <jsp:include page="/Header.jsp"/>
      <h1>회원 목록</h1>
      <p><a href="add">신규 회원</a></p>
      <%
      ArrayList<Member> members = (ArrayList<Member>) request.getAttribute("members");
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
  ```

  - \<jsp:include> 태그를 추가한다.

    ```jsp
    <jsp:include page="/Header.jsp"/>
    ...
    <jsp:include page="/Tail.jsp"/>
    ```

    이 태그들은 JSP 엔진에 의해 서블릿이 생성될 때 다음과 같은 코드로 바뀐다.

    ```java
    RequestDispatcher rd = request.getRequestDispatcher("/Header.jsp");
    rd.include(request, response);
    ```

- **실행 결과**

  <img src="../capture/스크린샷 2019-09-11 오전 12.35.50.png" width=300>

<br>

# 5.6. 데이터 보관소

서블릿 기술은 데이터를 공유하기 위한 방안으로 네 가지 종류의 데이터 보관소를 제공한다.

- **네 가지 객체 보관소**

  <img src="https://t1.daumcdn.net/cfile/tistory/2459234C55AD02A621">

  - **ServletContext 보관소** : 웹 애플리케이션이 시작될 때 생성되어 종료될 때까지 유지된다. 웹 애플리케이션이 실행되는 동안에는 모든 서블릿이 사용할 수 있다. <u>JSP에서는 application 변수를 통해 이 보관소를 참조할 수 있다.</u>
  - **HttpSession 보관소** : 클라이언트의 최초 요청시 생성되어 브라우저를 닫을 때까지 유지된다. <u>JSP에서는 session 변수를 통해 이 보관소를 참조할 수 있다.</u>
  - **ServletRequest 보관소** : 클라이언트의 요청이 들어올 때 생성되어, 클라이언트에게 응답할 때까지 유지된다. 이 보관소는 포워딩이나 인클루딩하는 서블릿들 사이에서 값을 공유할 때 유용하다. <u>JSP에서는 request 변수를 통해 이 보관소를 참조할 수 있다.</u>
  - **JspContext 보관소** : JSP 페이지를 실행하는 동안만 유지된다. <u>JSP에서는 pageContext 변수를 통해 이 보관소를 참조할 수 있다.</u>

<br>

- **보관소 값 저장 및 조회**

  ```java
  보관소객체.setAttribute(키, 값);	// 값 저장
  보관소객체.getAttribute(키);		// 값 조회
  ```

  - 보관소 객체의 사용법은 Map 객체의 put()과 get() 메서드와 유사하다.

<br>

## 5.6.1. ServletContext의 활용

어떤 객체를 웹 애플리케이션이 실행되는 동안 모든 서블릿들과 공유하고 싶다면 ServletContext 보관소에 저장하면 된다.

**데이터베이스 커넥션 객체를 웹 애플리케이션이 시작될 때 생성하여 ServletContext 에 저장한다.** 이렇게 하면 데이터베이스를 이용하는 **모든 서블릿은 ServletContext에서 DB 커넥션 객체를 얻을 수 있다.**

<br>

### 공유 자원을 준비하는 서블릿 작성

웹 애플리케이션이 시작될 때 데이터베이스 커넥션 객체를 준비하는 서블릿을 작성한다.

- **src/spms/servlets/AppInitServlet.java**

  ```java
  package spms.servlets;
  
  import javax.servlet.ServletConfig;
  import javax.servlet.ServletContext;
  import javax.servlet.ServletException;
  import javax.servlet.http.HttpServlet;
  import java.sql.Connection;
  import java.sql.DriverManager;
  
  public class AppInitServlet extends HttpServlet {
  
    @Override
    public void init(ServletConfig config) throws ServletException {
      System.out.println("AppInitServlet 준비");
      super.init(config);
      
      try {
        ServletContext sc = this.getServletContext();
        Class.forName(sc.getInitParameter("driver"));
        Connection conn = DriverManager.getConnection(
            sc.getInitParameter("url"),
            sc.getInitParameter("username"),
            sc.getInitParameter("password"));
        sc.setAttribute("conn", conn);
      } catch (Throwable e) {
        throw new ServletException(e);
      }
    }
  
    @Override
    public void destroy() {
      System.out.println("AppInitServlet 마무리");
      super.destroy();
      
      Connection conn = (Connection) this.getServletContext().getAttribute("conn");
      try {
        if (conn != null && !conn.isClosed()) conn.close();
      } catch (Exception e) {}
    }
    
  }
  ```

  - **init()는 서블릿 객체가 생성될 때 딱 한 번 호출되기** 때문에 공유 자원을 준비하는 코드가 놓이기에 최적의 장소이다.

  - **super.init(config)** 을 사용하면 상속받은 기능은 그대로 두고 새로운 작업을 추가할 수 있다.

  - 데이터베이스 커넥션 객체를 준비한 다음, **모든 서블릿들이 사용할 수 있도록 ServletContext 객체에 저장한다.**

    ```java
    sc.setAttribute("conn", conn);
    ```

  - **destory()** 는 서블릿이 언로드 될 때 호출되므로 init()에서 준비했던 **DB 커넥션 객체를 해제시켜 준다.**

    ```java
    if (conn != null && !conn.isClosed) {
      conn.close();
    }
    ```

<br>

### 서블릿 배치와 \<load-on-startup> 태그

DD 파일(web.xml)에 AppInitServlet 의 배치 정보를 추가한다.

- **web/WEB-INF/web.xml**

  ```xml
  ...
  <!-- 서블릿 선언 -->
  <servlet>
    <servlet-name>AppInitServlet</servlet-name>
    <servlet-class>spms.servlets.AppInitServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>
  ...
  ```

  - **\<load-on-startup> 태그를 지정하면,** 해당 서블릿은 웹 애플리케이션이 시작될 때 자동으로 생성된다. 이 태그의 값은 생성 순서이다.

<br>

### ServletContext에 저장된 DB 커넥션 사용

이제 서블릿에서 DB 커넥션을 직접 준비할 필요 없이, ServletContext 보관소에 저장된 DB 커넥션 객체를 꺼내 쓰면 된다.

- **src/spms/servlets/MemberListServlet.java**

  ```java
  ...
  @WebServlet("/member/list")
  public class MemberListServlet extends HttpServlet {
  
    private ServletContext sc;
  
    @Override
    public void init() throws ServletException {
      super.init();
      sc = this.getServletContext();
    }
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String query = "select mno, mname, email, cre_date" +
          " from members" +
          " order by mno";
  
      // ServletContext 객체에서 커넥션 객체를 불러옴
      try (Connection connection = (Connection) sc.getAttribute("conn");
           PreparedStatement statement = connection.prepareStatement(query);
           ResultSet resultSet = statement.executeQuery()) {
        resp.setContentType("text/html; charset=UTF-8");
        ArrayList<Member> members = new ArrayList<>();
  ...
  ```

- **실행 결과**

  <img src="../capture/스크린샷 2019-09-11 오후 12.25.01.png" width=300>

<br>

## 5.6.2. HttpSession의 활용 - 로그인

HttpSession 객체는 클라이언트 당 한 개가 생성된다. 로그인되어 있는 동안 지속적으로 사용할 데이터를 HttpSession 객체에 저장한다.

- **로그인 시나리오**

  ```mermaid
  sequenceDiagram
  웹브라우저->>LoginServlet: (1) GET 요청
  LoginServlet->>LoginForm.jsp: (2) 포워딩
  LoginForm.jsp-->>웹브라우저: (3) 응답
  웹브라우저->>LoginServlet: (4) POST 요청
  alt (5)성공
  LoginServlet->>Member: 생성
  LoginServlet->>HttpSession: 저장
  LoginServlet-->>웹브라우저: (6) 리다이렉트
  else (5)실패
  LoginServlet->>LoginForm.jsp: 실패
  LoginForm.jsp-->>웹브라우저: (6) 리프래시(/auth/login)
  end
  ```

  1. 웹 브라우저에서 '/auth/login' 서블릿을 요청
  2. LoginServlet은 LoginForm.jsp로 화면 출력 작업 위임
  3. LoginForm.jsp는 로그인 입력폼을 만들어 출력
  4. 사용자가 입력한 정보를 가지고 다시 '/auth/login' 서블릿을 POST 요청
  5. LoginServlet은 이메일과 암호가 일치하는 회원 정보를 데이터베이스에서 찾아서 값 객체 'Member'에 담는다. 또한 Member 객체를 HttpSession 객체에 보관한다. 만약 이메일과 암호가 일치하는 회원을 찾지 못한다면, LoginFail.jsp로 작업을 위임한다.
  6. 로그인 성공시 회원 목록 페이지로 리다이렉트, 실패시 로그인 입력폼으로 리프래시

<br>

### 로그인 컨트롤러 만들기

- **src/spms/servlets/LogInServlet.java**

  ```java
  package spms.servlets;
  
  import spms.vo.Member;
  
  import javax.servlet.RequestDispatcher;
  import javax.servlet.ServletContext;
  import javax.servlet.ServletException;
  import javax.servlet.annotation.WebServlet;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import javax.servlet.http.HttpSession;
  import java.io.IOException;
  import java.sql.Connection;
  import java.sql.PreparedStatement;
  import java.sql.ResultSet;
  
  @WebServlet("/auth/login")
  public class LogInServlet extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      RequestDispatcher rd = req.getRequestDispatcher(
          "/auth/LogInForm.jsp");
      rd.forward(req, resp);
    }
  
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String query = "select mname, email from members where email=? and pwd=?";
      ServletContext sc = this.getServletContext();
      Connection conn = (Connection) sc.getAttribute("conn");
      ResultSet resultSet = null;
  
      try (PreparedStatement preparedStatement = conn.prepareStatement(query)
      ) {
        preparedStatement.setString(1, req.getParameter("email"));
        preparedStatement.setString(2, req.getParameter("password"));
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          Member member = new Member()
              .setEmail(resultSet.getString("email"))
              .setName(resultSet.getString("mname"));
          HttpSession session = req.getSession();
          session.setAttribute("member", member);
  
          resp.sendRedirect("../member/list");
        } else {
          RequestDispatcher rd = req.getRequestDispatcher("/auth/LogInFail.jsp");
          rd.forward(req, resp);
        }
      } catch (Exception e) {
        throw new ServletException(e);
      } finally {
        try { if (resultSet != null) resultSet.close();} catch (Exception e) {}
      }
    }
  
  }
  ```

  - 웹 브라우저로부터 GET 요청이 들어오면 LogInForm.jsp 로 포워딩 한다.

    ```java
    rd.forward(request, response);
    ```

    > JSP에서 다시 서블릿으로 돌아올 필요가 없으므로 포워딩 처리

  - 사용자가 이메일과 암호를 입력한 후 POST 요청을 하면 데이터베이스로부터 회원 정보를 조회한다. 그리고 회원이 존재하면 값 객체 Member에 회원 정보를 담는다.

    ```java
    Member memeber = new Member()
      .setEmail(rs.getString("email"))
      .setName(rs.getString("mname"));
    ```

  - Member 객체를 HttpSession 에 보관

    ```java
    HttpSession session = request.getSession();
    session.setAttribute("member", member);
    ```

  - 로그인 성공시, /member/list 로 리다이렉트

    ```java
    rd.forward(req,resp);
    ```

  - 로그인 실패시, /auth/LogInFail.jsp로 포워딩

    ```java
    RequestDispatcher rd = req.getRequestDispatcher(
    "/auth/LogInFail.jsp");
    rd.forward(req, resp);
    ```

<br>

### 로그인 입력폼 만들기

- **web/auth/LogInForm.jsp**

  ```jsp
  <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>로그인</title>
    </head>
    <body>
      <h2>사용자 로그인</h2>
      <form action="/auth/login" method="post">
        이메일: <input type="text" name="email"><br>
        암호: <input type="password" name="password"><br>
        <input type="submit" value="로그인">
      </form>
    </body>
  </html>
  ```

  - 로그인 입력폼 값을 서버에 전달할 때는 post 요청을 하도록 \<form> 태그를 설정

    ```jsp
    <form action="login" method="post">
    ```

<br>

### 로그인 실패 시 출력작업을 수행할 JSP 만들기

- **web/auth/LogInFail.jsp**

  ```jsp
  <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <meta http-equiv="Refresh" content="1;url=login">
      <title>로그인 실패</title>
    </head>
    <body>
      <p>로그인 실패입니다. 이메일 또는 암호가 맞지 않습니다.!<br>
        잠시 후에 다시 로그인 화면으로 갑니다.</p>
    </body>
  </html>
  ```

  - 1초가 지난 다음에는 다시 서버에 로그인 입력폼을 요청하도록 설정

    ```jsp
    <meta http-equiv="Refresh" content="1;url=login">
    ```

<br>

### 실행 결과

로그인 입력폼

<img src="../capture/스크린샷 2019-09-11 오후 3.58.58.png" width=500>

<br>

로그인 실패 안내 문구 출력 화면

<img src="../capture/스크린샷 2019-09-11 오후 3.59.52.png" width=500>

<br>

로그인 성공 후 회원 목록 화면

<img src="../capture/스크린샷 2019-09-11 오후 4.01.11.png" width=300>

### 5.6.3. HttpSession의 활용 - 로그인 정보 사용

LogInServlet에서 HttpSession 보관소에 저장한 Member 객체를 Header.jsp에서 꺼내 보자.

- **HttpSession 객체로부터 Member 얻기 시나리오**

  ```sequence
  웹브라우저->MemberListServlet: (1) GET 요청
  MemberListServlet->MemberList.jsp: (2) 인클루딩
  MemberList.jsp->Header.jsp: (3) 인클루딩
  Header.jsp->HttpSession: (4) Member 객체 꺼내기
  HttpSession-->Header.jsp: Member 객체 반환
  Header.jsp->Member: (5) getName()
  Header.jsp-->MemberList.jsp: 반환
  MemberList.jsp->Tail.jsp: (6) 인클루딩
  Tail.jsp-->MemberList.jsp: 반환
  MemberList.jsp-->MemberListServlet: 반환
  MemberListServlet-->웹브라우저: (7) 응답
  ```

  1. 로그인을 성공하면, 서버로부터 리다이렉트 응답을 받는다.
  2. MemberListServlet은 데이터베이스에서 회원 목록을 가져온 후, MemberList.jsp에 화면 출력 작업을 위임
  3. MemberList.jsp는 Header.jsp를 인클루딩
  4. Header.jsp는 HttpSession 객체에 보관된 로그인 회원의 정보(Member 객체) 추출
  5. 또한 Header.jsp 는 Member 객체로부터 이름을 추출 및 출력
  6. MemberList.jsp는 Tail.jsp를 인클루딩
  7. MemberListServlet은 MemberList.jsp가 작업한 내용을 최종적으로 출력 후 응답 완료

<br>

### 페이지 헤더에 로그인 사용자 이름 출력

- **web/Header.jsp**

  ```jsp
  <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ page import="spms.vo.Member" %>
  <%
      Member member = (Member) session.getAttribute("member");
  %>
  <div style="background-color: #00008b; color: #ffffff; height: 20px; padding: 5px;">
      SPMS(Simple Project Management System)
      <span style="float: right;">
      <%=member.getName()%>
      <a style="color: white;"
         href="<%=request.getContextPath()%>/auth/logout">로그아웃</a>
      </span>
  </div>
  ```

  - JSP 내장 객체 session을 사용하여 "member" 라는 키로 저장된 값을 꺼낸다.

    ```jsp
    <%
    Member member = (Member) session.getAttribute("member");
    %>
    ```

  - HttpSession 보관소에서 꺼낸 Member 객체로부터 이름을 알아내어 사용자 고르인 정보를 출력

    ```java
    <span style="float: right;">
    <%=member.getName()%>
    <a style="color:white;"
      href="<%=request.getContextPath()%>/auth/logout">로그아웃</a>
    </span>
    ```

<br>

- **로그인 사용자의 이름을 출력한 화면**

  <img src="../capture/스크린샷 2019-09-11 오후 4.32.50.png" width=500>

<br>

## 5.6.4. HttpSession의 활용 - 로그아웃

- **로그아웃 시나리오**

```sequence
웹브라우저->LogOutServlet: (1) 로그아웃 요청
LogOutServlet->HttpSession: (2) invalidate()
LogOutServlet-->웹브라우저: (3) 리다이렉트(/auth/login)
웹브라우저->MemberListServlet: (4) 회원 목록 요청
MemberListServlet->MemberList.jsp: (5) 인클루딩
MemberList.jsp->Header.jsp: (6) 인클루딩
Header.jsp->HttpSession: (7) Member 객체 꺼내기
HttpSession-->Header.jsp: null 반환
```

1. '로그아웃' 링크를 클릭하면, 웹 브라우저는 LogOutServlet 을 요청
2. LogOutServlet은 HttpSession 객체를 없애기 위해 invalidate() 호출
3. 로그인 입력폼으로 리다이렉트 하고, 다시 로그인 입력폼을 출력할 때 HttpSession 객체를 새로 생성
4. 새로 생성된 HttpSession을 가지고 회원 목록 서블릿을 실행. MemberListServlet은 데이터베이스에서 회원 목록 정보를 가져옴
5. 그리고 MemberListServlet은 MemberList.jsp에 화면 출력을 위임합니다.
6. MemberList.jsp는 화면 상단의 내용을 출력하기 위해 Header.jsp를 인클루딩 한다.
7. null 반환

### 로그아웃 서블릿 생성

- **src/spms/servlets/LogOutServlet.java**

  ```java
  package spms.servlets;
  
  import javax.servlet.ServletException;
  import javax.servlet.annotation.WebServlet;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import javax.servlet.http.HttpSession;
  import java.io.IOException;
  
  @WebServlet("/auth/logout")
  public class LogOutServlet extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      HttpSession session = req.getSession();
      session.invalidate();
  
      resp.sendRedirect("login");
    }
    
  }
  ```

  - HttpSession 객체를 무효화 하기 위해 invalidate()를 호출한다. 세션 객체가 무효화 된다는 것은 HttpSession 객체가 제거된다는 것을 의미한다.

    ```java
    session.invalidate();
    ```

<br>

## 5.6.5. ServletRequest의 활용

**ServletRequest 객체에 데이터를 보관하면 포워딩이나 인클루딩을 통해 협업하는 서블릿(JSP 포함)끼리 데이터를 공유할 수 있다.** 그 이유는 request와 response를 같이 사용하기 때문이다.

- **LogInServlet.java의 일부분**

  ```java
  protected void doGet(
    HttpServletRequest request, HttpServletResponse response)) 
      throws ServletException, IOException {
    RequestDispatcher rd = request.getRequestDispatcher("/auth/LogInForm.jsp");
    rd.forward(request, response);
  }
  ```

  - forward() 를 호출할 때 doGet() 의 매개변수 값을 그대로 넘겨 주고 있다. 즉, forward 매개변수를 통해 ServletRequest를 공유하는 것을 알 수 있다.

<br>

- **협업을 하는 서블릿들 끼리의 ServletRequest의 공유**

  <img src="../capture/스크린샷 2019-09-11 오후 6.57.47.png">

  - MVC 아키텍처에 예제들을 보면, ServletRequest 보관소를 통해 컨트롤러와 뷰 사이에서 데이터를 공유하였다.

<br>

## 5.6.6. JspContext의 활용

**JspContext** 보관소는 JSP 페이지를 실행할 때 생성되고, 실행이 완료되면 이 객체는 제거된다. 따라서 **JSP 페이지 내부에서만 사용될 데이터를 공유할 때 사용한다.**

- **JspContext 활용 사례**

  <img src="../capture/스크린샷 2019-09-11 오후 7.50.31.png">

<br>

- **MemberList.jsp 의 일부 코드**

  ```jsp
  <body>
    <jsp:include page="/Header.jsp"/>
    <h1>회원목록</h1>
    <p><a href='add'>신규 회원</a></p>
  ```

  - **\<jsp:include>** 와 같은 태그의 값을 다루는 객체를 **'태그 핸들러'** 라고 부른다. 바로 이 **태그 핸들러에게 데이터를 전달하고자 할 때 JspContext 보관소를 사용한다.**

<br>

# 5.7. JSP 액션 태그의 사용

JSP 페이지를 작성할 때, 가능한 자바 코드의 삽입을 최소화하는 것이 유지 보수에 좋다. 이를 위해 JSP에서는 기본으로 제공하는 태그들의 집합 **'JSP 액션(Action)'** 을 제공한다.

JSP 액션을 사용하면 자바로 직접 코딩하는 것보다 빠르고 쉽게 원하는 기능을 작성할 수 있다.

<br>

**JSP 2.2 기준, JSP 액션 태그들**

| 액션               | 설명                                                         |
| ------------------ | ------------------------------------------------------------ |
| \<jsp:useBean>     | 자바 인스턴스를 새로 만들어 보관소에 저장하는 코드를 생성한다. 자바 인스턴스를 자바 빈 이라고 부른다. |
| \<jsp:setProperty> | 자바 빈의 프로퍼티 값을 설정. setter 메서드 역할             |
| \<jsp:getProperty> | 자바 빈의 프로퍼티 값을 꺼낸다. getter 메서드 역할           |
| \<jsp:include>     | 정적(HTML, 텍스트 파일 등) 또는 동적 자원(서블릿/JSP)을 인클루딩 |
| \<jsp:forward>     | 현재 페이지의 실행을 멈추고 다른 정적 자원(HTML)이나 동적 자원(JSP)을 포워딩 |
| \<jsp:param>       | jsp:include, jsp:forward, jsp:params 의 자식 태그로 사용할 수 있다. |
| \<jsp:plugin>      | OBJECT 또는 EMBED HTML 태그를 생성한다.                      |
| \<jsp:element>     | 임의의 XML 태그나 HTML 태그를 생성한다.                      |

<br>

## 5.7.1. JSP 액션 태그 - \<jsp:useBean>

### jsp:useBean 문법

```jsp
<jsp:useBean id="이름" scope="page|request|session|application" 
             class="클래스명" type="타입명"/>
```

<br>

### \<jsp:useBean> 사용 예

```jsp
<jsp:useBean id="members"
             scope="request"
             class="java.util.ArrayList"
             type="java.util.ArrayList<spms.vo.Member>"/>
```

- **id 속성** : 객체의 이름을 설정
- **scope 속성** : 기존의 객체를 조회하거나 새로 만든 객체를 저장할 보관소로 지정한다. 네 가지 보관소 중에서 하나를 지정할 수 있다. **'page'** 는 JspContext, **'request'** 는 ServletRequest, **'session'** 은 HttpSession, **'application'** 은 ServletContext 보관소를 의미. 기본값은 'page' 이다.
- **class 속성** : 자바 객체를 생성할 때 사용할 클래스 이름을 지정. 
- **type 속성** : 참조 변수를 선언할 때 사용할 타입의 이름이다. 클래스 이름 또는 인터페이스 이름이 올 수 있다.

<br>

**앞의 액션 태그를 자바 코드로 변환했을 때**

```java
java.util.ArrayList<spms.vo.Member> members =
  (java.util.ArrayList<spms.vo.Member>)request.getAttribute("members");
if (members == null) {
  members = new java.util.ArrayList();
  request.setAttribute("members", members);
}
```

<br>

## 5.7.2. \<jsp:useBean>의 활용

회원 목록 화면을 만드는 MemberList.jsp 와 Header.jsp에 \<jsp:useBean> 액션 태그를 적용해보자.

- **web/member/MemberList.jsp**

  ```jsp
  ...
  <jsp:useBean id="members"
               scope="request"
               class="java.util.ArrayList"
               type="java.util.ArrayList<spms.vo.Member>"/>
  ...
  <%
  //    ArrayList<Member> members = (ArrayList<Member>) request.getAttribute("members");
  	for (Member member : members) {
  %>
  ...
  ```

- **web/Header.jsp**

  ```jsp
  ...
  <jsp:useBean id="member"
               scope="session"
               class="spms.vo.Member"/>
  ...
  <%
  //    Member member = (Member) session.getAttribute("member");
  %>
  ```

<br>

## 5.7.3. JSP 액션 태그의 존재 의의

비즈니스 로직을 처리하는 부분과 화면을 처리하는 부분을 나눠서 개발할 수 있다.

- **JSP 액션 태그와 업무의 분리**

  <img src="https://t1.daumcdn.net/cfile/tistory/99B80B3359D1D5CA24">

<br>

# 실력 향상 과제

회원 목록을 출력하는 MemberListServlet 처럼 회원 등록, 변경, 삭제 서블릿도 JSP를 도입하여 뷰 컴포넌트를 분리하세요.

1. **회원 등록 - MemberAddServlet에서 입력화면을 생성하는 코드를 제거하고, 대신 MemberAdd.jsp 를 만들어 화면 출력을 위임하세요. 또한, MemberAddServlet에서 회원 정보를 등록하다가 오류가 발생했을 때 /Error.jsp로 위임하도록 코드를 변경하세요.**

   - web/member/MemberUpdate.jsp

     ```jsp
     <%@ page contentType="text/html;charset=UTF-8" language="java" %>
     <html>
       <head>
         <title>회원 등록</title>
       </head>
       <body>
         <h1>회원 등록</h1>
         <form action="/member/add" method="post">
           이름: <input type="text" name="name"><br>
           이메일: <input type="text" name="email"><br>
           암호: <input type="password" name="password"><br>
           <input type="submit" value="추가">
           <input type="reset" value="취소">
         </form>
       </body>
     </html>
     ```

   - src/spms/servlets/MemberAddServlet.java

     ```java
     ...
     @WebServlet("/member/add")
     public class MemberAddServlet extends HttpServlet {
     
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         // JSP로 출력 위임
         RequestDispatcher rd = req.getRequestDispatcher(
             "/member/MemberAdd.jsp");
         // 포워딩
         rd.forward(req, resp);
       }
     
       @Override
       protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         String query = "insert into members (email, pwd, mname, cre_date, mod_date) values" +
             " (?, ?, ?, now(), now())";
         ServletContext sc = this.getServletContext();
         Connection connection = (Connection)sc.getAttribute("conn");
         try (PreparedStatement ps = connection.prepareStatement(query)) {
           ps.setString(1, req.getParameter("email"));
           ps.setString(2, req.getParameter("password"));
           ps.setString(3, req.getParameter("name"));
           ps.executeUpdate();
     
           // 추가를 완료하고 /member/list 로 리다이렉트
           resp.sendRedirect("list");
         } catch (Exception e) {
           // 에러시 error 정보를 보관한다.
           req.setAttribute("error", e);
           // Error.jsp로 에러 화면 출력 위임
           RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
           // 포워딩
           rd.forward(req, resp);
         }
       }
     
     }
     ```

   <br>

2. **회원 삭제 - MemberDeleteServlet에서 삭제를 수행하다가 오류가 발생하면 /Error.jsp로 위임하도록 코드를 변경하세요.**

   - src/spms/serlvets/MemberDeleteServlet.java

     ```java
     ...
     @WebServlet("/member/delete")
     public class MemberDeleteServlet extends HttpServlet {
     
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         ServletContext sc = this.getServletContext();
         Connection conn = (Connection) sc.getAttribute("conn");
         String query = "delete from members where mno=" + req.getParameter("no");
     
         try (PreparedStatement ps = conn.prepareStatement(query)){
           ps.executeUpdate();
           resp.sendRedirect("list");
         } catch (Exception e) {
           req.setAttribute("error", e);
           RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
           rd.forward(req, resp);
         }
       }
     }
     ```

   <br>

3. **회원 상세 정보 조회 및 변경 - MemberUpdateServlet에서 상세 정보 출력을 MemberUpdateForm.jsp에게 포워딩한다. 예외 처리는 /Error.jsp에게 위임하세요.**

   - web/member/MemberUpdate.jsp

     ```jsp
     <%@ page contentType="text/html;charset=UTF-8" language="java" %>
     <jsp:useBean id="updateMember"
                  scope="request"
                  class="spms.vo.Member"/>
     <html>
       <head>
         <title>회원정보</title>
       </head>
       <body>
         <h1>회원정보</h1>
         <form action="/member/update" method="post">
           번호: <input type="text" name="no" value="<%=updateMember.getNo()%>" readonly><br>
           이름: <input type="text" name="name" value="<%=updateMember.getName()%>"><br>
           이메일: <input type="text" name="email" value="<%=updateMember.getEmail()%>"><br>
           가입일: <%=updateMember.getCreateDate()%><br>
           <input type="submit" value="저장">
           <input type="button" value="삭제" onclick="location.href='delete?no=<%=updateMember.getNo()%>'">
           <input type="button" value="취소" onclick="location.href='list'">
         </form>
       </body>
     </html>
     ```

   - src/spms/servlets/MemberUpdateServlet.java

     ```java
     ...
     @WebServlet("/member/update")
     public class MemberUpdateServlet extends HttpServlet {
     
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         String query = "select mno, email, mname, cre_date from members" +
           " where mno=" + req.getParameter("no");
         ServletContext sc = this.getServletContext();
         Connection conn = (Connection) sc.getAttribute("conn");
     
         try (PreparedStatement ps = conn.prepareStatement(query);
              ResultSet rs = ps.executeQuery()) {
           rs.next();
           Member member = new Member()
             .setName(rs.getString("mname"))
             .setEmail(rs.getString("email"))
             .setNo(rs.getInt("mno"))
             .setCreateDate(rs.getDate("cre_date"));
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
         String query = "update members set email=?, mname=?, mod_date=now() where mno=?";
         ServletContext sc = this.getServletContext();
         Connection conn = (Connection) sc.getAttribute("conn");
     
         try (PreparedStatement ps = conn.prepareStatement(query)) {
           ps.setString(1, req.getParameter("email"));
           ps.setString(2, req.getParameter("name"));
           ps.setInt(3, Integer.parseInt(req.getParameter("no")));
           ps.executeUpdate();
     
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

<br>

# 5.8. EL 사용하기

**EL(Expression Language)은** 콤마(,)와 대괄호([])를 사용하여 자바 빈의 프로퍼티나 맵, 리스트, 배여르이 값을 보다 쉽게 꺼내게 해주는 기술이다.

<br>

## 5.8.1. EL 표기법

EL은 **${} 와 #{}를** 사용하여 값을 표현한다. 

**${표현식}** 으로 지정된 값은 JSP가 실행될 때 JSP 페이지에 즉시 반영된다. 그래서 **${}을 '즉시 적용(immediate evaluation)'** 이라 부른다. 

**#{표현식}** 으로 지정된 값은 시스템에서 필요하다고 판단될 때 그 값을 사용한다. 그래서 **#{}을 '지연 적용(deferred evaluation)'** 이라 부른다. #{}은 특히 JSF(JavaServer Faces) 기술에서 UI를 만들 때 많이 사용한다.

- **EL 표기법**

  ```jsp
  ${member.no}			// 객체이름.프로퍼티
  ${member["no"]}		// 객체이름["프로퍼티"]
  ```

- **EL 표기법 자바 코드로 변환**

  ```java
  Member obj = (Member)pageContext.findAttribute("member");
  int value = obj.getNo();
  ```

- **PageContext의 findAttribute() 호출시 객체를 찾는 순서**

  1. JspContext
  2. ServletRequest
  3. HttpSession
  4. ServletContext
  5. null

<br>

### EL에서 검색 범위 지정

EL도 \<jsp:useBean> 처럼 네 군데 보관소에서 값을 꺼낼 수 있다.

- **EL 보관소에서 참조할 때 사용하는 이름**
  - pageScope => JspContext
  - requestScope => ServletRequest
  - sessionScope => HttpSession
  - applicationScope => ServletContext

객체를 찾는 범위를 지정하게 되면 **해당 보관소에서만 객체를 찾고,** 못 찾으면 null을 반환한다.

<br>

## 5.8.2. JSP에서 제공하는 EL 기본 객체

**EL 기본 객체**

| 객체             | 설명                               | 코드                                 |
| ---------------- | ---------------------------------- | ------------------------------------ |
| pageContext      | JSP의 pageContext 객체             | -                                    |
| servletContext   | ServletContext 객체                | ${pageContext.servletContext.객체명} |
| session          | HttpSession 객체                   | ${pageContext.session.객체명}        |
| request          | ServletReqeust 객체                | ${pageContext.request.객체명}        |
| response         | ServletResponse 객체               | -                                    |
| param            | 요청 매개변수의 값 조회            | ${param.매개변수명}                  |
| paramValues      | 요청 매개변수의 값 배열 조회       | ${paramValues.매개변수명}            |
| header           | HTTP 헤더의 값 조회                | ${header.헤더명}                     |
| headerValues     | HTTP 헤더의 값 배열 조회           | ${headerValues.헤더명}               |
| cookie           | 쿠기 값 조회                       | ${cookie.쿠키명}                     |
| initParam        | 컨텍스트 초기화 매개변수의 값 조회 | ${initParam.매개변수명}              |
| pageScope        | page 보관소의 값 조회              | ${pageScope.객체명}                  |
| requestScope     | request 보관소의 값 조회           | ${requestScope.객체명}               |
| sessionScope     | session 보관소의 값 조회           | ${sessionScope.객체명}               |
| applicationScope | application 보관소의 값 조회       | ${applicationScope.객체명}           |

<br>

## 5.8.3. 리터럴 표현식

EL 블록에서 사용할 수 있는 값은 문자열, 정수, 부동소수점, 참거짓(Boolean), 널(Null)이 가능하다.

<br>

**EL 표현식**

- **문자열** : ${"test"}, \${'test'}
- **정수** : ${20}
- **부동소수점** : ${3.14}
- **참거짓** : ${true}
- **null** : ${null}

<br>

### 5.8.4. 값 표현식

자바 객체, 배열, List, Map, ResourceBundle로부터 값을 꺼낼 때 사용하는 EL 표현식

<br>

### 배열에서 값 꺼내기

```jsp
<%
pageContext.setAttribute("scores", new int[]{90, 80, 70, 100});
%>
<%-- 배열에서 인덱스 2의 값 꺼내기 --%>
${scores[2]}
```

<br>

### List 객체에서 값 꺼내기

```jsp
<%
List<String> nameList = new LinkedList<String>();
nameList.add("홍길동");
nameList.add("임꺽정");
nameList.add("일지매");
pageContext.setAttribute("nameList", nameList);
%>
<%-- 리스트 객체에서 인덱스 1의 값 꺼내기 --%>
${nameList[1]}
```

<br>

### Map 객체에서 값 꺼내기

```jsp
<%
Map<String, String> map = new HashMap<>();
map.put("s01", "홍길동");
map.put("s02", "임꺽정");
map.put("s3", "일지매");
pageContext.setAttribute("map", map);
%>
<%-- 맵 객체에서 키 s02로 지정된 값 꺼내기 --%>
${map.s02}
```

<br>

### 자바 객체에서 프로퍼티 값 꺼내기

```jsp
<%
pageContext.setAttribute("member",
                        new Member()
                        .setNo(100)
                        .setName("홍길동")
                        .setEmail("hong@test.com"));
%>
<%-- 자바빈에서 프로퍼티 email의 값 꺼내기 --%>
${member.email}
```

<br>

### ResourceBundle 객체에서 값 꺼내기

- **src/MyResourceBundle.java**

  ```java
  import java.util.ListResourceBundle;
  public class MyResourceBundle_ko_KR extends ListResourceBundle {
    public Object[][] getContents() {
      return new Object[][] {
        {"OK", "확인"},
        {"Cancel", "취소"},
        {"Reset", "재설정"},
        {"Submit", "제출"}
      };
    }
  }
  ```

- **EL 표현식**

  ```jsp
  <%
  pageContext.setAttribute("myRB",
                          ResourceBundle.getBundle("MyResourceBundle"));
  %>
  <%-- 리소스번들 객체에서 값 꺼내기 --%>
  ${myRB.OK}
  ```

<br>

## 5.8.5. 연산자

EL 블록에서도 간단한 연산을 수행할 수 있다.

<br>

### 산술 연산자

- **EL 표현식**

  더하기(+), 빼기(-), 곱하기(*), 나누기(/, div), 나머지(%, mod) 값을 구하는 연산자

  ```jsp
  \${10 div 20} = ${10 div 20}
  \${10 mod 20} = ${10 mod 20}
  ```

  > **'${' 앞에 '\\'가 붙으면** EL 문법이 아닌 일반 텍스트로 취급한다.

<br>

### 논리 연산자

EL에서 제공하는 논리 연산자는 **AND(&&, and), OR(||, or), NOT(!, not)** 이 있다.

<br>

### 관계 연산자

EL에서 제공하는 관계 연산자는 **같다(==, eq), 같지 않다(!=, ne), 크다(>, gt), 작다 (<, lt), 크거나 같다(>=, ge), 작거나 같다(<=, le)** 가 있다.

<br>

### empty

'empty'는 값이 비어 있거나 null 인지를 조사할 때 사용하는 연산자이다. 값이 null이면 true를 반환한다.

- **EL 표현식**

  ```jsp
  <%
  pageContext.setAttribute("title", "EL 연산자!");
  %>
  <%-- 자바빈에서 프로퍼티 email의 값 꺼내기 --%>
  \${empty title} = ${empty titile}
  \${empty title2} = ${empty title2}
  ```

- **실행결과**

  ```
  ${empty title} = true
  ${empty title2} = false
  ```

<br>

### 조건

```jsp
${10 > 20 ? "크다" : "작다"}
```

<br>

## 5.8.6. 예약 키워드

- **EL에서 사용하는 예약 키워드**

  ```
  and, or, not, eq, ne, lt, gt, le, ge, ture, false, null, instanceof, empty,
  div, mod
  ```

  - JspContext 나 ServletRequest, HttpSession, ServletContext 보관소에 객체를 저장할 때, 그 식별자는 EL의 예약 키워드 이름과 같아서는 안된다.

<br>

## 5.8.7. EL 활용 - 회원 정보 페이지

## 5.8.3. 리터럴 표현식

EL 블록에서 사용할 수 있는 값은 문자열, 정수, 부동소수점, 참거짓(Boolean), 널(Null)이 가능하다.

<br>

**EL 표현식**

- **문자열** : ${"test"}, \${'test'}
- **정수** : ${20}
- **부동소수점** : ${3.14}
- **참거짓** : ${true}
- **null** : ${null}

<br>

### 5.8.4. 값 표현식

자바 객체, 배열, List, Map, ResourceBundle로부터 값을 꺼낼 때 사용하는 EL 표현식

<br>

### 배열에서 값 꺼내기

```jsp
<%
pageContext.setAttribute("scores", new int[]{90, 80, 70, 100});
%>
<%-- 배열에서 인덱스 2의 값 꺼내기 --%>
${scores[2]}
```

<br>

### List 객체에서 값 꺼내기

```jsp
<%
List<String> nameList = new LinkedList<String>();
nameList.add("홍길동");
nameList.add("임꺽정");
nameList.add("일지매");
pageContext.setAttribute("nameList", nameList);
%>
<%-- 리스트 객체에서 인덱스 1의 값 꺼내기 --%>
${nameList[1]}
```

<br>

### Map 객체에서 값 꺼내기

```jsp
<%
Map<String, String> map = new HashMap<>();
map.put("s01", "홍길동");
map.put("s02", "임꺽정");
map.put("s3", "일지매");
pageContext.setAttribute("map", map);
%>
<%-- 맵 객체에서 키 s02로 지정된 값 꺼내기 --%>
${map.s02}
```

<br>

### 자바 객체에서 프로퍼티 값 꺼내기

```jsp
<%
pageContext.setAttribute("member",
                        new Member()
                        .setNo(100)
                        .setName("홍길동")
                        .setEmail("hong@test.com"));
%>
<%-- 자바빈에서 프로퍼티 email의 값 꺼내기 --%>
${member.email}
```

<br>

### ResourceBundle 객체에서 값 꺼내기

- **src/MyResourceBundle.java**

  ```java
  import java.util.ListResourceBundle;
  public class MyResourceBundle_ko_KR extends ListResourceBundle {
    public Object[][] getContents() {
      return new Object[][] {
        {"OK", "확인"},
        {"Cancel", "취소"},
        {"Reset", "재설정"},
        {"Submit", "제출"}
      };
    }
  }
  ```

- **EL 표현식**

  ```jsp
  <%
  pageContext.setAttribute("myRB",
                          ResourceBundle.getBundle("MyResourceBundle"));
  %>
  <%-- 리소스번들 객체에서 값 꺼내기 --%>
  ${myRB.OK}
  ```

<br>

## 5.8.5. 연산자

EL 블록에서도 간단한 연산을 수행할 수 있다.

<br>

### 산술 연산자

- **EL 표현식**

  더하기(+), 빼기(-), 곱하기(*), 나누기(/, div), 나머지(%, mod) 값을 구하는 연산자

  ```jsp
  \${10 div 20} = ${10 div 20}
  \${10 mod 20} = ${10 mod 20}
  ```

  > **'${' 앞에 '\\'가 붙으면** EL 문법이 아닌 일반 텍스트로 취급한다.

<br>

### 논리 연산자

EL에서 제공하는 논리 연산자는 **AND(&&, and), OR(||, or), NOT(!, not)** 이 있다.

<br>

### 관계 연산자

EL에서 제공하는 관계 연산자는 **같다(==, eq), 같지 않다(!=, ne), 크다(>, gt), 작다 (<, lt), 크거나 같다(>=, ge), 작거나 같다(<=, le)** 가 있다.

<br>

### empty

'empty'는 값이 비어 있거나 null 인지를 조사할 때 사용하는 연산자이다. 값이 null이면 true를 반환한다.

- **EL 표현식**

  ```jsp
  <%
  pageContext.setAttribute("title", "EL 연산자!");
  %>
  <%-- 자바빈에서 프로퍼티 email의 값 꺼내기 --%>
  \${empty title} = ${empty titile}
  \${empty title2} = ${empty title2}
  ```

- **실행결과**

  ```
  ${empty title} = true
  ${empty title2} = false
  ```

<br>

### 조건

```jsp
${10 > 20 ? "크다" : "작다"}
```

<br>

## 5.8.6. 예약 키워드

- **EL에서 사용하는 예약 키워드**

  ```
  and, or, not, eq, ne, lt, gt, le, ge, ture, false, null, instanceof, empty,
  div, mod
  ```

  - JspContext 나 ServletRequest, HttpSession, ServletContext 보관소에 객체를 저장할 때, 그 식별자는 EL의 예약 키워드 이름과 같아서는 안된다.

<br>

## 5.8.7. EL 활용 - 회원 정보 페이지

회원 정보 페이지는 UI를 생성할 때, 컨트롤러가 작업한 결과물을 request에서 꺼내 쓴다. 바로 이 request 에서 값을 꺼내는 부분을 EL로 대체해보자.

- **MemberUpdate.jsp**

  ```jsp
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
  <form action="/member/update" method="post">
      번호: <input type="text" name="no" value="${updateMember.no}" readonly><br>
      이름: <input type="text" name="name" value="${updateMember.name}"><br>
      이메일: <input type="text" name="email" value="${updateMember.email}"><br>
      가입일: ${updateMember.createDate}<br>
      <input type="submit" value="저장">
      <input type="button" value="삭제" onclick="location.href='delete?no=${updateMember.no}'">
      <input type="button" value="취소" onclick="location.href='list'">
  </form>
  </body>
  </html>
  ```

  - \<jsp:useBean> 과 \<%=%> 태그를 제거한다.

  - \<%=%>을 EL 표기법으로 바꾼다.

    ```jsp
    ${updateMember.no}, ${updateMember.name}, ...
    ```

<br>

### 객체 범위 명시하기

만약 page 보관소에 같은 이름의 객체가 존재하면 순서상 page에 보관된 객체가 사용된다. 이 문제를 해결하기 위해서는 보관소 범위를 정확히 지정해주면 된다.

```jsp
이름: <input type='text' name='name' value='${requestScope.member.name}'><br>
```

<br>

# 5.9. JSTL 사용하기

**'JSTL(JSP Standard Tag Library)'** 은 JSP의 기본 태그가 아니고 JSP의 확장 태그이다. 이것을 사용하면 JSP 페이지에서 자바 코딩을 줄일 수 있다.

<br>

## 5.9.1 JSTL 라이브러리 준비

https://mvnrepository.com/artifact/javax.servlet.jsp.jstl/jstl-api 에서 jar 파일을 다운받아서 lib에 추가해준다.

<br>

## 5.9.2. JSTL 주요 태그의 사용법

### 사용할 태그 라이브러리 선언

JSTL 확장 태그를 사용하려면 그 태그의 라이브러리를 선언해야 한다.

- **태그 라이브러리 선언하는 문법**

  ```jsp
  <%@ taglib uri="사용할 태그의 라이브러리 URI" prefix="접두사" %>
  ```

  - **<% taglib %>** : JSP의 지시자 태그
  - **uri** : 태그 라이브러리의 네임 스페이스 이름
  - **prefix** : JSTL 태그를 사용할 때 태그 이름 앞에 붙일 접두사

<br>

- **태그 라이브러리와 URI 접두사**

| 태그 라이브러리 | 접두사 | 네임스페이스의 URI 식별자              |
| --------------- | ------ | -------------------------------------- |
| Core            | c      | http://java.sun.com/jsp/jstl/core      |
| XML             | x      | http://java.sun.com/jsp/jstl/xml       |
| I18N            | fmt    | http://java.sun.com/jsp/jstl/fmt       |
| Database        | sql    | http://java.sun.com/jsp/jstl/sql       |
| Functions       | fn     | http://java.sun.com/jsp/jstl/functions |

<br>

### JSTL 태그

| 태그 라이브러리        | 기능              | 태그들: 부모태그(자식태그)                                   |
| ---------------------- | ----------------- | ------------------------------------------------------------ |
| 기본(Core)             | 변수 지원         | remove, set                                                  |
| "                      | 흐름 제어         | choose(when, otherwise), forEach, forTokens, if              |
| "                      | URL 관리          | import(param), redirect(param), url(param)                   |
| "                      | 기타              | catch, out                                                   |
| XML                    | 기본              | out, parse, set                                              |
| "                      | 흐름 제어         | choose(when, otherwise), forEach, if                         |
| "                      | 변환              | transform(param)                                             |
| 국제화(I18N)           | 로케일            | setLocale, requestEncoding                                   |
| "                      | 메시지 포맷       | bundle, message(param), setBundle                            |
| "                      | 숫자 및 날짜 포맷 | formatNumber, formatDate, parseDate, parseNumber, setTimeZone, timeZone |
| 데이터베이스           | 데이터 소스 설정  | setDateSource                                                |
| "                      | SQL               | query(dateParam, param), transaction, update(dateParam, param) |
| 기타 함수들(Functions) | 집합의 원소개수   | length                                                       |
| "                      | 문자열 처리       | toUpperCase, toLowerCase, substring, substringAfter, substringBefore, trim, replace, indexOf, startsWith, endsWith, contains, containsIgnoreCase, split, join, escapeXml |

<br>

### \<c:out> 태그

출력문을 만드는 태그

- **문법**

  ```jsp
  <c:out value="출력할 값" default="기본값"/>
  <c:out value="출력할 값">기본값</c:out>
  ```

<br>

### \<c:set> 태그

변수를 생성하거나 기존 변수의 값을 덮어쓸 때 사용. 이 태그로 생성한 변수는 JSP 페이지의 로컬 변수가 아니라 보관소(JspContext, ServletRequest, HttpSession, ServletContext)에 저장된다.

- **문법**

  ```jsp
  // value 속성을 사용하여 값 설정
  <c:set var="변수명" value="값" scope="page|request|session|application"/>
  // 태그 콘텐츠를 사용하여 값 설정
  <c:set var="변수명" scope="page|request|session|application">값</c:set>
  ```

<br>

### \<c:set> 태그를 이용한 객체의 프로퍼티 값 설정

\<c: set> 을 이용하면, 보관소에 저장된 객체의 프로퍼티 값도 바꿀 수 있다.

1. **web/WEB-INF 로 lib 추가**
2. **web/WEB-INF/web.xml 에 welcome-file로 index.jsp 추가**

3. **web/index.jsp**

   ```jsp
   <%@ page contentType="text/html;charset=UTF-8" language="java"%>
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
   <%!
     public static class MyMember {
       int no;
       String name;
   
       public int getNo() {
         return no;
       }
   
       public void setNo(int no) {
         this.no = no;
       }
   
       public String getName() {
         return name;
       }
   
       public void setName(String name) {
         this.name = name;
       }
     }
   %>
   <%
   MyMember member = new MyMember();
   member.setNo(100);
   member.setName("홍길동");
   session.setAttribute("member", member);
   %>
   <html>
     <head>
       <title>Test</title>
     </head>
     <body>
       <h3>객체의 프로퍼티 값 변경</h3>
       ${member.name}<br>
       <c:set target="${member}" property="name" value="임꺽정"/>
       ${member.name}<br>
     </body>
   </html>
   ```

   - \<c:set>을 사용하여 객체의 프로퍼티 값을 설정할 때는 셋터 메서드의 리턴 타입이 void 이어야 한다.

<br>

### \<c:remove> 태그

보관소에 저장된 값을 제거하는 태그

- **문법**

  ```jsp
  <c:remove var="변수명" scope="page|request|session|application"/>
  ```

- **예제**

  ```jsp
  <h3>보관소에 저장된 값 제거</h3>
  <% request.setAttribute("username1", "홍길동"); %>
  \${username1} = ${username1}<br>
  <c:remove var="username1"/>
  \${username1} = ${username1}<br>
  ```

- **실행 결과**

  ```
  보관소에 저장된 값 제거
  ${username1} = 홍길동
  ${username1} =
  ```

<br>

### \<c:if> 태그

이 태그의 test 속성값이 참이면, 콘텐츠가 실행된다. 참거짓 테스트 결과를 보관소에 저장할 수도 있다.

- **문법**

  ```jsp
  <c:if test="조건" var="변수명" scope="page|request|session|application">
    콘텐츠
  </c:if>
  ```

- **예제**

  ```jsp
  <h3>c:if 태그</h3>
  <c:if test="${10 > 20}" var="result1">
    10은 20보다 크다.<br>
  </c:if>
  ${result1}<br>
  <c:if test="${10 < 20}" var="result2">
    10은 20보다 작다.<br>
  </c:if>
  ${result2}<br>
  ```

- **실행 결과**

  ```
  c:if 태그
  false
  10은 20보다 작다.
  true
  ```

<br>

### \<c:choose> 태그

자바의 switch, case 등과 같은 기능을 수행한다. 즉 여러 가지 조건에 따라 다른 작업을 해야 할 필요가 있을 때 이 태그를 사용한다.

- **문법**

  ```jsp
  <c:choose>
    <c: when test="참거짓 값"></c:when>
    <c: when test="참거짓 값"></c:when>
    ...
    <c: otherwise></otherwise>
  </c:choose>
  ```

- **예제**

  ```jsp
  <h3>c:choose 태그</h3>
  <c:set var="userid" value="admin"/>
  <c:choose>
    <c:when test="${userid == 'hong'}">
      홍길동님 반값습니다.
    </c:when>
    <c:when test="${userid == 'leem'}">
      임꺽정님 반값습니다.
    </c:when>
    <c:when test="${userid == 'admin'}">
      관리자 전용 페이지 입니다.
    </c:when>
    <c:otherwise>
      등록되지 않은 사용자입니다.
    </c:otherwise>
  </c:choose>
  ```

- **실행 결과**

  ```
  c:choose 태그
  관리자 전용 페이지 입니다.
  ```

<br>

### \<c:forEach> 태그

반복적인 작업을 정의할 때 사용한다. 목록에서 값을 꺼내어 처리하고 싶을 때 이 태그를 사용한다.

- **문법**

  ```jsp
  <c:forEach var="변수명" items="목록데이터" begin="시작인덱스" end="종료인덱스">
    콘텐츠
  </c:forEach>
  ```

- **예제**

  ```jsp
  <h3>c:forEach 태그</h3>
  <%
  session.setAttribute("nameList",
                       new String[]{"홍길동", "임꺽정", "일지매"});
  %>
  <ul>
    <c:forEach var="name" items="${nameList}">
      <li>${name}</li>
    </c:forEach>
  </ul>
  <ul>
    <c:forEach var="no" begin="1" end="6">
      <li><a href="jstl0${no}.jsp">JSTL 예제 ${no}</a></li>
    </c:forEach>
  </ul>
  ```

- **실행 결과**

  ```
  c:forEach 태그
  * 홍길동
  * 임꺽정
  * 일지매
  * JSTL 예제 1
  * JSTL 예제 2
  * JSTL 예제 3
  * JSTL 예제 4
  * JSTL 예제 5
  * JSTL 예제 6
  ```

<br>

### \<c:forTokens> 태그

이 태그를 사용하면 문자열을 특정 구분자(delimiter)로 분리하여 반복문을 돌릴 수 있다.

- **예제**

  ```jsp
  <h3>c:forTokens 태그</h3>
  <%
  request.setAttribute("tokens", "v1=20&v2=30&op=+");
  %>
  <ul>
    <c:forTokens var="item" items="${tokens}" delims="&">
      <li>${item}</li>
    </c:forTokens>
  </ul>
  ```

<br>

### \<c:url> 태그

URL을 만들 때 사용하는 태그이다. 이 태그를 사용하면 매개변수를 포함한 URL을 손쉽게 만들 수 있다.

- **예제**

  ```jsp
  <h3>c:url 태그</h3>
  <c:url var="calcUrl" value="http://localhost:8080/calc/Calculator.jsp">
    <c:param name="v1" value="20"/>
    <c:param name="v2" value="30"/>
    <c:param name="op" value="+"/>
  </c:url>
  <a href="${calcUrl}">계산하기</a>
  ```

<br>

### \<c:import> 태그

여러 사이트의 내용을 가져와서 새로운 서비스를 만드는 매쉬업(Mashup)에 매우 유용한 태그이다. url 속성에 콘텐츠가 있는 주소를 지정하면, 해당 주소로 요청하고, 응답 결과를 받아서 반환한다.

- **예제**

  ```jsp
  <h3>c:import 태그</h3>
  <textarea rows="10" cols="80">
    <c:import url="http://www.zdnet.co.kr/Include2/ZDNetKorea_News.xml"/>
  </textarea>
  ```

  - 위 코드에서 var 속성을 추가로 설정하면 URL 응답 결과를 바로 출력하지 않고 var에 설정된 이름으로 보관소에 저장한다.

<br>

### \<c:redirect> 태그

이 태그를 사용하여 리다이렉트를 처리할 수 있다. 

- **예제**

  ```jsp
  <c:redirect url="http://www.daum.net"/>
  ```

<br>

### \<fmt:parseDate> 태그

날짜 형식으로 작성된 문자열을 분석하여 java.util.Date 객체를 생성한다. 그리고 저장된 보관소에 저장한다.

- **예제**

  ```jsp
  <h3>fmt:parseDate 태그</h3>
  <fmt:parseDate var="date1" value="2013-11-16" pattern="yyyy-MM-dd"/>
  날짜: ${date1}<br>
  ```

- **실행 결과**

  ```
  fmt:parseDate 태그
  날짜: Sat Nov 16 00:00:00 KST 2013
  ```

<br>

### \<fmt:formatDate> 태그

날짜 객체로부터 우리가 원하는 형식으로 날짜를 표현하고자 할 때 이 태그를 사용한다.

- **예제**

  ```jsp
  <h3>fmt:formatDate 태그</h3>
  <fmt:formatDate var="date2" value="${date1}" pattern="MM/dd/yy"/>
  날짜: ${date2}
  ```

- **실행 결과**

  ```
  fmt:formatDate 태그
  날짜: 11/16/13
  ```

<br>

## 5.9.3. JSTL 활용 - 회원 목록 페이지에서 자바 코드 없애기

1. JSP 페이지에 자바 코드가 있으면 웹 디자이너와 웹 퍼블리셔와 함께 작업하기 어렵다.
2. JSP 페이지에 자바 코드를 넣지 않으면, 자연스럽게 JSP 페이지에 비즈니스 로직을 작성하지 않게 된다.

위와 같은 이유로 JSTL 과 EL 을 사용하여 JSP 파일에서 자바 코드를 없애야 한다.

<br>

- **web/member/MemberList.jsp**

  ```jsp
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  <%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>회원 목록</title>
    </head>
    <body>
      <jsp:include page="/Header.jsp"/>
      <h1>회원 목록</h1>
      <p><a href="/member/add">신규 회원</a></p>
      <c:forEach var="member" items="${members}">
        ${member.no},
        <a href="update?no=${member.no}">${member.name}</a>,
        ${member.email},
        ${member.createDate}
        <a href="delete?no=${member.no}">[삭제]</a><br>
      </c:forEach>
      <jsp:include page="/Tail.jsp"/>
    </body>
  </html>
  ```

  - **\<%@ tag lib %> 지시자를** 사용하여 JSTL 기본(Core) 태그 라이브러리를 선언한다.
  - 반복문을 **\<c:forEach> JSTL 태그로** 대체한다.

<br>

# 5.10. DAO 만들기

서블릿으로부터 분리해야 하는 기능은 데이터베이스와 연동하여 데이터를 처리하는 부분이다. 이렇게 **데이터 처리를 전문으로 하는 객체를 "DAO(data access object)"** 라고 부른다.

<br>

- **DAO의 분리**

  <img src="../capture/스크린샷 2019-09-14 오후 3.54.18.png">

  - DAO는 데이터베이스나 파일, 메모리 등을 이용하여 애플리케이션 데이터를 생성, 조회, 변경, 삭제하는 역할을 수행한다. 
  - 업무 로직에서 데이터 처리 부분을 분리하여 별도의 객체로 정의하면, 여러 업무에서 공통으로 사용할 수 있기 때문에 유지보수가 쉬워지고 재사용성이 높아진다.

<br>

## 5.10.1. DAO 사용하기

MemberListServlet에서 데이터 처리 로직을 분리하여 MemberDao를 정의한다.

<br>

- **DAO가 적용된 후의 회원 목록 조회 시나리오**

  ```sequence
  웹브라우저->MemberListServlet: (1)요청
  MemberListServlet->MemberDao: (2)selectList()
  MemberDao->Database: (3)SELECT
  Database-->MemberDao: 결과
  MemberDao->Member: (4)목록생성
  MemberDao-->MemberListServlet: List<Member>
  MemberListServlet->MemberList.jsp: (5)인클루딩
  MemberList.jsp->Member: (6)참조
  MemberList.jsp-->MemberListServlet: 회원 목록 화면
  MemberListServlet-->웹브라우저: (7)응답
  ```

<br>

## 5.10.2. DAO 생성

- **src/spms/dao/MemberDao.java**

  ```java
  package spms.dao;
  
  import spms.vo.Member;
  
  import java.sql.Connection;
  import java.sql.PreparedStatement;
  import java.sql.ResultSet;
  import java.util.ArrayList;
  import java.util.List;
  
  public class MemberDao {
  
    Connection connection;
  
    public void setConnection(Connection connection) {
      this.connection = connection;
    }
  
    public List<Member> selectList() throws Exception {
      String query = "select mno, mname, email, cre_date" +
          " from members" +
          " order by mno";
      try (PreparedStatement ps = connection.prepareStatement(query);
           ResultSet rs = ps.executeQuery()) {
        ArrayList<Member> members = new ArrayList<>();
  
        while (rs.next()) {
          members.add(new Member()
          .setNo(rs.getInt("mno"))
          .setName(rs.getString("mname"))
          .setEmail(rs.getString("email"))
          .setCreateDate(rs.getDate("cre_date")));
        }
        return members;
      } catch (Exception e) {
        throw e;
      }
    }
  
  }
  ```

<br>

### Connection 객체 주입

MemberDao 는 selectList() 가 호출되기 전에, Connection 객체가 먼저 설정되어야 한다. 이렇게 **작업에 필요한 객체를 외부로부터 주입 받는 것을 '의존성 주입(DI, Dependency Injection)'** 이라고 부른다. **다른 말로 '역제어(IoC, Inbersion of Control)'** 라고도 부른다.

그래서 MemberDao 의 Connection 을 설정해주는 셋터 메서드는 MemberListServlet에서 호출된다.

<br>

## 5.10.3. 서블릿에서 DAO 사용하기

- **src/spms/servlets/MemberListServlet.java**

  ```java
  ...
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Connection connection = (Connection) sc.getAttribute("conn");
  
    // 데이터 베이스 연결 및 쿼리 실행
    try {
      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(connection);
  
      req.setAttribute("members", memberDao.selectList());
      resp.setContentType("text/html; charset=UTF-8");
  
      RequestDispatcher rd = req.getRequestDispatcher(
        "/member/MemberList.jsp");
      rd.include(req, resp);
  
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }
  ...
  ```

  - MemberListServlet이 할 일은, 클라이언트의 요청에 대해 어떤 Dao를 사용하고, 어느 JSP로 그 결과를 보내야 하는지 조정(Control)하는 것이다.

<br>

이제서야 컴포넌트 별로 역할 구분이 명확해졌다. **서블릿은 컨트롤러, Dao는 모델, JSP는 뷰의 역할을 수행한다.**

<br>

# 실력 향상 과제

1. MemberDao 클래스에 다음 메서드를 추가한다.

   ```java
   public int insert(Member member) throws Exception { /* 회원 등록 */ }
   public int delete(int no) throws Exception { /* 회원 삭제 */ }
   public Member selectOne(int no) throws Exception { /* 회원 상세 정보 조회 */ }
   public int update(Member member) throws Exception { /* 회원 정보 변경 */ }
   public Member exist(String email, String password) throws Exception 
   { /* 있으면 Member 객체 리턴, 없으면 null 리턴 */ }
   ```

   - **src/spms/dao/MemberDao.java**

     ```java
     package spms.dao;
     
     import spms.vo.Member;
     
     import java.sql.Connection;
     import java.sql.PreparedStatement;
     import java.sql.ResultSet;
     import java.sql.SQLException;
     import java.util.ArrayList;
     import java.util.List;
     
     public class MemberDao {
     
       Connection connection;
     
       public void setConnection(Connection connection) {
         this.connection = connection;
       }
     
       public List<Member> selectList() throws Exception {
         String query = "select mno, mname, email, cre_date" +
             " from members" +
             " order by mno";
         try (PreparedStatement ps = connection.prepareStatement(query);
              ResultSet rs = ps.executeQuery()) {
           ArrayList<Member> members = new ArrayList<>();
     
           while (rs.next()) {
             members.add(new Member()
                 .setNo(rs.getInt("mno"))
                 .setName(rs.getString("mname"))
                 .setEmail(rs.getString("email"))
                 .setCreateDate(rs.getDate("cre_date")));
           }
           return members;
         }
       }
     
       public int insert(Member member) throws Exception {
         int success;
         String query = "insert into members (email, pwd, mname, cre_date, mod_date) values" +
             " (?, ?, ?, now(), now())";
     
         try (PreparedStatement ps = connection.prepareStatement(query)) {
           ps.setString(1, member.getEmail());
           ps.setString(2, member.getPassword());
           ps.setString(3, member.getName());
           success = ps.executeUpdate();
         }
     
         return success;
       }
     
       public int delete(int no) throws Exception {
         int success;
         String query = "delete from members where mno=?";
     
         try (PreparedStatement ps = connection.prepareStatement(query)) {
           ps.setInt(1, no);
           success = ps.executeUpdate();
         }
     
         return success;
       }
     
       public Member selectOne(int no) throws Exception {
         Member member;
         String query = "select mno, email, mname, cre_date from members" +
             " where mno=" + no;
     
         try (PreparedStatement ps = connection.prepareStatement(query);
              ResultSet rs = ps.executeQuery()) {
           rs.next();
           member = new Member()
               .setNo(no)
               .setEmail(rs.getString("email"))
               .setName(rs.getString("mname"))
               .setCreateDate(rs.getDate("cre_date"));
         }
     
         return member;
       }
     
       public int update(Member member) throws Exception {
         int success = 0;
         String query = "update members set email=?, mname=?, mod_date=now() where mno=?";
     
         try (PreparedStatement ps = connection.prepareStatement(query)) {
           ps.setString(1, member.getEmail());
           ps.setString(2, member.getName());
           ps.setInt(3, member.getNo());
           success = ps.executeUpdate();
         } catch (SQLException e) {
           System.out.println(e.getSQLState());
           System.out.println(e.getMessage());
         }
     
         return success;
       }
     
       public Member exist(String email, String password) throws Exception {
         Member member = null;
         String query = "select mno, mname, cre_date, mod_date from members" +
             " where email=? and pwd=?";
     
         try (PreparedStatement ps = connection.prepareStatement(query)) {
           ps.setString(1, email);
           ps.setString(2, password);
           ResultSet rs = ps.executeQuery();
     
           if (rs.next()) {
             member = new Member()
                 .setName(rs.getString("mname"))
                 .setEmail(email)
                 .setNo(rs.getInt("mno"))
                 .setCreateDate(rs.getDate("cre_date"))
                 .setModifiedDate(rs.getDate("mod_date"))
                 .setPassword(password);
           }
         } catch (Exception e) {
           e.printStackTrace();
         }
     
         return member;
       }
     
     }
     ```

     <br>

2. MemberAddServlet 클래스 변경 - MemberDao 의 insert()를 사용하여 회원 정보를 추가한다.

   - **src/spms/servlets/MemberAddServlet.java**

     ```java
     package spms.servlets;
     
     import spms.dao.MemberDao;
     import spms.vo.Member;
     
     import javax.servlet.RequestDispatcher;
     import javax.servlet.ServletContext;
     import javax.servlet.ServletException;
     import javax.servlet.annotation.WebServlet;
     import javax.servlet.http.HttpServlet;
     import javax.servlet.http.HttpServletRequest;
     import javax.servlet.http.HttpServletResponse;
     import java.io.IOException;
     import java.sql.Connection;
     
     @WebServlet("/member/add")
     public class MemberAddServlet extends HttpServlet {
     
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         RequestDispatcher rd = req.getRequestDispatcher(
             "/member/MemberAdd.jsp");
         rd.forward(req, resp);
       }
     
       @Override
       protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         ServletContext sc = this.getServletContext();
         Connection connection = (Connection) sc.getAttribute("conn");
     
         try {
           MemberDao memberDao = new MemberDao();
           memberDao.setConnection(connection);
     
           Member member = new Member()
               .setEmail(req.getParameter("email"))
               .setPassword(req.getParameter("password"))
               .setName(req.getParameter("name"));
     
           memberDao.insert(member);
           resp.sendRedirect("list");
         } catch (Exception e) {
           req.setAttribute("error", e);
           RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
           rd.forward(req, resp);
         }
       }
     
     }
     ```

   <b>

3. MemberUpdateServlet 클래스 변경 - MemberDao 의 selectOne() 과 update()를 사용하여 회원 정보를 변경한다.

   - **src/spms/servlets/MemberUpdateServlet.java**

     ```java
     package spms.servlets;
     
     import spms.dao.MemberDao;
     import spms.vo.Member;
     
     import javax.servlet.RequestDispatcher;
     import javax.servlet.ServletContext;
     import javax.servlet.ServletException;
     import javax.servlet.annotation.WebServlet;
     import javax.servlet.http.HttpServlet;
     import javax.servlet.http.HttpServletRequest;
     import javax.servlet.http.HttpServletResponse;
     import java.io.IOException;
     import java.sql.Connection;
     
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

   <br>

4. MemberDeleteServlet 클래스 변경 - MemberDao 의 delete()을 사용하여 회원 정보를 삭제 한다.

   - **src/spms/servlets/MemberDeleteServlet.java**

     ```java
     package spms.servlets;
     
     import spms.dao.MemberDao;
     
     import javax.servlet.RequestDispatcher;
     import javax.servlet.ServletContext;
     import javax.servlet.ServletException;
     import javax.servlet.annotation.WebServlet;
     import javax.servlet.http.HttpServlet;
     import javax.servlet.http.HttpServletRequest;
     import javax.servlet.http.HttpServletResponse;
     import java.io.IOException;
     import java.sql.Connection;
     
     @WebServlet("/member/delete")
     public class MemberDeleteServlet extends HttpServlet {
     
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         ServletContext sc = this.getServletContext();
         Connection conn = (Connection) sc.getAttribute("conn");
     
         try {
           MemberDao memberDao = new MemberDao();
           memberDao.setConnection(conn);
           memberDao.delete(Integer.parseInt(req.getParameter("no")));
           resp.sendRedirect("list");
         } catch (Exception e) {
           req.setAttribute("error", e);
           e.printStackTrace();
           RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
           rd.forward(req, resp);
         }
       }
     }
     ```

   <br>

5. LogInServlet 클래스 변경 - MemberDao 의 exist() 메서드를 이용하여 사용자 인증을 처리한다.

   - **src/spms/servlets/LogInServlet.java**

     ```java
     package spms.servlets;
     
     import spms.dao.MemberDao;
     import spms.vo.Member;
     
     import javax.servlet.RequestDispatcher;
     import javax.servlet.ServletContext;
     import javax.servlet.ServletException;
     import javax.servlet.annotation.WebServlet;
     import javax.servlet.http.HttpServlet;
     import javax.servlet.http.HttpServletRequest;
     import javax.servlet.http.HttpServletResponse;
     import javax.servlet.http.HttpSession;
     import java.io.IOException;
     import java.sql.Connection;
     
     @WebServlet("/auth/login")
     public class LogInServlet extends HttpServlet {
     
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         RequestDispatcher rd = req.getRequestDispatcher(
             "/auth/LogInForm.jsp");
         rd.forward(req, resp);
       }
     
       @Override
       protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         ServletContext sc = this.getServletContext();
         Connection conn = (Connection) sc.getAttribute("conn");
     
         try {
           MemberDao memberDao = new MemberDao();
           memberDao.setConnection(conn);
           Member member = memberDao.exist(req.getParameter("email"), req.getParameter("password"));
           if (member != null) {
             HttpSession session = req.getSession();
             session.setAttribute("member", member);
             resp.sendRedirect("../member/list");
           } else {
             RequestDispatcher rd = req.getRequestDispatcher("/auth/LogInFail.jsp");
             rd.forward(req, resp);
           }
         } catch (Exception e) {
           req.setAttribute("error", e);
           RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
           rd.forward(req, resp);
         }
       }
     
     }
     ```

<br>

# 5.11. ServletContextListener와 객체 공유

웹 애플리케이션의 시작에서 종료까지 주요한 사건에 대해 알림 기능을 이용하기 위해서는 규칙에 따라 객체를 만들어 DD 파일(web.xml)에 등록하면 된다. 이렇게 **사건이 발생했을 때 알림을 받는 객체를 '리스너(Listener)'** 라고 부른다.

<br>

- **웹 애플리케이션의 상태 감시와 사건 인지**

  <img src="../capture/스크린샷 2019-09-15 오후 2.31.12.png" width=500>

<br>

- **웹 애플리케이션의 주요 사건과 인터페이스**

| 분류            | 사건                                   | 인터페이스                                       |
| --------------- | -------------------------------------- | ------------------------------------------------ |
| 웹 애플리케이션 | 시작 or 종료                           | javax.servlet.ServletContextListener             |
| "               | ServletContext에 값을 추가, 제거, 대체 | javax.servlet.ServletContextAttributeListener    |
| 세션            | 생성, 소멸                             | javax.servlet.http.HttpSessionListener           |
| "               | 활성, 비활성                           | javax.servlet.http.HttpSessionActivationListener |
| "               | HttpSession에 값을 추가,제거, 대체     | javax.servlet.http.HttpSessionAttributeListener  |
| 요청            | 요청을 받고, 응답                      | javax.servlet.ServletRequestListener             |
| "               | ServletRequest에 값을 추가, 제거, 대체 | javax.servlet.ServletRequestAttributeListener    |

<br>

예제를 살펴보면 **매번 DAO 인스턴스를 생성한다.** 이렇게 요청을 처리할 때마다 객체를 만들면 가비지(garbage)가 생성되고, 실행 시간이 길어진다.

DAO의 경우처럼 여러 서블릿이 사용하는 객체는 서로 공유하는 것이 메모리 관리나 실행 속도 측면에서 좋다. 그래서 **DAO를 공유하려면 ServletContext에 저장하는 것이 좋다. ServletContext는 웹 애플리케이션이 종료될 때까지 유지되는 보관소이기 때문이다.**

<br>

- **ServletContext를 통해 DAO 공유**

  웹 애플리케이션이 시작되거나 종료되는 사건이 발생하면, 이를 알리고자 서블릿 컨테이너는 리스너의 메서드를 호출한다. 바로 이 <u>리스너에서 DAO를 준비하면 된다.</u>

<br>

## 5.11.1. ServletContextListener의 활용

웹 애플리케이션의 시작과 종료 사건을 담당할 리스너를 준비한다. AppInitServlet이 하던 일을 리스너로 옮긴다. 또한, MemberDao의 인스턴스 생성도 이 리스너에서 준비한다.

- **리너의 구동 과정과 DAO 공유**

  ```sequence
  웹애플리케이션->서블릿컨테이너: 웹 애플리케이션 시작
  서블릿컨테이너->ContextLoaderListener: (1)contextInitalized()
  ContextLoaderListener->Connection: (2)생성
  ContextLoaderListener->MemberDao: (3)생성
  ContextLoaderListener->MemberDao: (4)setConnection(conn)
  ContextLoaderListener->ServletContext: (5)setAttribute("memberDao", dao)
  웹애플리케이션->서블릿컨테이너: 웹 애플리케이션 종료
  서블릿컨테이너->ContextLoaderListener: contextDestroyed()
  ```

<br>

## 5.11.2. 리스너 ServletContextListener 만들기

웹 애플리케이션의 시작과 종료 이벤트를 처리할 리스너는 **ServletContextListener 인터페이스를** 구현해야 한다.

- **ServletContextListener의 구현체**

  <img src="../capture/스크린샷 2019-09-15 오후 5.44.31.png">

  - **contextInitialized()** : 웹 애플리케이션이 시작될 때 호출된다. 공용 객체를 준비할 때 사용.
  - **contextDestroyed()** : 웹 애플리케이션이 종료되기 전에 호출된다. 자원 해제를 할 때 사용.

<br>

- **src/listeners/ContextLoaderListener.java**

  ```java
  package spms.listeners;
  
  import spms.dao.MemberDao;
  
  import javax.servlet.ServletContext;
  import javax.servlet.ServletContextEvent;
  import javax.servlet.ServletContextListener;
  import java.sql.Connection;
  import java.sql.DriverManager;
  
  
  @WebListener	// 리스너 배치
  // ServletContextListener 구현
  public class ContextLoaderListener implements ServletContextListener {
  
    Connection conn;
  
    // DB 커넥션 객체 준비 코드 추가
    @Override
    public void contextInitialized(ServletContextEvent sce) {
      try {
        ServletContext sc = sce.getServletContext();
  
        Class.forName(sc.getInitParameter("driver"));
        conn = DriverManager.getConnection(
            sc.getInitParameter("url"),
            sc.getInitParameter("username"),
            sc.getInitParameter("password"));
  
        MemberDao memberDao = new MemberDao();
        memberDao.setConnection(conn);
  
        // ServletContext에 보관
        sc.setAttribute("memberDao", memberDao);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  
    // 데이터베이스 연결을 끊음
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
      try {
        conn.close();
      } catch (Exception e) {}
    }
  
  }
  ```

<br>

## 5.11.3. 기존의 서블릿 변경하기

직접 MemberDao 객체를 생성하는 대신, ServletContext에 저장된 DAO 객체를 꺼내 쓰는 것으로 변경한다.

- **src/spms/MemberListServlet.java**

  ```java
  ...
  public class MemberListServlet extends HttpServlet {
  
    private ServletContext sc;
  
    @Override
    public void init() throws ServletException {
      super.init();
      sc = this.getServletContext();
    }
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      // Connection을 생성하는 부분과 MemberDao를 생성하는 부분을 제거
      try {
        MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
  
        req.setAttribute("members", memberDao.selectList());
        resp.setContentType("text/html; charset=UTF-8");
  
        RequestDispatcher rd = req.getRequestDispatcher(
            "/member/MemberList.jsp");
        rd.include(req, resp);
  
      } catch (Exception e) {
        e.printStackTrace();
        req.setAttribute("error", e);
        RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
        rd.forward(req, resp);
      }
    }
  
  }
  ```

- **src/spms/serlbets/AppInitServlet을 제거**

<br>

# 실력 향상 과제

**MemberListServlet에 적용해 봤으니, 나머지 서블릿도 적용하세요.**

1. MemberAddServlet 클래스 변경 **(src/servlets/MemberAddServlet.java)**

   ```java
   ...
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     ServletContext sc = this.getServletContext();
   
     try {
       MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
   
       Member member = new Member()
         .setEmail(req.getParameter("email"))
         .setPassword(req.getParameter("password"))
         .setName(req.getParameter("name"));
   
       memberDao.insert(member);
       resp.sendRedirect("list");
     } catch (Exception e) {
       req.setAttribute("error", e);
       RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
       rd.forward(req, resp);
     }
   }
   ...
   ```

2. MemberUpdateServelt 클래스 변경 **(src/servlets/MemberUpdateServlet.java)**

   ```java
   ...
   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     ServletContext sc = this.getServletContext();
     try {
       MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
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
     try {
       Member member = new Member();
       member.setEmail(req.getParameter("email"))
         .setName(req.getParameter("name"))
         .setNo(Integer.parseInt(req.getParameter("no")));
   
       MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
       memberDao.update(member);
   
       resp.sendRedirect("list");
     } catch (Exception e) {
       e.printStackTrace();
       req.setAttribute("error", e);
       RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
       rd.forward(req, resp);
     }
   }
   ...
   ```

3. MemberDeleteServlet 클래스 변경

   ```java
   ...
   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     ServletContext sc = this.getServletContext();
     try {
       MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
       memberDao.delete(Integer.parseInt(req.getParameter("no")));
       resp.sendRedirect("list");
     } catch (Exception e) {
       req.setAttribute("error", e);
       e.printStackTrace();
       RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
       rd.forward(req, resp);
     }
   }
   ...
   ```

4. LogInServlet 클래스 변경

   ```java
   ...
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     ServletContext sc = this.getServletContext();
   
     try {
       MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
       Member member = memberDao.exist(req.getParameter("email"), req.getParameter("password"));
       if (member != null) {
         HttpSession session = req.getSession();
         session.setAttribute("member", member);
         resp.sendRedirect("../member/list");
       } else {
         RequestDispatcher rd = req.getRequestDispatcher("/auth/LogInFail.jsp");
         rd.forward(req, resp);
       }
     } catch (Exception e) {
       req.setAttribute("error", e);
       RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
       rd.forward(req, resp);
     }
   }
   ...
   ```

<br>

# 5.12. DB 커넥션풀

**DB 커넥션 객체를 여러 개 생성하여 풀(Pool)에 담아 놓고 필요할 때 꺼내 쓰는 방식이다.** 즉, 자주 쓰는 객체를 미리 만들어 두고, 필요할 때마다 빌리고, 사용한 다음 반납하는 방식을 **'풀링(pooling)'** 이라 한다. 이렇게 객체를 모아둔 것을 **'객체 풀(object pool)'** 이라 하고, 여러 개의 DB 커넥션을 관리하는 객체를 **'DB 커넥션풀'** 이라 한다.

- **싱글 커넥션 사용의 문제점**

  <img src="../capture/스크린샷 2019-09-15 오후 6.58.10.png">

  - '롤백(rollback)' 이 한 곳에서 발생하게 되면 모든 Statement는 같은 커넥션에서 생성한 객체이므로 그 커넥션을 통해 이루어진 모든 작업들도 롤백이 된다. 따라서 **웹 브라우저의 요청을 처리할 때, 각 요청에 대해 개별 DB 커넥션을 가용해야 한다.**
  - 커넥션을 맺을 때마다 데이터베이스 서버는 사용자 인증과 권한 검사를 수행하고 요청 처리를 위한 준비 작업을 해야 한다. 이런 문제를 해결하기 위해 등장한 것이 **DB 커넥션 풀이다.**

<br>

## 5.12.1. DB 커넥션풀

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile23.uf.tistory.com%2Fimage%2F215D3A33591A5EAF109B08">

<br>

# 5.12.2. DB 커넥션풀 만들기

- **src/spms/util/DBConnectionPool.java**

  ```java
  package spms.util;
  
  import java.sql.Connection;
  import java.sql.DriverManager;
  import java.util.ArrayList;
  
  public class DBConnectionPool {
  
    private String url;
    private String username;
    private String password;
    // Connection 객체를 보관할 ArrayList
    private ArrayList<Connection> connList = new ArrayList<>();
  
    // DB 커넥션 생성에 필요한 값을 매개변수로 받음
    public DBConnectionPool(String driver, String url,
                            String username, String password) throws Exception {
      this.url = url;
      this.username = username;
      this.password = password;
  
      Class.forName(driver);
    }
  
    public Connection getConnection() throws Exception {
      if (connList.size() > 0) {
        Connection conn = connList.get(0);
        // DB 커넥션 객체도 일정 시간이 지나면 서버와의 연결이 끊어지기 때문에 유효성 체크 후 반환
        if (conn.isValid(10)) {
          return conn;
        }
      }
      // ArrayList에 보관된 객체가 없다면, DriverManager를 통해 새로 만들어 반환
      return DriverManager.getConnection(url, username, password);
    }
  
    // 커넥션 객체를 쓰고 난 다음에 커넥션 풀에 반환
    public void returnConnection(Connection conn) throws Exception {
      connList.add(conn);
    }
  
    // 웹 애플리케이션이 종료하기 전에 이 메서드를 호출하여 데이터베이스와 연결된 것을 모두 끊는다.
    public void closeAll() {
      for (Connection conn : connList) {
        try {conn.close();} catch (Exception ignored){}
      }
    }
  
  }
  ```

<br>

## 5.12.3. MemberDAO에 DB 커넥션풀 적용하기

- **src/spms/dao/MemberDao.java**

  ```java
  ...
  public class MemberDao {
  
    // DB 커넥션 풀 필드로 정의
    private DBConnectionPool connPool;
  
    // 셋터 메소드 추가
    public void setDbConnectionPool(DBConnectionPool connPool) {
      this.connPool = connPool;
    }
    
    // 모든 메소드들에 다음과 같이 적요
    public List<Member> selectList() throws Exception {
      String query = "select mno, mname, email, cre_date" +
          " from members" +
          " order by mno";
      
      // 커넥션 객체를 DB 커넥션 풀로 부터 커넥션을 가져온다.
      Connection conn = connPool.getConnection();
  
      try (PreparedStatement ps = conn.prepareStatement(query);
           ResultSet rs = ps.executeQuery()) {
        ArrayList<Member> members = new ArrayList<>();
  
        while (rs.next()) {
          members.add(new Member()
              .setNo(rs.getInt("mno"))
              .setName(rs.getString("mname"))
              .setEmail(rs.getString("email"))
              .setCreateDate(rs.getDate("cre_date")));
        }
        return members;
      } finally {
        // 마지막에 커넥션을 다시 반환해준다.
        if (conn != null) connPool.returnConnection(conn);
      }
    }
    ...
  ```

<br>

