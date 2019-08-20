# 3. 서블릿 프로그래밍

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
    System.out.println("init() 호출됨");
    this.config = servletConfig;
  }

  @Override
  public ServletConfig getServletConfig() {
    System.out.println("getServletConfig() 호출됨");
    return this.config;
  }

  @Override
  public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    System.out.println("service() 호출됨");
  }

  @Override
  public String getServletInfo() {
    System.out.println("getServletInfo() 호출됨");
    return "version=1.0;author=sangmin;";
  }

  @Override
  public void destroy() {
    System.out.println("destroy() 호출됨");
  }
}
```

<br/>

## 3.4.2. javax.servlet.Servlet 인터페이스

서블릿 클래스는 반드시 javax.servlet.Servlet 인터페이스를 구현해야 합니다. 서블릿 컨테이너가 서블릿에 대해 호출할 메서드를 정의한 것이 Servlet 인터페이스입니다.

![스크린샷 2019-08-21 오전 12.29.08](../capture/스크린샷 2019-08-21 오전 12.29.08.png)

