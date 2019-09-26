package spms.servlets;

import spms.vo.Member;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 프런트 컨트롤러도 서블릿이기 때문에 HttpServlet 을 상속받는다.
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    String servletPath = req.getServletPath();

    try {
      String pageControllerPath = null;

      if ("/member/list.do".equals(servletPath)) {
        pageControllerPath = "/member/list";
      } else if ("/member/add.do".equals(servletPath)) {
        pageControllerPath = "/member/add";
        if (req.getParameter("email") != null) {
          req.setAttribute("member", new Member()
              .setEmail(req.getParameter("email"))
              .setPassword(req.getParameter("password"))
              .setName(req.getParameter("name")));
        }
      } else if ("/member/update.do".equals(servletPath)) {
        pageControllerPath = "/member/update";
        if (req.getParameter("email") != null) {
          req.setAttribute("member", new Member()
              .setNo(Integer.parseInt(req.getParameter("no")))
              .setEmail(req.getParameter("email"))
              .setName(req.getParameter("name")));
        }
      } else if ("/member/delete.do".equals(servletPath)) {
        pageControllerPath = "/member/delete";
      } else if ("/auth/login.do".equals(servletPath)) {
        pageControllerPath = "/auth/login";
      } else if ("/auth/logout.do".equals(servletPath)) {
        pageControllerPath = "/auth/logout";
      }

      RequestDispatcher rd = req.getRequestDispatcher(pageControllerPath);
      rd.include(req, resp);

      String viewUrl = (String) req.getAttribute("viewUrl");
      if (viewUrl.startsWith("redirect:")) {
        resp.sendRedirect(viewUrl.substring(9));
      } else {
        rd = req.getRequestDispatcher(viewUrl);
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
