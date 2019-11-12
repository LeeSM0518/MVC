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
