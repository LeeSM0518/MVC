package spms.servlets;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// 서블릿을 만들고자 서블릿 어노테이션을 쓰고
// GenericServlet 을 상속받는다.
@WebServlet("/member/list")
public class MemberListServlet extends GenericServlet {

  String url = "jdbc:postgresql://arjuna.db.elephantsql.com:5432/fsmfppcj";
  String username = "fsmfppcj";
  String password = "opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH";

  @Override
  public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    String query = "select mno, mname, email, cre_date" +
        " from members" +
        " order by mno";

    // 데이터 베이스 연결 및 쿼리 실행
    try (Connection connection = DriverManager.getConnection(url, username, password);
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(query)) {
      servletResponse.setContentType("text/html; charset=UTF-8");

      PrintWriter out = servletResponse.getWriter();
      out.println("<html><head><title>회원목록</title></head>");
      out.println("<body><h1>회원목록</h1>");

      while (resultSet.next()) {
        out.println(
            resultSet.getInt("mno") + " , " +
                resultSet.getString("mname" + " , ") +
                resultSet.getString("email" + " , ") +
                resultSet.getDate("cre_date") + "<br>");
      }

      out.println("</body></html>");

    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
