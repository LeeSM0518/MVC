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
