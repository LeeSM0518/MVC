package spms.servlets;

import spms.controls.*;
import spms.vo.Member;

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
      ServletContext sc = this.getServletContext();

      HashMap<String, Object> model = new HashMap<>();
      model.put("memberDao", sc.getAttribute("memberDao"));

      Controller pageController = null;

      if ("/member/list.do".equals(servletPath)) {
        pageController = new MemberListController();
      } else if ("/member/add.do".equals(servletPath)) {
        pageController = new MemberAddController();
        if (req.getParameter("email") != null) {
          model.put("member", new Member()
              .setEmail(req.getParameter("email"))
              .setPassword(req.getParameter("password"))
              .setName(req.getParameter("name")));
        }
      } else if ("/member/update.do".equals(servletPath)) {
        pageController = new MemberUpdateController();
        model.put("no", req.getParameter("no"));
        if (req.getParameter("email") != null) {
          model.put("member", new Member()
              .setNo(Integer.parseInt(req.getParameter("no")))
              .setEmail(req.getParameter("email"))
              .setName(req.getParameter("name")));
        }
      } else if ("/member/delete.do".equals(servletPath)) {
        pageController = new MemberDeleteController();
        model.put("no", req.getParameter("no"));
      } else if ("/auth/login.do".equals(servletPath)) {
        pageController = new LogInController();
        if (req.getParameter("email") != null) {
          model.put("member", new Member()
          .setEmail(req.getParameter("email"))
          .setPassword(req.getParameter("password")));
          model.put("session", req.getSession());
        }
      } else if ("/auth/logout.do".equals(servletPath)) {
        pageController = new LogOutController();
        model.put("session", req.getSession());
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
}
