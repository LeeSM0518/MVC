# 07. 퍼시스턴스 프레임워크의 도입

퍼시스턴스 프레임워크(persistence framework)를 사용하면 직접 JDBC API를 호출하지 않고도 데이터베이스에 있는 데이터를 다룰 수 있다.

<br>

### *퍼시스턴스(Persistence)*

퍼시스턴스는 **데이터의 지속성을** 의미한다. 퍼시스턴스 프레임워크는 데이터의 저장, 조회, 변경, 삭제를 다루는 클래스 및 설정 파일들의 집합이다.

<br>

### *프레임워크(Framework)*

'프레임워크(framework)'는 동작에 필요한 구조를 어느 정도 완성해 놓은 반제품 형태의 도구이다.

<br>

### *퍼시스턴스 프레임워크*

퍼시스턴스 프레임워크를 사용하면 JDBC 프로그래밍의 복잡함이나 번거로움 없이 간단한 작업만으로 데이터베이스와 연동되는 시스템을 빠르게 개발할 수 있다.

퍼시스턴스 프레임워크에는 SQL 문장으로 직접 DB 데이터를 다루는 **'SQL 맵퍼(mapper)'** 와 자바 객체를 통해 간접적으로 DB 데이터를 다루는 **'객체 관계 맵퍼(Object-Relational mapper)'** 가 있다.

<br>

### *객체 관계 맵퍼*

객체 관계 맵퍼는 프레임워크에서 제공하는 API와 전용 객체 질의어를 사용하여 데이터를 다룬다.

프레임워크에서는 **하이버네이트(HQL: Hibernate Query Language)** 라는 객체 질의어를 제공한다.

실행 시에 DBMS에 맞추어 SQL 문을 자동 생성하기 때문에 특정 DBMS 종속되지 않는 애플리케이션을 만들 수 있다.

<br>

### *객체 관계 맵퍼의 한계*

실무에서는 객체 관계 맵퍼보다 SQL 맵퍼를 더 많이 사용한다.

* 객체 관계 맵퍼를 사용하려면 **데이터베이스의 정규화(normalized)가 잘돼 있어야 한다.** 하지만 프로젝트가 끝난 이후에 유지보수 단계에 들어가면 잦은 기능 변경과 추가로 인해 데이터베이스 구조가 흐트러지게 된다. 그래서 객체 관계 맵퍼를 사용하면 개발 비용이 증가하게 된다.
* 객체 관계 맵퍼의 또 다른 단점은 데이터베이스의 특징에 맞추어 최적화를 할 수 없다. 객체 관계 맵퍼에서는 **SQL 문을 직접 작성하지 않기 때문에 이런 특장점을 십분 활용할 수 없다.**

<br>

# 7.1. mybatis 소개

mybatis의 핵심은 **개발과 유지보수가 쉽도록 소스 코드에 박혀있는 SQL을 별도의 파일로 분리하는 것이다.** 또한 **단순하고 반복적인 JDBC 코드를 캡슐화하여 데이터베이스 프로그래밍을 간결하게 만드는 것이다.**

* **PostgresSqlProjectDao로부터 분리된 SQL**

  <img src="../capture/스크린샷 2019-10-17 오후 6.50.29.png">

  * 개발자가 SQL문을 더 쉽게 작성하고 관리하도록 SQL을 별도의 파일로 분리한다.

  <br>

## 7.1.1. mybatis 사용하기

**DAO에서 mybatis를 사용하는 시나리오**

<img src="../capture/스크린샷 2019-10-17 오후 9.50.03.png">

1. 데이터 처리를 위해 DAO는 mybatis에 제공하는 객체의 메서드를 호출한다.
2. mybatis는 SQL 문이 저장된 맵퍼 파일에서 데이터 처리에 필요한 SQL 문을 찾는다.
3. mybatis는 맵퍼 파일에서 찾은 SQL을 서버에 보내고자 JDBC 드라이버를 사용한다.
4. JDBC 드라이버는 SQL 문을 데이터베이스 서버로 보낸다.
5. mybatis는 select 문의 실행 결과를 값 객체에 담아서 반환한다.

<br>

## 7.1.2. mybatis 사용 준비

1. https://github.com/mybatis/mybatis-3/releases 에서 [mybatis-3.5.2.zip](https://github.com/mybatis/mybatis-3/releases/download/mybatis-3.5.2/mybatis-3.5.2.zip) 을 다운받는다.
2. 의존 라이브러리와 mybatis 라이브러리를 lib에 추가해준다.

<br>

# 7.2. mybatis 적용

## 7.2.1. mybatis 구동하기

* **mybatis를 이용한 프로젝트 목록 가져오기 시나리오**

  <img src="../capture/스크린샷 2019-10-18 오전 12.03.43.png">

  1. PostgresSqlProjectDao는 SqlSessionFactory에게 SQL을 실행할 객체를 요구한다.
  2. SqlSessionFactory는 SqlSession 객체를 생성하여 반환한다.
  3. PostgresSqlProjectDao는 SqlSession 객체에게 SQL 실행을 요청한다.
  4. SqlSession 객체는 SQL이 저장된 맵퍼 파일에서 SQL을 찾는다.
  5. SqlSession은 JDBC 드라이버를 통해 데이터베이스에 질의를 실행한다.
  6. SqlSession은 데이터베이스로부터 가져온 데이터로 Project 목록을 생성하여 반환한다.
  7. PostgresSqlProjectDao는 사용이 끝난 SqlSession을 닫는다.

<br>

## 7.2.2. mybatis 프레임워크의 핵심 컴포넌트

프레임워크를 사용할 때는 **그 프레임워크의 핵심을 담당하는 컴포넌트를 먼저 파악해야 한다.**

* **mybatis의 핵심 컴포넌트**

  | 컴포넌트                 | 설명                                                         |
  | ------------------------ | ------------------------------------------------------------ |
  | SqlSession               | 실제 SQL을 실행하는 객체.                                    |
  | SqlSessionFactory        | SqlSession 객체를 생성함.                                    |
  | SqlSessionFactoryBuilder | PostgreSQL 설정 파일의 내용을 토대로 SqlSessionFactory를 생성함. |
  | mybatis 설정 파일        | 데이터베이스 연걸 정보, 트랜잭션 정보, mybatis 제어 정보 등의 설정 내용을 포함하고 있다. SqlSessionFactory를 만들 때 사용. |
  | SQL 맵퍼 파일            | SQL 문을 담고 있는 파일. SqlSession 객체가 참조함.           |

<br>

## 7.2.3. DAO에서 SqlSessionFactory 사용

먼저 SqlSession을 사용하는 DAO를 만들어보자.

* **spms/dao/PostgresSqlProjectDao**

  ```java
  package spms.dao;
  
  import org.apache.ibatis.session.SqlSession;
  import org.apache.ibatis.session.SqlSessionFactory;
  import spms.annotation.Component;
  import spms.vo.Project;
  
  import javax.sql.DataSource;
  import java.sql.Connection;
  import java.sql.PreparedStatement;
  import java.sql.ResultSet;
  import java.util.ArrayList;
  import java.util.List;
  
  @Component("projectDao")
  public class PostgresSqlProjectDao implements ProjectDao {
  
    private SqlSessionFactory sqlSessionFactory;
  
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
      this.sqlSessionFactory = sqlSessionFactory;
    }
  
    @Override
    public List<Project> selectList() throws Exception {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        return sqlSession.selectList("spms.dao.ProjectDao.selectList");
      }
    }
  
    @Override
    public int insert(Project project) throws Exception {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        int count = sqlSession.insert("spms.dao.ProjectDao.insert", project);
        sqlSession.commit();
        return count;
      }
    }
  
    @Override
    public Project selectOne(int no) throws Exception {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        return sqlSession.selectOne("spms.dao.ProjectDao.selectOne", no);
      }
    }
  
    @Override
    public int update(Project project) throws Exception {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        int count = sqlSession.update("spms.dao.ProjectDao.update", project);
        sqlSession.commit();
        return count;
      }
    }
  
    @Override
    public int delete(int no) throws Exception {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        int count = sqlSession.delete("spms.dao.ProjectDao.delete", no);
        sqlSession.commit();
        return count;
      }
    }
  
  }
  ```

<br>

### *의존 객체 SqlSessionFactory*

mybatis를 사용한다면 DataSource는 더 이상 필요 없다. 대신 SqlSessionFactory 객체가 필요하다.

```java
SqlSessionFactory sqlSessionFactory;

public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
  this.sqlSessionFactory = sqlSessionFactory;
}
```

* **SqlSessionFactory** 는 SQL을 실행할 때 사용할 도구를 만들어 준다.

<br>

### *SqlSession 사용*

SqlSession은 **SQL을 실행하는 도구이다.** 이 객체가 있어야만 SQL 문을 실행할 수 있다. 이 객체는 직접 생성할 수는 없고, **SqlSessionFactory를 통해서만 얻을 수 있다.**

```java
SqlSession sqlSession = sqlSessionFactory.openSession();
```

<br>

### *SqlSession의 주요 메서드*

| 메서드       | 설명                                           |
| ------------ | ---------------------------------------------- |
| selectList() | SELECT 문을 실행. 값 객체(VO) 목록 반환        |
| selectOne()  | SELECT 문을 실행. 하나의 값 객체 반환          |
| insert()     | INSERT 문을 실행. 반환값은 입력한 데이터 개수. |
| update()     | UPDATE 문을 실행. 반환값은 변경한 데이터 개수  |
| delete()     | DELETE 문을 실행. 반환값은 삭제한 데이터 개수  |

<br>

### *SqlSession의 selectList() 호출 문법*

* **호출 규칙**

  ```java
  List<E> selectList(String sqlId)
  ```

  * 매개변수 값은 **SQL 아이디** 이다. 

  * sqlId는 **SQL 멥퍼의 네임스페이스 이름과 SQL 문의 아이디를 결합하여 만든 문자열이다.**

    > sqlId = SQL 멥퍼의 네임스페이스 이름 + SQL 문 ID

    ```java
    "spms.dao.ProjectDao.selectList" = "spms.dao.ProjecDao" + "selectList"
    ```

  * 호출 부분 코드

    ```java
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      return sqlSession.selectList("spms.dao.ProjectDao.selectList");
    }
    ```

    