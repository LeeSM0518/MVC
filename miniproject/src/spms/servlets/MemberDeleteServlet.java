package spms.servlets;

import spms.dao.MemberDao;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/member/delete")
public class MemberDeleteServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext sc = this.getServletContext();
    try {
      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
      // 프론트 컨트롤러가 저장한 "no"을 불러온다.
      memberDao.delete(Integer.parseInt(req.getParameter("no")));
      // list 로 리다이렉트 요청 URL 저장
      req.setAttribute("viewUrl", "redirect:list.do");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

}
