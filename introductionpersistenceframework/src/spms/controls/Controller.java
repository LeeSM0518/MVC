package spms.controls;

import java.util.Map;

// 페이지 컨트롤러를 위한 인터페이스 정의
public interface Controller {

  // 프런트 컨트롤러가 페이지 컨트롤러에게 일을 시키기 위해 호출하는 메서드
  String execute(Map<String, Object> model) throws Exception;

}
