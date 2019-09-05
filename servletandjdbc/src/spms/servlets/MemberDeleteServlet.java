package spms.servlets;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/member/delete")
public class MemberDeleteServlet extends HttpServlet {

  private ServletContext sc;

  @Override
  public void init() throws ServletException {
    super.init();
    sc = this.getServletContext();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//    req.setCharacterEncoding("UTF-8");
    String query = "delete from members where mno=" + req.getParameter("no");

    try (Connection conn = DriverManager.getConnection(
        sc.getInitParameter("url"),
        sc.getInitParameter("username"),
        sc.getInitParameter("password"));
         PreparedStatement ps = conn.prepareStatement(query);
    ) {
      ps.executeUpdate();
      resp.sendRedirect("list");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

}