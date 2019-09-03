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

