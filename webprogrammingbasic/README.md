# 02. 웹 프로그래밍의 기초 다지기

**HTTP(Hyper-Text Transfer Protocol)** : 웹 브라우저와 웹 서버 간의 통신 프로토콜

웹 애플리케이션은 웹을 기반으로 구동되기 때문에 웹 프로그래밍을 하려면 **HTTP 프로토콜을** 이해해야 합니다.

</br>

# 2.1. HTTP 프로토콜의 이해

**HTTP 프로토콜 이란?**

* 웹 브라우저와 웹 서버 사이의 **데이터 통신 규칙이다.**
* 웹 페이지의 링크를 클릭하면 웹 브라우저는 **HTTP 요청 형식에 따라 웹 서버에 데이터를 보낸다.**
* 웹 서버는 웹 브라우저가 보낸 데이터를 분석하여 요청 받은 일을 처리하여 응답한다.
* 즉, HTML 파일을 요청하면 해당 파일을 찾아서 보내주고, 또한, 이미지 파일을 요청하면 찾아서 보내준다.
* 이때 보내는 데이터는 **HTTP 응답 형식에 맞추어 보낸다.**

<br>

**웹 브라우저와 웹 서버 간의 통신 프로토콜 HTTP**

<img src="../capture/스크린샷 2019-08-14 오후 11.52.56.png">

<br>

웹 애플리케이션을 개발하다 보면 **SOAP(Simple Object Access Protocol)나 RESTful(REpresentational State Transfer)이라는** 용어를 만나게 되는데, 이것은 클라이언트와 서버 사이에 서비스를 요청하고 응답을 하는 방식을 말한다. 이 두 가지 모두 **HTTP 프로토콜을 응용하거나 확장한 기술이다.**

<br>

HTTP 프로토콜 응용 기술 몇 가지를 더 소개하자면 **WebDAV(World Wide Web Distributed Authoring and Versioning)를** 들 수 있다. 이는 **웹상에서 여러 사람이 문서나 파일을 더 쉽게 편집하고 다룰 수 있게** 협업을 도와주는 기술이다.

<br>

## 2.1.1. HTTP 모니터링

웹 브라우저와 웹 서버 사이에 주고받는 데이터를 들여다보려면 **HTTP 프록시 프로그램이** 필요합니다.

**프록시 서버(Proxy Server)** : 클라이언트와 서버 사이에서 통신을 중계해 주는 컴퓨터나 프로그램을 말합니다.

</br>

**프록시 서버의 용도**

* 빠른 정송을 위하여 서버의 응답 결과를 캐시에 저장해 두는 것이다.
* 외부로 전달되는 데이터를 검사하여 특정 단어가 포함된 자료의 송수신을 차단하거나 보안 팀에 경고 메시지를 보낼 수 있다

</br>

* **프록시 프로그램의 요청, 응답 데이터 가로채기**

<img src="../capture/스크린샷 2019-08-15 오전 12.04.15.png">

> 이렇게 웹 브라우저와 웹 서버의 중간에서 요청이나 응답 내용을 중계해 주기 때문에 둘 사이에서 주고받는 내용이 무엇인지 엿볼 수 있습니다.

<br>

## 2.1.2. HTTP 프록시 실행

**'Charles'** 라는 HTTP 모니터링 프로그램을 사용해보자.

<img src="../capture/스크린샷 2019-08-15 오전 12.12.15.png">

1. **Charles를 실행한다.**

2. **웹 브라우저를 실행하고, 통계청(http://kostat.go.kr/portal/korea/index.action) 사이트를 들어간다.**
3. **HTTP 프로토콜의 상세 내용을 확인한다.**

<img src="../capture/스크린샷 2019-08-15 오전 12.23.47.png">

</br>

## 2.1.3. HTTP 요청

4. **웹 브라우저가 웹 서버에게 요청하는 형식을 살펴보자.**

<img src="../capture/스크린샷 2019-08-15 오전 12.25.22.png">

</br>

### 서버에게 보낸 HTTP 요청 내용

* **http://kostat.go.kr/portal/korea/index.action 의 HTTP 요청 정보의 일부분**

  ```http
  GET /portal/korea/index.action HTTP/1.1
  Host: kostat.go.kr
  Upgrade-Insecure-Requests: 1
  User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36
  Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3
  Referer: http://kostat.go.kr/
  Accept-Encoding: gzip, deflate
  Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
  Cookie: WMONID=VgT5C69OAbZ; JSESSIONID=RUmUs6iRf0XPuI8J5ia6HNVcfPFQkOv4eq3kagDKjQTrtcE2ynEbp1JJSCR8wRiq.nsoweb2_servlet_portal; xloc=1920X1200; XTVID=A190811201939875608
  Connection: keep-alive
  ```

</br>

### 요청 라인(Request-Line)

: 요청메시지의 첫 라인은 메서드와 요청하는 자원, 프로토콜 버전으로 구성된다.

* **HTTP 요청의 Request-Line 형식**

  ```http
  GET /portal/korea/index.action HTTP/1.1
  ```

  * **GET** : 메서드, 요청하는 자원에 대해 웹 서버에게 내리는 명령
    * **메서드 종류** : GET, POST, HEAD, PUT, DELETE, TRACE, CONNTECT, OPTIONS
  * **공백**
  * **/portal/korea/index.action** : 요청 URI, 요청하는 자원의 식별자
    * 즉 HTML이나 이미지, 동영상, 애플리케이션 등이 있는 가상의 경로
  * **공백**
  * **HTTP/1.1** : HTTP 버전, 요청 정보가 어떤 버전에 맞추어 작성했는지 웹 서버에게 알려주기 위함이다.
  * **줄바꿈** : CRLF

<br>

### 요청 헤더

: 서버가 요청을 처리할 때 참고하라고 클라이언트에서 웹 서버에 알려주는 정보

* **HTTP 요청의 헤더 정보 형식**

  ```http
  User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36
  Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3
  Referer: http://kostat.go.kr/
  Accept-Encoding: gzip, deflate
  Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
  Cookie: WMONID=VgT5C69OAbZ; JSESSIONID=RUmUs6iRf0XPuI8J5ia6HNVcfPFQkOv4eq3kagDKjQTrtcE2ynEbp1JJSCR8wRiq.nsoweb2_servlet_portal; xloc=1920X1200; XTVID=A190811201939875608
  Connection: keep-alive
  ```

  * **User-Agent** : 헤더 이름
  * **Mozilla/5.0 (Macint…** : 헤더 값
  * **줄바꿈** : CRLF
  
* [HTTP 헤더 종류](http://www.ktword.co.kr/abbr_view.php?m_temp1=3790&id=902)

  * **일반 헤더(General-header)** : 요청이나 응답 모두에 적용할 수 있는 헤더
  * **응답 헤더(Response Header Fields)** : 응답을 적용할 수 있는 헤더
  * **요청 헤더(Request Header Fields)** : 요청을 적용할 수 있는 헤더
  * **엔티티 헤더(Entity-header)** : 보내거나 받는 본문 데이터를 설명

* 요청 헤더 중에서 클라이언트의 정보를 서버에게 알려주는 헤더인 **User-Agent를** 눈여겨볼 필요가 있다. 웹 서버는 이 헤더를 분석하여 요청자의 OS와 브라우저를 구분한다.

</br>

### 공백 라인과 요청 데이터(message-body)

HTTP 요청 내용 중에서 마지막 라인은 요청 헤더의 끝을 표시하는 공백 라인이다.

* **GET 요청** : 웹 브라우저의 주소창에 URL을 입력하거나 웹 페이지에서 링크를 클릭하는 경우
* **POST 요청** : 로그인이나 게시글을 등록하는 경우로서, 공백 라인 다음에 <u>서버에 보낼 데이터(message-body)가 온다.</u>

</br>

## 2.1.4. HTTP 응답

웹 브라우저가 요청하면 웹 서버는 그에 대한 작업을 수행한 후 응답 데이터를 보낸다. 웹서버가 응답한 내용을 살펴보자.

<img src="../capture/스크린샷 2019-08-15 오후 12.53.59.png">

### 서버로부터 받은 HTTP 응답 내용

```http
HTTP/1.1 200 OK
Date: Thu, 15 Aug 2019 03:51:31 GMT
Content-Type: text/html; charset=UTF-8
Content-Language: ko-KR
Set-Cookie: JSESSIONID=EeDYuKhelM01DsoTOdcRbiqCa3he9J8mqs1ACCwJzl33G1fayOtxjoa7Zq37Hpv6.nsoweb2_servlet_portal;Path=/
Transfer-Encoding: chunked
Proxy-Connection: keep-alive
```

</br>

### 상태 라인(Status-Line)

응답 메시지의 첫 라인은 응답 결과에 대한 정보이다.

* **HTTP 응답의 Status-Line 형식**

  ```http
  HTTP/1.1 200 OK
  ```

  * **HTTP/1.1** : HTTP 버전
  * **공백**
  * **200** : 상태 코드
  * **공백**
  * **OK** : 상태 설명
  * **엔터** : CRLF

* **HTTP 응답 상태 코드**

| 상태코드 | 상태설명                                                     |
| :------: | ------------------------------------------------------------ |
|   200    | 요청이 성공적으로 처리되었다.                                |
|   301    | 요청한 자원이 이동되었다. 헤더 정보에 이동 위치를 알려줄 테니 다시 요청해라. |
|   304    | 클라이언트가 임시 보관한 응답결과와 다르지 않다.             |
|   400    | 잘못된 요청이다.                                             |
|   404    | 요청한 자원을 못 찾았다. 서버 내부에서 오류가 발생하였다.    |
|   500    | 요청한 자원을 못 찾았다. 서버 내부에서 오류가 발생하였다.    |

</br>

### 응답 헤더

HTTP 응답 내용 중에서 상태 라인과 html 코드를 뺀 나머지 라인들은 응답 데이터를 처리할 때 참고하라고 웹 브라우저에게 알려 주는 정보이다.

* **Content-Type 헤더** : 서버가 웹 브라우저에게 보내는 데이터의 형식을 나타낸다.
* **Content-Length 헤더** : 웹 브라우저에게 보내는 데이터(message-body)의 크기이다.

</br>

### 공백 라인과 응답 데이터(message-body)

HTTP 응답 내용 중에서 공백 라인은 메시지 헤더와 응답 데이터를 구분하기 위한 공백 라인이다.

웹 브라우저와 웹 서버는 HTTP 형식에 맞추어 데이터를 보내고 받는다. HTTP 프로토콜을 포함하여 HTML, CSS, XML 등 웹과 관련된 표준 명세는 World Wide Web 컨소시엄에서 관리하고 있다. 자세한 것은 [www.w3.org](www.w3.org)

</br>

## 2.1.5. HTTP 클라이언트 만들기

* **예제) 웹에 데이터 요청, SimpleHttpClient.java**

```java
package web_programming_basic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class SimpeHttpClient {

  public static void main(String[] args) throws Exception {
    // 소켓 및 입출력 스트림 준비
    Socket socket = new Socket("www.kostat.go.kr", 80);
    BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    PrintStream out = new PrintStream(
        socket.getOutputStream());

    // 요청라인 출력
    out.println("GET / HTTP/1.1");

    // 헤더정보 출력
    out.println("Host: www.kostat.go.kr");
    out.println("User-Agent: Mocintosh; Intel Mac OS X 10_14_6)"
    + " AppleWebKit/537.36 (KHTML, like Gecko)"
    + " Chrome/75.0.3770.142 Safari/537.36");

    // 공백라인 출력
    out.println();

    // 응답 내용 출력
    String line = null;
    while ((line = in.readLine()) != null) {
      System.out.println(line);
    }

    in.close();
    out.close();
    socket.close();
  }

}
```

1. 웹 서버의 기본 포트번호는 80이므로 접속할 서버의 포트번호를 80으로 지정한다. 그리고 소켓으로 입출력을 하기 위한 객체 준비.
2. 먼저 서버에게 수행할 작업을 알려주는 요청라인을 보낸다. **요청 형식은 GET, 원하는 자원은 웹 서버 루트 폴더에 있는 기본문서(/), 사용할 프로토콜은 HTTP, 버전은 1.1이다.**
3. 웹 서버에 부가 정보를 보낸다. 요청자의 정보는 크롬 브라우저라고 설정한다. 그리고 Host, User-Agent 이렇게 두 가지 헤더만 보내도 정상적으로 응답한다.
4. 요청의 **끝을 표시하기 위해 공백 라인을 보낸다.**
5. 서버로부터 받은 데이터를 라인 단위로 읽어서 출력한다.

</br>

* **실행 결과**

  ```
  HTTP/1.1 200 OK
  Date: Tue, 13 Aug 2019 09:00:31 GMT
  ETag: "0-32f-5d52793e"
  Last-Modified: Tue, 13 Aug 2019 08:47:58 GMT
  Accept-Ranges: bytes
  Content-Length: 815
  Content-Type: text/html
  
  <!DOCTYPE html>
  <html lang="ko">
  <head>
  <script>
  	var mobileKeyWords = new Array('iPhone', 'iPod','iPad', 'BlackBerry', 'Android', 'Windows CE', 'LG', 'MOT', 'SAMSUNG', 'SonyEricsson');
  	var returnflag = false;
  	for (var word in mobileKeyWords) {
  		if (navigator.userAgent.match(mobileKeyWords[word]) != null) {
  			returnflag = true;
  			location.href = "http://kostat.go.kr/portal/korea/index.action";
  			//location.href = "http://m.kostat.go.kr/main/main.action?bmode=main&parm=direc"; 19.04.01반응형웹으로 변경되면서 m.kostat.go.kr은 사용중지
  			break;
  		}
  	} 
  
  	if(returnflag == false) {
  		location.href = "http://kostat.go.kr/portal/korea/index.action";
  	}
  
  </script>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"  />
  <title>통계청</title>
  </head>
  <body>
  </body>
  </html>
  ```

</br>

### 널리 알려진 프로토콜

* **FTP(File Transfer Protocol)** : 클라이언트와 서버 간에 파일을 주고받기 위해 만든 통신 규약이다.
* **Telnet 프로토콜** : 인터넷이나 LAN 상에서 문자 기반으로 원격의 컴퓨터를 제어하기 위해 만든 통신 규약이다. 요즘은 보안 때문에 SSH(Secure Shell) 프로토콜 기반 원격 접속 프로그램을 주료 사용한다.
* **XMPP(Extensible Messaging and Presence Protocol)** : 인스턴스 메시지 및 사용자의 접속 상태 정보를 교환할 목적으로 만든 통신 규약이다.
* **SMTP(Siple Mail Transfer Protocol)** : 인터넷 상에서 메일을 보내기 위한 통신 규약이다.
* **IMAP(Internet Message Access Protocol)** : 이메일을 가져온 뒤에 서버의 메일을 지우지 않으며 요즘처럼 여러 대의 장비에서 이메일을 조회하는 경우에 적합하다.
* **LDAP(Lightweight Directory Access Protocol)** : 디렉터리 서비스에 등록된 자원들을 찾는 통신 규약이다.
* **IRC(Internet Relay Chat)** : 실시간 채팅을 위해 만든 통신 규약이다.

</br>

# 2.2. GET 요청

GET 요청 시 서버에 데이터를 전달해보자.

* **GET 요청의 특징**
  * URL에 데이터를 포함 => 데이터 조합에 적합
  * 바이너리 및 대용량 데이터 전송 불가
  * 요청라인과 헤드 필드의 최대 크기
    * HTTP 사양에는 제한사항 없음
    * 대용량 URL로 인한 문제 발생 => 웹 서버에 따라 최대 크기 제한

</br>

## 2.2.1. GET 요청 1 - 웹 브라우저 주소창에 URL을 입력하는 경우

웹 브라우저 주소창에 URL을 입력하여 서버의 자원을 요청하는 경우 GET 요청이 발생한다.

**URL : http://localhost:8080/GetTest.html**

<img src="../capture/스크린샷 2019-08-15 오후 7.53.11.png" width=500>

</br>

## 2.2.2. GET 요청 2 - 링크를 클릭하는 경우

링크를 클릭할 때에도 GET 요청이 발생한다. 브라우저 화면에서 '구글'이나 '페이스북' 클릭

* **GetTest.html 일부 소스**

  ```html
  <h3>GET 요청 2: 링크를 클릭할 때</h3>
  <p>
      <a href="http://www.google.com">구글</a><br>
      <a href="http://www.facebook.com">페이스북</a><br>
  </p>
  ```

  * **\<a> 태그** : GET 요청을 만든다. 링크를 클릭할 때 GET으로 요청한다는 것을 확인할 수 있다.

</br>

## 2.2.3. GET 요청 3 - 입력 폼의 method 속성값이 get인 경우

입력 폼의 method 속성값이 'get' 인 경우, 서버에 GET 요청을 보낸다.

* **GET 요청 3 - 입력 폼 화면**

  <img src="../capture/스크린샷 2019-08-15 오후 7.59.29.png"  width=500>

* **GetTest.html 일부 소스**

  ```html
  <h3>GET 요청 3: 입력폼의 method 속성을 GET으로 지정했을 때</h3>
  <form action="CalculatorServlet" method="get">
      <input type="text" name="v1" size="4">
      <select name="op">
          <option value="+">+</option>
          <option value="-">-</option>
          <option value="*">*</option>
          <option value="/">/</option>
      </select>
      <input type="text" name="v2" size="4">
      <input type="submit" value="="><br>
  </form>
  ```

  * **\<form> 태그** : method 속성값을 'get' 으로 설정하면 된다. method 속성의 기본값이 'get'이기 때문에 생략해도 된다.

</br>

## 2.2.4. GET 요청의 데이터 전달 형식

GET으로 요청하는 경우 서버에 보낼 데이터는 URI에 붙인다.

* **예시**

  ```http
  GET /web02/CalculatorServlet?v1=20&op=%2B&v2=30 HTTP/1.1
  ```

  * **GET**
  * **/web02/CalculatorServlet** : 서비스주소
  * **?** : 서비스와 데이터를 분리
  * **v1** : 매개변수명
  * **=** 
  * **20** : 값
  * **&** : 데이터 구분자
  * **op, v2** : 매개변수명

</br>

## 2.2.5. GET 요청의 쓰임새

GET 요청은 자료를 검색한다거나, 게시글의 상세 정보를 본다거나, 특정 상품의 정보를 조회하는 것과 같이 데이터를 조회하는 경우에 적합하다.

<img src="../capture/스크린샷 2019-08-15 오후 8.16.32.png" width=500>

</br>

## 2.2.6. 문제점과 개선방안

### 보안에 좋지 않다!

GET 요청의 경우 웹 브라우저의 주소창에 사용자가 입력한 정보가 그대로 노출되기 때문에 보안 문제가 발생할 수 있다. 따라서 **'로그인' 이나 '개인정보 관리'와 같은 보안을 요구하는 경우에는 'GET' 요청을 사용하지 않도록 한다.**

</br>

### 바이너리 데이터를 전송할 수 없다!

GET 요청으로는 이미지나 동영상과 같은 바이너리 파일의 데이터는 URL에 붙여서 보낼 수 없다.

</br>

# 2.3. POST 요청

* **Post 요청의 특징**
  * URL에 데이터가 포함되지 않음 => <u>외부 노출 방지</u>
  * 메시지 본문에 데이터 포함 => <u>실행 결과 공유 불가</u>
  * <u>바이너리 및 대용량 데이터 전송 가능</u>

</br>

## 2.3.1. POST 요청의 장점 - 입력값을 URL에 노출하지 않는다.

* 웹 브라우저 주소창에 'http://localhost:8080/PostTest.html' 을 입력한다.

<img src="../capture/스크린샷 2019-08-15 오후 8.28.14.png" width=500>

* **PostTest.html의 일부분**

  ```html
  <h3>POST 요청 1 - 로그인 폼</h3>
  <p>
      로그인 폼은 입력값 노출을 방지하기 위해 POST를 사용해야 한다.
  </p>
  <form action="LoginServlet" method="post">
      아이디: <input type="text" name="id"><br>
      암호: <input type="password" name="password"><br>
      <input type="submit" value="로그인">
  </form>
  ```

  * **\<form> 태그** : 입력 폼의 값을 POST 방식으로 전달하려면 method 속성을 'post'로 지정해야 한다.

</br>

* '로그인 폼' 에서 아이디와 암호를 입력한 후 \<로그인> 버튼을 클릭한다.

  <img src="../capture/스크린샷 2019-08-15 오후 8.31.18.png" width=500>

  </br>

  <img src="../capture/스크린샷 2019-08-15 오후 8.31.44.png" width=500>

  * POST 방식으로 요청을 보내게 되면, 사용자가 입력한 값은 **URL에 포함되지 않기 때문에 외부에 노출되지 않는다.**

</br>

## 2.3.2. POST 요청의 단점 - 요청 결과를 공유할 수 없다.

POST 요청 방식에서는 요청 결과를 공유할 수 없다.

* **계산기 입력 폼에서 POST 요청을 한 결과**

  <img src="../capture/스크린샷 2019-08-15 오후 8.37.45.png" width=500>

  * POST 요청은 데이터를 메시지 본문에 붙여 보내기 때문에, URL을 보면 사용자가 입력한 값이 보이지 않는다.
  * 데이터를 포함하지 않은 URL은 동일한 결과를 얻을 수 없다.

</br>

## 2.3.3. 문제점과 개선방안

GET 메서드와 마찬가지로 POST도 데이터를 전달할 때 "이름=값&이름=값" 형태를 사용한다.

문자 데이터를 보낼 때는 문제 없지만, **이미지나 동영상과 같은 바이너리 데이터를 보낼 때는 문제가 발생할 수 있다.**

서버는 '&' 문자로 매개변수를 구분하고, '=' 문자로 매개변수의 이름과 값을 분리하기 때문이다.

따라서 바이너리 데이터를 보낼 때는 **특별한 형식으로 작성하여 보낸다.**

</br>

# 2.4. 파일 업로드

웹 서버에 바이너리 데이터를 보내도록 고안된 멀티파트 인코딩 방법을 확인해보자. 또한, 이를 위해 **HTML의 \<FORM> 태그를 어떻게 설정하는지 알아보자.**

</br>

## 2.4.1. 파일 업로드를 위한 \<form> 태그 설정

웹 브라우저 주소창에 'http://localhost:8080/MultipartTest.html' 을 입력한다.

* **파일 업로드 입력 폼**

  <img src="../capture/스크린샷 2019-08-15 오후 8.46.31.png" width=500>

* **MultipartTest.html 일부분**

  ```html
  <form action="FileUploadServlet" method="post"
        enctype="multipart/form-data">
      사진: <input type="file" name="photo"><br>
      설명: <textarea name="description" cols="50" rows="3"></textarea><br>
      <input type="submit" value="추가"><br>
  </form>
  ```

  * **\<from> 태그** : enctype 속성을 'multipart/form-data' 로 지정하였다. enctype 속성의 기본값은 'application/x-www-form-urlencoded' 이다. 
  * **\<input> 태그** : 파일을 입력하기 위해서 type 값을 'file' 로 지정하였다.

<br>

입력 폼에서 \<파일 선택> 버튼을 클릭하여 이미지를 선택하고 추가 버튼을 클릭.

<img src="../capture/스크린샷 2019-08-15 오후 9.06.53.png" width=500>

</br>

## 2.4.2. 멀티파트 방식의 POST 요청 프로토콜 분석

### 일반 전송 방식의 Content-Type 헤더와 데이터 형식

POST 요청에서 일반 전송과 멀티파트 전송의 가장 큰 차이점은 Content-Type 헤더와 메시지 본문의 형식이다.

</br>

* **일반 전송 방식의 Content-Type 헤더**

  ```
  Content-Type: application/x-www-form-urlencoded
  ```

* **멀티 파트 전송 방식의 Content-Type 헤더**

  ```
  Content-Type: multipart/form-data; boundary=----Web ... Pyz
  ```

  * **Content-Type** : 엔티티 헤더
  * **multipart/form-data** : 미디어 타입
  * **boundary=---Web ... PyZ** : 파트 구분자

</br>

### 멀티 파트 전송의 데이터 형식

데이터에 파일을 첨부할 때는 매개변수를 정확히 구분하기 위해 특별한 구분자를 사용한다. 

Content-Type 헤더의 boundary 값이 바로 각각의 **매개변수 값을 분리할 때 사용할 구분자를 정의한 것이다.**

이 구분자는 웹 브라우저에서 입의로 생성한다.

웹 서버는 이 구분자를 사용하여 메시지 본문에서 매개변수를 분리하고 해석한다.

</br>

* **멀티 파트 데이터의 매개변수 정보**

  ```
  ... name="photo"; filename="DSC00863.JPG"
  ```

  * **name="photo"** : 매개변수 이름
  * **filename="DSC00863.JPG"** : 첨부 파일 이름

</br>

이렇게 멀티 파트 형식으로 데이터를 보낼 때는 **웹 서버에서도 그에 맞추어 데이터를 분리하고 해석해야 한다.**

</br>

# 2.5. 정리

* HTTP 프로토콜에서 가장 많이 사용되는 요청 형식은 **GET 과 POST** 이다.
* **GET** : 브라우저의 주소창에 직접 URL을 입력하거나 사용자가 링크를 클릭하는 경우에 발생
  * 데이터가 주소창에 그대로 노출되므로 보안이 취약
* **POST** : 웹 서버에 데이터를 보낼 때 메시지 본문 부분에 붙여서 보내기 때문에 주소창에 노출될 위험이 없다. 또한, 보내는 데이터의 크기에 제한이 없다.
* 웹 서버에 파일을 첨부하여 보낼 경우에는 멀티 파트 인코딩이라는 아주 특별한 형식을 사용해야 한다.