# 최종 코드 분석

* src
  * annotation
    * Component (@interface)
  * bind
    * DataBinding (interface)
    * ServletRequestDataBinder (class)
  * context
    * ApplicationContext (class)
  * controls
    * project
      * ProjectAddController (class)
      * ProjectDeleteController (class)
      * ProjectListController (class)
      * ProjectUpdateController (class)
    * Controller (interface)
    * LogInController (class)
    * LogOutController (class)
    * MemberAddController (class)
    * MemberDeleteController (class)
    * MemberListController (class)
    * MemberUpdateController (class)
  * dao
    * db.properties (properties)
    * MemberDao (interface)
    * mybatis-config (XML)
    * PostgresSqlMemberDao (class)
    * PostgresSqlMemberDao (XML)
    * PostgresSqlProjectDao (class)
    * PostgresSqlProjectDao (XML)
    * ProjectDao (interface)
  * listeners
    * ContextLoaderListener (class)
  * servlets
    * DispatcherServlet (class)
  * vo
    * Member (class)
    * Project (class)

<br>

* web
  * auth
    * LogInFail.jsp
    * LogInForm.jsp
  * member
    * MemberAdd.jsp
    * MemberList.jsp
    * MemberUpdate.jsp
  * project
    * ProjectForm.jsp
    * ProjectList.jsp
    * ProjectUpdateForm.jsp
  * WEB-INF
    * web.xml
  * Error.jsp
  * Header.jsp
  * Tail.jsp

<br>

# Model & Controller

## Component (@interface)

* Component 라는 애노테이션 정의한다.
* 애노테이션을 통해 클래스나 필드, 메서드에 대해 부가 정보를 등록할 수 있다.

```java
package spms.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Retention : 애노테이션 유지 정책.
//   애노테이션 정보를 언제까지 유지할 것인지 설정하는 문법
// RetentionPolicy.RUNTIME : 클래스 파일에 기록됨. 실행 중에
//   기록된 애노테이션 값을 참조할 수 있다.
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
  String value() default "";    // 객체 이름을 저장하는 용도로 사용
}
```

<br>

## DataBinding (interface)

프런트 컨트롤러가 페이지 컨트롤러를 호출할 때 VO 객체를 준비하는 인터페이스

```java
package spms.bind;

public interface DataBinding {

  // 이 메소드의 반환 값은 데이터의 이름과 타입 정보를 담은 Object 배열이다.
  Object[] getDataBinders();

}
```

<br>

## ServletRequestDataBinder (class)

요청 매개변수의 값과 데이터 이름, 데이터 타입을 받아서 데이터 객체를 만드는 클래스

```java
package spms.bind;

import javax.servlet.ServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

public class ServletRequestDataBinder {

  // 데이터 객체를 만드는 메서드
  public static Object bind(ServletRequest request, Class<?> dataType, String dataName)
    throws Exception {
    // 데이터 타입이 기본 타입인지 확인
    if (isPrimitiveType(dataType)) {
      // 기본 타입 객체 생성
      return createValueObject(dataType, request.getParameter(dataName));
    }

    // 기본 타입이 아닌 경우 요청 매개 변수의 이름과 일치하는 셋터 메서드 호출
    // request.getParameterMap() : 매개변수의 이름과 값을 맵 객체에 담아서 반환.
    Set<String> paramNames = request.getParameterMap().keySet();
    // newInstance 를 통해 해당 클래스의 인스턴스 생
    Object dataObject = dataType.newInstance();
    Method m;
    
    for (String paramName : paramNames) {
      // 데이터 타입 클래스에서 매개변수 이름과 일치하는 프로퍼티(셋터 메서드)를 찾음.
      m = findSetter(dataType, paramName);
      if (m != null) {
        // 이전에 생성한 dataObject에 대해 찾은 셋터 메서드를 호출
        m.invoke(dataObject,
            // 셋터 메서드를 호출할 때 요청 매개변수의 값을 그 형식에 맞춤
            createValueObject(m.getParameterTypes()[0], // 셋터 메서드의 매개변수 타입 
                request.getParameter(paramName)));      // 요청 매개변수의 값
      }
    }
    return dataObject;
  }

  // 매개변수로 주어진 타입이 기본 타입인지 검사하는 메서드
  private static boolean isPrimitiveType(Class<?> type) {
    if (type.getName().equals("int") || type == Integer.class ||
    type.getName().equals("long") || type == Long.class ||
    type.getName().equals("float") || type == Float.class ||
    type.getName().equals("double") || type == Double.class ||
    type.getName().equals("boolean") || type == Boolean.class ||
    type == Date.class || type == String.class) {
      return true;
    }
    return false;
  }

  // 셋터로 값을 할당할 수 없는 기본 타입에 대해 객체를 생성하는 메서드
  private static Object createValueObject(Class<?> type, String value) {
    if (type.getName().equals("int") || type == Integer.class) {
      return Integer.valueOf(value);
    } else if (type.getName().equals("float") || type == Float.class) {
      return Float.valueOf(value);
    } else if (type.getName().equals("double") || type == Double.class) {
      return Double.valueOf(value);
    } else if (type.getName().equals("long") || type == Long.class) {
      return Long.valueOf(value);
    } else if (type.getName().equals("boolean") || type == Boolean.class) {
      return Boolean.valueOf(value);
    } else if (type == Date.class) {
      return java.sql.Date.valueOf(value);
    } else {
      return value;
    }
  }

  // 클래스를 조사하여 주어진 지음과 일치하는 셋터 메서드를 찾는 메서드
  private static Method findSetter(Class<?> type, String name) {
    // 메서드 목록을 얻는다.
    Method[] methods = type.getMethods();

    String propName;
    for (Method m : methods) {
      // 셋터 메서드가 아니면 무시
      if (!m.getName().startsWith("set")) continue;
      propName = m.getName().substring(3);
      // 요청 매개변수의 이름과 일치하는지 검사
      if (propName.toLowerCase().equals(name.toLowerCase())) {
        return m;
      }
    }
    return null;
  }

}
```

<br>

## ApplicationContext (class)

이 클래스는 페이지 컨트롤러나 DAO가 추가되더라도 ContextLoaderListener를 변경하지 않기 위해 구현한다.

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

  public Object getBean(String key) {
    return objTable.get(key);
  }

  public void addBean(String name, Object obj) {
    objTable.put(name, obj);
  }

  // 자바 classpath를 뒤져서 @Component 애노테이션이 붙은 클래스를 찾는다.
  // 그리고 그 객체를 생성하여 객체 테이블에 담는다.
  public void prepareObjectsByAnnotation(String basePackage) throws Exception {
    // 우리가 원하는 클래스를 찾아주는 도구
    // backPackage 부터 하위 패키지를 모두 검색한다.
    Reflections reflector = new Reflections(basePackage);

    // Component 애노테이션이 붙은 클래스들을 찾아 준다.
    Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
    String key;
    for (Class<?> clazz : list) {
      // 클래스로부터 애노테이션을 추출한다.
      // @Component("/project/add.do") : key = /project/add.do 를 추출
      key = clazz.getAnnotation(Component.class).value();
      System.out.println("key: " + key);
      // 애노테이션을 통해 알아낸 객체 이름으로 인스턴스를 저장한다.
      objTable.put(key, clazz.newInstance());
    }
  }

  public void prepareObjectsByProperties(String propertiesPath) throws Exception {
    // Properties 는 '이름=값' 형태로 된 파일을 다룰 때 사용하는 클래스
    Properties props = new Properties();
    // 매개변수로 받은 프로퍼티 파일의 내용을 로딩한다.
    props.load(new FileReader(propertiesPath));

    // JNDI 객체를 찾을 때 사용할 initialContext 를 준비
    Context ctx = new InitialContext();
    String key;
    String value;

    // 프로퍼티에 들어있는 정보를 꺼내서 객체를 생성
    for (Object item : props.keySet()) {
      key = (String) item;
      value = props.getProperty(key);
      // 프로퍼티 키가 jndi. 으로 시작한다면 객체를 생성하지 않고
      //  InitialContext 를 통해 얻는다.
      if (key.startsWith("jndi.")) {
        objTable.put(key, ctx.lookup(value));
      } else {
        // Class.forName() 을 통해 클래스를 로딩하고,
        //  newInstance() 로 인스턴스를 생성
        objTable.put(key, Class.forName(value).newInstance());
      }
    }
  }

  // 각 객체가 필요로 하는 의존 객체를 할당해주는 메서드
  public void injectDependency() throws Exception {
    for (String key : objTable.keySet()) {
      // 객체가 jndi. 로 시작하는 경우 톰갯 서버에서 제공한 객체이므로
      // 의존 객체를 호출하지 안는다.
      if (!key.startsWith("jndi.")) {
        // 나머지 객체에 대해서는 셋터 메서드를 호
        callSetter(objTable.get(key));
      }
    }
  }

  // 매개변수로 주어진 객체에 대해 셋터 메서드를 찾아서 호출하는 메서드
  private void callSetter(Object obj) throws Exception {
    Object dependency;
    // 셋터 메서드를 찾아서 호출
    for (Method m : obj.getClass().getMethods()) {
      if (m.getName().startsWith("set")) {
        // 셋터 메서드의 매개변수와 타입이 일치하는 객체를 objTable 에서 찾는다.
        dependency = findObjectByType(m.getParameterTypes()[0]);
        if (dependency != null) {
          // 의존 객체를 찾았으면, 셋터 메서드를 호출
          // 컨트롤러 객체들은 DAO 객체를 주입하고
          // DAO 객체는 SqlSessionFactory 를 주입한다.
          m.invoke(obj, dependency);
        }
      }
    }
  }

  // 셋터 메서드를 호출할 때 넘겨줄 의존 객체를 찾는 일을 한다.
  private Object findObjectByType(Class<?> type) {
    for (Object obj : objTable.values()) {
      // 셋터 메서드의 매개변수 타입과 일치하는 객체를 찾았다면 그 객체의
      // 주소를 리턴한다.
      if (type.isInstance(obj)) {
        return obj;
      }
    }
    return null;
  }

}
```

<br>

## ProjectAddController (class)

Project를 추가해주는 컨트롤러

```java
package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.vo.Project;

import java.util.Map;

@Component("/project/add.do")
public class ProjectAddController implements Controller, DataBinding {

  // DAO 객체
  PostgresSqlProjectDao projectDao;

  // 의존성 주입을 통해 DAO 가 주입된다.
  public ProjectAddController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  // 클라이언트가 보낸 매개변수 값을 인스턴스에 담아서
  //  Map 객체에 저장을 요청하는 메서드이다.
  // 프런트 컨트롤러 쪽에서 Map 객체를 저장하고 excute를 호출할 때
  //  매개변수로 넘길 것이다.
  // project : 데이터 이름
  // Project.class : 데이터 타입
  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "project", Project.class
    };
  }

  // 프런트 컨트롤러가 페이지 컨트롤러에게 일을
  //  시키기 위해 호출하는 메서드
  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Project project = (Project) model.get("project");
    if (project.getTitle() == null) {
      return "/project/ProjectForm.jsp";
    } else {
      projectDao.insert(project);
      return "redirect:list.do";
    }
  }
}
```

<br>

## ProjectDeleteController (class)

```java
package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;

import java.util.Map;

@Component("/project/delete.do")
public class ProjectDeleteController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectDeleteController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "no", Integer.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Integer no = (Integer) model.get("no");
    projectDao.delete(no);
    return "redirect:list.do";
  }
}
```

<br>

## ProjectListController (class)

```java
package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.dao.ProjectDao;

import java.util.HashMap;
import java.util.Map;

@Component("/project/list.do")
public class ProjectListController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectListController setMemberDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    // DAO 에게 정렬 조건 전달
    HashMap<String, Object> paramMap = new HashMap<>();
    paramMap.put("orderCond", model.get("orderCond"));
    model.put("projects", projectDao.selectList(paramMap));
    return "/project/ProjectList.jsp";
  }

  // 클라이언트가 보낸 'orderCond' 값을 받기위함
  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "orderCond", String.class
    };
  }
}
```

<br>

## ProjectUpdateController (class)

```java
package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.vo.Project;

import java.util.Map;

@Component("/project/update.do")
public class ProjectUpdateController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectUpdateController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "no", Integer.class,
        "project", Project.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Project project = (Project)model.get("project");

    if (project.getTitle() == null) {
      Integer no = (Integer)model.get("no");
      Project detailInfo = projectDao.selectOne(no);
      model.put("project", detailInfo);
      return "/project/ProjectUpdateForm.jsp";
    } else {
      projectDao.update(project);
      return "redirect:list.do";
    }
  }
}
```

<br>

## Controller (interface)

페이지 컨트롤러를 위한 인터페이스 정의

```java
package spms.controls;

import java.util.Map;

public interface Controller {

  // 프런트 컨트롤러가 페이지 컨트롤러에게 일을 시키기 위해 호출하는 메서드
  String execute(Map<String, Object> model) throws Exception;

}
```

<br>

## LogInController (class)

```java
package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.PostgresSqlMemberDao;
import spms.vo.Member;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Component("/auth/login.do")
public class LogInController implements Controller, DataBinding {
  PostgresSqlMemberDao memberDao;

  public LogInController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Member member = (Member) model.get("member");
    if (member.getEmail() == null) {
      return "/auth/LogInForm.jsp";
    } else {
      Member existMember = memberDao.exist(member.getEmail(), member.getPassword());
      if (existMember != null) {
        ((HttpSession) model.get("session"))
            .setAttribute("member", existMember);
        return "redirect:/member/list.do";
      } else {
        return "/auth/LogInFail.jsp";
      }
    }
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "member", Member.class
    };
  }
}
```

<br>

## LogOutController (class)

```java
package spms.controls;

import spms.annotation.Component;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Component("/auth/logout.do")
public class LogOutController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    HttpSession session = (HttpSession) model.get("session");
    session.invalidate();
    return "redirect:login.do";
  }

}
```

<br>

## MemberAddController (class)

```java
package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.PostgresSqlMemberDao;
import spms.vo.Member;

import java.util.Map;

@Component("/member/add.do")
// DataBinding 인터페이스 구현 선언
public class MemberAddController implements Controller, DataBinding {

  PostgresSqlMemberDao memberDao;

  public MemberAddController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Member member = (Member)model.get("member");
    if (member.getEmail() == null) {
      return "/member/MemberAdd.jsp";
    } else {
      memberDao.insert(member);

      return "redirect:list.do";
    }
  }

  // 메서드 구현
  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "member", Member.class
    };
  }

}
```

<br>

## MemberDeleteController (class)

```java
package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.PostgresSqlMemberDao;

import java.util.Map;

@Component("/member/delete.do")
public class MemberDeleteController implements Controller, DataBinding {

  PostgresSqlMemberDao memberDao;

  public MemberDeleteController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    memberDao.delete(Integer.parseInt(String.valueOf(model.get("no"))));
    return "redirect:list.do";
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "no", Integer.class
    };
  }

}
```

<br>

## MemberListController (class)

```java
package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.MemberDao;
import spms.dao.PostgresSqlMemberDao;

import java.util.HashMap;
import java.util.Map;

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

  // 정렬 순서를 제공받기 위함
  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "orderCond", String.class
    };
  }
}
```

<br>

## MemberUpdateController (class)

```java
package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.PostgresSqlMemberDao;
import spms.vo.Member;

import java.util.Map;

@Component("/member/update.do")
public class MemberUpdateController implements Controller, DataBinding {

  PostgresSqlMemberDao memberDao;

  public MemberUpdateController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Member member = (Member) model.get("member");
    if (member.getEmail() == null) {
      member = memberDao.selectOne(Integer.parseInt(String.valueOf(model.get("no"))));
      model.put("member", member);
      return "/member/MemberUpdate.jsp";
    } else {
      memberDao.update(member);
      return "redirect:list.do";
    }
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
      "no", Integer.class,
      "member", Member.class
        };
  }
}
```

<br>

## db.properties

```properties
driver=org.postgresql.Driver
url=jdbc:postgresql://arjuna.db.elephantsql.com:5432/
username=******
password=******
```

<br>

## MemberDao (interface)

다른 데이터베이스들을 용이하게 사용하기 위함.

```JAVA
package spms.dao;

import spms.vo.Member;

import java.util.HashMap;
import java.util.List;

public interface MemberDao {

  // order by 절이 바뀌기 때문
  List<Member> selectList(HashMap<String, Object> paramMap) throws Exception;
  int insert(Member member) throws Exception;
  int delete(int no) throws Exception;
  Member selectOne(int no) throws Exception;
  int update(Member member) throws Exception;
  Member exist(String email, String password) throws Exception;

}
```

<br>

## mybatis-config (XML)

SqlSessionFactory를 생성할 때 필요한 파일이다.

DB 커넥션을 생성하는 데이터소스에 대한 정보와 트랜잭션 관리자, mybatis 동작을 제어하는 환경값, SQL 문이 저장된 맵퍼 파일

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

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

## PostgresSqlMemberDao (class)

```java
package spms.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import spms.annotation.Component;
import spms.vo.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@Component("memberDao")
public class PostgresSqlMemberDao implements MemberDao {

  // SqlSession 을 생성할 객체
  SqlSessionFactory sqlSessionFactory;

  // 의존 주입을 통해 객체가 주입됨
  public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionFactory = sqlSessionFactory;
  }

  public List<Member> selectList(HashMap<String, Object> paramMap) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      // 맵퍼 파일을 통해 selectList 호출
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
      // Member 정보를 가져옴
      Member original = sqlSession.selectOne("spms.dao.MemberDao.selectOne", member.getNo());

      // update 문을 전달할 Map 객체 준비
      Hashtable<String, Object> paramMap = new Hashtable<>();
      if (!member.getName().equals(original.getName()))
        paramMap.put("name", member.getName());
      if (!member.getEmail().equals(original.getEmail())) {
        paramMap.put("email", member.getEmail());
      }
      // Map 객체에 저장된 값이 있다면 UPDATE 문을 실행 없으면 0 반환
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

## PostgresSqlMemberDao (XML)

MemberDao의 SQL 맵퍼 파일

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--태그 규칙을 정의한 DTD 선언-->
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!-- 맵퍼 파일에 작성하는 모든 SQL 문은 <mapper> 태그에 놓는다.-->
<!-- namespace : 자바의 패키지처럼 SQL 문을 묶는 용도로 사용-->
<mapper namespace="spms.dao.MemberDao">

  <!-- 칼럼에 별명을 붙이는 대신 <resultMap> 을 이용하여 클래스의 별명을 지어줌-->
  <!-- type : 칼럼 데이터를 저장할 클래스 이름 또는 클래스 별명-->
  <!-- id : 별명 -->
  <resultMap id="memberResultMap" type="member">
    <id column="MNO" property="no"/>
    <!-- <result> : column 속성과 property 속성과 매핑시킴 -->
    <!-- column : 칼럼 이름을 지정-->
    <!-- property : 객체의 프로퍼티 이름 지정-->
    <!-- MNAME 칼럼 값을 setName() 메서드와 매핑시킴-->
    <result column="MNAME" property="name"/>
    <result column="EMAIL" property="email"/>
    <result column="PWD" property="password"/>
    <!-- javaType : 칼럼의 값을 특정 자바 객체로 변환-->
    <result column="CRE_DATE" property="createDate" javaType="java.sql.Date"/>
    <result column="MOD_DATE" property="modifiedDate" javaType="java.sql.Date"/>
  </resultMap>

  <!-- SELECT 역할을 하는 태그-->
  <!-- <resultMap> 에 정의된 대로 자바 객체를 생성 -->
  <!-- parameterType : 매개변수 타입-->
  <select id="selectList" parameterType="map" resultMap="memberResultMap">
    select MNO, MNAME, EMAIL, CRE_DATE
    from MEMBERS
    order by
    -- 여러 개의 조건을 검사해서 해당하는 조건의 SQL을 반환
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
    -- #{프로퍼티명} : member 객체의 getter/setter 프로퍼티 값이 놓인다.
    values (#{name}, #{email}, #{password}, now(), now())
  </insert>

  <!-- parameter : 기본 타입의 객체 랩퍼로 부터 값을 꺼낼 때는-->
  <!--    아무 이름(value)이나 사용해도 된다.-->
  <select id="selectOne" parameterType="int" resultMap="memberResultMap">
    select MNO, MNAME, EMAIL, CRE_DATE, MOD_DATE
    from MEMBERS
    where MNO=#{value}
  </select>

  <update id="update" parameterType="map">
    update MEMBERS
    -- 이 태그는 UPDATE 문의 SET 절을 만들 때 사용한다.
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

## PostgresSqlProjectDao (class)

```java
package spms.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import spms.annotation.Component;
import spms.vo.Project;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

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
      // 프로젝트 정보를 가져온다.
      Project original = sqlSession.selectOne("spms.dao.ProjectDao.selectOne",
                                              project.getNo());

      // update 문에 전달할 map 객체 준비
      Hashtable<String, Object> paramMap = new Hashtable<>();

      // 기존 객체 정보와 매개변수로 받은 객체가 
      // 변경된 값들이 있는지 확인
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

      // Map 객체에 저장된 값이 있다면 UPDATE 실행
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

## PostgresSqlProjectDao (XML)

ProjectDao의 SQL 맵퍼 파일

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

  <insert id="insert" parameterType="project">
    insert into PROJECTS(PNAME, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS)
    values (#{title}, #{content}, #{startDate}, #{endDate}, 0, now(), #{tags})
  </insert>

  <select id="selectOne" parameterType="int" resultMap="projectResultMap">
    select PNO, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS
    from PROJECTS
    where PNO=#{value}
  </select>

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

  <delete id="delete" parameterType="int">
    delete from PROJECTS
    where PNO=#{value}
  </delete>

</mapper>
```

<br>

## ProjectDao (interface)

ProjectDao 인더페이스 구현

```java
package spms.dao;

import spms.vo.Project;

import java.util.HashMap;
import java.util.List;

public interface ProjectDao {

  List<Project> selectList(HashMap<String, Object> paramMap) throws Exception;
  int insert(Project project) throws Exception;
  Project selectOne(int no) throws Exception;
  int update(Project project) throws Exception;
  int delete(int no) throws Exception;

}
```

<br>

## ContextLoaderListener (class)

웹 애플리케이션의 시작과 종료 사건을 담당할 리스너 클래스

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

@WebListener // 리스너 배치
public class ContextLoaderListener implements ServletContextListener {

  static ApplicationContext applicationContext;

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      applicationContext = new ApplicationContext();

      // mybatis 설정 파일 경로 저장
      String resource = "spms/dao/mybatis-config.xml";
      // getResourceAsStream 을 통해 설정 파일의 입력 스트림을 가져온다.
      InputStream inputStream = Resources.getResourceAsStream(resource);
      // SqlSessionFactory 를 Builder의 bind를 통해 가져온다.
      SqlSessionFactory sqlSessionFactory =
          new SqlSessionFactoryBuilder().build(inputStream);

      // ApplicationContext에 factory 객체를 등록
      applicationContext.addBean("sqlSessionFactory", sqlSessionFactory);
      
      ServletContext sc = sce.getServletContext();
      // request의 인코딩 방식을 지정 
      sc.setRequestCharacterEncoding("UTF-8");

      // 프로퍼티 파일을 경로를 가져온다
      String propertiesPath = sc.getRealPath(
          sc.getInitParameter("contextConfigLocation"));
      // 프로퍼티 파일 대로 객체 생성
      applicationContext.prepareObjectsByProperties(propertiesPath);
      // 애노테이션 설정 대로 객체 생성
      applicationContext.prepareObjectsByAnnotation("");
      // 의존 객체 주입
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

<br>

## DispatcherServlet (class)

프런트 컨트롤러 역할을 하는 클래스

```java
package spms.servlets;

import spms.bind.DataBinding;
import spms.bind.ServletRequestDataBinder;
import spms.context.ApplicationContext;
import spms.controls.*;
import spms.listeners.ContextLoaderListener;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

// 프런트 컨트롤러도 서블릿이기 때문에 HttpServlet 을 상속받는다.
@WebServlet("*.do") // ".do" 로 끝나는 모든 url은 이 객체가 요청을 받는다.
public class DispatcherServlet extends HttpServlet {

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    // 서블릿 경로를 가져온다. ex. /member/list.do
    String servletPath = req.getServletPath();

    try {
      ApplicationContext ctx = ContextLoaderListener.getApplicationContext();

      HashMap<String, Object> model = new HashMap<>();
      model.put("session", req.getSession());


      Controller pageController = (Controller) ctx.getBean(servletPath);

      if (pageController == null) {
        throw new Exception("요청한 서비스를 찾을 수 없습니다.");
      }

      // 데이터 바인딩이 필요한 컨트롤러 일때
      if (pageController instanceof DataBinding) {
        // 데이터 준비를 자동으로 수행하는 메소드 호출
        prepareRequestData(req, model, (DataBinding)pageController);
      }

      String viewUrl = pageController.execute(model);

      for (String key : model.keySet())
        req.setAttribute(key, model.get(key));

      // "redirect:" 로 시작하면, 인클루딩 하는 대신에
      //  sendRedirect() 를 통해 리다이랙트를 요청한다.
      if (viewUrl.startsWith("redirect:")) {
        resp.sendRedirect(viewUrl.substring(9));
      } else {
        // 페이지 컨트롤러의 실행이 끝나면, 화면 출력을 위해
        //  ServletRequest 에 보관된 뷰 URL로 실행을 위임한다.
        RequestDispatcher rd = req.getRequestDispatcher(viewUrl);
        rd.include(req, resp);
      }
    } catch (Exception e) {
      e.printStackTrace();
      // 에러 메시지를 요청에 추가하고 에러 페이지로 URL 실행을 위임한다.
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

  // 데이터 준비를 자동으로 수행하는 메소드
  private void prepareRequestData(HttpServletRequest request,
                                  HashMap<String, Object> model, DataBinding dataBinding) throws Exception {
    // 페이지 컨트롤러에게 필요한 데이터가 무엇인지 가져온다.
    Object[] dataBinders = dataBinding.getDataBinders();
    // 임시 변수 준비
    String dataName;
    Class<?> dataType;
    Object dataObj;
    for (int i = 0; i < dataBinders.length; i += 2) {
      dataName = (String) dataBinders[i];
      dataType = (Class<?>) dataBinders[i+1];
      // dataName과 일치하는 요청 매개변수를 찾고
      //  dataType을 통해 해당 클래스의 인스턴스를 생성
      dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
      // map 객체에 담아서 페이지 컨트롤러가 사용할 데이터를 준비시킨다.
      model.put(dataName, dataObj);
    }
  }
}
```

<br>

## Member (class)

Member VO 클래스

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

<br>

## Project (class)

Project VO 클래스

```java
package spms.vo;

import java.util.Date;

public class Project {

  protected int no;
  protected String title;
  protected String content;
  protected Date startDate;
  protected Date endDate;
  protected int state;
  protected Date createdDate;
  protected String tags;

  public int getNo() {
    return no;
  }

  public Project setNo(int no) {
    this.no = no;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Project setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getContent() {
    return content;
  }

  public Project setContent(String content) {
    this.content = content;
    return this;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Project setStartDate(Date startDate) {
    this.startDate = startDate;
    return this;
  }

  public Date getEndDate() {
    return endDate;
  }

  public Project setEndDate(Date endDate) {
    this.endDate = endDate;
    return this;
  }

  public int getState() {
    return state;
  }

  public Project setState(int state) {
    this.state = state;
    return this;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public Project setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public String getTags() {
    return tags;
  }

  public Project setTags(String tags) {
    this.tags = tags;
    return this;
  }
}
```

<br>

## log4j.properties

로그의 수준, 출력 방식, 출력 형식 로그 대상 등에 대한 정보가 들어간다. 이 파일은 자바 클래스 경로(CLASSPATH)에 두어야 한다.

```properties
# 로그의 출력 등급 설정
# ERROR: 출력 등급
# stdout : 출력 담당자
log4j.rootLogger=ERROR, stdout

log4j.logger.spms.dao=TRACE

# 로그의 출력 형식 정의
# org.apache.log4j.ConsoleAppender : System.out 으로 로그 출력
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
# org.apache.log4j.PatternLayout : 변환 패턴의 형식에 따라 로그 출력
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
# PatternLayout 패턴 정의
# %5p : 5글자 문자열로 출력
# %t : 스레드 이름
# %m : 로그 내용 출력
log4j.appender.stdout.layout.ConversionPattern=%5p [%t] - %m%n
```

<br>

## LogInFail.jsp

로그인 실패 View

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%-- 1초 뒤, login.do url로 리플래시--%>
    <meta http-equiv="Refresh" content="1;url=login.do">
    <title>로그인 실패</title>
</head>
<body>
<p>로그인 실패입니다. 이메일 또는 암호가 맞지 않습니다!<br>
    잠시 후에 다시 로그인 화면으로 갑니다.</p>
</body>
</html>

```

<br>

## LogInForm.jsp

로그인 형식 VIew

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>로그인</title>
  </head>
  <body>
    <h2>사용자 로그인</h2>
    <%-- URL에 .do 붙임 --%>
    <%-- URL, login.do로 post 요청 --%>
    <form action="login.do" method="post">
      이메일: <input type="text" name="email"><br>
      암호: <input type="password" name="password"><br>
      <input type="submit" value="로그인">
    </form>
  </body>
</html>
```

<br>

## MemberAdd.jsp

회원 추가 View

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>회원 등록</title>
  </head>
  <body>
    <h1>회원 등록</h1>
    <%-- add.do URL에 post 형식으로 요청--%>
    <form action="add.do" method="post">
      이름: <input type="text" name="name"><br>
      이메일: <input type="text" name="email"><br>
      암호: <input type="password" name="password"><br>
      <input type="submit" value="추가">
      <input type="reset" value="취소">
    </form>
  </body>
</html>
```

<br>

## MemberList.jsp

회원 목록 View

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
      <%-- 정렬 링크가 걸린 칼럼 헤더--%>
      <tr>
        <th>
          <%-- orderCond 값에 따라 MNO 정렬 방식을 결정한다--%>
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
      <%-- members 리스트를 가져와서 member 객체에 넣고 for문 돌림--%>
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

<br>

## MemberUpdate.jsp

회원 정보 수정 View

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>회원정보</title>
  </head>
  <body>
    <h1>회원정보</h1>
    <%-- URL에 .do 붙이기--%>
    <form action="update.do" method="post">
      번호: <input type="text" name="no" value="${member.no}" readonly><br>
      이름: <input type="text" name="name" value="${member.name}"><br>
      이메일: <input type="text" name="email" value="${member.email}"><br>
      가입일: ${member.createDate}<br>
      <input type="submit" value="저장">
      <%-- URL에 .do 붙이기 --%>
      <input type="button" value="삭제" onclick="location.href='delete.do?no=${member.no}'">
      <input type="button" value="취소" onclick="location.href='list.do'">
    </form>
  </body>
</html>
```

<br>

## ProjectForm.jsp

프로젝트 생성 View

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>회원정보</title>
  </head>
  <body>
    <h1>회원정보</h1>
    <%-- URL에 .do 붙이기--%>
    <form action="update.do" method="post">
      번호: <input type="text" name="no" value="${member.no}" readonly><br>
      이름: <input type="text" name="name" value="${member.name}"><br>
      이메일: <input type="text" name="email" value="${member.email}"><br>
      가입일: ${member.createDate}<br>
      <input type="submit" value="저장">
      <%-- URL에 .do 붙이기 --%>
      <input type="button" value="삭제" onclick="location.href='delete.do?no=${member.no}'">
      <input type="button" value="취소" onclick="location.href='list.do'">
    </form>
  </body>
</html>
```

<br>

## ProjectList.jsp

프로젝트 목록 View

```jsp
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

## ProjectUpdateForm.jsp

프로젝트 정보 수정 View

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
  <head>
    <meta charset="UTF-8">
    <title>프로젝트 정보</title>
    <style>
      ul {
        padding: 0;
      }

      li {
        list-style: none
      }

      label {
        float: left;
        text-align: right;
        width: 60px;
      }
    </style>
  </head>
  <body>
    <jsp:include page="/Header.jsp"/>
    <h1>프로젝트 정보</h1>
    <form action="update.do" method="post">
      <ul>
        <li>
          <label for="no">번호</label>
          <input id="no" type="text" name="no" size="5" value="${project.no}" readonly>
        </li>
        <li>
          <label for="title">제목</label>
          <input id="title" type="text" name="title" size="50" value="${project.title}">
        </li>
        <li>
          <label for="content">내용</label>
          <textarea id="content" name="content" rows="5" cols="40">${project.content}</textarea>
        </li>
        <li>
          <label for="sdate">시작일</label>
          <input id="sdate" type="text" name="startDate" placeholder="예)2013-01-01"
                 value="${project.startDate}">
        </li>
        <li>
          <label for="edate">종료일</label>
          <input id="edate" type="text" name="endDate" placeholder="예)2013-01-01"
                 value="${project.endDate}">
        </li>
        <li>
          <label for="state">상태</label>
          <select id="state" name="state">
            <option value="0" ${project.state == 0 ? "selected" : ""}>준비</option>
            <option value="1" ${project.state == 1 ? "selected" : ""}>진행</option>
            <option value="2" ${project.state == 2 ? "selected" : ""}>완료</option>
            <option value="3" ${project.state == 3 ? "selected" : ""}>취소</option>
          </select>
        </li>
        <li>
          <label for="tags">태그</label>
          <input id="tags" type="text" name="tags" placeholder="예)태그1 태그2 태그3" size="50"
                 value="${project.tags}">
        </li>
      </ul>
      <input type="submit" value="저장">
      <input type="button" value="삭제"
             onclick="location.href='delete.do?no=${project.no}';">
    </form>
    <jsp:include page="/Tail.jsp"/>
  </body>
</html>
```

<br>

## web.xml

```jsp
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
  <display-name>Apache-Axis</display-name>

  <!-- properties 파일 경로 정보 추가-->
  <!-- 이 프로젝트에서는 안쓰인다.-->
  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/application-context.properties</param-value>
  </context-param>

  <servlet>
    <display-name>Apache-Axis Servlet</display-name>
    <servlet-name>AxisServlet</servlet-name>
    <servlet-class>org.apache.axis.transport.http.AxisServlet</servlet-class>
  </servlet>
  <servlet>
    <display-name>Axis Admin Servlet</display-name>
    <servlet-name>AdminServlet</servlet-name>
    <servlet-class>org.apache.axis.transport.http.AdminServlet</servlet-class>
    <load-on-startup>100</load-on-startup>
  </servlet>
  <servlet>
    <display-name>SOAPMonitorService</display-name>
    <servlet-name>SOAPMonitorService</servlet-name>
    <servlet-class>org.apache.axis.monitor.SOAPMonitorService</servlet-class>
    <init-param>
      <param-name>SOAPMonitorPort</param-name>
      <param-value>5101</param-value>
    </init-param>
    <load-on-startup>100</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>AxisServlet</servlet-name>
    <url-pattern>/servlet/AxisServlet</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>AxisServlet</servlet-name>
    <url-pattern>*.jws</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>AxisServlet</servlet-name>
    <url-pattern>/services/*</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>SOAPMonitorService</servlet-name>
    <url-pattern>/SOAPMonitor</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>AdminServlet</servlet-name>
    <url-pattern>/servlet/AdminServlet</url-pattern>
  </servlet-mapping>
  <mime-mapping>
    <extension>wsdl</extension>
    <mime-type>text/xml</mime-type>
  </mime-mapping>
</web-app>
```

<br>

## Error.jsp

에러 View

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

<br>

## Header.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div style="background-color: #00008b; color: #ffffff; height: 20px; padding: 5px;">
  SPMS(Simple Project Management System)
  <span style="float: right;">
    <a style="color: white;"
       href="<%=request.getContextPath()%>/project/list.do">프로젝트</a>
    <a style="color: white;"
       href="<%=request.getContextPath()%>/member/list.do">회원</a>
    <c:if test="${empty sessionScope.member or empty sessionScope.member.email}">
      <a style="color: white;" href="<%=request.getContextPath()%>/auth/login.do">로그인</a>
    </c:if>
    <c:if test="${!empty sessionScope.member and !empty sessionScope.member.name}">
      ${sessionScope.member.name}
      (<a style="color: white;"
          href="<%=request.getContextPath()%>/auth/logout.do">로그아웃</a>
    </c:if>
  </span>
</div>
```

<br>

## Tail.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div style="background-color: #f0fff0; height: 20px; padding: 5px; margin-top: 10px">
  SPMS &copy; 2013
</div>
```