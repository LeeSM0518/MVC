# 04. 서블릿과 JDBC

# 4.1. 데이터베이스에서 데이터 가져오기

**서블릿이** 하는 주된 일은 클라이언트가 요청한 데이터를 다루는 일이다.

**데이터베이스는** 개발자들이 쉽게 데이터를 저장하고 꺼낼 수 있도록 도와주는 프로그램이다.

<br/>

데이터베이스를 사용하려면 두 가지가 필요하다.

1. 데이터베이스에 요청을 전달하고 결과를 받을 때 사용할 도구. **(JDBC)**
2. 데이터베이스에 명령을 내릴 때 사용할 언어.  **(SQL)**

<br/>

* **서블릿과 JDBC**

  <img src="../capture/스크린샷 2019-09-02 오후 10.54.36.png">

<br/>

## 4.1.1. 회원 목록 조회 구현

### 회원 테이블 생성

회원 데이터를 입력하고 조회할 테이블을 생성한다.

* 기본 키 칼럼의 값은 한 번 설정되면 변경할 수 없기 때문에 **'일련번호' 칼럼을 기본 키로 사용한다.**
* **이메일 값은 중복되지 않도록 유니크(Unique)로 설정한다.**
* **'마지막암호변경일'** 칼럼은 암호를 변경할 때 날짜를 저장해 두었다가 일정 시간이 지나면 다시 암호를 변경하도록 요구하기 위한 칼럼이다.

<br/>

### 회원 기본 정보 테이블을 생성한다.

```sql
create table members (
  mno SERIAL,
  email varchar(40) unique not null,
  pwd varchar(100) not null,
  mname varchar(50) not null,
  cre_date timestamp not null,
  mod_date timestamp not null,
  constraint pk_members primary key (mno)
);
```

<br/>

### 테스트 데이터 준비

```sql
insert into members (email, pwd, mname, cre_date, mod_date) values
('s1@test.com', '1111', '홍길동', now(), now()),
('s2@test.com', '1111', '임꺽정', now(), now()),
('s3@test.com', '1111', '일지매', now(), now()),
('s4@test.com', '1111', '이몽룡', now(), now()),
('s5@test.com', '1111', '성춘향', now(), now());
```

<br/>

### 출력 확인

```sql
select * from members;

 mno |    email    | pwd  | mname  |          cre_date          |          mod_date          
-----+-------------+------+--------+----------------------------+--------------------------
   1 | s1@test.com | 1111 | 홍길동 | 2019-09-02 14:30:40.045191 | 2019-09-02 14:30:40.045191
   2 | s2@test.com | 1111 | 임꺽정 | 2019-09-02 14:30:40.045191 | 2019-09-02 14:30:40.045191
   3 | s3@test.com | 1111 | 일지매 | 2019-09-02 14:30:40.045191 | 2019-09-02 14:30:40.045191
   4 | s4@test.com | 1111 | 이몽룡 | 2019-09-02 14:30:40.045191 | 2019-09-02 14:30:40.045191
   5 | s5@test.com | 1111 | 성춘향 | 2019-09-02 14:30:40.045191 | 2019-09-02 14:30:40.045191
(5 rows)
```

<br/>

### '회원 목록 조회' 서블릿 만들기

* **src/spms/servlets/MemberListServlet.java**

  ```java
  package spms.servlets;
  
  import javax.servlet.GenericServlet;
  import javax.servlet.ServletException;
  import javax.servlet.ServletRequest;
  import javax.servlet.ServletResponse;
  import javax.servlet.annotation.WebServlet;
  import java.io.IOException;
  import java.io.PrintWriter;
  import java.sql.Connection;
  import java.sql.DriverManager;
  import java.sql.ResultSet;
  import java.sql.Statement;
  
  // 서블릿을 만들고자 서블릿 어노테이션을 쓰고
  // GenericServlet 을 상속받는다.
  @WebServlet("/member/list")
  public class MemberListServlet extends GenericServlet {
  
    String url = "jdbc:postgresql://arjuna.db.elephantsql.com:5432/";
    String username = "fsmfppcj";
    String password = "****";
  
    static {
      try {
        Class.forName("org.postgresql.Driver");
      } catch (ClassNotFoundException e) {
        System.err.println("PostgreSQL DataSource unable to load PostgreSQL JDBC Driver");
      }
    }
  
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
      String query = "select mno, mname, email, cre_date" +
          " from members" +
          " order by mno";
  
      // 데이터 베이스 연결 및 쿼리 실행
      try (Connection connection = DriverManager.getConnection(url, username, password);
           Statement statement = connection.createStatement();
           ResultSet resultSet = statement.executeQuery(query)) {
        servletResponse.setContentType("text/html; charset=UTF-8");
  
        PrintWriter out = servletResponse.getWriter();
        out.println("<html><head><title>회원목록</title></head>");
        out.println("<body><h1>회원목록</h1>");
  
        while (resultSet.next()) {
          out.println(
              resultSet.getInt("mno") + " , " +
                  resultSet.getString("mname") + " , " +
                  resultSet.getString("email") + " , " +
                  resultSet.getDate("cre_date") + "<br>");
        }
  
        out.println("</body></html>");
  
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }
  }
  ```

  <br/>

* **java.sql.Driver 인터페이스의 구현체**
  * **getMajorVersion(), getMinorVersion()** : DriverManager에게 JDBC 드라이버의 버전 정보를 제공한다.
  * **acceptsURL()** : JDBC URL이 이 드라이버에서 사용 가능한지 알려준다.
  * **connect()** : 데이터베이스에 연결을 수행한다.
  * **getConnection()** : 첫 번째 인자값은 DB 서버의 접속 정보이고, 두 번째와 세 번째 인자값은 데이터베이스의 사용자 아이디와 암호 입니다. 데이터베이스 연결에 성공하면 DB 접속 정보를 다루는 객체를 반환한다.

<br/>

* **java.sql.Connection 인터페이스의 구현체**
  * **createStatement(), prepareStatement(), prepareCall()** : SQL 문을 실행하는 객체를 반환한다.
  * **commit(), rollback()** : 트랜잭션 처리를 수행하는 메서드이다.

<br/>

* **java.sql.Statement 인터페이스의 구현체**
  * **executeQuery()** : 결과가 만들어지는 SQL 문을 실행할 때 사용한다. 보통 SELECT 문을 실행한다.
  * **executeUpdate()** : DML과 DDL 관련 SQL 문을 실행할 때 사용한다. DML에는 INSERT, UPDATE, DELETE 명령문이 있고, DDL에는 CREATE, ALTER, DROP 명령문이 있다.
  * **execute()** : SELECT, DML, DDL 명령문 모두에 사용 가능하다.
  * **executeBatch()** : addBatch()로 등록한 여러 개의 SQL 문을 한꺼번에 실행할 때 사용한다.

<br/>

* **java.sql.ResultSet 인터페이스의 구현체**
  * **first()** : 서버에서 첫 번째 레코드를 가져온다.
  * **last()** : 서버에서 마지막 번째 레코드를 가져온다.
  * **previous()** : 서버에서 이전 레코드를 가져온다.
  * **next()** : 서버에서 다음 레코드를 가져온다.
  * **getXXX()** : 레코드에서 특정 칼럼의 값을 꺼내며 XXX는 칼럼의 타입에 따라 다른 이름을 갖는다.
  * **updateXXX()** : 레코드에서 특정 컬럼의 값을 변경한다.
  * **deleteRow()** : 현재 레코드를 지운다.

<br/>

## 4.1.2. 서블릿 배치 정보 설정

**@WebServlet 어노테이션으로** 서블릿의 배치 정보를 설정하거나 MemberListServlet의 URL 패턴 값은 **'/member/list'로** 설정한다.

* **src/spms/servlets/MemberListServlet.java**

  ```java
  @WebServlet("/member/list")
  public class MemberListServlet extends GenericServlet { ...
  ```

* **web/WEB-INF/web.xml**

  ```xml
  ...
  <!-- 서블릿 선언 -->
  <servlet>
    <servlet-name>MemberList</servlet-name>
    <servlet-class>spms.servlets.MemberListServlet</servlet-class>
  </servlet>
  
  <!-- 서블릿을 URL 과 연결-->
  <servlet-mapping>
    <servlet-name>MemberList</servlet-name>
    <url-pattern>/member/list</url-pattern>
  </servlet-mapping>
  ...
  ```

<br/>

## 4.1.3. '회원 목록 조회' 서블릿 테스트

* **jdbc driver를 web/WEB-INF/lib 에 넣어야 한다.**

<img src="../capture/스크린샷 2019-09-03 오후 3.56.34.png">

* **실행 결과**

<img src="../capture/스크린샷 2019-09-03 오후 3.58.10.png" width=500>

<br/>

# 4.2. HttpServlet으로 GET 요청 다루기

지금까지 서블릿 클래스를 만들 때 service() 메서드를 정의하였는데, **HttpServlet 클래스를 상속받게 되면** service() 대신 **doGet() 이나 doPost() 를 정의한다.**

* **MemberListServlet.java 에 '신규 회원' 링크를 추가한다.**

  ```java
  out.println("<html><head><title>회원목록</title></head>");
  out.println("<body><h1>회원목록</h1>");
  out.println("<p><a href='add'>신규 회원</a></p>");
  ...
  ```

  * **\<a href='add'>** : \<a> 태그의 링크 URL은 'add' 이다. URL이 '/' 으로 시작하면 절대 경로이고 '/' 으로 시작하지 않으면 상대 경로이다.

<br/>

### 절대 경로 URL

* **서버 루트** : http://localhost:9999
* **최종 경로** : http://localhost:9999/web04/member/add
* **절대 경로** : /web04/member/add

<br/>

### 상대 경로 URL

* **현재 경로** : https://localhost:9999/member/list
* **최종 경로** : https://localhost:9999/member/add
* **상대 경로** : add

<br/>

### 회원 정보 입력폼 만들기

* **src/spms/servlets/MemberAddServlet.java**

  ```java
  package spms.servlets;
  
  import javax.servlet.ServletException;
  import javax.servlet.annotation.WebServlet;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  import java.io.PrintWriter;
  
  // GenericServlet 클래스 대신 HttpServlet 클래스를 상속
  @WebServlet("/member/add")
  public class MemberAddServlet extends HttpServlet {
  
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
  
  }
  ```

  * HttpServlet 클래스는 GenericServlet 클래스의 하위 클래스이다. 따라서 **HttpServlet을 상속받으면 GenericServlet 클래스를 상속받는 것과 마찬가지로 javax.servlet.Servlet 인터페이스를 구현한 것이 된다.**

<br>

* **HttpServlet의 service() 호출**

  클라이언트 요청이 들어오면, 첫째로 상속받은 HttpServlet의 service() 메서드가 호출되고, 둘째로 service()는 클라이언트 요청 방식에 따라 **doGet() 이나 doPost(), doPut()** 등의 메서드를 호출한다.

<br>

* **GET 요청이 발생하는 경우**
  * 웹 브라우저 주소창에 URL을 입력한 후 엔터를 누를 때
  * a 태그로 만들어진 링크를 누를 때
  * Form의 method 속성값이 get이거나 method 속성이 생략된 경우(method의 기본값은 get이다.)

<br>

### 회원 정보 입력폼 실행

<img src="../capture/스크린샷 2019-09-03 오후 5.56.51.png" width=500>

<br>

### 회원 정보 입력폼의 HTML 소스 분석

```html
<html>
  <head>
    <title>회원 등록</title>
    <body>
      <h1>
        회원 등록
      </h1>
      <form action='add' method='post'>
        이름: <input type='text' name='name'><br>
        이메일: <input type='text' name='email'><br>
        암호: <input type='password' name='password'><br>
        <input type='submit' value='추가'>
        <input type='reset' value='취소'>
      </form>
    </body>
  </head>
</html>
```

* **\<form action='add' method='post'>** 
  * **action 속성은 실행할 서블릿의 URL 주소이다.** 상대 경로('add')를 사용했기 때문에 실제 URL은 'http://localhost:9999/member/add' 이다.
  * **method** 속성은 서버에 요청하는 방식을 지정한다. 기본값은 'get'이다. 예제는 post로 지정되어 있다. 따라서 입력폼의 데이터를 보낼 때 **post** 방식으로 보낸다.
  * 문자 대신 '*' 문자가 나타나도록 하기 위해서는 input 태그의 type 속성을 'password' 라고 설정해야 한다.
  * **'submit'** 타입의 \<input> 태그를 사용하면 서버에 요청을 보내는 버튼을 만들 수 있다. 
  * **'reset'** 타입의 \<input> 태그는 입력폼을 초기화시키는 버튼을 생성한다.

<br>

### 회원 등록 입력폼의 POST 요청

톰캣 서버가 클라이언트로부터 /member/add 요청을 받으면, MemberAddServlet의 service() 메서드를 호출합니다. 

service() 에서는 요청 방식에 따라 **GET 요청이면 doGet()을 호출하고, POST 요청이면 doPost()를 호출합니다.**

<br>

# 4.3. HttpServlet으로 POST 요청 다루기

### 4.3.1. doPost() 오버라이딩

* **src/spms/servlets/MemberAddServlet.java**

  ```java
  package spms.servlets;
  
  import javax.servlet.ServletException;
  import javax.servlet.annotation.WebServlet;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  import java.io.PrintWriter;
  import java.rmi.ServerException;
  import java.sql.Connection;
  import java.sql.DriverManager;
  import java.sql.PreparedStatement;
  
  @WebServlet("/member/add")
  public class MemberAddServlet extends HttpServlet {
  
    private String url = "jdbc:postgresql://arjuna.db.elephantsql.com:5432/";
    private String username = "fsmfppcj";
    private String password = "opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH";
  
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
  
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String query = "insert into members (email, pwd, mname, cre_date, mod_date) values" +
          " (?, ?, ?, NOW(), NOW())";
  
      try (Connection connection = DriverManager.getConnection(url, username, password);
           PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        // 입력 매개변수의 값 설정
        preparedStatement.setString(1, req.getParameter("email"));
        preparedStatement.setString(2, req.getParameter("password"));
        preparedStatement.setString(3, req.getParameter("name"));
        preparedStatement.executeUpdate();
  
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><head><title>회원등록결과</title></head>");
        out.println("<body>");
        out.println("<p>등록 성공입니다!</p>");
        out.println("</body></html>");
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }
  }
  ```

<br>

## 4.3.2. 회원 등록 테스트

* **회원 등록**

<img src="../capture/스크린샷 2019-09-03 오후 8.39.11.png" width=500>

* **회원 추가**

<img src="../capture/스크린샷 2019-09-03 오후 9.11.16.png" width=500>

* **회원 목록**

<img src="../capture/스크린샷 2019-09-03 오후 9.11.51.png" width=500>

<br>

### Statement vs. PreparedStatement

| 비교 항목            | Statement                                                    | PreparedStatement                                            |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 실행 속도            | 질의할 때마다 SQL 문을 컴파일한다.                           | SQL 문을 미리 준비하여 컴파일해 둔다. 입력 매개변수 값만 추가하여 서버에 전송한다. 여러 번 반복하여 질의하는 경우, 실행속도가 빠름. |
| 바이너리 데이터 전송 | 불가능                                                       | 가능                                                         |
| 프로그래밍 편의성    | SQL 문 안에 입력 매개변수 값이 포함되어 있어서 SQL 문이 복잡하고 매개 변수가 여러 개인 경우 코드 관리가 힘들다. | SQL 문과 입력 매개변수가 분리되어 있어서 코드 작성이 편리하다. |

<br>

# 4.4. 요청 매개변수의 한글 깨짐 처리

## 4.4.1. 한글 깨짐 현상

1. 회원 등록 입력폼의 이름 입력란에 한글을 입력하고 \<추가> 버튼을 클릭한다.

<img src="../capture/스크린샷 2019-09-03 오후 9.16.49.png" width=500>

2. 회원 목록 확인

<img src="../capture/스크린샷 2019-09-03 오후 9.17.29.png" width=500>

- 한글로 된 이름이 깨진 것을 확인할 수 있다.

<br>

## 4.4.2. 한글 입력값이 깨진 이유

- 웹 브라우저의 기본 문자집합을 확인한다.
  - 현재 웹 페이지의 기본 문자 집합이 유니코드(UTF-8)로 설정된 것을 확인할 수 있다.

<br>

### 웹 브라우저가 웹 서버에 데이터를 보낼 때 문자 형식

웹 브라우저가 웹 서버로 데이터를 보낼 때는 웹 페이지의 기본 문자집합으로 인코딩하여 보내기 때문에 사용자가 입력한 값은 **UTF-8로 인코딩되어 서버에 전달됩니다.**

<br>

### 서블릿에서 데이터를 꺼낼 때 문자 형식

서블릿에서 **getParameter()를 호출하면** 이 메서드는 기본적으로 매개변수의 값이 ISO-8859-1 로 인코딩되었다고 가정하고 **각 바이트를 유니코드로 바꾸고 나서 반환한다.** 즉, 클라이언트가 보낸 문자를 영어라고 간주하고 유니코드로 바꾼다.

서블릿이 웹 브라우저로부터 받은 한글 데이터는 UTF-8로 인코딩된 값이다. UTF-8은 한글자를 3바이트로 표현한다. 그래서 **각각의 의미 없는 바이트들을 유니코드로 바꿨기 때문에 한글이 깨진 것이다.**

<br>

## 4.4.3. 한글 깨짐 해결책

한글이 깨지는 것을 해결하려면 getParameter()를 호출하기 전에 클라이언트가 보낸 **매개변수의 값이 어떤 인코딩으로 되어 있는지 지정해야 한다.**

- **src/spms.servlets/MemberAddServlet.java**

  ```java
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String query = "insert into members (email, pwd, mname, cre_date, mod_date) values" +
      " (?, ?, ?, NOW(), NOW())";
    ...
  ```

  - HttpServletRequest의 **setCharacterEncoding()은 매개변수 값의 인코딩 형식을 지정하는 메서드이다.**
  - 단, 이 메서드가 호출되기 전에 getParameter()가 먼저 호출된다면 아무 소용이 없다.

<br>

- **실행 결과**

  <img src="../capture/스크린샷 2019-09-03 오후 9.57.11.png" width=500>

<br>

## 4.4.4. GET 요청 매개변수의 한글 깨짐 해결책

- **URL 구성 요소의 명칭**

  ```
  http://localhost:9999/member/update?no=20
  ```

  - `localhost` : 서버주소
  - `9999` : 포트번호
  - `/member/update` : 요청경로
  - `no=20` : 쿼리스트링

<br>

GET 요청에서는 매개변수의 값을 URL에 포함하여 보내는데 setCharacterEncoding()은 이런 쿼리스트링에 대해서는 적용되지 않기 때문에 여전히 한글이 깨진다. 이런 경우에는 톰캣 서버의 **server.xml** 에서 다음 코드를 **URIEncoding="UTF-8"을** 추가하면 해결됩니다.

- **server.xml 수정하기**

  <img src="../capture/스크린샷 2019-09-03 오후 10.24.53.png" width=500>

  - server.xml 들어가서

    ```xml
    ...
    <Connector connectionTimeout="20000" port="9999"
               protocol="HTTP/1.1" redirectPort="8443"
               URIEncoding="UTF-8"/>
    ...
    ```

    > 위와 같은 위치에 URIEncoding="UTF-8" 을 추가한다.

<br>

# 4.5. 리프래시

일정 시간이 지나고 나서 자동으로 서버에 요청을 보내는 방법을 알아보자. 이것을 **'리프래시(Refresh)' 즉, 새로고침이라고 한다.**

<br>

## 4.5.1. 자동으로 회원 목록을 출력하기

<img src="../capture/스크린샷 2019-09-03 오후 10.36.55.png">

(5) 웹 브라우저는 회원 등록 결과를 출력하고, 1초 후에 서버에 '회원 목록'을 요청한다.

(6) MemberListServlet은 회원 목록을 클라이언트로 보낸다.

<br>

### 응답 헤더를 이용한 리프래시

- **src/spms/servlets/MemberAddServlet.java**

  ```java
  ...
  out.println("<p>등록 성공입니다!</p>");
  out.println("</body></html>");
  
  // 리프래시 정보를 응답 헤더에 추가
  resp.addHeader("Refresh", "1;url=list");
  ...
  ```

  - HttpServletResponse의 **addHeader()는 HTTP 응답 정보에 헤더를 추가하는 메서드이다.**
  - **'Refresh' 헤더의 값을** 살펴보면 숫자 **'1'은 응답 본문을 출력하고 나서 1초 뒤에 다시 서비스를 요청하라는 뜻이다.**
  - 이때 **url은 다시 요청할 서비스 주소이다.** URL이 '/' 로 시작하지 않기 때문에 상대 경로로 계산된다.

<br>

### HTML의 meta 태그를 이용한 리프래시

리프래시 정보는 HTML 본문에 포함시켜 보낼 수 있다.

- **src/spms/servlets/MemberAddServlet.java**

  ```java
  ...
  resp.setContentType("text/html; charset=UTF-8");
  PrintWriter out = resp.getWriter();
  out.println("<html><head><title>회원등록결과</title>");
  out.println("<meta http-equiv='Refresh' content='1; url=list'>");
  out.println("<body>");
  out.println("</head>");
  out.println("<p>등록 성공입니다!</p>");
  out.println("</body></html>");
  
  // 리프래시 정보를 응답 헤더에 추가
  // resp.addHeader("Refresh", "1;url=list");
  ...
  ```

  - \<meta> 태그는 반드시 \<head> 태그 안에 선언해야 한다. \<body> 태그 안에 선언해서는 안된다.
  - 작업 결과를 출력한 후 다른 페이지로 이동할 때는 **리프래시** 사용
  - 작업 결과를 출력하지 않고 다른 페이지로 이동할 때는 **'리다이렉트'** 로 처리해야 한다.

<br>

# 4.6. 리다이렉트

회원 정보를 등록하고 나서 그 결과를 출력하지 않고 즉시 회원 목록 화면으로 이동하게 하는 방벙을 배워보자. 이러한 기법을 **리다이렉트(Redirect)** 라고 한다.

<br>

## 4.6.1. 리다이렉트 실습하기

- **리다이렉트 실습 시나리오**

<img src="../capture/스크린샷 2019-09-03 오후 10.53.26.png">

<br>

### 리다이렉트 메서드 sendRedirect()

- **src/spms/servlets/MemberAddServlet.java**

  ```java
  ...
  preparedStatement.setString(2, req.getParameter("password"));
  preparedStatement.setString(3, req.getParameter("name"));
  preparedStatement.executeUpdate();
  
  resp.sendRedirect("list");	// 리다이렉트
  
  // 리다이렉트는 HTML 을 출력하지 않는다.
  //      resp.setContentType("text/html; charset=UTF-8");
  //      PrintWriter out = resp.getWriter();
  ...
  ```

  - 클라이언트에게 리다이렉트를 요구하는 **HttpServletResponse의 sendRedirect() 호출 코드를 추가.**
  - sendRedirect()에 넘기는 URL이 '/'로 시작하지 않기 때문에 **상대 주소로 계산된다.**

  <br>

# 4.7. 서블릿 초기화 매개변수

- **서블릿 초기화 매개변수란?** 
  - 서블릿을 생성하고 초기화할 때, 즉 init()를 호출할 때 **서블릿 컨테이너가 전달하는 데이터이다.**
  - 보통 데이터베이스 연결 정보와 같은 **정적인 데이터를 서블릿에 전달할 때 사용한다.**
  - 서블릿 초기화 매개변수는 **DD파일(web.xml)의** 서블릿 배치 정보에 설정할 수 있고, 어노테이션을 사용하여 서블릿 소스 코드에 설정할 수 있다.
  - 실무에서도 데이터베이스 정보와 같은 **시스템 환경과 관련된 정보는 외부 파일에 두어 관리한다.**

<br>

## 4.7.1. 회원 정보 조회와 변경

서블릿 초기화 매개변수를 이용하여 소스 코드에 박혀 있던 데이터베이스 연결 정보를 web.xml 파일로 옮기겠습니다.

- **회원 정보 조회 및 변경 시나리오**

<img src="../capture/스크린샷 2019-09-03 오후 11.13.35.png">

<br>

## 4.7.2. 회원 목록 페이지에 상세 정보 링크 추가

- **src/spms/servlets/MemberListServlet.java**

  ```java
  ...
  out.println("<html><head><title>회원목록</title></head>");
  out.println("<body><h1>회원목록</h1>");
  out.println("<p><a href='add'>신규 회원</a></p>");
  
  while (resultSet.next()) {
    out.println(
      resultSet.getInt("mno") + " , " +
      "<a href='update?no=" + resultSet.getInt("mno") + "'>" +
      resultSet.getString("mname") + "</a> , " +
      resultSet.getString("email") + " , " +
      resultSet.getDate("cre_date") + "<br>");
  }
  
  out.println("</body></html>");
  ...
  ```

  - MemberListServlet 클래스에서 회원 이름 앞뒤로 **\<a> 태그를 추가한다.**
  - 회원 상세 정보를 출력하는 서블릿의 URL은 '/member/update' 로 할 것이다.

<br>

## 4.7.3. DD파일에 서블릿 초기화 매개변수 설정

- **web/WEB-INF/web.xml**

  ```xml
  <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
           version="4.0">
  
    <!-- 서블릿 선언 -->
    <servlet>
      <servlet-name>MemberUpdateServlet</servlet-name>
      <servlet-class>spms.servlets.MemberUpdateServlet</servlet-class>
      <init-param>
        <param-name>driver</param-name>
        <param-value>org.postgresql.Driver</param-value>
      </init-param>
      <init-param>
        <param-name>url</param-name>
        <param-value>jdbc:postgresql://arjuna.db.elephantsql.com:5432/</param-value>
      </init-param>
      <init-param>
        <param-name>username</param-name>
        <param-value>fsmfppcj</param-value>
      </init-param>
      <init-param>
        <param-name>password</param-name>
        <param-value>opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH</param-value>
      </init-param>
    </servlet>
  
    <servlet-mapping>
      <servlet-name>MemberUpdateServlet</servlet-name>
      <url-pattern>/member/update</url-pattern>
    </servlet-mapping>
    ...
  ```

  - MemberUpdateServlet의 서블릿 배치 정보를 작성한다.
  - **\<init-param>** : 서블릿 초기화 매개변수를 설정하는 태그
    - 이 태그는 \<servlet>의 자식 태그이다.
    - **\<param-name>** : 매개변수의 이름을 지정
    - **\<param-value>** : 매개변수의 값을 지정
  - 실무에서도 이처럼 변경되기 쉬운 값들은 **XML 파일이나 프로퍼티 파일에 두어 관리한다.**

  <br>

## 4.7.4. 회원 상세 정보 출력하는 서블릿 작성

- **클래스 로딩**

  ```java
  Class.forName(/*JDBC 드라이버 클래스의 이름*/"org.postgresql.Driver");
  ```

  - Class.forName()을 사용하여 JDBC 드라이버 클래스, 즉 **java.sql.Drivet를 구현한 클래스를 로딩한다.**
  - 인자값으로 클래스 이름을 넘기면 해당 클래스를 찾아 로딩한다. 클래스 이름을 반드시 패키지 이름을 포함해야 한다. 보통 영어로 **'QNAME(fully qualified name)'** 라고 표현한다. 

<br>

- **서블릿 초기화 매개변수의 값 꺼내기**

  ```java
  this.getInitParameter(/*매개변수 이름*/"driver")
  ```

  - **getInitParamet()** 는 해당 서블릿의 배치 정보가 있는 web.xml로 부터 \<init-param>의 매개변수 값을 꺼내 준다.

<br>

- **서블릿 초기화 매개변수를 이용한 JDBC 드라이버 클래스의 로딩**

  ```java
  Class.forName(this.getInitParameter("driver"));
  ```

<br>

- **src/spms/servlets/MemberUpdateServlet.java**

  ```java
  package spms.servlets;
  
  import javax.servlet.ServletException;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  import java.io.PrintWriter;
  import java.sql.Connection;
  import java.sql.DriverManager;
  import java.sql.PreparedStatement;
  import java.sql.ResultSet;
  
  @SuppressWarnings("serial")
  public class MemberUpdateServlet extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      // 요청 매개변수로 넘어온 회원 번호를 가지고 회원 정보를 질의
      String query = "select mno, email, mname, cre_date from members" +
          " where mno= " + req.getParameter("no");
  
      try {
        Class.forName(getInitParameter("driver"));
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
  
      try (Connection connection = DriverManager.getConnection(
        // 초기화 매개변수를 이용하여 데이터베이스에 연결
          this.getInitParameter("url"),
          this.getInitParameter("username"),
          this.getInitParameter("password"));
           PreparedStatement preparedStatement = connection.prepareStatement(query);
           ResultSet resultSet = preparedStatement.executeQuery()) {
        resultSet.next();
  
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        // 회원 상세 정보 출력
        out.println("<html><head><title>회원정보</title></head>");
        out.println("<form action='update' method='post'>");
        out.println("번호: <input type='text' name='no' value='" + req.getParameter("no") + "'readonly><br>");
        out.println("이름: *<input type='text' name='name' value='" + resultSet.getString("mname") +
            "'><br>");
        out.println("이메일: <input type='text' name='email' value='" + resultSet.getString("email") +
            "'><br>");
        out.println("가입일: " + resultSet.getDate("cre_date") + "<br>");
        out.println("<input type='submit' value='저장'>");
        out.println("<input type='button' value='취소' onclick='location.href=\"list\"'>");
        out.println("</form>");
        out.println("</body></html>");
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }
  }
  ```

<br>

- **회원 상세 정보 출력**

  ```html
  <html>
    <head>
      <title>회원정보</title>
    </head>
    <body>
      <h1>회원정보</h1>
      <form action='update' method='post'>
        번호: <input type='text' name='no' value='1' readonly><br>
        이름: <input type='text' name='name' value='홍길동'><br>
        이메일: <input type='text' name='email' value='s1@test.com'><br>
        가입일: 2013-10-27<br>
        <input type='submit' value='저장'>
        <input type='button' value='취소' onclick='location.href="list"'>
      </form>
    </body>
  </html>
  ```

  * **\<form> 태그**
    * 회원 번호는 주 키(Primary Key) 칼럼이기 때문에 값을 변경할 수 없으므로, 입력상자는  readonly 속성을 추가하여 읽기 전용으로 설정하였다.
    * '취소' 버튼을 눌렀을 때 회원 목록 페이지로 이동하고자  **onclick 속성에** 자바스크립트를 넣었다. **location은 웹 브라우저의 페이지 이동을 관리하는 자바스크립트 객체이다. href 프로퍼티는 웹 브라우저가 출력할 페이지의  URL을 설정한다.**

<br>

### 회원 상세 정보 페이지 테스트

- **회원 상세 페이지**

<img src="../capture/스크린샷 2019-09-04 오전 10.20.15.png" width=500>

<br>

## 4.7.5. 회원 정보 변경하기

- **src/spms/servlets/MemberUpdateServlet.java**

  ```java
  ...
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String query = "update members set email=?, mname=?, mod_date=now() where MNO=?";
  
    try {
      Class.forName(getInitParameter("driver"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  
    try (Connection connection = DriverManager.getConnection(
      this.getInitParameter("url"),
      this.getInitParameter("username"),
      this.getInitParameter("password"));
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, req.getParameter("email"));
      preparedStatement.setString(2, req.getParameter("name"));
      preparedStatement.setInt(3, Integer.parseInt(req.getParameter("no")));
      preparedStatement.executeUpdate();
  
      resp.sendRedirect("list");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
  ...
  ```

  - **now()** : 현재 날짜와 시간을 반환한다.

<br>

## 4.7.6. 어노테이션으로 서블릿 초기화 매개변수 설정

web.xml이 아닌 서블릿 소스 코드에 어노테이션으로 서블릿 초기화 매개변수를 설정할 수 있다.

<br>

### @WebServlet 어노테이션

@WebServlet 어노테이션을 사용하여 서블릿 배치 정보를 설정해보자.

1. **web.xml에 작성했던 MemberUpdateServlet 배치 정보를 제거하거나 주석으로 막는다.**

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
            version="4.0">
   
     <!--    &lt;!&ndash; 서블릿 선언 &ndash;&gt;-->
     <!--    <servlet>-->
     <!--        <servlet-name>MemberUpdateServlet</servlet-name>-->
     <!--        <servlet-class>spms.servlets.MemberUpdateServlet</servlet-class>-->
     <!--        <init-param>-->
     <!--            <param-name>driver</param-name>-->
     <!--            <param-value>org.postgresql.Driver</param-value>-->
     <!--        </init-param>-->
     <!--        <init-param>-->
     <!--            <param-name>url</param-name>-->
     <!--            <param-value>jdbc:postgresql://arjuna.db.elephantsql.com:5432/</param-value>-->
     <!--        </init-param>-->
     <!--        <init-param>-->
     <!--            <param-name>username</param-name>-->
     <!--            <param-value>fsmfppcj</param-value>-->
     <!--        </init-param>-->
     <!--        <init-param>-->
     <!--            <param-name>password</param-name>-->
     <!--            <param-value>opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH</param-value>-->
     <!--        </init-param>-->
     <!--    </servlet>-->
   
     <!--    <servlet-mapping>-->
     <!--        <servlet-name>MemberUpdateServlet</servlet-name>-->
     <!--        <url-pattern>/member/update</url-pattern>-->
     <!--    </servlet-mapping>-->
     ...
   ```

2. **MemberUpdateServlet 클래스에 서블릿 배치 정보를 설정하는 어노테이션을 추가한다.**

   ```java
   ...
   @WebServlet(
     urlPatterns = {"/member/update"},
     initParams = {
       @WebInitParam(name="driver", value="org.postgresql.Driver"),
       @WebInitParam(name="url", value = "jdbc:postgresql://arjuna.db.elephantsql.com:5432/"),
       @WebInitParam(name="username", value = "fsmfppcj"),
       @WebInitParam(name="password", value = "opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH")
     }
   )
   
   @SuppressWarnings("serial")
   public class MemberUpdateServlet extends HttpServlet {
   ...
   ```

   - **@WebServlet** 어노테이션의 **initParams는 서블릿 초기화 매개변수를 설정하는 속성이다.** 이 속성의 **값은 @WebInitParam의 배열이다.**

<br>

# 4.8. 컨텍스트 초기화 매개변수

**컨텍스트 초기화 매개변수는 같은 웹 애플리케이션에 소속된 서블릿들이 공유하는 매개변수이다.** 이는 각각의 서블릿마다 초기화 매개변수를 선언하는 것을 막기위해 사용된다.

<br>

## 4.8.1. 컨텍스트 초기화 매개변수의 선언

- **web/WEB-INF/web.xml**

  ```xml
  ...
  <!-- 서블릿 선언 -->
  <!--    <servlet>-->
  <!--        <servlet-name>MemberUpdateServlet</servlet-name>-->
  <!--        <servlet-class>spms.servlets.MemberUpdateServlet</servlet-class>-->
  <!--        <init-param>-->
  <!--            <param-name>driver</param-name>-->
  <!--            <param-value>org.postgresql.Driver</param-value>-->
  <!--        </init-param>-->
  <!--        <init-param>-->
  <!--            <param-name>url</param-name>-->
  <!--            <param-value>jdbc:postgresql://arjuna.db.elephantsql.com:5432/</param-value>-->
  <!--        </init-param>-->
  <!--        <init-param>-->
  <!--            <param-name>username</param-name>-->
  <!--            <param-value>fsmfppcj</param-value>-->
  <!--        </init-param>-->
  <!--        <init-param>-->
  <!--            <param-name>password</param-name>-->
  <!--            <param-value>opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH</param-value>-->
  <!--        </init-param>-->
  <!--    </servlet>-->
  
  <context-param>
    <param-name>driver</param-name>
    <param-value>org.postgresql.Driver</param-value>
  </context-param>
  <context-param>
    <param-name>url</param-name>
    <param-value>jdbc:postgresql://arjuna.db.elephantsql.com:5432/</param-value>
  </context-param>
  <context-param>
    <param-name>username</param-name>
    <param-value>fsmfppcj</param-value>
  </context-param>
  <context-param>
    <param-name>password</param-name>
    <param-value>opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH</param-value>
  </context-param>
  ...
  ```

  - 이전에 써놓은 서블릿 초기화 매개변수와 어노테이션 서블릿 초기화 매개변수를 주석 처리한다. 그리고 web.xml에 컨텍스트 초기화 매개변수를 선언한다.

<br>

- **src/spms/servlets/MemberUpdateServlet.java**

  ```java
  ...
  @WebServlet("/member/update")
  @SuppressWarnings("serial")
  public class MemberUpdateServlet extends HttpServlet {
  
    private ServletContext sc;
  
    @Override
    public void init() throws ServletException {
      super.init();
      sc = this.getServletContext();
    }
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String query = "select mno, email, mname, cre_date from members" +
          " where mno= " + req.getParameter("no");
  
      try {
        Class.forName(sc.getInitParameter("driver"));		// 서블릿 컨텍스트 사용
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
  
      try (Connection connection = DriverManager.getConnection(
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));
  			...
      }
    }
  
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      req.setCharacterEncoding("UTF-8");
      String query = "update members set email=?, mname=?, mod_date=now() where MNO=?";
  
      try {
        Class.forName(sc.getInitParameter("driver"));
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
  
      try (Connection connection = DriverManager.getConnection(
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));
  ...
  ```

  - 컨텍스트 초기화 매개변수의 값을 얻으려면 **ServletContext** 객체가 필요하다. HttpServlet으로 부터 상속받은 **getServletContext()를 호출하여 ServletContext 객체를 준비한다.**

    ```java
    private ServletContext sc;
    
    @Override
    public void init() throws ServletException {
      super.init();
      sc = this.getServletContext();
    }
    ```

  - ServletContext 객체를 통해 **getInitParameter()를 호출하면 web.xml에 선언된 컨텍스트 초기화 매개변수 값을 얻을 수 있다.**

    ```java
    Class.forName(sc.getInitParameter("driver"));
    ```

<br>

# 실력 향상 과제

1. MemberAddServlet과 MemberListServlet도 컨텍스트 초기화 매개변수를 사용하는 것으로 바꾸시오.

   - **src/spms/servlets/MemberListServlet.java**

     ```java
     package spms.servlets;
     
     import javax.servlet.*;
     import javax.servlet.annotation.WebServlet;
     import java.io.IOException;
     import java.io.PrintWriter;
     import java.sql.*;
     
     // 서블릿을 만들고자 서블릿 어노테이션을 쓰고
     // GenericServlet 을 상속받는다.
     @WebServlet("/member/list")
     public class MemberListServlet extends GenericServlet {
     
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
       public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
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
           servletResponse.setContentType("text/html; charset=UTF-8");
     
           PrintWriter out = servletResponse.getWriter();
           out.println("<html><head><title>회원목록</title></head>");
           out.println("<body><h1>회원목록</h1>");
           out.println("<p><a href='add'>신규 회원</a></p>");
     
           while (resultSet.next()) {
             out.println(
                 resultSet.getInt("mno") + " , " +
                     "<a href='update?no=" + resultSet.getInt("mno") + "'>" +
                     resultSet.getString("mname") + "</a> , " +
                     resultSet.getString("email") + " , " +
                     resultSet.getDate("cre_date") + "<br>");
           }
     
           out.println("</body></html>");
     
         } catch (Exception e) {
           throw new ServletException(e);
         }
       }
     }
     ```

<br>

2. MemberListServlets의 슈퍼 클래스를 GenericServlet에서 HttpServlet으로 교체하시오. 또한, service() 대신 doGet()으로 오버라이딩 하시오.

   - **src/spms/servlets/MemberListServlet.java**

     ```java
     package spms.servlets;
     
     import javax.servlet.*;
     import javax.servlet.annotation.WebServlet;
     import javax.servlet.http.HttpServlet;
     import javax.servlet.http.HttpServletRequest;
     import javax.servlet.http.HttpServletResponse;
     import java.io.IOException;
     import java.io.PrintWriter;
     import java.sql.*;
     
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
     
           PrintWriter out = resp.getWriter();
           out.println("<html><head><title>회원목록</title></head>");
           out.println("<body><h1>회원목록</h1>");
           out.println("<p><a href='add'>신규 회원</a></p>");
     
           while (resultSet.next()) {
             out.println(
                 resultSet.getInt("mno") + " , " +
                     "<a href='update?no=" + resultSet.getInt("mno") + "'>" +
                     resultSet.getString("mname") + "</a> , " +
                     resultSet.getString("email") + " , " +
                     resultSet.getDate("cre_date") + "<br>");
           }
     
           out.println("</body></html>");
     
         } catch (Exception e) {
           throw new ServletException(e);
         }
       }
     }
     ```

<br>

3. 회원 정보를 삭제하는 서블릿을 구현하시오.

   - **src/spms/servlets/MemberListServlet.java**

     ```java
     ...
     while (resultSet.next()) {
       out.println(
         resultSet.getInt("mno") + " , " +
         "<a href='update?no=" + resultSet.getInt("mno") + "'>" +
         resultSet.getString("mname") + "</a> , " +
         resultSet.getString("email") + " , " +
         resultSet.getDate("cre_date") +
         " <a href='delete?no=" + resultSet.getInt("mno") + "'>[삭제]</a><br>");
     }
     ...
     ```

   - **src/spms/servlets/MemberDeleteServlet.java**

     ```java
     package spms.servlets;
     
     import javax.servlet.ServletContext;
     import javax.servlet.ServletException;
     import javax.servlet.annotation.WebServlet;
     import javax.servlet.http.HttpServlet;
     import javax.servlet.http.HttpServletRequest;
     import javax.servlet.http.HttpServletResponse;
     import java.io.IOException;
     import java.sql.Connection;
     import java.sql.DriverManager;
     import java.sql.PreparedStatement;
     
     @WebServlet("/member/delete")
     public class MemberDeleteServlet extends HttpServlet {
     
       private ServletContext sc;
     
       @Override
       public void init() throws ServletException {
         super.init();
         sc = this.getServletContext();
       }
     
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         req.setCharacterEncoding("UTF-8");
         String query = "delete from members where mno=" + req.getParameter("no");
     
         try (Connection conn = DriverManager.getConnection(
             sc.getInitParameter("url"),
             sc.getInitParameter("username"),
             sc.getInitParameter("password"));
              PreparedStatement ps = conn.prepareStatement(query);
         ) {
           ps.executeUpdate();
           resp.sendRedirect("list");
         } catch (Exception e) {
           throw new ServletException(e);
         }
       }
       
     }
     ```

4. 회원 정보 변경 양식에서 삭제 버튼을 추가하세요. 삭제를 클릭하면 회원정보를 삭제하고, 회원 목록 페이지로 이동합니다.

   - **src/spms/servlets/MemberUpdateServlet.java**

     ```java
     ...
     out.println("<input type='submit' value='저장'>");
     out.println("<input type='button' value='삭제' onclick='location.href=\"delete?no=" + req.getParameter("no") +
                 "\"'>");
     out.println("<input type='button' value='취소' onclick='location.href=\"list\"'>");
     ...
     ```

<br>

# 4.9. 필터 사용하기

**필터는** 서블릿 실행 전후에 어떤 작업을 하고자 할 때 사용하는 기술이다. 

- **필터 사용 예시**
  - 클라이언트가 보낸 데이터의 암호를 해제
  - 서블릿이 실행되기 전에 필요한 자원 준비
  - 서블릿이 실행될 때마다 로그를 남김

<br>

- **필터의 실행**

<img src="../capture/스크린샷 2019-09-04 오후 3.31.04.png">

<br>

## 4.9.1. 필터

한글이 깨지는 것을 방지하기 위해 setCharacterEncoding()을 호출하여 메시지 바디의 데이터가 어떤 문자집합으로 인코딩되었는지 설정하였다. 하지만, 각 서블릿 마다 앞의 코드를 작성하는 것은 매우 번거로운 일이기 때문에 이럴 때 **서블릿 필터를** 이용하면 간단히 처리할 수 있다.

<br>

## 4.9.2. 필터 만들기

- **Filter 인터페이스의 구현체**

  <img src="../capture/스크린샷 2019-09-04 오후 5.49.07.png" width=500>

<br>

- **src/spms/filters/CharacterEncodingFilter.java**

  ```java
  package spms.filters;
  
  import javax.servlet.*;
  import java.io.IOException;
  
  public class CharacterEncodingFilter implements Filter {
  
    private FilterConfig config;
  
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
      this.config = filterConfig;
    }
  
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      // 서블릿이 실행되기 전에 해야 할 작업 위치
      servletRequest.setCharacterEncoding(config.getInitParameter("encoding"));
      
      // 다음 필터를 호출. 더이상 필터가 없다면 서블릿의 service()가 호출됨.
      filterChain.doFilter(servletRequest, servletResponse);
      
      // 서블릿을 실행한 후, 클라이언트에게 응답하기 전에 해야할 작업 위치
    }
  
    @Override
    public void destroy() {}
  
  }
  ```

  - 필터는 서블릿과 마찬가지로 한 번 생성되면 웹 애플리케이션이 실행되는 동안 계속 유지된다.
  - **init()** 메서드는 필터 객체가 생성되고 나서 **준비 작업을 위해 딱 한 번 호출된다.**
  - **doFilter()** 메서드는 필터와 연결된 URL에 대해 요청이 들어오면 실행된다.
  - **destroy()** 메서드는 서블릿 컨테이너가 웹 애플리케이션을 종료하기 전에 호출된다.

<br>

## 4.9.3. 필터의 구동

Filter 인터페이스에 선언된 세 개의 메서드는 필터의 생명주기와 관련된 메서드이다.

- **필터의 실행**

