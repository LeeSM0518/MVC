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
import java.io.IOException;

@WebServlet("/member/update")
public class MemberUpdateServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext sc = this.getServletContext();
    try {
      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
      Member member = memberDao.selectOne(Integer.parseInt(req.getParameter("no")));
      req.setAttribute("updateMember", member);

      RequestDispatcher rd = req.getRequestDispatcher("/member/MemberUpdate.jsp");
      rd.forward(req, resp);
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext sc = this.getServletContext();
    try {
      Member member = new Member();
      member.setEmail(req.getParameter("email"))
          .setName(req.getParameter("name"))
          .setNo(Integer.parseInt(req.getParameter("no")));

      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
      memberDao.update(member);

      resp.sendRedirect("list");
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

}