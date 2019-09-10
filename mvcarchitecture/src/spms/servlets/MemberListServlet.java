package spms.servlets;

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
      ArrayList<Member> members = new ArrayList<>();

      // 데이터베이스에서 회원 정보를 가져와 Member 에 담는다.
      // 그리고 Member 객체를 ArrayList 에 추가한다.
      while (resultSet.next()) {
        members.add(new Member()
        .setNo(resultSet.getInt("mno"))
        .setName(resultSet.getString("mname"))
        .setEmail(resultSet.getString("email"))
        .setCreateDate(resultSet.getDate("cre_date")));
      }

      // request 에 회원 목록 데이터 보관한다.
      req.setAttribute("members", members);

      // JSP 로 출력을 위임한다.
      RequestDispatcher rd = req.getRequestDispatcher(
          "/member/MemberList.jsp");
      rd.include(req, resp);

    } catch (Exception e) {
//      throw new ServletException(e);
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }
}
