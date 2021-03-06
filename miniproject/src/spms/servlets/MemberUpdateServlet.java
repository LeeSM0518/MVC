//package spms.servlets;
//
//import spms.dao.MemberDao;
//import spms.vo.Member;
//
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@WebServlet("/member/update")
//public class MemberUpdateServlet extends HttpServlet {
//
//  @Override
//  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//    ServletContext sc = this.getServletContext();
//    MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
//    try {
//      req.setAttribute("member", memberDao.selectOne(Integer.parseInt(req.getParameter("no"))));
//      req.setAttribute("viewUrl", "/member/MemberUpdate.jsp");
//    } catch (Exception e) {
//      throw new ServletException(e);
//    }
//  }
//
//  @Override
//  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//    ServletContext sc = this.getServletContext();
//    try {
//      Member member = (Member) req.getAttribute("member");
//      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
//      memberDao.update(member);
//
//      req.setAttribute("viewUrl", "redirect:list.do");
//    } catch (Exception e) {
//      throw new ServletException(e);
//    }
//  }
//
//}