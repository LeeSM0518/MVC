package spms.servlets;//package spms.servlets;
//
//import spms.dao.MemberDao;
//
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@WebServlet("/member/list")
//public class MemberListServlet extends HttpServlet {
//
//  @Override
//  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//    try {
//      ServletContext sc = this.getServletContext();
//      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
//      req.setAttribute("members", memberDao.selectList());
//      req.setAttribute("viewUrl", "/member/MemberList.jsp");
//    } catch (Exception e) {
//      throw new ServletException(e);
//    }
//  }
//
//}