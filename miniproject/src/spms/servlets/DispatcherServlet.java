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
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    String servletPath = req.getServletPath();

    try {
//      ServletContext sc = this.getServletContext();
      ApplicationContext ctx = ContextLoaderListener.getApplicationContext();

      HashMap<String, Object> model = new HashMap<>();
      model.put("session", req.getSession());

//      Controller pageController = (Controller) sc.getAttribute(servletPath);
      Controller pageController = (Controller) ctx.getBean(servletPath);

      if (pageController == null) {
        throw new Exception("요청한 서비스를 찾을 수 없습니다.");
      }

      if (pageController instanceof DataBinding) {
        prepareRequestData(req, model, (DataBinding)pageController);
      }

      String viewUrl = pageController.execute(model);

      for (String key : model.keySet())
        req.setAttribute(key, model.get(key));

      if (viewUrl.startsWith("redirect:")) {
        resp.sendRedirect(viewUrl.substring(9));
      } else {
        RequestDispatcher rd = req.getRequestDispatcher(viewUrl);
        rd.include(req, resp);
      }
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

  private void prepareRequestData(HttpServletRequest request,
                                  HashMap<String, Object> model, DataBinding dataBinding) throws Exception {
    Object[] dataBinders = dataBinding.getDataBinders();
    String dataName;
    Class<?> dataType;
    Object dataObj;
    for (int i = 0; i < dataBinders.length; i += 2) {
      dataName = (String) dataBinders[i];
      dataType = (Class<?>) dataBinders[i+1];
      dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
      model.put(dataName, dataObj);
    }
  }
}
