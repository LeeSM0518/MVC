package spms.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.ServerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/member/add")
public class MemberAddServlet extends HttpServlet {

  private String url = "jdbc:postgresql://arjuna.db.elephantsql.com:5432/";
  private String username = "fsmfppcj";
  private String password = "opXwqwWLpezpFQHX6OWFl3mQW1xf0VqH";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    PrintWriter out = resp.getWriter();
    out.println("<html><head><title>회원 등록</title></head>");
    out.println("<body><h1>회원 등록</h1>");
    out.println("<form action='add' method='post'>");
    out.println("이름: <input type='text' name='name'><br>");
    out.println("이메일: <input type='text' name='email'><br>");
    out.println("암호: <input type='password' name='password'><br>");
    out.println("<input type='submit' value='추가'>");
    out.println("<input type='reset' value='취소'>");
    out.println("</form>");
    out.println("</body></html>");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String query = "insert into members (email, pwd, mname, cre_date, mod_date) values" +
        " (?, ?, ?, NOW(), NOW())";

    try (Connection connection = DriverManager.getConnection(url, username, password);
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      // 입력 매개변수의 값 설정
      preparedStatement.setString(1, req.getParameter("email"));
      preparedStatement.setString(2, req.getParameter("password"));
      preparedStatement.setString(3, req.getParameter("name"));
      preparedStatement.executeUpdate();

      resp.sendRedirect("list");

      // 리다이렉트는 HTML 을 출력하지 않는다.
//      resp.setContentType("text/html; charset=UTF-8");
//      PrintWriter out = resp.getWriter();
//      out.println("<html><head><title>회원등록결과</title>");
//      out.println("<meta http-equiv='Refresh' content='1; url=list'>");
//      out.println("<body>");
//      out.println("</head>");
//      out.println("<p>등록 성공입니다!</p>");
//      out.println("</body></html>");
//
//      // 리프래시 정보를 응답 헤더에 추가
//      // resp.addHeader("Refresh", "1;url=list");

    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}