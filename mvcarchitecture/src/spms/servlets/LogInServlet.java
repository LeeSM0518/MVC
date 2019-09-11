package spms.servlets;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    String query = "select mname, email from members where email=? and pwd=?";
    ServletContext sc = this.getServletContext();
    Connection conn = (Connection) sc.getAttribute("conn");
    ResultSet resultSet = null;

    try (PreparedStatement preparedStatement = conn.prepareStatement(query)
    ) {
      preparedStatement.setString(1, req.getParameter("email"));
      preparedStatement.setString(2, req.getParameter("password"));
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        Member member = new Member()
            .setEmail(resultSet.getString("email"))
            .setName(resultSet.getString("mname"));
        HttpSession session = req.getSession();
        session.setAttribute("member", member);

        resp.sendRedirect("../member/list");
      } else {
        RequestDispatcher rd = req.getRequestDispatcher("/auth/LogInFail.jsp");
        rd.forward(req, resp);
      }
    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
      try { if (resultSet != null) resultSet.close();} catch (Exception e) {}
    }
  }

}
