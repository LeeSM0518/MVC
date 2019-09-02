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
                  resultSet.getString("mname" + " , ") +
                  resultSet.getString("email" + " , ") +
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

**@WebServlet 어노테이션으로** 서블릿의 배치 정보를 설정한다. MemberListServlet의 URL 패턴 값은 **'/member/list'로** 설정한다.

```java
@WebServlet("/member/list")
public class MemberListServlet extends GenericServlet {
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

