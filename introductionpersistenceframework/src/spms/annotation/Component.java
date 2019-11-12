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
