package spms.servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

// 서블릿을 만들고자 서블릿 어노테이션을 쓰고
// GenericServlet 을 상속받는다.
@WebServlet("/member/list")
public class MemberListServlet extends HttpServlet {

  private ServletContext sc;

  static {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void init() throws ServletException {
    super.init();
    sc = this.getServletContext();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String query = "select mno, mname, email, cre_date" +
        " from members" +
        " order by mno";
    // 데이터 베이스 연결 및 쿼리 실행
    try (Connection connection = DriverManager.getConnection(
        sc.getInitParameter("url"),
        sc.getInitParameter("username"),
        sc.getInitParameter("password"));
         PreparedStatement statement = connection.prepareStatement(query);
         ResultSet resultSet = statement.executeQuery()) {
      resp.setContentType("text/html; charset=UTF-8");

      PrintWriter out = resp.getWriter();
      out.println("<html><head><title>회원목록</title></head>");
      out.println("<body><h1>회원목록</h1>");
      out.println("<p><a href='add'>신규 회원</a></p>");

      while (resultSet.next()) {
        out.println(
            resultSet.getInt("mno") + " , " +
                "<a href='update?no=" + resultSet.getInt("mno") + "'>" +
                resultSet.getString("mname") + "</a> , " +
                resultSet.getString("email") + " , " +
                resultSet.getDate("cre_date") +
            " <a href='delete?no=" + resultSet.getInt("mno") + "'>[삭제]</a><br>");
      }

      out.println("</body></html>");

    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
