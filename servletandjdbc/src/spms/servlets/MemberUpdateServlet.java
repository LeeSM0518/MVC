package spms.servlets;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//@WebServlet(
//    urlPatterns = {"/member/update"},
//    initParams = {
//        @WebInitParam(name="driver", value="org.postgresql.Driver"),
//        @WebInitParam(name="url", value = "jdbc:postgresql://arjuna.db.elephantsql.com:5432/"),
//        @WebInitParam(name="username", value = "fsmfppcj"),
//        @WebInitParam(name="password", value = "opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH")
//    }
//)

@WebServlet("/member/update")
@SuppressWarnings("serial")
public class MemberUpdateServlet extends HttpServlet {

  private ServletContext sc;

  @Override
  public void init() throws ServletException {
    super.init();
    sc = this.getServletContext();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String query = "select mno, email, mname, cre_date from members" +
        " where mno= " + req.getParameter("no");

    try {
      Class.forName(sc.getInitParameter("driver"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    try (Connection connection = DriverManager.getConnection(
        sc.getInitParameter("url"),
        sc.getInitParameter("username"),
        sc.getInitParameter("password"));
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         ResultSet resultSet = preparedStatement.executeQuery()) {
      resultSet.next();
      resp.setContentType("text/html; charset=UTF-8");
      PrintWriter out = resp.getWriter();
      out.println("<html><head><title>회원정보</title></head>");
      out.println("<body><h1>회원정보</h1>");
      out.println("<form action='update' method='post'>");
      out.println("번호: <input type='text' name='no' value='" + req.getParameter("no") + "'readonly><br>");
      out.println("이름: <input type='text' name='name' value='" + resultSet.getString("mname") +
          "'><br>");
      out.println("이메일: <input type='text' name='email' value='" + resultSet.getString("email") +
          "'><br>");
      out.println("가입일: " + resultSet.getDate("cre_date") + "<br>");
      out.println("<input type='submit' value='저장'>");
      out.println("<input type='button' value='삭제' onclick='location.href=\"delete?no=" + req.getParameter("no") +
          "\"'>");
      out.println("<input type='button' value='취소' onclick='location.href=\"list\"'>");
      out.println("</form>");
      out.println("</body></html>");
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//    req.setCharacterEncoding("UTF-8");
    String query = "update members set email=?, mname=?, mod_date=now() where MNO=?";

    try {
      Class.forName(sc.getInitParameter("driver"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    try (Connection connection = DriverManager.getConnection(
        sc.getInitParameter("url"),
        sc.getInitParameter("username"),
        sc.getInitParameter("password"));
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, req.getParameter("email"));
      preparedStatement.setString(2, req.getParameter("name"));
      preparedStatement.setInt(3, Integer.parseInt(req.getParameter("no")));
      preparedStatement.executeUpdate();

      resp.sendRedirect("list");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
