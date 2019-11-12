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
