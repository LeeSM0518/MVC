package spms.servlets;

import spms.vo.Member;

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
import java.sql.ResultSet;

@WebServlet("/member/update")
public class MemberUpdateServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String query = "select mno, email, mname, cre_date from members" +
        " where mno=" + req.getParameter("no");
    ServletContext sc = this.getServletContext();
    Connection conn = (Connection) sc.getAttribute("conn");

    try (PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
      rs.next();
      Member member = new Member()
          .setName(rs.getString("mname"))
          .setEmail(rs.getString("email"))
          .setNo(rs.getInt("mno"))
          .setCreateDate(rs.getDate("cre_date"));
      req.setAttribute("updateMember", member);
      RequestDispatcher rd = req.getRequestDispatcher("/member/MemberUpdate.jsp");
      rd.forward(req, resp);
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String query = "update members set email=?, mname=?, mod_date=now() where mno=?";
    ServletContext sc = this.getServletContext();
    Connection conn = (Connection) sc.getAttribute("conn");

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, req.getParameter("email"));
      ps.setString(2, req.getParameter("name"));
      ps.setInt(3, Integer.parseInt(req.getParameter("no")));
      ps.executeUpdate();

      resp.sendRedirect("list");
    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", e);
      RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
      rd.forward(req, resp);
    }
  }
}
