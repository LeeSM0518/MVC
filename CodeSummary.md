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
username=fsmfppcj
password=opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH
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

```xml

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

