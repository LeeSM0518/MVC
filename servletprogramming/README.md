# 03. 서블릿 프로그래밍

**'서블릿(Servlet)'** : 웹 브라우저와 웹 서버를 활용하여 좀 더 쉽게 서버 애플리케이션을 개발할 수 있도록 하는 기술.

서블릿 프로그래밍의 핵심은 **Servlet 인터페이스를** 이해하는 것이다.

<br/>

# 3.1. CGI 프로그램과 서블릿

## 3.1.1. CGI의 이해

* **'애플리케이션', '데스크톱 애플리케이션'** : 사용자가 직접 아이콘을 더블 클릭하거나 명령 창을 통해 실행시키는 프로그램
* **'웹 애플리케이션'** : 사용자가 웹 서버를 통해 간접적으로 실행시키는 프로그램

<br/>

### CGI 규칙

1. 웹 브라우저가 웹 서버에게 실행 요청
2. 웹 서버는 클라이언트가 요청한 프로그램을 찾아서 실행
3. 해당 프로그램이 작업을 수행 후, 결과를 웹 서버에게 돌려줌
4. 웹 서버는 그 결과를 HTTP 형식에 맞추어 웹 브라우저에게 보냄

<img src="../capture/스크린샷 2019-08-20 오후 1.47.04.png">

* 이때 웹 서버와 프로그램 사이의 데이터를 주고받는 규칙을 **CGI(Common Gateway Interface)라고** 한다.
* 이렇게 웹 서버에 의해 실행되며, CGI 규칙에 따라서 **웹 서버와 데이터를 주고 받도록 작성된 프로그램을 'CGI 프로그램'** 이라고 한다.

<br/>

### CGI 프로그램

**컴파일 방식은** 기계어로 번역된 코드를 바로 실행하기 때문에 실행 속도가 빠르지만, 변경 사항이 발생할 때 마다 다시 컴파일하고 재배포 해야 하는 문제가 있다.

* **컴파일 방식 CGI 프로그램**

  ​	<img src="../capture/스크린샷 2019-08-20 오후 4.01.02.png" width=500>

<br/>

그에 비해 **스크립트 방식은** 실행할 때마다 소스 코드의 문법을 검증하고 해석해야 하기 때문에 실행 속도가 느리다. 하지만 변경 사항이 발생하면 단지 소스 코드를 수정하고 저장만 하면 되기 때문에 편리합니다.

* **인터프리터 방식 CGI 프로그램**

  <img src="../capture/스크린샷 2019-08-20 오후 4.16.09.png">

<br/>

## 3.1.2. 서블릿

* 자바 CGI 프로그램은 C/C++처럼 **컴파일 방식이다.** 
* 자바로 만든 CGI 프로그램을 **'서블릿(Servlet)'** 이라고 부른다. 
* 자바 서블릿은 웹 서버와 직접 데이터를 주고받지 않으며, 전문 프로그램에 의해 관리된다.

<br/>

### 서블릿 컨테이너

**'서블릿 컨테이너(Servlet Container)'** : 서블릿의 생성과 실행, 소멸 등 생명주기를 관리하는 프로그램.

서블릿 컨테이너가 서블릿 대신하여 CGI 규칙에 따라 **웹 서버와 데이터를 주고받는다.**

* **서블릿과 서블릿 컨테이너**

  <img src="../capture/스크린샷 2019-08-20 오후 4.23.31.png">

<br/>

# 3.2. 서블릿, JSP vs. Java EE vs. WAS

## 3.2.1. Java EE 와 서블릿/JSP

**Java EE는** 기능 확장이 쉽고, 이기종 간의 이식이 쉬우며, 신뢰성과 보안성이 높고, 트랜잭션 관리와 분산 기능을 쉽게 구현할 수 있는 기술을 제공한다.

Java EE 기술 중에서 **서블릿, JSP는 웹을 기반으로 한 클라이언트, 서버 기술을 정의하고 있다.** 자바로 웹 애플리케이션을 개발한다는 것은 바로 이 서블릿과 JSP 기술을 사용하여 애플리케이션을 개발한다는 것을 말한다.

<br/>

## 3.2.2. WAS의 이해

클라이언트 서버 시스템 구조에서 서버 쪽 애플리케이션의 생성과 실행, 소멸을 관리하는 프로그램을 **'애플리케이션 서버(Application Server)'라 한다.**

서블릿과 서블릿 컨테이너와 같이 웹 기술을 기반으로 동작하는 애플리케이션 서버를 **'WAS(Web Application Server)'** 라 부른다.

특히 Java에서 말하는 WAS란, **Java EE 기술 사양을 준수하여 만든 서버를 가리킨다.** 다른 말로 'Java EE 구현체(Implementation)' 라고도 부른다.

<br/>

### 서블릿 컨테이너

Java EE 기술 중에서 서블릿, JSP 등 웹 관련 부분만 구현한 서버도 있다. 이런 서버를 **'서블릿 컨테이너' 또는 '웹 컨테이너'라고 부른다.** (Ex. 아파치 재단의 톰캣)

서블릿이나 JSP 프로그램을 할 때는 사용하는 **제품의 버전에 맞추어 API를 사용해야 한다.** 즉 사용하는 WAS가 어떤 버전의 Java EE 구현체인지 또는 서블릿 컨테이너인지 확인하여 그 버전에 맞는 API를 사용한다.

| JavaEE   | Servlet/JSP          | Tomcat | JBoss                 | WebLogic | JEUS |
| -------- | -------------------- | ------ | --------------------- | -------- | ---- |
| JavaEE 6 | Servlet 3.0, JSP 2.2 | 7.0.x  | 7.x(all), 6.x(almost) | 12.x     | 7.x  |

* all(모든 기술 구현함), almost(일부 기술 미구현)
* 예를 들어 고객사에서 사용하는 WAS가 WebLogic 12.x 라면, 서블릿/JSP를 만들 때 Java EE 6 버전에 맞추어 프로그래밍을 한다.

<br/>

# 3.3. 웹 프로젝트 준비

## 3.3.1. 웹 프로젝트 폴더 구조

### /src

자바 소스 파일을 두는 폴더이다. 앞으로 이 폴더에 서블릿 클래스나 필터, 리스너 등 필요한 모든 자바 클래스 파일을 둘 것이다.

### /build/classes

컴파일된 자바 클래스 파일(.class)이 놓이는 폴더이다. 물론 패키지에 소속된 클래스인 경우 이 폴더에 해당 패키지가 자동으로 만들어진다.

### /web

HTML(.html), CSS(.css), JavaScript(.js), JSP, 이미지 파일 등 웹 콘텐츠를 두는 폴더이다. 웹 애플리케이션을 서버에 배치할 때 이 폴더의 내용물이 그대로 복사된다.

### /web/WEB-INF

웹 애플리케이션의 설정과 관련된 파일을 두는 폴더이다. HTML이나 JavaScript, CSS 등 클라이언트에서 요청할 수 있는 파일들을 이 폴더에 두어서는 안된다.

### /lib

자바 아카이브(Archive) 파일(.jar)을 두는 폴더이다. 아카이브 파일은 클래스 파일(.class)과 프로퍼티 파일(.properties)들을 모아 놓은 보관소 파일이다.

<br/>

# 3.4. 서블릿 만들기

## 3.4.1. 서블릿 클래스 작성

**src/lesson3/servlets/HelloWord.java**

```java
package lesson03.servlets;

import javax.servlet.*;
import java.io.IOException;

public class HelloWorld implements Servlet {

  ServletConfig config;

  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    System.out.println("init() calls");
    this.config = servletConfig;
  }

  @Override
  public ServletConfig getServletConfig() {
    System.out.println("getServletConfig() calls");
    return this.config;
  }

  @Override
  public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    System.out.println("service() calls");
  }

  @Override
  public String getServletInfo() {
    System.out.println("getServletInfo() calls");
    return "version=1.0;author=sangmin;";
  }

  @Override
  public void destroy() {
    System.out.println("destroy() calls");
  }
}
```

<br>

## 3.4.2. javax.servlet.Servlet 인터페이스

서블릿 클래스는 반드시 javax.servlet.Servlet 인터페이스를 구현해야 합니다. 서블릿 컨테이너가 서블릿에 대해 호출할 메서드를 정의한 것이 Servlet 인터페이스입니다.

<img src="../capture/스크린샷 2019-08-21 오전 12.29.08.png" width=700>

<br>

### 서블릿의 생명주기와 관련된 메서드: init(), service(), destroy()

* **init()** 
  * 서블릿 컨테이너가 서블릿을 생성한 후 초기화 작업을 수행하기 위해 호출하는 메서드이다.
  * 서블릿이 클라이언트의 요청을 처리하기 전에 준비할 작업이 있다면 이 메서드에 작성해야한다.
* **service()**
  * 클라이언트가 요청할 때 마다 호출되는 메서드이다.
  * 실질적으로 서비스 작업을 수행하는 메서드이다.
* **destroy()**
  * 서블릿 컨테이너가 종료되거나 웹 애플리케이션이 멈출 때, 또는 해당 서블릿을 비활성화 시킬 때 호출된다.
  * 자원을 해제한다거나 데이터를 저장하는 등의 마무리 작업을 작성한다.

<br/>

### Servlet 인터페이스 기타 메서드: getServletConfig(), getServletInfo()

이들 메서드는 서블릿 정보를 추출할 필요가 있을 때 호출하는 메서드이다.

* **getServletConfig()** 
  * 서블릿 설정 정보를 다루는 ServletConfig 객체를 반환한다.
  * ServletConfig 객체를 통해 서블릿 이름과 서블릿 초기 매개변수 값, 서블릿 환경정보를 얻을 수 있다.
* **getServletInfo()**
  * 서블릿을 작성한 사람에 대한 정보라든가 서블릿 버전, 권리 등을 담은 문자열을 반환한다.

<br/>

## 3.4.3. 서블릿 배치 정보 작성

**web/WEB-INF/web.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    <display-name>Apache-Axis</display-name>
    
    <!-- 서블릿 선언 -->
    <servlet>
        <servlet-name>Hello</servlet-name>
        <servlet-class>lesson03.servlets.HelloWorld</servlet-class>
    </servlet>
    
    <!-- 서블릿을 URL 과 연결 -->
    <servlet-mapping>
        <servlet-name>Hello</servlet-name>
        <url-pattern>/Hello</url-pattern>
    </servlet-mapping>
  
  ...
```

web.xml 파일을 **배치 기술서(Deployment Descriptor, DD)** 라고 부른다. 웹 애플리케이션의 배치 정보를 담고 있는 파일이다. 따라서 **서블릿을 만들었으면 DD파일에 배치 정보를 등록해야 한다.** 그래야만 클라이언트에서 해당 서블릿의 실행을 요청할 수 있다.

<br/>

* **서블릿 선언**

  ```xml
  <servlet>
    <servlet-name>Hello</servlet-name>
    <servlet-class>lesson03.servlets.HelloWorld</servlet-class>
  </servlet>
  ```

  * **\<servlet-name>** : 서블릿 별명을 설정한다.
  * **\<servlet-class>** : 패키지 이름을 포함한 서블릿 클래스명을 설정한다.

* **서블릿에 URL 부여**

  ```xml
  <servlet-mapping>
    <servlet-name>Hello</servlet-name>
    <url-pattern>/Hello</url-pattern>
  </servlet-mapping>
  ```

  * **\<servlet-mapping>** : 서블릿과 URL을 매핑할 때 사용한다.
  * **\<servlet-name>** : \<servlet> 태그에서 정의한 서블릿 별명이 온다.
  * **\<url-pattern>** : 서블릿을 요청할 때 클라이언트가 사용할 URL을 설정한다.

<br/>

## 3.4.4. 서블릿 실행

* **웹**

<img src="../capture/스크린샷 2019-08-21 오전 12.51.19.png" width=500>

<br/>

* **서버**

<img src="../capture/스크린샷 2019-08-21 오전 12.52.26.png" width=500>

<br/>

## 3.4.5. URL과 서블릿 매핑

서블릿을 실행하고 싶으면 웹 브라우저에 URL을 입력하고 서버에 요청하면 된다. 앞에서 사용한 URL을 보면, 맨 뒤에 있는 **'/Hello' 가 바로 web.xml 에 설정했던 HelloWorld 서블릿의 URL 이름이다.**

* **서블릿의 URL 매핑**

  ```
  http://localhost:8080/Hello
  ```

  > \<url-pattern>**/Hello**\</url-pattern>

<br/>

## 3.4.6. 서블릿 구동 절차

<img src="../capture/스크린샷 2019-11-25 오후 9.11.43.png">

> **서블릿 인스턴스는 하나만 생성되어 웹 애플리케이션이 종료될 때까지 사용된다.** 따라서 인스턴스 변수에 특정 사용자를 위한 데이터를 보관해서는 안된다. 또한, 클라이언트가 보낸 데이터를 일시적으로 보관하기 위해 서블릿의 인스턴스 변수를 사용해서도 안된다.

<br/>

## 3.4.7. 웰컴 파일들

**web/WEB-INF/web.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
  <display-name>Apache-Axis</display-name>

  <!-- 서블릿 선언 -->
  <servlet>
    <servlet-name>Hello</servlet-name>
    <servlet-class>lesson03.servlets.HelloWorld</servlet-class>
  </servlet>

  <!-- 서블릿을 URL 과 연결 -->
  <servlet-mapping>
    <servlet-name>Hello</servlet-name>
    <url-pattern>/Hello</url-pattern>
  </servlet-mapping>

  <!-- welcome 태그 추가 -->
  <welcome-file-list>
    <welcome-file>default.html</welcome-file>
  </welcome-file-list>
  ...
```

* 위와 같이 welcome 태그에 여러 개의 웰컴 파일을 등록하게 되면, 디렉터리에서 웰컴 파일을 찾을 때 위에서부터 아래로 순차적으로 조회한다. 먼저 찾은 것을 클라이언트로 보내게 된다.

<br/>

**web/default.html**

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>환영합니다.</title>
  </head>
  <body>
    <h1>default.html</h1>
    <p>환영합니다.</p>
  </body>
</html>
```

<br/>

**웹 결과**

<img src="../capture/스크린샷 2019-08-21 오전 1.27.02.png" width=500>

<br/>

# 3.5. GenericServlet의 사용

## 3.5.1 GenericServlet이 없던 시절

우리는 서블릿을 만들 때마다 Servlet 인터페이스에 선언된 다섯 개의 메서드를 모두 구현하였습니다. 사실 이 메서드 중에서 반드시 구현해야 하는 메서드는 **service()** 입니다. 나머지 메서드들은 상황에 따라 구현하지 않아도 됩니다.

그럼에도 '인터페이스를 구현하는 클래스는 반드시 인터페이스에 선언된 모든 메서드를 구현해야 한다.' 라는 것이 자바의 법이기 때문에 빈 메서드라도 구현해야 합니다.

이런 불편한 점을 해소하기 위해 등장한 것이 **GenericServlet 추상 클래스** 입니다.

<br/>

## 3.5.2. GenericServlet의 용도

GenericServlet은 서블릿 클래스가 필요로 하는 init(), destroy(), getServletConfig(), getServletInfo()를 **미리 구현하여 상속해 줍니다.** 

service() 는 어차피 각 서블릿 클래스마다 별로도 구현해야 하기 때문에 GenericServlet에서는 구현되어 있지 않습니다.

<br/>

* **Servlet 인터페이스와 GenericServlet 추상 클래스**

  <img src="../capture/스크린샷 2019-08-21 오전 1.40.59.png" width=500>

* **src/lesson03/services/HelloWorld.java**

  ```java
  package lesson03.servlets;
  
  import javax.servlet.*;
  import java.io.IOException;
  
  public class HelloWorld extends GenericServlet {
  
    ServletConfig config;
  
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
      System.out.println("service() calls");
    }
    
  }
  ```

<br/>

## 3.5.3. 계산기 서블릿 작성

* **src/lesson03/servlets/CalculatorServlet.java**

  ```java
  package lesson03.servlets;
  
  import javax.servlet.GenericServlet;
  import javax.servlet.ServletException;
  import javax.servlet.ServletRequest;
  import javax.servlet.ServletResponse;
  import java.io.IOException;
  import java.io.PrintWriter;
  
  public class CalculatorServlet extends GenericServlet {
  
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
      int a = Integer.parseInt(servletRequest.getParameter("a"));
      int b = Integer.parseInt(servletRequest.getParameter("b"));
  
      servletResponse.setContentType("text/plain");
      servletResponse.setCharacterEncoding("UTF-8");
      PrintWriter writer = servletResponse.getWriter();
      writer.println("a=" + a + "," + "b=" + b + "의 계산결과 입니다.");
      writer.println("a + b = " + (a + b));
      writer.println("a - b = " + (a - b));
      writer.println("a * b = " + (a * b));
      writer.println("a / b = " + ((float)a / (float)b));
      writer.println("a % b = " + (a % b));
    }
  
  }
  ```

<br/>

## 3.5.4. ServletRequest

service()의 매개변수 중에서 **ServletRequest 객체는 클라이언트의 요청 정보를 다룰 때 사용한다.**

<br/>

**ServletRequest의 주요 메서드**

| 메서드                 | 설명                                                         |
| ---------------------- | ------------------------------------------------------------ |
| getRemoteAddr()        | 서비스를 요청한 클라이언트의 IP 주소를 반환                  |
| getScheme()            | 클라이언트가 요청한 URL 형식 Scheme을 반환한다. Ex) http, https, ftp, file, news |
| getProtocol()          | 요청 프로토콜의 이름과 버전을 반환한다. Ex) HTTP/1.1         |
| getParameterNames()    | 요청 정보에서 매개변수 이름만 추출하여 반환한다.             |
| getParameterValues()   | 요청 정보에서 매개변수 값만 추출하여 반환한다.               |
| getParameterMap()      | 요청 정보에서 매개변수들을 Map 객체에 담아서 반환한다.       |
| setCharacterEncoding() | POST 요청의 매개변수에 대해 문자 집합을 설정한다. 주의할 점은 처음 getParameter()를 호출하기 전에 이 메서드를 먼저 호출해야만 적용이 된다. |
| getParameter()         | GET 이나 POST 요청으로 들어온 매개변수 값을 꺼낼 때 사용한다. |

<br/>

## 3.5.5. ServletResponse

**ServletResponse 객체 기능**

* 클라이언트에 출력하는 데이터의 인코딩 타입을 설정
* 문자집합을 지정
* 출력 데이터를 임시 보관하는 버퍼의 크기를 조정
* 데이터를 출력하기 위해 출력 스트림을 준비

<br/>

### setContentType()

출력할 데이터의 인코딩 형식과 문자 집합을 지정한다. 이렇게 클라이언트에게 출력할 데이터의 정보를 알려주어야 클라이언트는 그 형식에 맞추어 올바르게 화면에 출력(Rendering)할 수 있습니다.

- **예시) 텍스트 형식**

  ```java
  response.setContentType("text/plain");
  ```

  - 출력할 데이터가 텍스트이고 별도의 메타정보가 없는 순수한 텍스트임을 지정하고 있다.

<br/>

### setCharacterEncoding()

출력할 데이터의 문자 집합을 지정한다.

- **예시) UTF-8 형식을 변환**

  ```java
  response.setCharacterEncoding("UTF-8");
  ```

- **출력 데이터의 문자 집합 다른 방식으로 설정**

  ```java
  response.setContentType("text/plain;chartset=UTF-8");
  ```

<br/>

### getWriter()

클라이언트로 출력할 수 있도록 출력 스트림 객체를 반환한다. 이미지나 동영상과 같은 바이너리 데이터를 출력하고 싶을 때는 getOutputStream()을 사용해야 한다.

```java
PrintWriter writer = response.getWriter();
```

- **주의할 점** : getWriter()를 호출하기 전에 setContentType() 이나 setCharacterEncoding()을 호출해야 한다. 그래야만 정상적으로 유니코드가 지정된 문자 집합으로 변환된다.

<br/>

## 3.5.6. CalculatorServlet 배치 및 테스트

**web/WEB-INF/web.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    <display-name>Apache-Axis</display-name>

    <!-- 서블릿 선언 -->
    <servlet>
        <servlet-name>Calculator</servlet-name>
        <servlet-class>lesson03.servlets.CalculatorServlet</servlet-class>
    </servlet>

    <!-- 서블릿을 URL 과 연결 -->
    <servlet-mapping>
        <servlet-name>Calculator</servlet-name>
        <url-pattern>/calc</url-pattern>
    </servlet-mapping>
  ...
```

**실행 결과**

<img src="../capture/스크린샷 2019-08-21 오전 2.07.04.png" width=500>

<br/>

## 3.5.7. @WebServlet 어노테이션을 이용한 서블릿 배치 정보 설정

Servlet 3.0 사양부터는 어노테이션으로 서블릿 배치 정보를 설정할 수 있다.

- **src/lesson03/servlets/CalculatorServlet.java**

  ```java
  ...
  @WebServlet("/calc")
  public class CalculatorServlet extends GenericServlet {
    ...
  ```

  - @WebServlet 어노테이션 작성

- **web/WEB-INF/web.xml**

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
           version="4.0">
      <display-name>Apache-Axis</display-name>
  
  <!--    &lt;!&ndash; 서블릿 선언 &ndash;&gt;-->
  <!--    <servlet>-->
  <!--        <servlet-name>Calculator</servlet-name>-->
  <!--        <servlet-class>lesson03.servlets.CalculatorServlet</servlet-class>-->
  <!--    </servlet>-->
  
  <!--    &lt;!&ndash; 서블릿을 URL 과 연결 &ndash;&gt;-->
  <!--    <servlet-mapping>-->
  <!--        <servlet-name>Calculator</servlet-name>-->
  <!--        <url-pattern>/calc</url-pattern>-->
  <!--    </servlet-mapping>-->
  ```

  - DD 파일(web.xml)에서 CalculatorServlet에 대한 서블릿 정의와 URL 매핑을 설정하는 부분을 주석처리

> 실행해보면 어노테이션을 쓰기 전과 같은 결과가 나온다.

<br/>

## 3.5.8. @WebServlet 어노테이션 주요 속성

### name

서블릿의 이름을 설정하는 속성이다. 기본값은 빈 문자열("")이다.

```java
@WebServlet(name="서블릿이름")
```

<br/>

### urlPatterns

서블릿의 URL 목록을 설정하는 속성이다. 속성값으로 String 배열이 온다. 기본값은 빈 배열이다.

```java
// 서블릿에 대해 한 개의 URL을 설정하는 경우
@WebServlet(urlPatterns="/calc")			// 일반적인 문자열로 표기 가능
@WebServlet(urlPatterns={"/calc"})		// 중괄호를 사용하여 배열 표기

// 서블릿에 대해 여러 개의 URL을 설정하는 경우
@WebServlet(urlPatterns={"/calc", "calc.do", "calculator.action"})
```

<br/>

### value

urlPatterns와 같은 용도이다. 어노테이션의 문법에서는 속성 이름이 'value'인 경우는 속성 이름 없이 값만 설정할 수 있다.

```java
@WebServlet(value="/calc")
@WebServlet("/calc")				// 속성명 'value' 생략 가능
```

만약 다음과 같이 value 속성 외에 다른 속성의 값도 함께 설정한다면 value 속성의 이름을 생략할 수 없다.

```java
@WebServlet(value="/calc", name="Calculator")		// OK
@WebServlet("/calc", name="Calculator")					// 속성명 'value'를 생략하면 오류!
```

<br/>

# 3.6. 정리

- 웹 서버와 웹 애플리케이션 사이에는 **데이터를 주고받기 위한 규칙이 있는데 이것을 'CGI(Common Gateway Interface)'** 라고 한다.
- 자바로 만든 웹 애플리케이션을 **서블릿** 이라고 부르는데, **'클라이언트에게 서비스를 제공하는 작은 단위의 서버 프로그램'** 이라는 뜻이다.
- 서블릿 컨테이너는 서블릿의 생성에서 실행, 소멸까지 서블릿의 생명주기를 관리하는 프로그램이다. 서블릿을 만들 때는 반드시 **Servlet 인터페이스를 구현해야만 한다.**
- Servlet이라는 규칙 외에 JSP를 만들고 실행하는 규칙, EJB(Enterprise JavaBeans)라는 분산 컴포넌트에 관한 규칙, 웹 서비스에 관한 규칙 등 기업용 애플리케이션에 제작에 필요한 기술들의 사양을 정의한 것을 **Java EE** 라고 한다.
- 서블릿 라이브러리는 서블릿을 좀 더 편리하게 개발할 수 있도록 GenericServlet이라는 추상 클래스를 제공한다. 따라서 **GenericServlet 클래스를 상속받으면** 좀 더 쉽고 편리하게 개발할 수 있다. 즉 **service() 구현에만 집중하면 되기 때문이다.**

