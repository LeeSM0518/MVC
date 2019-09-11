package spms.servlets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/member/add")
public class MemberAddServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    RequestDispatcher rd = req.getRequestDispatcher(
        "/member/MemberAdd.jsp");
    rd.forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String query = "insert into members (email, pwd, mname, cre_date, mod_date) values" +
        " (?, ?, ?, now(), now())";
    ServletContext sc = this.getServletContext();
    Connection connection = (Connection)sc.getAttribute("conn");
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, req.getParameter("email"));
      ps.setString(2, req.getParameter("password"));
      ps.setString(3, req.getParameter("name"));
      ps.executeUpdate();

      resp.sendRedirect("list");
    } catch (Exception e) {
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

}