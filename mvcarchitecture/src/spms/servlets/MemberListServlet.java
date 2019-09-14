package spms.servlets;

import spms.dao.MemberDao;
import spms.vo.Member;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

// 서블릿을 만들고자 서블릿 어노테이션을 쓰고
// GenericServlet 을 상속받는다.
@WebServlet("/member/list")
public class MemberListServlet extends HttpServlet {

  private ServletContext sc;

  @Override
  public void init() throws ServletException {
    super.init();
    sc = this.getServletContext();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Connection connection = (Connection) sc.getAttribute("conn");

    // 데이터 베이스 연결 및 쿼리 실행
    try {
      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(connection);

      req.setAttribute("members", memberDao.selectList());
      resp.setContentType("text/html; charset=UTF-8");

      RequestDispatcher rd = req.getRequestDispatcher(
          "/member/MemberList.jsp");
      rd.include(req, resp);

    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

}
