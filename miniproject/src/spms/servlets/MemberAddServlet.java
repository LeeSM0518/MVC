package spms.servlets;

import spms.dao.MemberDao;
import spms.vo.Member;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/member/add")
public class MemberAddServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setAttribute("viewUrl", "/member/MemberAdd.jsp");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      ServletContext sc = this.getServletContext();
      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");

      Member member = (Member) req.getAttribute("member");
      memberDao.insert(member);

      req.setAttribute("viewUrl", "redirect:list.do");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

}
