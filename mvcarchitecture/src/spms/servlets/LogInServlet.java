package spms.servlets;

import spms.dao.MemberDao;
import spms.vo.Member;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth/login")
public class LogInServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    RequestDispatcher rd = req.getRequestDispatcher(
        "/auth/LogInForm.jsp");
    rd.forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext sc = this.getServletContext();

    try {
      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
      Member member = memberDao.exist(req.getParameter("email"), req.getParameter("password"));
      if (member != null) {
        HttpSession session = req.getSession();
        session.setAttribute("member", member);
        resp.sendRedirect("../member/list");
      } else {
        RequestDispatcher rd = req.getRequestDispatcher("/auth/LogInFail.jsp");
        rd.forward(req, resp);
      }
    } catch (Exception e) {
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

}
