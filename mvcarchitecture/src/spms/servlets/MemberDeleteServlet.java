package spms.servlets;

import spms.dao.MemberDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/member/delete")
public class MemberDeleteServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext sc = this.getServletContext();
    Connection conn = (Connection) sc.getAttribute("conn");

    try {
      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(conn);
      memberDao.delete(Integer.parseInt(req.getParameter("no")));
      resp.sendRedirect("list");
    } catch (Exception e) {
      req.setAttribute("error", e);
      e.printStackTrace();
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }
}
