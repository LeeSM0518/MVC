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

| 메서드       | 설명                                          |
| ------------ | --------------------------------------------- |
| selectList() | SELECT 문을 실행. 값 객체(VO) 목록 반환       |
| selectOne()  | SELECT 문을 실행. 하나의 값 객체 반환         |
| insert()     | INSERT 문을 실행. 반환값은 입력한 데이터 개수 |
| update()     | UPDATE 문을 실행. 반환값은 변경한 데이터 개수 |
| delete()     | DELETE 문을 실행. 반환값은 삭제한 데이터 개수 |

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

* **SQL 맵퍼 파일의 일부분**

  ```xml
  <mapper namespace="spms.dao.ProjectDao">
  
    <select id="selectList" resultMap="projectResultMap">
      select PNO, PNAME, STA_DATE, END_DATE, STATE
      from PROJECTS
      order by PNO desc
    </select>
    ...
  ```

<br>

### *SqlSession의 insert()와 매개변수 값 전달*

SqlSession의 insert()를 호출할 때 두 번째 매개변수로 프로젝트 정보를 담은 값 객체를 넘겨야 한다.

```java
public int insert(Project project) throws Exception {
  try (  SqlSession sqlSession = sqlSessionFactory.openSession()) {
    int count = sqlSession.insert("spms.dao.ProjectDao.insert", project);
  ..
```

이렇게 전달된 'project' 객체는 다음과 같이 SQL 맵퍼 파일의 insert 문을 실행할 때 사용된다.

* **SQL 맵퍼 파일의 일부분**

  ```xml
  <insert id="insert" parameterType="project">
    insert into PROJECTS(PNAME, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS)
    values (#{title}, #{content}, #{startDate}, #{endDate}, 0, now(), #{tags})
  </insert>
  ```

  * **#{프로퍼티명}** : project 객체의 프로퍼티 값이 놓인다. 객체의 프로퍼티란 getter/setter 를 가리키는 용어이다.

  <br>

* **프로퍼티와 getter/setter 메서드**

  <img src="../capture/스크린샷 2019-10-18 오후 4.21.12.png" width=500>

  * 프로퍼티 이름은 **getter/setter 메서드의 이름에서 추출한다.**

<br>

### *SqlSession의 selectOne()과 delete()에 Integer 객체 전달*

selectOne() 과 delete() 를 호출할 때 프로젝트 번호(no)를 매개변수 값으로 전달한다. 기본 데이터 형인 int no를 넘겨줘도 컴파일 시 **Interger 객체로 자동 포장(Auto-boxing)** 되기 때문에 컴파일 오류가 발생하지 않는다.

```java
public Project selectOne(int no) throws Exception {
  try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
    return sqlSession.selectOne("spms.dao.ProjectDao.selectOne", no);
  }
}

public int delete(int no) throws Exception {
  try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
    int count = sqlSession.delete("spms.dao.ProjectDao.delete", no);
    ...
  }
}
```

* **SQL 맵퍼 파일의 일부분**

  ```xml
  <delete id="delete" parameterType="int">
    delete from PROJECTS
    where PNO=#{value}
  </delete>
  ```

  * 기본 타입의 객체 랩퍼(wrapper)로부터 값을 꺼낼 때는 아무 이름이나 사용해도 된다. 위의 코드에서는 Integer 객체의 값을 꺼내고자 'value' 라는 이름을 사용함.

<br>

### *commit()과 rollback() 메서드*

DBMS는 INSERT, UPDATE, DELETE 문을 실행하면 그 작업 결과를 임시 데이터베이스에 보관한다. 클라이언트 요청이 있어야만 임시 데이터베이스의 작업물을 운영 데이터베이스에 반연한다.

* **임시 데이터베이스와 커밋**

  <img src="../capture/스크린샷 2019-10-18 오후 4.33.13.png">

  * **commit()** : 임시 데이터베이스에 보관된 작업 결과를 운영 데이터베이스에 적용하라고 요청할 때 사용하는 메서드이다.

    ```java
    int count = sqlSession.insert("spms.dao.ProjectDao.insert", project);
    sqlSession.commit();
    ```

  * **rollback()** : 임시 데이터베이스의 작업 결과를 운영 데이터베이스에 반영하지 않고 취소할 때 호출한다.

<br>

### *자동 커밋*

INSERT UPDATE, DELETE 을 실행할 때 자동으로 커밋하고 싶다면, SqlSession 객체를 생성할 때 다음과 같이 지정한다.

```java
SqlSession sqlSession = sqlSessionFactory.openSession(true);
```

* **openSession()의 매개변수 값을 true로 지정하면** 자동 커밋(Auto-commit)을 수행하는 SqlSession 객체를 반환한다.
* 자동 커밋으로 설정해 놓고 쓰면 편리하지만, **트랜잭션을 다룰 수 없다.**

<br>

## 7.2.4. SQL 맵퍼 파일 작성

SqlSession에서 SQL을 실행할 때 참조하는 SQL 맵퍼 파일을 작성해보자.

* **spms.dao.PostgresSqlProjectDao.xml**

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="spms.dao.ProjectDao">
      <resultMap type="project" id="projectResultMap">
          <id column="PNO" property="no"/>
          <result column="PNAME" property="title"/>
          <result column="CONTENT" property="content"/>
          <result column="STA_DATE" property="startDate"
                  javaType="java.sql.Date"/>
          <result column="END_DATE" property="endDate"
                  javaType="java.sql.Date"/>
          <result column="STATE" property="state"/>
          <result column="CRE_DATE" property="createdDate"
                  javaType="java.sql.Date"/>
          <result column="TAGS" property="tags"/>
      </resultMap>
  
      <select id="selectList" resultMap="projectResultMap">
          select PNO, PNAME, STA_DATE, END_DATE, STATE
          from PROJECTS
          order by PNO desc
      </select>
  
      <insert id="insert" parameterType="project">
          insert into PROJECTS(PNAME, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS)
          values (#{title}, #{content}, #{startDate}, #{endDate}, 0, now(), #{tags})
      </insert>
  
      <select id="selectOne" parameterType="int" resultMap="projectResultMap">
          select PNO, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS
          from PROJECTS
          where PNO=#{value}
      </select>
  
      <update id="update" parameterType="project">
          update PROJECTS set
          PNAME=#{title},
          CONTENT=#{content},
          STA_DATE=#{startDate},
          END_DATE=#{endDate},
          STATE=#{state},
          TAGS=#{tags}
          where PNO=#{no}
      </update>
  
      <delete id="delete" parameterType="int">
          delete from PROJECTS
          where PNO=#{value}
      </delete>
  
  </mapper>
  ```

  * \<mapper> 태그의 **namespace 값과 \<select>, \<insert> 등** SQL 태그의 id 같은 SqlSession 객체가 SQL 문을 찾을 때 사용한다.

<br>

## 7.2.5. ApplicationContext 변경

mybatis 도입함에 따라 ApplicationContext를 변경해야 한다. 즉, 별도로 SqlSessionFactory를 준비하여 ApplicationContext에 등록해야 한다. 외부에서 생성한 객체를 등록할 수 있게 ApplicationContext를 변경해보자.

* **spms/content/ApplicationContext 클래스**

  ```java
  package spms.context;
  
  import org.reflections.Reflections;
  import spms.annotation.Component;
  
  import javax.naming.Context;
  import javax.naming.InitialContext;
  import java.io.FileReader;
  import java.lang.reflect.Method;
  import java.util.Hashtable;
  import java.util.Properties;
  import java.util.Set;
  
  public class ApplicationContext {
  
    Hashtable<String, Object> objTable = new Hashtable<>();
    
    // 생성자 제거
    // 이제는 외부에서 객체를 주입하는 경우도 고려해야 하기 때문에 일괄처리 방식을 개별처리 방식으로
    // 변경해야 한다.
  
    public Object getBean(String key) {
      return objTable.get(key);
    }
    
    // 외부에서 생성한 SqlSessionFactory를 등록할 수 있게 메소드 추가
    public void addBean(String name, Object obj) {
      objTable.put(name, obj);
    }
    
    public void prepareObjectsByAnnotation(String basePackage) throws Exception {
      Reflections reflector = new Reflections(basePackage);
      
      Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
      String key = null;
      for (Class<?> clazz : list) {
        key = clazz.getAnnotation(Component.class).value();
        objTable.put(key, clazz.newInstance());
      }
    }
    
    public void prepareObjectsByProperties(String propertiesPath) throws Exception {
      Properties props = new Properties();
      props.load(new FileReader(propertiesPath));
      
      Context ctx = new InitialContext();
      String key;
      String value;
      
      for (Object item : props.keySet()) {
        key = (String) item;
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

### *addBean() 추가*

**외부에서 생성한 SqlSessionFactory를 등록할 수 있게 addBean() 메서드를 추가했다.** 매개변수로 객체 이름과 객체 주소를 받아서 objTable에 보관한다.

```java
public void addBean(String name, Object obj) {
  objTable.put(name, obj);
}
```

<br>

### *생성자 제거*

이제는 외부에서 객체를 주입하는 경우도 고려해야 하기 때문에 **일괄처리 방식을 개별처리 방식으로 변경해야 한다.** 따라서 기존 생성자를 제거한다.

<br>

### *prepareObjectsByAnnotation() 메서드*

* 외부에서 호출해야 하기 때문에 접근 제한자를 **public으로 변경하였다.**
* 애노테이션을 검색할 **패키지 이름을 매개변수로 받는다.**

```java
// private void prepareAnnotationObjects() throws Exception {
public void prepareObjectsByAnnotation(String basePackage) throws Exception {
  Reflections reflector = new Reflections(basePackage);
}
```

<br>

### *prepareObjectsByProperties() 메서드*

* 이 메서드 또한 외부에서 호출해야 하기 때문에 접근 제한자를 **public으로 변경한다.**
* 매개변수는 Properties 객체를 직접 받는 대신, **프로퍼티 파일의 경로를 받아서** 내부에서 Properties 객체를 생성하도록 변경한다.

```java
// private void prepareObjects(Properties props) throws Exception {
public void prepareObjectsByProperties(String propertiesPath) throws Exception {
  Properties props = new Properties();
  props.load(new FileReader(propertiesPath));
  
  Context ctx = new InitialContext();
}
```

<br>

### *injectDependency() 메서드*

의존 객체를 주입하는 injectDependency() 메서드도 외부에서 호출할 수 있게 **접근 제한을 private 에서 public으로 변경한다.**

```java
// private void injectDependency() throws Exception {
public void injectDependency() throws Exception {
```

<br>

## 7.2.6. SqlSessionFactory 객체 준비

**DAO 객체에 주입할** SqlSessionFactory 객체를 준비한다.

* **src/spms/listeners/ContextLoaderListener**

  ```java
  package spms.listeners;
  
  import org.apache.ibatis.io.Resources;
  import org.apache.ibatis.session.SqlSessionFactory;
  import org.apache.ibatis.session.SqlSessionFactoryBuilder;
  import spms.context.ApplicationContext;
  import javax.servlet.ServletContext;
  import javax.servlet.ServletContextEvent;
  import javax.servlet.ServletContextListener;
  import javax.servlet.annotation.WebListener;
  import java.io.InputStream;
  
  @WebListener
  public class ContextLoaderListener implements ServletContextListener {
  
    static ApplicationContext applicationContext;
  
    public static ApplicationContext getApplicationContext() {
      return applicationContext;
    }
  
    @Override
    public void contextInitialized(ServletContextEvent sce) {
      try {
        applicationContext = new ApplicationContext();
        
        String resource = "spms/dao/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory =
            new SqlSessionFactoryBuilder().build(inputStream);
  
        applicationContext.addBean("sqlSessionFactory", sqlSessionFactory);
  
        ServletContext sc = sce.getServletContext();
        sc.setRequestCharacterEncoding("UTF-8");
  
        String propertiesPath = sc.getRealPath(
            sc.getInitParameter("contextConfigLocation"));
        applicationContext.prepareObjectsByProperties(propertiesPath);
        applicationContext.prepareObjectsByAnnotation("");
        applicationContext.injectDependency();
  
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
  
  }
  ```

  * 기존에는 ApplicationContext가 모두 처리하였으나, 이제는 **ApplicationContext의 생성자에 프로퍼티 파일의 경로를 넘겨주면** 프로퍼티 파일에 등록된 객체 뿐만 아니라, @Component 애노테이션이 붙은 **클래스를 찾아서 객체를 생성한다.**

<br>

### *ApplicationContext의 기본 생성자 호출*

SqlSessionFactory 객체를 별도로 생성해서 등록해야 하기 때문에 기존의 방식처럼 객체 생성과 의존 객체 주입을 생성자에서 일괄처리를 할 수 없다. 따라서 **ApplicationContext 객체를 기본 생성자를 호출하도록 코드를 변경한다.**

```java
applicationContext = new ApplicationContext();
```

<br>

### *SqlSessionFactory 객체 생성과 빌더 패턴*

SqlSessionFactory는 new 연산자로 객체를 생성할 수 없다.

* **SqlSessionFactory 객체 생성 과정**

  <img src="../capture/스크린샷 2019-10-18 오후 9.46.06.png">

  * 이처럼 복잡한 객체는 전문가를 통해 생성하도록 설계한다. 이런 식의 객체 생성 패턴을 **'빌더 패턴(Builder Pattern)'** 이라 부른다.
  * SqlSessionFactory 클래스도 이름에서 알 수 있듯이 SqlSession 객체를 만드는 공장 객체이다.
  * SqlSessionFactoryBuilder 클래스의 build()를 호출해야만 SqlSessionFactory 객체를 생성할 수 있다.

  ```java
  String resource = "spms/dao/mybatis-config.xml";
  InputStream inputStream = Resources.getResourceAsStream(resource);
  SqlSessionFactory sqlSessionFactory =
    new SqlSessionFactoryBuilder().build(inputStream);
  ```

  * mybatis 설정 파일 **'mybatis-config.xml'** 은 SqlSessionFactory를 생성할 때 필요한 파일이다.
  * bind()의 **매개변수 값으로 이 파일의 입력 스트림을** 넘겨줘야 한다.
  * Resource의 getResourceAsStream() 메서드를 이용하면 **자바 클래스 경로에 있는 파일의 입력 스트림을 손쉽게 얻을 수 있다.**

<br>

### *SqlSessionFactory 객체 등록*

SqlSessionFactory 객체를 생성했으면 ApplicationContext에 등록해야 한다.

```java
applicationContext.addBean("sqlSessionFactory", sqlSessionFactory);
```

<br>

### *프로퍼티 파일의 경로 알아내기*

```java
ServletContext sc = event.getServletContext();
String propertiesPath = sc.getRealPath(
  sc.getInitParameter("contextConfigLocation"));
```

<br>

### *프로퍼티 파일과 애노테이션에 설정된 대로 객체 생성*

이제 프로퍼티 파일의 내용에 따라 객체를 생성하도록 ApplicationContext에 지시한다.

```java
applicationContext.prepareObjectsByProperties(propertiesPath);
```

@Component 애노테이션이 붙은 클래스를 찾아 객체를 생성한다.

```java
applicationContext.prepareObjectsByAnnotation("");
```

<br>

### *의존 객체 주입*

ApplicationContext에서 관리하는 객체들을 조사하여 의존 객체를 주입한다.

```java
applicationContext.injectDependency();
```

<br>

## 7.2.7. mybatis 설정 파일 준비

mybatis 설정 파일은 DB 커넥션을 생성하는 데이터소스에 대한 정보와 트랜잭션 관리자, mybatis 동작을 제어하는 환경값, SQL 문이 저장된 맵퍼 파일의 경로 등의 정보를 포함하고 있다.

* **src/spms/dao/mybatis-config.xml**

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  
      <properties resource="spms/dao/db.properties"/>
  
      <typeAliases>
          <typeAlias type="spms.vo.Project" alias="project"/>
          <typeAlias type="spms.vo.Member" alias="member"/>
      </typeAliases>
  
      <environments default="development">
          <environment id="development">
              <transactionManager type="JDBC"/>
              <dataSource type="POOLED">
                  <property name="driver" value="${driver}"/>
                  <property name="url" value="${url}"/>
                  <property name="username" value="${username}"/>
                  <property name="password" value="${password}"/>
              </dataSource>
          </environment>
      </environments>
  
      <mappers>
          <mapper resource="spms/dao/PostgresSqlProjectDao.xml"/>
      </mappers>
  
  </configuration>
  ```

<br>

## 7.2.8. db.properties 파일 작성

이 프로퍼티 파일은 mybatis 설정 파일에서 참조한다.

* **src/spms/dao/db.properties**

  ```properties
  driver=org.postgresql.Driver
  url=jdbc:postgresql://arjuna.db.elephantsql.com:5432/
  username=fsmfppcj
  password=opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH
  ```

  * DBMS에 대한 정보를 별도의 파일로 관리하면 유지 보수에 좋다.

<br>

# 7.3. SQL 맵퍼 파일

mybatis의 가장 중요한 목적 중 하나가 DAO로부터 SQL 문을 분리하는 것이다. 이렇게 분리된 SQL 문은 SqlSession에서 사용한다.

* **src/spms/dao/PostgresSqlProjectDao.xml**

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="spms.dao.ProjectDao">
      <resultMap type="project" id="projectResultMap">
          <id column="PNO" property="no"/>
          <result column="PNAME" property="title"/>
          <result column="CONTENT" property="content"/>
          <result column="STA_DATE" property="startDate"
                  javaType="java.sql.Date"/>
          <result column="END_DATE" property="endDate"
                  javaType="java.sql.Date"/>
          <result column="STATE" property="state"/>
          <result column="CRE_DATE" property="createdDate"
                  javaType="java.sql.Date"/>
          <result column="TAGS" property="tags"/>
      </resultMap>
  
      <select id="selectList" resultMap="projectResultMap">
          select PNO, PNAME, STA_DATE, END_DATE, STATE
          from PROJECTS
          order by PNO desc
      </select>
  
      <insert id="insert" parameterType="project">
          insert into PROJECTS(PNAME, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS)
          values (#{title}, #{content}, #{startDate}, #{endDate}, 0, now(), #{tags})
      </insert>
  
      <select id="selectOne" parameterType="int" resultMap="projectResultMap">
          select PNO, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS
          from PROJECTS
          where PNO=#{value}
      </select>
  
      <update id="update" parameterType="project">
          update PROJECTS set
          PNAME=#{title},
          CONTENT=#{content},
          STA_DATE=#{startDate},
          END_DATE=#{endDate},
          STATE=#{state},
          TAGS=#{tags}
          where PNO=#{no}
      </update>
  
      <delete id="delete" parameterType="int">
          delete from PROJECTS
          where PNO=#{value}
      </delete>
  
  </mapper>
  ```

<br>

## 7.3.1. SQL 맵퍼 파일 작성

SQL 맵퍼 파일은 XML이기 때문에 **제일 먼저 XML 선언이 온다.**

```xml
<?xml version="1.0" encoding="UTF-8"?>
```

<br>

다음으로 태그 규칙을 정의한 DTD 선언이 온다.

```xml
<!DOCTYPE mapper
    PUBLIC "~//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
```

<br>

### \<mapper> 루트 엘리먼트

* **\<mapper> 태그**

  * **namespace 속성** : 자바의 패키지처럼 SQL 문을 묶는 용도로 사용

  * 맵퍼 파일에 작성하는 모든 SQL 문은 \<mapper> 태그에 놓는다.

    ```xml
    <mapper namespace="spms.dao.ProjectDao">
      ...
    </mapper>
    ```

<br>

### \<select>, \<insert>, \<update>, \<delete> 엘리먼트

* **SELECT** : \<select> 태그
* **INSERT** : \<insert> 태그
* **UPDATE** : \<update> 태그
* **DELETE** : \<delete> 태그

명령어와 태그가 달라도 딱히 문제는 되지 않으나, 유지 보수를 위해서라도 다른 사람이 알아보기 쉽도록 의미 있는 태그를 사용하자.

<br>

### *id 속성*

SQL 문을 작성할 때 각각의 SQL 문을 구분하기 위해 **id 속성을** 사용한다.

```xml
<select id="selectList" ...> ... </select>
<insert id="insert" ...> ... </insert>
<select id="selectOne" ..> ... </select>
<update id="update" ...> ... </update>
<delete id="delete" ...> ... </delete>
```

* 일부러 id 값을 SqlSession의 메서드 이름과 비슷하게 지었다.

<br>

### *resultType 속성*

SELECT 문을 실행하면 결과가 생성되는데, 이 **결과를 담을 객체를 지정하는 속성이 resultType 이다.** 

resultType 에는 다음과 같이 **클래스 이름(패키지 이름 포함)이 온다.**

```xml
<select id="selectList" resultType="spms.vo.Project">
```

<br>

만약 mybatis 설정 파일에 다음과 같이 **spms.vo.Project에** 대한 **별명이 정의되어 있다면,**

```xml
<typeAliases>
  <typeAlias type="spms.vo.Project" alias="project"/>
  <typeAlias type="spms.vo.Member" alias="member"/>
</typeAliases>
```

resultType의 값으로 그 별명을 사용할 수 있다.

```xml
<select id="selectList" resultType="project">
```

<br>

### *칼럼과 셋터 메서드*

mybatis는 SELECT 결과를 저장하고자 **resultType에 선언된 클래스의 인스턴스를 생성한다.** 그리고 **각 칼럼에 대응하는 셋터 메서드를 찾아서 호출해준다.** 이때 셋터 메서드는 대소문자 구분없이 set을 뺀 메서드의 이름과 칼럼 이름이 같으면 된다.

<br>

칼럼의 이름과 셋터의 메서드 이름과 일치시키기 위해 셋터 이름과 똑같은 이름으로 각 칼럼에 대해 별명을 붙인다.

```xml
<select id="selectList" resultType="project">
  select
  	PNO as no,
  	PNAME as title,
  	STA_DATE as startDate,
  	END_DATE as endDate,
  	STATE
  from PROJECTS
  order by PNO desc
</select>
```

* 하지만 이렇게 칼럼에 별명을 붙이는 방식은 매 SELECT 문 마다 별명을 붙여야 하므로 매우 번거롭다.

<br>

### \<resultMap> 엘리먼트

칼럼에 별명을 붙이는 대신 \<resultMap>을 이용하면 **칼럼 이름과 셋터 이름의 불일치 문제를** 해결할 수 있다.

```xml
<resultMap type="project" id="projectResultMap">
  <id column="PNO" property="no"/>
  <result column="PNAME" property="title"/>
  <result column="CONTENT" property="content"/>
  <result column="STA_DATE" property="startDate" javaType="java.sql.Date"/>
  ...
</resultMap>
```

* **\<resultMap> 태그** 
  * **type** : 칼럼 데이터를 저장할 클래스 이름 또는 클래스의 별명. 위에 서는 spms.vo.Project 클래스를 가리키는 별명으로 project 라 하였다.

<br>

### \<result> 엘리먼트

* **column 속성** : 칼럼 이름을 지정
* **property 속성** : 객체의 프로퍼티 이름을 지정

```xml
<result column="PNAME" property="title"/>
```

> result 태그를 사용하여 PNAME 칼럼 값을 setTitle() 메서드와 연결시킨 예

<br>

### *javaType 속성*

\<result>에서 javaType을 사용하면, **칼럼의 값을 특정 자바 객체로 변환할 수 있다.** 

```xml
<result column="STA_DATE" property="startDate" javaType="java.sql.Date"/>
```

> 'STA_DATE' 칼럼에 대해 javaType을 java.sql.Date으로 설정하면 ,칼럼 값을 꺼낼 대 그 객체로 변환한다.

javaType 속성을 지정하지 않는다면, 셋터의 매개변수 타입에 맞추어 칼럼 값이 변환된다.

<br>

### \<id> 엘리먼트

\<id> 태그에서 지정한 프로퍼티는 객체 식별자로 사용된다.

* SELECT 문을 실행할 때마다 매번 결과 객체를 생성한다면 실행 성능이 나빠질 것이다.
* 이를 해결하기 위해 SELECT를 통해 생성된 결과 객체들은 **별도의 보관소에 저장(캐싱, caching)** 해두고, 다음 SELECT를 실행할 때 재사용한다.
* 이때 보관소에 저장된 **객체를 구분하는 값으로 \<id> 에서 지정한 프로퍼티를 사용한다.**

```xml
<id column="PNO" property="no"/>
```

<br>

## 7.3.2. mybatis의 SELECT 결과 캐싱

mybatis는 **객체 캐싱을 제공한다.** 즉, 한 번 생성된 객체는 버리지 않고 보관해 두었다가, 다음 SELECT를 실행할 때 재사용한다.

\<resultMap>의 \<id>를 사용하여 **식별자로 사용할 프로퍼티를 지정하면, 캐싱된 객체를 더욱 빨리 찾을 수 있다.**

<br>

### *mybatis의 객체 캐싱*

<img src="../capture/스크린샷 2019-11-07 오후 9.53.44.png">

* 첫 번째 질의를 수행할 때 생성된 결과 객체는 **풀(pool)에 보관해 둔다.**
* 두 번째 질의에서는 질의 결과에 대해 새로 객체를 생성하기 전에, **객체 풀에 보관된 객체 중에서 PNO 칼럼의 값(\<id> 속성 값)과 일치하는 객체를 먼저 찾는다.** 있다면 기존 객체를 사용하고 없다면 새로운 객체를 생성한다.

<br>

### *\<select> 태그에 resultMap 적용*

SELECT 결과에 대해 \<resultMap>에 정의된 대로 자바 객체를 생성하고 싶다면, \<select> 의 resultMap 속성에 \<resultMap> id를 지정합니다.

```xml
<select id="selectList" resultMap="projectResultMap">
  select PNO, PNAME, STA_DATE, END_DATE, STATE
  from PROJECTS
  order by PNO desc
</select>

<select id="selectOne" parameterType="int" resultMap="projectResultMap">
  select PNO, PNAME, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS
  from PROJECTS
  where PNO=#{value}
</select>
```

> resultMap을 사용하기 때문에 SELECT 문의 칼럼에는 더 이상 별명을 붙이지 않는다.

<br>

## 7.3.3. SQL 문의 입력 매개변수 처리

SELECT나 INSERT, UPDATE, DELETE 문에서 입력 매개변수를 사용하는 경우를 살펴보자.

<br>

### *mybatis의 입력 매개변수*

mybatis에서는 입력 매개변수를 **'#{프로퍼티명}'** 으로 표시합니다.

```xml
<insert id="insert" parameterType="project">
  insert into PROJECTS(PNAME, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS)
  values (#{title}, #{content}, #{startDate}, #{endDate}, 0, now(), #{tags})
</insert>
```

* '#{프로퍼티명}'이 가리키는 값은 \<insert>의 **parameterType에 지정한 객체의 프로퍼티 값(겟터 메서드의 반환값)이다.**

<br>

### *입력 매개변수에 값 공급*

mybatis에서 입력 매개변수에 값을 공급하는 방법은 SqlSession의 메서드를 호출할 때 값 객체를 전달하는 것이다.

```java
public int insert(Project project) throws Exception {
  SqlSession sqlSession = sqlSessionFactory.openSession();
  try {
    int count = sqlSession.insert("spms.dao.ProjectDao.insert", project);
    sqlSession.commit();
    return count;
  } finally {
    sqlSession.close();
  }
}
```

* sqlSession.insert()를 호출하면 **SQL 맵퍼 파일에서** 'spms.dao.ProjectDao.insert' 아이디를 가진 SQL 문을 찾아 실행한다. 

  * **spms.dao.ProjectDao** : SQL 맵퍼 파일의 네임스페이스 이름

    ```xml
    <mapper namespace="spms.dao.ProjectDao">
    ```

  * **insert** : SQL 아이디

    ```xml
    <insert id="insert" parameterType="project">
    ```

* sqlSession.insert() 의 매개변수에 **project는 입력 매개변수에 값을 공급할 객체이다.**

<br>

### *값을 공급하는 객체가 기본 타입인 경우*

```java
public int delete(int no) throws Exception {
  try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
    int count = sqlSession.delete("spms.dao.ProjectDao.delete", no);
    sqlSession.commit();
    return count;
  }
}
```

* SqlSession의 delete() 메서드는 두 번째 매개변수에 객체를 요구한다. 하지만 위의 코드에서는 **int 타입인 no를 매개변수로 메서드를 호출하였다.** 그래도 문제는 없다.
* 자바 컴파일러는 int에 대응하는 랩퍼 객체(Integer)를 생성하여 **no 값을 포장(Auto-boxing)한다.**

<br>

### *입력 매개변수에서 랩퍼 객체 사용*

값 객체가 랩퍼 타입일 때 입력 매개변수의 이름은 어떤 이름을 사용해도 무방하다.

```xml
<delete id="delete" parameterType="int">
	delete from PROJECTS
  where PNO=#{value}
```

* 'value' 이름을 사용하였지만, #{okok} 라고 해도 상관없다.

<br>

# 7.4. mybatis 설정 파일

mybatis 프레임워크는 **자체 커넥션풀을 구축할 수 있습니다.** 또한 여러 개의 데이터베이스 연결 정보를 설정해 두고 실행 상황(개발, 테스트 , 운영 등)에 따라 **사용할 DB를 지정할 수 있다.** 실행 성능을 높이기 위해 **SELECT 결과를 캐싱해 두기도 하고,** SQL 맵퍼 파일에서 **사용할 값 객체(Value Object)에 대해 별명을 부여할 수 있다.**

* **mybatis 프레임워크의 동작 환경을 설정하는 파일(mybatis-config.xml)**

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE configuration
  	PUBLIC "~//mybatis.org//DTD Config 3.0//EN"
  	"http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
    <properties resource="spms/dao/db.properties"/>
    
    <typeAlias>
      <typeAlias type="spms.vo.Project" alias="project"/>
      <typeAlias type="spms.vo.Member" alias="member"/>
    </typeAlias>
    
    <environments default="development">
      <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
          <property name="driver" value="${driver}"/>
          <property name="url" value="${url}"/>
          <property name="username" value="${username}"/>
          <property name="password" value="${password}"/>
        </dataSource>
      </environment>
    </environments>
    
    <mappers>
      <mapper resource="spms/dao/PostgresSqlProjectDao.xml"/>
    </mappers>
  </configuration>
  ```

<br>

## XML 선언과 mybatis 설정을 위한 DTD 선언

mybatis 설정 파일은 XML 기술을 사용하여 작성하기 때문에 제일 먼저 **XML 선언과 태그 규칙을 정의한 DTD 선언이 온다.**

```xml
<? xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
	PUBLIC "~//mybatis.org//DTD Config 3.0//EN"
	"http://mybatis.org/dtd/mybatis-3-config.dtd">
```

<br>

## \<configuration> 루트 엘리먼트

mybatis 설정 파일의 루트 엘리먼트는 configuration 이다.

* **DTD 규칙**

  ```xml
  <!ELEMENT configuration (properties?, settings?, typeAlias?,
  	typeHandlers?, objectFactory?, objectWrapperFactory?, plugins?,
  	environment?, databaseIdProvider?, mappers?)>
  ```

* **configuration의 주요 자식 엘리먼트들**

  | 엘리먼트     | 용도                                                         |
  | ------------ | ------------------------------------------------------------ |
  | properties   | 프로퍼티 파일이 있는 경로 설정.                              |
  | settings     | 프레임워크의 실행 환경을 설정                                |
  | typeAliases  | 자바 클래스 이름(패키지 이름 포함)에 대한 별명 설정          |
  | typeHandlers | 칼럼의 값을 자바 객체로, 자바 객체를 칼럼의 값으로 변환해 주는 클래스를 설정 |
  | environments | 프레임워크에서 사용할 데이터베이스 정보(트랜잭션 관리자, 데이터 소스)를 설정. |
  | mappers      | SQL 맵퍼 파일들이 있는 경로 설정                             |

<br>

## \<properties> 엘리먼트

데이터베이스 연결 정보처럼 자주 변경될 수 있는 값은 mybatis 설정 파일에 두지 않고 보통 프로퍼티 파일에 저장한다.

* **프로퍼티 파일 로딩 예시**

  ```xml
  <properties rosource="spms/dao/db.properties"/>
  ```

  > 프로퍼티 파일이 클래스 경로(CLASSPATH)에 있다면 **resource 속성 사용**

* **프로퍼티 파일이 클래스 경로가 아닌 다른 경로에 있는 예시**

  ```xml
  <properties url="file:///c:/conf/db.properties"/>
  ```

<br>

## 프로퍼티 파일 작성 예

* **db.properties 파일**

  ```properties
  driver=org.postgresql.Driver
  url=jdbc:postgresql://arjuna.db.elephantsql.com:5432/
  username=*******
  password=*******
  ```

<br>

## 개별 프로퍼티 설정

프로퍼티 파일에 정의된 것 외 추가로 프로퍼티를 정의할 수 있다.

```xml
<properties resource="spms/dao/db.properties">
  <property name="username" value="test"/>
  <property name="password" value="testok"/>
</properties>
```

<br>

## 프로퍼티 값 참조

프로퍼티 파일에 저장된 값은 **${프로퍼티명}** 형식으로 참조한다.

```xml
<dataSource type="POOLED">
  <property name="driver" value="${driver}"/>
  <property name="url" value="${url}"/>
  <property name="username" value="${username}"/>
  <property name="password" value="${password}"/>
</dataSource>
```

<br>

## \<typeAliases> 엘리먼트

SQL 맵퍼 파일에서 매개변수 타입(parameterType)이나 결과 타입(resultType)을 지정할 때 긴 이름의 클래스명 대신 짧은 이름의 별명을 사용할 수 있다. **typeAliases 엘리먼트는 SQL 맵퍼 파일에서 사용할 별명들을 설정한다.**

```xml
<typeAliases>
  <typeAlias type="spms.vo.Project" alias="project"/>
  <typeAlias type="spms.vo.Member" alias="member"/>
</typeAliases>
```

* **\<typeAlias>**
  * **type 속성** : 패키지 이름을 포함한 클래스 이름
  * **alias 속성** : type에서 지정한 클래스의 별명이다.

<br>

## SQL 맵퍼에서 별명 사용

```xml
<update id="update" parameterType="project">
  ...
</update>

<select id="selectList" resultType="project">
  ...
</select>
```

* \<update>의 parameterType과 \<select>의 **resultType에 지정한 "project"는** spms.vo.Project 클래스를 가리키는 별명이다.

<br>

### *mybatis에 미리 정의된 별명들*

mybatis는 기본 데이터 형이나 랩퍼 클래스에 대해 미리 별명을 정의하였다.

| 별명           | 타입                 | 별명         | 타입                 |
| -------------- | -------------------- | ------------ | -------------------- |
| _byte          | byte                 | byte         | java.lang.Byte       |
| _short         | short                | short        | java.lang.Short      |
| _int, _integer | int                  | int, integer | java.lang.Integer    |
| _long          | long                 | long         | java.lang.Long       |
| _float         | float                | float        | java.lang.Float      |
| _double        | double               | double       | java.lang.Double     |
| _boolean       | boolean              | boolean      | java.lang.Double     |
| string         | java.lang.String     | date         | java.util.Date       |
| decimal        | java.math.BigDecimal | bigdecimal   | java.math.BigDecimal |
| map            | java.util.Map        | hashmap      | java.util.Hashmap    |
| list           | java.util.List       | arraylist    | java.util.ArrayList  |
| collection     | java.util.Collection | iterator     | java.util.iterator   |
| object         | java.lang.Object     |              |                      |

<br>

## \<environments> 엘리먼트

\<environments> 태그는 데이터베이스 환경 정보를 설정할 때 사용하는 태그이다. 이 태그를 이용하면 여러 개의 데이터베이스 접속 정보를 설정할 수 있다.

```xml
<environments default="development">
	<environment id="development"> ... </environment>
  <environment id="test"> ... </environment>
  <environment id="real"> ... </environment>
</environments>
```

* 설정된 DB 정보 중에서 하나를 선택할 때는 default 속성을 사용한다.
* **\<environment>** : 각각의 데이터베이스 접속 정보 정의
  * **id 속성** : \<environment>를 구분할 때 사용할 식별자

<br>

## \<environment> 엘리먼트

environment는 **트랜잭션 관리 및 데이터 소스를** 설정하는 태그이다.

<br>

### *트랜잭션 관리 방식 설정*

**트랜잭션(Transaction)이란,** 여러 개의 데이터 변경 작업(insert, update, delete)을 하나의 작업으로 묶은 것이다.

* **mybatis의 트랜잭션 관리 유형**

  | 트랜잭션 관리 유형 | 설명                                                         |
  | ------------------ | ------------------------------------------------------------ |
  | JDBC               | 직접 JDBC의 커밋(commit), 롤백(rollback) 기능을 사용하여 mybatis 자체에서 트랜잭션을 관리 |
  | MANAGED            | 서버의 트랜잭션 관리 기능을 이용. 즉 Java EE 애플리케이션 서버(JBoss, WebLogic, WebSphere 등)나 서블릿 컨테이너(톰캣 서버 등)에서 트랜잭션을 관리 |


## 개별 프로퍼티 설정

프로퍼티 파일에 정의된 것 외 추가로 프로퍼티를 정의할 수 있다.

```xml
<properties resource="spms/dao/db.properties">
  <property name="username" value="test"/>
  <property name="password" value="testok"/>
</properties>
```

<br>

## 프로퍼티 값 참조

프로퍼티 파일에 저장된 값은 **${프로퍼티명}** 형식으로 참조한다.

```xml
<dataSource type="POOLED">
  <property name="driver" value="${driver}"/>
  <property name="url" value="${url}"/>
  <property name="username" value="${username}"/>
  <property name="password" value="${password}"/>
</dataSource>
```

<br>

## \<typeAliases> 엘리먼트

SQL 맵퍼 파일에서 매개변수 타입(parameterType)이나 결과 타입(resultType)을 지정할 때 긴 이름의 클래스명 대신 짧은 이름의 별명을 사용할 수 있다. **typeAliases 엘리먼트는 SQL 맵퍼 파일에서 사용할 별명들을 설정한다.**

```xml
<typeAliases>
  <typeAlias type="spms.vo.Project" alias="project"/>
  <typeAlias type="spms.vo.Member" alias="member"/>
</typeAliases>
```

* **\<typeAlias>**
  * **type 속성** : 패키지 이름을 포함한 클래스 이름
  * **alias 속성** : type에서 지정한 클래스의 별명이다.

<br>

## SQL 맵퍼에서 별명 사용

```xml
<update id="update" parameterType="project">
  ...
</update>

<select id="selectList" resultType="project">
  ...
</select>
```

* \<update>의 parameterType과 \<select>의 **resultType에 지정한 "project"는** spms.vo.Project 클래스를 가리키는 별명이다.

<br>

### *mybatis에 미리 정의된 별명들*

mybatis는 기본 데이터 형이나 랩퍼 클래스에 대해 미리 별명을 정의하였다.

| 별명           | 타입                 | 별명         | 타입                 |
| -------------- | -------------------- | ------------ | -------------------- |
| _byte          | byte                 | byte         | java.lang.Byte       |
| _short         | short                | short        | java.lang.Short      |
| _int, _integer | int                  | int, integer | java.lang.Integer    |
| _long          | long                 | long         | java.lang.Long       |
| _float         | float                | float        | java.lang.Float      |
| _double        | double               | double       | java.lang.Double     |
| _boolean       | boolean              | boolean      | java.lang.Double     |
| string         | java.lang.String     | date         | java.util.Date       |
| decimal        | java.math.BigDecimal | bigdecimal   | java.math.BigDecimal |
| map            | java.util.Map        | hashmap      | java.util.Hashmap    |
| list           | java.util.List       | arraylist    | java.util.ArrayList  |
| collection     | java.util.Collection | iterator     | java.util.iterator   |
| object         | java.lang.Object     |              |                      |

<br>

## \<environments> 엘리먼트

\<environments> 태그는 데이터베이스 환경 정보를 설정할 때 사용하는 태그이다. 이 태그를 이용하면 여러 개의 데이터베이스 접속 정보를 설정할 수 있다.

```xml
<environments default="development">
	<environment id="development"> ... </environment>
  <environment id="test"> ... </environment>
  <environment id="real"> ... </environment>
</environments>
```

* 설정된 DB 정보 중에서 하나를 선택할 때는 default 속성을 사용한다.
* **\<environment>** : 각각의 데이터베이스 접속 정보 정의
  * **id 속성** : \<environment>를 구분할 때 사용할 식별자

<br>

## \<environment> 엘리먼트

environment는 **트랜잭션 관리 및 데이터 소스를** 설정하는 태그이다.

<br>

### *트랜잭션 관리 방식 설정*

**트랜잭션(Transaction)이란,** 여러 개의 데이터 변경 작업(insert, update, delete)을 하나의 작업으로 묶은 것이다.

* **mybatis의 트랜잭션 관리 유형**

  | 트랜잭션 관리 유형 | 설명                                                         |
  | ------------------ | ------------------------------------------------------------ |
  | JDBC               | 직접 JDBC의 커밋(commit), 롤백(rollback) 기능을 사용하여 mybatis 자체에서 트랜잭션을 관리 |
  | MANAGED            | 서버의 트랜잭션 관리 기능을 이용. 즉 Java EE 애플리케이션 서버(JBoss, WebLogic, WebSphere 등)나 서블릿 컨테이너(톰캣 서버 등)에서 트랜잭션을 관리 |

<br>

\<transactionManager>를 사용하여 트랜잭션 관리 유형을 설정한다.

```xml
<transactionManager type="JDBC"/>
```

* type 속성에 트랜잭션 관리 유형(JDBC또는 MANAGED)을 지정한다.

<br>

## 데이터 소스 설정

* **mybatis의 데이터 소스 유형**

  | 데이터 소스 유형 | 설명                                                         |
  | ---------------- | ------------------------------------------------------------ |
  | UNPOOLED         | DB 커넥션을 요청할 때마다 매번 커넥션 객체를 생성한다. 높은 성능을 요구하지 않는 단순한 애플리케이션에 적합하다. |
  | POOLED           | 미리 DB 커넥션 객체를 생성해 두고, 요청하면 즉시 반환한다. 처리 속도가 빠르다. |
  | JDNI             | Java EE 애플리케이션이나 서버나 서플릿 컨테이너에서 제공하는 데이터 소스를 사용한다. |

<br>

```xml
<dataSource type="POOLED">
  <property name="driver" value="${driver}"/>
  <property name="url" value="${url}"/>
  <property name="username" value="${username}"/>
  <property name="password" value="${password}"/>
</dataSource>
```

* 데이터 소스를 설정하려면 \<dataSource> 엘리먼트를 사용해야 한다.
* \<property>의 value 값을 보면 ${}로 되어 있다. 프로퍼티를 가리키는 문법이다. \<properties>에서 설정한 파일로부터 프로퍼티 값을 가져온다.

<br>

## \<mappers> 엘리먼트

\<mappers> 태그는 SQL 맵퍼 파일들의 정보를 설정할 때 사용한다.

* **클래스 경로를 사용하여 맵퍼 파일을 저장할 경우**

  ```xml
  <mappers>
    <mapper resource="spms/dao/MySqlProjectDao.xml"/>
  </mappers>
  ```

* **파일 시스템 경로를 사용할 경우**

  ```xml
  <mappers>
    <mapper url="file:///c:/dao/MySqlProjectDao.xml"/>
  </mappers>
  ```

  > xml 파일이 c:/dao 폴더에 있다고 가정

<br>

## mybatis에서 JDNI 사용하기

* **mybatis-config.xml**

  ```xml
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="JDNI">
        <property name="data_source" value="java:comp/env/jdbc/postgresql"/>
      </dataSource>
    </environment>
  </environments>
  ```

  * \<dataSource>의 type 값을 JDNI로 변경한다.
  * 그 후, data_source의 속성을 'java:comp/env/jdbc/postgresql' 로 지정한다.

* **mybatis에서 DBMS 연결을 관리하지 않기 때문에 db.properties 파일을 참조할 일이 없다.**

  mybatis-config.xml

  ```xml
  <configuration>
  
    <!--    <properties resource="spms/dao/db.properties"/>-->
  
    <typeAliases>
      <typeAlias type="spms.vo.Project" alias="project"/>
      <typeAlias type="spms.vo.Member" alias="member"/>
    </typeAliases>
  ```

# 7.5. 로그 출력 켜기

mybatis는 내부 작업을 추적하고 실행 상태를 파악하기 위해 주요 작업마다 로그를 출력한다.

mybatis의 로그 출력 기능을 켜면 mybatis에서 실행하는 **SQL 문과 매개변수 값, 실행 결과를 실시간으로 확인할 수 있다.**

<br>

## 7.5.1. mybatis 설정 파일에 로그 설정 추가

mybatis의 로그 출력 기능을 켜려면 설정 파일에 그 내용을 추가해야 한다.

* **mybatis-config.xml**

  ```xml
  <configuration>
  
    <settings>
      <setting name="logImpl" value="LOG4J"/>
    </settings>
  
    <typeAliases>
      <typeAlias type="spms.vo.Project" alias="project"/>
      <typeAlias type="spms.vo.Member" alias="member"/>
    </typeAliases>
    ...
  ```

  * **\<setting> 태그를** 사용하여 로그 출력기를 지정한다. mybatis는 여기에서 지정한 도구를 사용하여 로그를 출력한다.

  <br>

  ```xml
  <setting name="logImpl" value="LOG4J"/>
  ```

  * **logImpl** : 로그 출력기를 설정할 때 name 속성
  * **LOG4J** : 로그 출력기 이름

<br>

### 로그 출력 구현체

LOG4J 외에 value에 넣을 수 있는 값들

| value 속성값            | 설명                                             |
| ----------------------- | ------------------------------------------------ |
| SLF4J                   | SLF4J                                            |
| LOG4J                   | Log4j                                            |
| LOG4J2                  | Log4j 2                                          |
| JDK_LOGGING             | JDK logging                                      |
| COMMONS_LOGGING         | Apache Commons Logging                           |
| STDOUT_LOGGING          | 표준 출력 장치로 출력                            |
| NO_LOGGING              | 로그 출력 기능 사용 안 함                        |
| 클래스명(패키지명 포함) | org.apache.ibatis.logging.Log 인터페이스 구현체. |

<br>

## Log4J 라이브러리 파일 준비

<img src="../capture/스크린샷 2019-11-11 오후 3.31.08.png" width=500>

* lib 폴더에 log4j-x.x.x.jar 파일이 있는지 확인한다.

<br>

## Log4J 설정 파일 작성

이 파일에는 **로그의 수준, 출력 방식, 출력 형식, 로그 대상** 등에 대한 정보가 들어간다.

이 파일은 자바 클래스 경로(CLASSPATH)에 두어야 한다.

* **src/log4j.properties**

  ```properties
  log4j.rootLogger=ERROR, stdout
  
  log4j.logger.spms.dao=DEBUG
  
  log4j.appender.stdout=org.apache.log4j.ConsoleAppender
  log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
  log4j.appender.stdout.layout.ConversionPattern=%5p [%t] - %m%n
  ```

<br>

### *로그의 출력 등급 설정*

```properties
log4j.rootLogger=ERROR, stdout
```

* 루트 로거의 출력 등급(Level)을 ERROR로 설정한다.
  * **루트 로거(rootLogger)** : 최상위 로거
  * 하위 로거들은 항상 부모의 출력 등급을 상속받는다.
  * 하위 로거들의 등급을 별도로 설정하지 않으면 출력 등급과 같은 ERROR가 된다.

<br>

**출력 등급에 대한 상속 관계**

* **rootLogger=ERROR**
  * spms = **ERROR** (상속 받음)
  * spms.controls = **INFO**
    * spms.controls.project = **INFO** (상속 받음)
  * spms.util = **ERROR** (상속 받음)
  * spms.dao = **DEBUG**

<br>

**로그 출력 등급표**

| 로그 출력 등급 | 설명                                                        |
| -------------- | ----------------------------------------------------------- |
| FATAL          | 애플리케이션을 중지해야 할 심각한 오류                      |
| ERROR          | 오류가 발생했지만, 애플리케이션은 계속 실행할 수 있는 상태  |
| WARN           | 잠재적인 위험을 안고 있는 상태                              |
| INFO           | 애플리케이션의 주요 실행 정보                               |
| DEBUG          | 애플리케이션의 내부 실행 상황을 추적해 볼 수 있는 상세 정보 |
| TRACE          | 디버그보다 더 상세한 정보                                   |

* 로그 출력 등급표에서 **아래의 등급은 위의 등급을 포함한다.**

<br>

### *출력 담당자 선언*

```properties
log4j.rootLogger=ERROR, stdout
```

* **stdout** : 출력 담당자
* 출력 담당자가 여럿일 때 담당자의 이름도 그 수만큼 지정한다.

<br>

### *출력 담당자의 유형을 결정*

로그를 어디로 출력할지 설정한다. 기본 출력 장치인 모니터로 출력할 수 있고, 파일로 출력할 수 있다. 또한 네트워크를 이용하여 원격의 서버로 출력할 수 있다.

* **설정 문법**

  ```properties
  log4j.appender.이름=출력 담당자(패키지명 포함한 클래스명)
  ```

<br>

**주요 로그 출력자 클래스들**

| 출력 담당자 클래스                  | 설명                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| org.apache.log4j.ConsoleAppender    | System.out 또는 System.err 로 로그를 출력한다.               |
| org.apache.log4j.FileAppender       | 파일로 로그를 출력한다.                                      |
| org.apache.log4j.net.SocketAppender | 원격의 로그 서버에 로그 정보를 담은 LoggingEvent 객체를 보낸다. |

<br>

### *로그의 출력 형식 정의*

간단히 문자열을 출력할 수 있고, XML 형식으로 출력할 수 있다. 또는 HTML 테이블 형식이나 특정 패턴을 가진 문자열을 출력할 수 있다.

* **문법**

  ```properties
  log4j.appender.이름.layout=출력형식 클래스(패키지명을 포함한 클래스명)
  ```

<br>

**출력 형식 클래스**

| 출력 형식 클래스               | 설명                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| org.apache.log4j.SimpleLayout  | 출력형식은 '출력 등록 - 메시지' 이다.                        |
| org.apache.log4j.HTMLLayout    | HTML 테이블 형식으로 출력한다.                               |
| org.apache.log4j.PatternLayout | 변환 패턴의 형식에 따라 로그를 출력한다.<br />변환 패턴 예)<br />%-5p [%t]: %m%n<br />출력 결과 예)<br />DEBUG [main]: Message 1<br />WARN [main]: Message 2 |
| org.apache.log4j.xml.XMLLayout | log4j.dtd 규칙에 따라 XML을 만들어 출력한다.                 |

<br>

### *PatternLayout의 패턴 정의*

```properties
log4j.appender.stdout.layout.ConversionPattern=%5p [%t] - %m%n
```

* **%5p** : 로그 출력 등급을 5자리 문자열로 출력
* **%t** : 스레드 이름 출력
* **%m** : 로그 내용 출력

<br>

### *특정 패키지의 클래스에 대해 로그의 출력 등급 설정하기*

루트 로거에서 정의한 등급보다 다른 등급의 로그를 출력하고 싶다면, 하위 로거에 대해 별도의 등급을 지정하면 된다.

```properties
log4j.logger.spms.dao=DEBUG
```

* **log4j.logger** : 하위 로거를 정의할 때 프로퍼티 이름 앞에 붙이는 접두어
* **spms.dao** : 패키지 이름
* **DEBUG** : 패키지에 소속된 클래스에 대해 로그 출력 등급을 DEBUG로 설정

<br>

## 7.5.2. 로그 출력 테스트

* **이클립스 콘솔창에 출력된 로그의 일부분**

  * /project/list.do 

    ```
    DEBUG [http-nio-8080-exec-9] - ==>  Preparing: select PNO, PNAME, STA_DATE, END_DATE, STATE from PROJECTS order by PNO desc 
    DEBUG [http-nio-8080-exec-9] - ==> Parameters: 
    DEBUG [http-nio-8080-exec-9] - <==      Total: 1
    ```

  * /project/update.do?no=6

    ```
    DEBUG [http-nio-8080-exec-2] - ==>  Preparing: select PNO, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS from PROJECTS where PNO=? 
    DEBUG [http-nio-8080-exec-2] - ==> Parameters: 6(Integer)
    DEBUG [http-nio-8080-exec-2] - <==      Total: 1
    ```

<br>

## 로그 출력 등급 'TRACE'

DEBUG 보다 더 자세한 내용을 보고 싶다면 로그 출력 등급을 'TRACE'로 올리면 된다.

* **src/log4j.properties**

  ```properties
  # 하위 로거의 출력 등급 설정
  log4j.logger.spms.dao=TRACE
  ```

* **/project/list.do 실행시 콘솔창**

  ```
  DEBUG [http-nio-8080-exec-7] - ==>  Preparing: select PNO, PNAME, STA_DATE, END_DATE, STATE from PROJECTS order by PNO desc 
  DEBUG [http-nio-8080-exec-7] - ==> Parameters: 
  TRACE [http-nio-8080-exec-7] - <==    Columns: pno, pname, sta_date, end_date, state
  TRACE [http-nio-8080-exec-7] - <==        Row: 6, ???, 2019-01-01 00:00:00, 2019-02-03 00:00:00, 0
  DEBUG [http-nio-8080-exec-7] - <==      Total: 1
  ```

<br>

# 7.6. 동적 SQL의 사용

SQL 문을 변경해야 할 때 동적으로 SQL을 사용해야 한다.

* **src/spms/dao/PostgresSqlProjectDao.xml**

  ```xml
  <select id="selectList" resultMap="projectResultMap">
    select PNO, PNAME, STA_DATE, END_DATE, STATE
    from PROJECTS
    order by PNO desc
  </select>
  ```

  * 만약 프로젝트 제목이나 시작일, 종료일, 상태에 대해서도 정렬하려면 여러 개의 SQL 문을 준비해야 한다.
  * 이런 경우 mybatis에서 제공하는 **'동적 SQL(dynamic SQL)' 기능을 이용하면 하나의 SQL 문으로 여러 상황에 대처할 수 있다.**

<br>

## 7.6.1. 동적 SQL 엘리먼트

mybatis는 동적 SQL을 위한 엘리먼트를 제공한다. 이 엘리먼트들은 **JSTL 코어 라이브러리에 정의된 태그들과 비슷하다.**

* **동적 SQL을 작성할 때 사용하는 엘리먼트들**

  * **\<if> 태그**

    ```xml
    <if test="조건">SQL 문</if>
    ```

    * 이 태그는 어떤 값의 상태를 검사하여 참일 경우에만 SQL 문을 포함하고 싶을 때 사용한다.

  * **\<choose> 태그**

    ```xml
    <choose>
      <when test="조건1">SQL 문</when>
      <when test="조건2">SQL 문</when>
      <otherwise>SQL 문</otherwise>
    </choose>
    ```

    * 이 태그는 검사할 조건이 여러 개일 경우에 사용한다.

  * **\<where> 태그**

    ```xml
    <where>
      <if test="조건1">SQL 문</if>
      <if test="조건2">SQL 문</if>
    </where>
    ```

    * 이 태그는 where 절을 반환한다.

  * **\<trim> 태그**

    ```xml
    <trim prefix="단어" prefixOverride="문자열|문자열">
      <if test="조건1">SQL 문</if>
      <if test="조건2">SQL 문</if>
    </trim>
    ```

    * 이 태그는 특정 단어로 시작하는 SQL 문을 반환하고 싶을 때 사용한다.

  * **\<set> 태그**

    ```xml
    <set>
      <if test="조건1">SQL 문</if>
      <if test="조건2">SQL 문</if>
    </set>
    ```

    * 이 태그는 UPDATE 문의 SET 절을 만들 때 사용한다.

  * **\<foreach> 태그**

    ```xml
    <foreach
             item="항목"
             index="인덱스"
             collection="목록"
             open="시작문자열"
             close="종료문자열"
             separator="구분자">
    </foreach>
    ```

    * 이 태그는 목록의 값을 가지고 SQL 문을 만들 때 사용한다.

  * **\<bind> 태그**

    ```xml
    <bind name="변수명" value="값"/>
    ```

    * 이 태그는 변수를 생성할 때 사용한다.

<br>

## \<choose> 엘리먼트의 활용

프로젝트 목록을 정렬하기 위해 \<choose>를 사용하여 동적 SQL을 구성해보자.

* **현재 프로젝트 목록 출력 SELECT 문**

  ```sql
  select PNO, PNAME, STA_DATE, END_DATE, STATE
  from PROJECTS
  order by PNO desc
  ```

<br>

**프로젝트 목록의 정렬 조건**

| 정렬 조건              | 'orderCond' 매개변수 값 | ORDER BY 절            |
| ---------------------- | ----------------------- | ---------------------- |
| 프로젝트 제목 오름차순 | 'TITLE_ASC'             | order by PNAME asc     |
| 프로젝트 제목 내림차순 | 'TITLE_DESC'            | order by PNAME desc    |
| 시작일 오름차순        | 'STARTDATE_ASC'         | order by STA_DATE asc  |
| 시작일 내림차순        | 'STARTDATE_DESC'        | order by STA_DATE desc |
| 종료일 오름차순        | 'ENDDATE_ASC'           | order by END_DATE asc  |
| 종료일 내림차순        | 'ENDDATE_DESC'          | order by END_DATE desc |
| 상태(준비 -> 종료)     | 'STATE_ASC'             | order by STATE asc     |
| 상태(종료 -> 준비)     | 'STATE_DESC'            | order by STATE desc    |

* **'orderCond'** : mybatis에 넘기는 매개변수이다. 매개변수의 값에 따라 OREDR BY 절이 달라진다.

<br>

프로젝트 목록을 가져오는 SQL 문을 편집해보자.

* **src/spms/dao/PostgresSqlProjectDao.xml**

  ```xml
  <select id="selectList" parameterType="map" resultMap="projectResultMap">
    select PNO, PNAME, STA_DATE, END_DATE, STATE
    from PROJECTS
    order by
    <choose>
      <when test="orderCond == 'TITLE_ASC'">PNAME asc</when>
      <when test="orderCond == 'TITLE_DESC'">PNAME desc</when>
      <when test="orderCond == 'STARTDATE_ASC'">STA_DATE asc</when>
      <when test="orderCond == 'STARTDATE_DESC'">STA_DATE desc</when>
      <when test="orderCond == 'ENDDATE_ASC'">END_DATE asc</when>
      <when test="orderCond == 'ENDDATE_DESC'">END_DATE desc</when>
      <when test="orderCond == 'STATE_ASC'">STATE asc</when>
      <when test="orderCond == 'STATE_DESC'">STATE desc</when>
      <when test="orderCond == 'PNO_ASC'">PNO asc</when>
      <otherwise>PNO desc</otherwise>
    </choose>
  </select>
  ```

  * 매개변수를 받기 위해 \<select> 태그에 **parameterType 속성을 추가했다.** orderCond의 값은 매개변수 객체 **"map"에** 들어 있다.

    ```xml
    <select id="selectList" parameterType="map" resultMap="projectResultMap">
    ```

<br>

### *ORDER BY 절에 \<choose> 적용*

매개변수 객체 들어 있는 orderCond 값을 검사하는 \<choose> 태그를 추가한다. \<when> 태그의 조건과 일치하는 경우 \<when>에 정의된 SQL 조각을 반환한다.

```xml
<choose>
  <when test="orderCond == 'TITLE_ASC'">PNAME asc</when>
  <when test="orderCond == 'TITLE_DESC'">PNAME desc</when>
  ...
</choose>
```

<br>

### *test 속성*

test 속성에는 조건을 검사하는 **OGNL 기반 표현식이** 온다. 표현식의 결과가 참인지 거짓인지 검사하여 **참일 경우 \<when> 태그의 내용을 반환한다.**

* **OGNL(Open-Graph Navigation Language)** : 자바 객체의 프로퍼티 값을 좀 더 쉽게 꺼내고 할당하기 쉽도록 만든 표현식 언어이다.

<br>

### *\<otherwise> 엘리먼트*

이 태그는 일치하는 조건이 없을 경우 수행된다.

```xml
<otherwise>PNO desc</otherwise>
```

<br>

## 7.6.2. 프로젝트 목록 페이지에 정렬 링크 추가

프로젝트 목록에서 정렬을 바꿀 수 있게 각 칼럼의 헤더에 정렬 링크를 추가해보자.

* **web/project/ProjectList.jsp**

  ```jsp
  <%--
    Created by IntelliJ IDEA.
    User: sangminlee
      Date: 2019/10/15
        Time: 11:09 오후
          To change this template use File | Settings | File Templates.
          --%>
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
          <th><c:choose>
            <c:when test="${orderCond == 'PNO_ASC'}">
              <a href="list.do?orderCond=PNO_DESC">번호▲</a>
            </c:when>
            <c:when test="${orderCond == 'PNO_DESC'}">
              <a href="list.do?orderCond=PNO_ASC">번호▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?orderCond=PNO_ASC">번호</a>
            </c:otherwise>
            </c:choose></th>
          <th><c:choose>
            <c:when test="${orderCond == 'TITLE_ASC'}">
              <a href="list.do?orderCond=TITLE_DESC">제목▲</a>
            </c:when>
            <c:when test="${orderCond == 'TITLE_DESC'}">
              <a href="list.do?orderCond=TITLE_ASC">제목▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?orderCond=TITLE_ASC">제목</a>
            </c:otherwise>
            </c:choose></th>
          <th><c:choose>
            <c:when test="${orderCond == 'STARTDATE_ASC'}">
              <a href="list.do?orderCond=STARTDATE_DESC">시작일▲</a>
            </c:when>
            <c:when test="${orderCond == 'STARTDATE_DESC'}">
              <a href="list.do?orderCond=STARTDATE_ASC">시작일▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?orderCond=STARTDATE_ASC">시작일</a>
            </c:otherwise>
            </c:choose></th>
          <th><c:choose>
            <c:when test="${orderCond == 'ENDDATE_ASC'}">
              <a href="list.do?orderCond=ENDDATE_DESC">종료일▲</a>
            </c:when>
            <c:when test="${orderCond == 'ENDDATE_DESC'}">
              <a href="list.do?orderCond=ENDDATE_ASC">종료일▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?orderCond=ENDDATE_ASC">종료일</a>
            </c:otherwise>
            </c:choose></th>
          <th><c:choose>
            <c:when test="${orderCond == 'STATE_ASC'}">
              <a href="list.do?orderCond=STATE_DESC">상태▲</a>
            </c:when>
            <c:when test="${orderCond == 'STATE_DESC'}">
              <a href="list.do?orderCond=STATE_ASC">상태▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?oderCond=STATE_ASC">상태</a>
            </c:otherwise>
            </c:choose></th>
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

### *이전의 칼럼 헤더*

```jsp
<tr>
  <th>번호</th>
  <th>제목</th>
  <th>시작일</th>
  <th>종료일</th>
  <th>상태</th>
  <th></th>
</tr>
```

<br>

### *정렬 링크가 걸린 칼럼 헤더*

```jsp
<th><c:choose>
  <c:when test="${orderCond == 'PNO_ASC'}">
    <a href="list.do?orderCond=PNO_DESC">번호▲</a>
  </c:when>
  <c:when test="${orderCond == 'PNO_DESC'}">
    <a href="list.do?orderCond=PNO_ASC">번호▼</a>
  </c:when>
  <c:otherwise>
    <a href="list.do?orderCond=PNO_ASC">번호</a>
  </c:otherwise>
  </c:choose></th>
<th><c:choose>
```

* **TITLE_ASC** : 오름차순 정렬 요청
* **TITLE_DESC** : 내림차순 정렬 요청
* 정렬 요청이 없으면 오름차순으로 정렬

<br>

## 7.6.3. 프로젝트 목록 컨트롤러 변경

* **src/spms/controls/project/ProjectListController.java**

  ```java
  @Component("/project/list.do")
  public class ProjectListController implements Controller, DataBinding {
  
    PostgresSqlProjectDao projectDao;
  
    public ProjectListController setMemberDao(PostgresSqlProjectDao projectDao) {
      this.projectDao = projectDao;
      return this;
    }
  
    @Override
    public String execute(Map<String, Object> model) throws Exception {
      HashMap<String, Object> paramMap = new HashMap<>();
      paramMap.put("orderCond", model.get("orderCond"));
      model.put("projects", projectDao.selectList(paramMap));
      return "/project/ProjectList.jsp";
    }
  
    @Override
    public Object[] getDataBinders() {
      return new Object[]{
          "orderCond", String.class
      };
    }
    
  }
  ```

<br>

### *DataBinding의 구현*

클라이언트가 보낸 'orderCond' 값을 받으려면 페이지 컨트롤러는 MVC 구조에 따라 DataBinding 인터페이스를 구현해야 한다.

```java
public class ProjectListController implements Controller, DataBinding {
```

<br>

getDataBinders() 메서드에서 받기를 원하는 매개변수 이름과 데이터형을 선언한다.

```java
public Object[] getDataBinders() {
  return new Object[] {
    "orderCond", String.class
  };
}
```

<br>

### *DAO에게 정렬 조건 전달*

전달할 정렬 조건이 필요하기 때문에, excute() 에서 projectDao의 selectList()를 호출하기 전에 **매개변수로 전달할 객체를 먼저 준비한다.** 매개변수 객체에는 SQL 맵퍼에서 사용할 정렬 조건이 들어가야 한다.

```java
HashMap<String, Object> paramMap = new HashMap<String, Object>();
paramMap.put("orderCond", model.get("orderCond"));
model.put("projects", projectDao.selectList(paramMap));
```

* 'orderCond' 라는 이름으로 정렬 조건을 저장한 뒤, projectDao의 selectList()를 호출할 때 이 매개변수 객체를 전달한다.

<br>

## 7.6.4. ProjectDao 인터페이스 변경

selectList()에서 매개변수 값을 받을 수 있게 인터페이스를 변경해보자.

* **src/spms/dao/projectDao.java**

  ```java
  public interface ProjectDao {
  
    // selectList 메소드에서 HashMap 객체를 매개변수로 받는다.
    List<Project> selectList(HashMap<String, Object> paramMap) throws Exception;
    int insert(Project project) throws Exception;
    Project selectOne(int no) throws Exception;
    int update(Project project) throws Exception;
    int delete(int no) throws Exception;  
  
  }
  ```

<br>

### *PostgresSqlProjectDao 클래스 변경*

* **src/spms/dao/PostgresSqlProjectDao.java**

  ```java
  @Component("projectDao")
  public class PostgresSqlProjectDao implements ProjectDao {
  
    private SqlSessionFactory sqlSessionFactory;
  
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
      this.sqlSessionFactory = sqlSessionFactory;
    }
  
    @Override
    public List<Project> selectList(HashMap<String, Object> paramMap) throws Exception {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        return sqlSession.selectList("spms.dao.ProjectDao.selectList", paramMap);
      }
    }
  ```

  * selectList()를 호출할 때 넘겨준 매개변수 객체는 SQL 맵퍼에 있는 SQL 문을 실행할 때 사용된다.

<br>

## 7.6.5. \<set> 엘리먼트의 활용

프로젝트 정보를 변경하는 기능에 대해서도 동적 SQL을 적용해보자.

* **현재 SQL**

  ```sql
  update PROJECTS set
  	PNAME=#{title},
  	CONTENT=#{content},
  	STA_DATE=#{startDate},
  	END_DATE=#{endDate},
  	STATE=#{state},
  	TAGS=#{tags}
  where PNO=#{no}
  ```

위의 SQL에서 한 개의 칼럼만 변경하는 경우(6가지)에서 모든 컬럼을 변경하는 경우(1가지)까지 계산해 보면 총 63가지가 나오기 때문에, 이것은 개발을 더욱 복잡하게 만든다.

바로 이런 상황일 때 동적 SQL을 사용한다.

<br>

* **src/spms/dao/PostgresSqlProjectDao.xml**

  ```xml
  <update id="update" parameterType="map">
    update PROJECTS
    <set>
      <if test="title != null">PNAME=#{title},</if>
      <if test="content != null">CONTENT=#{content},</if>
      <if test="startDate != null">STA_DATE=#{startDate},</if>
      <if test="endDate != null">END_DATE=#{endDate},</if>
      <if test="state != null">STATE=#{state},</if> 
      <if test="tags != null">TAGS=#{tags}</if>
    </set> 
    where PNO=#{no}
  </update>
  ```

  * 앞으로 변경할 값을 Map 객체에 담아야 하므로, **parameterType을 map으로 바꾼다.** 

<br>

### *\<set> 엘리먼트*

\<set> 태그는 SET 절을 만든다. **test의 값이 참이면 \<if>의 콘텐츠를 반환한다.**

<br>

## 7.6.6. PostgresSqlProjectDao 클래스 변경

* **src/spms/dao/PostgresSqlProjectDao.java**

  ```java
  @Override
  public int update(Project project) throws Exception {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Project original = sqlSession.selectOne("spms.dao.ProjectDao.selectOne",
                                              project.getNo());
  
      Hashtable<String, Object> paramMap = new Hashtable<>();
  
      if (!project.getTitle().equals(original.getTitle())) {
        paramMap.put("title", project.getTitle());
      }
      if (!project.getContent().equals(original.getContent())) {
        paramMap.put("content", project.getContent());
      }
      if (project.getStartDate().compareTo(original.getStartDate()) != 0) {
        paramMap.put("startDate", project.getStartDate());
      }
      if (project.getEndDate().compareTo(original.getEndDate()) != 0) {
        paramMap.put("endDate", project.getEndDate());
      }
      if (project.getState() != original.getState()) {
        paramMap.put("state", project.getState());
      }
      if (!project.getTags().equals(original.getTags())) {
        paramMap.put("tags", project.getTags());
      }
  
      if (paramMap.size() > 0) {
        paramMap.put("no", project.getNo());
        int count = sqlSession.update("spms.dao.ProjectDao.update", paramMap);
  
        sqlSession.commit();
        return count;
      } else {
        return 0;
      }
    }
  }
  ```

  * 먼저 프로젝트 정보를 가져온다.

    ```java
    Project original = sqlSession.selectOne(
    	"spms.dao.ProjectDao.selectOne", project.getNo());
    ```

  * UPDATE 문에 전달할 Map 객체를 준비한다.

    ```java
    Hashtable<String, Object> paramMap = new Hashtable<String, Object>();
    ```

  * Map 객체에 저장된 값이 있다면, UPDATE 문을 실행한다. 없다면 0을 반환한다.

    ```java
    if (paramMap.size() > 0) {
      paramMap.put("no", project.getNo());
      int count = sqlSession.update("spms.dao.ProjectDao.update", paramMap);
      sqlSession.commit();
      return count;
    } else {
      return 0;
    }
    ```

<br>

# 7.7. 실력 향상 훈련

## 7.7.1. 훈련. 회원 관리에 mybatis 적용

### 1) PostgresSqlMemberDao 클래스로부터 SQL문 분리

1. spms.dao 패키지에 PostgresSqlMemberDao.xml 파일을 생성한다.
2. PostgresSqlMemberDao 클래스에서 SQL을 분리하여 SQL 맵퍼 파일에 등록한다.
   * 이름과 이메일 생성일에 대해 정렬이 가능해야 한다.

* **src/spms/dao/PostgresSqlMemberDao.xml**

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="spms.dao.MemberDao">
  
    <resultMap id="memberResultMap" type="member">
      <id column="MNO" property="no"/>
      <result column="MNAME" property="name"/>
      <result column="EMAIL" property="email"/>
      <result column="PWD" property="password"/>
      <result column="CRE_DATE" property="createDate" javaType="java.sql.Date"/>
      <result column="MOD_DATE" property="modifiedDate" javaType="java.sql.Date"/>
    </resultMap>
  
    <select id="selectList" parameterType="map" resultMap="memberResultMap">
      select MNO, MNAME, EMAIL, CRE_DATE
      from MEMBERS
      order by
      <choose>
        <when test="orderCond == 'NAME_ASC'">MNAME asc</when>
        <when test="orderCond == 'NAME_DESC'">MNAME desc</when>
        <when test="orderCond == 'EMAIL_ASC'">EMAIL asc</when>
        <when test="orderCond == 'EMAIL_DESC'">EMAIL desc</when>
        <when test="orderCond == 'CREDATE_ASC'">CRE_DATE asc</when>
        <when test="orderCond == 'CREDATE_DESC'">CRE_DATE desc</when>
        <when test="orderCond == 'MNO_ASC'">MNO asc</when>
        <otherwise>MNO desc</otherwise>
      </choose>
    </select>
  
    <insert id="insert" parameterType="member">
      insert into MEMBERS(MNAME, EMAIL, PWD, CRE_DATE, MOD_DATE)
      values (#{name}, #{email}, #{password}, now(), now())
    </insert>
  
    <select id="selectOne" parameterType="int" resultMap="memberResultMap">
      select MNO, MNAME, EMAIL, CRE_DATE, MOD_DATE
      from MEMBERS
      where MNO=#{value}
    </select>
  
    <update id="update" parameterType="map">
      update MEMBERS
      <set>
        <if test="name != null">MNAME=#{name},</if>
        <if test="email != null">EMAIL=#{email},</if>
        MOD_DATE=now()
      </set>
      where MNO=#{no}
    </update>
  
    <delete id="delete" parameterType="int">
      delete from MEMBERS
      where MNO=#{value}
    </delete>
  
    <select id="exist" parameterType="map" resultMap="memberResultMap">
      select MNO, MNAME, EMAIL, CRE_DATE, MOD_DATE
      from MEMBERS
      where EMAIL=#{email} and PWD=#{password}
    </select>
  </mapper>
  ```

<br>

### 2) MemberDao 변경

정렬 항목에 따라 SELECT 문의 ORDER BY 절이 바뀌어야 한다.

spms.dao.MemberDao 인터페이스에 대해 selectList() 메서드를 다음과 같이 변경한다.

```java
List<Member> selectList(HashMap<String, Object> paramMap) throws Exception;
```

* **src/spms/dao/MemberDao.java**

  ```java
  public interface MemberDao {
  
    List<Member> selectList(HashMap<String, Object> paramMap) throws Exception;
    int insert(Member member) throws Exception;
    int delete(int no) throws Exception;
    Member selectOne(int no) throws Exception;
    int update(Member member) throws Exception;
    Member exist(String email, String password) throws Exception;
  
  }
  ```

<br>

### 3) PostgresSqlMemberDao에서 SqlSession 객체를 사용하여 데이터 처리

mybatis에서 제공하는 SqlSession 객체를 사용하여 멤버의 등록, 조회, 변경, 삭제를 처리

* **src/spms/dao/PostgresSqlMemberDao.java**

  ```java
  @Component("memberDao")
  public class PostgresSqlMemberDao implements MemberDao {
  
    SqlSessionFactory sqlSessionFactory;
  
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
      this.sqlSessionFactory = sqlSessionFactory;
    }
  
    public List<Member> selectList(HashMap<String, Object> paramMap) {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        return sqlSession.selectList("spms.dao.MemberDao.selectList", paramMap);
      }
    }
  
    public int insert(Member member) {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        int count = sqlSession.insert("spms.dao.MemberDao.insert", member);
        sqlSession.commit();
        return count;
      }
    }
  
    public Member selectOne(int no) {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        return sqlSession.selectOne("spms.dao.MemberDao.selectOne", no);
      }
    }
  
    public int update(Member member) {
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        Member original = sqlSession.selectOne("spms.dao.MemberDao.selectOne", member.getNo());
  
        Hashtable<String, Object> paramMap = new Hashtable<>();
        if (!member.getName().equals(original.getName()))
          paramMap.put("name", member.getName());
        if (!member.getEmail().equals(original.getEmail())) {
          paramMap.put("email", member.getEmail());
        }
        if (paramMap.size() > 0) {
          paramMap.put("no", member.getNo());
          int count = sqlSession.update("spms.dao.MemberDao.update", paramMap);
  
          sqlSession.commit();
          return count;
        } else {
          return 0;
        }
      }
    }
  
    public int delete(int no) {
      try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
        int count = sqlSession.delete("spms.dao.MemberDao.delete", no);
        sqlSession.commit();
        return count;
      }
    }
  
    public Member exist(String email, String password) {
      HashMap<String, String> paramMap = new HashMap<>();
      paramMap.put("email", email);
      paramMap.put("password", password);
  
      try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
        return sqlSession.selectOne("spms.dao.MemberDao.exist", paramMap);
      }
    }
  
  }
  ```

<br>

### 4) mybatis 설정 파일에 회원 관리 SQL 맵퍼 파일의 경로 추가

src/spms/dao/mybatis-config.xml 파일에 SQL 맵퍼 파일 'PostgresSqlMemberDao.xml' 의 경로 추가

* **src/spms/dao/mybatis-config.xml**

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  
    <!--    <properties resource="spms/dao/db.properties"/>-->
    <settings>
      <setting name="logImpl" value="LOG4J"/>
    </settings>
  
    <typeAliases>
      <typeAlias type="spms.vo.Project" alias="project"/>
      <typeAlias type="spms.vo.Member" alias="member"/>
    </typeAliases>
  
    <environments default="development">
      <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="JNDI">
          <property name="data_source" value="java:comp/env/jdbc/postgresql"/>
        </dataSource>
      </environment>
    </environments>
  
    <mappers>
      <mapper resource="spms/dao/PostgresSqlProjectDao.xml"/>  
      <mapper resource="spms/dao/PostgresSqlMemberDao.xml"/>
    </mappers>
  
  </configuration>
  ```

<br>

### 5) 회원 목록 컨트롤러 변경

spms.controls.MemberListController 클래스를 정렬 조건을 처리하도록 변경한다.

* **src/spms/controls/MemberListController.java**

  ```java
  @Component("/member/list.do")
  public class MemberListController implements Controller, DataBinding {
  
    MemberDao memberDao;
  
    public MemberListController setMemberDao(MemberDao memberDao) {
      this.memberDao = memberDao;
      return this;
    }
  
    @Override
    public String execute(Map<String, Object> model) throws Exception {
      HashMap<String, Object> paramMap = new HashMap<>();
      paramMap.put("orderCond", model.get("orderCond"));
      model.put("members", memberDao.selectList(paramMap));
      return "/member/MemberList.jsp";
    }
  
    @Override
    public Object[] getDataBinders() {
      return new Object[]{
        "orderCond", String.class
          };
    }
  }
  ```

<br>

### 6) 회원 목록 페이지의 JSP 변경

멤버 목록 페이지를 테이블 형태로 출력할 수 있도록 web/member/MemberList.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>회원 목록</title>
  </head>
  <body>
    <jsp:include page="/Header.jsp"/>
    <h1>회원 목록</h1>
    <p><a href="add.do">신규 회원</a></p>
    <table border="1">
      <tr>
        <th>
          <c:choose>
            <c:when test="${orderCond == 'MNO_ASC'}">
              <a href="list.do?orderCond=MNO_DESC">번호▲</a>
            </c:when>
            <c:when test="${orderCond == 'MNO_DESC'}">
              <a href="list.do?orderCond=MNO_ASC">번호▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?orderCond=MNO_ASC">번호</a>
            </c:otherwise>
          </c:choose>
        </th>
        <th>
          <c:choose>
            <c:when test="${orderCond == 'NAME_ASC'}">
              <a href="list.do?orderCond=NAME_DESC">이름▲</a>
            </c:when>
            <c:when test="${orderCond == 'NAME_DESC'}">
              <a href="list.do?orderCond=NAME_ASC">이름▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?orderCond=NAME_ASC">이름</a>
            </c:otherwise>
          </c:choose>
        </th>
        <th>
          <c:choose>
            <c:when test="${orderCond == 'EMAIL_ASC'}">
              <a href="list.do?orderCond=EMAIL_DESC">이메일▲</a>
            </c:when>
            <c:when test="${orderCond == 'EMAIL_DESC'}">
              <a href="list.do?orderCond=EMAIL_ASC">이메일▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?orderCond=EMAIL_ASC">이메일</a>
            </c:otherwise>
          </c:choose>
        </th>
        <th>
          <c:choose>
            <c:when test="${orderCond == 'CREDATE_ASC'}">
              <a href="list.do?orderCond=CREDATE_DESC">등록일▲</a>
            </c:when>
            <c:when test="${orderCond == 'CREDATE_DESC'}">
              <a href="list.do?orderCond=CREDATE_ASC">등록일▼</a>
            </c:when>
            <c:otherwise>
              <a href="list.do?orderCond=CREDATE_ASC">등록일</a>
            </c:otherwise>
          </c:choose>
        </th>
      </tr>
      <c:forEach var="member" items="${members}">
        <tr>
          <td>${member.no}</td>
          <td><a href="update.do?no=${member.no}">${member.name}</a></td>
          <td>${member.email}</td>
          <td>${member.createDate}</td>
          <td><a href="delete.do?no=${member.no}">[삭제]</a></td>
        </tr>
      </c:forEach>
    </table>
    <jsp:include page="/Tail.jsp"/>
  </body>
</html>
```