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

