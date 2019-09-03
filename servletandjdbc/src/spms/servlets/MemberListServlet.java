package spms.servlets;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

// 서블릿을 만들고자 서블릿 어노테이션을 쓰고
// GenericServlet 을 상속받는다.
@WebServlet("/member/list")
public class MemberListServlet extends GenericServlet {

  private String url = "jdbc:postgresql://arjuna.db.elephantsql.com:5432/";
  private String username = "fsmfppcj";
  private String password = "opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH";

  static {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      System.err.println("PostgreSQL DataSource unable to load PostgreSQL JDBC Driver");
    }
  }

  @Override
  public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    String query = "select mno, mname, email, cre_date" +
        " from members" +
        " order by mno";

    // 데이터 베이스 연결 및 쿼리 실행
    try (Connection connection = DriverManager.getConnection(url, username, password);
         PreparedStatement statement = connection.prepareStatement(query);
         ResultSet resultSet = statement.executeQuery()) {
      servletResponse.setContentType("text/html; charset=UTF-8");

      PrintWriter out = servletResponse.getWriter();
      out.println("<html><head><title>회원목록</title></head>");
      out.println("<body><h1>회원목록</h1>");
      out.println("<p><a href='add'>신규 회원</a></p>");

      while (resultSet.next()) {
        out.println(
            resultSet.getInt("mno") + " , " +
                resultSet.getString("mname") + " , " +
                resultSet.getString("email") + " , " +
                resultSet.getDate("cre_date") + "<br>");
      }

      out.println("</body></html>");

    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
