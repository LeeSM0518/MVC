package spms.servlets;//package spms.servlets;
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
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//@WebServlet("/auth/login")
//public class LogInServlet extends HttpServlet {
//
//  @Override
//  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//    // 로그인 URL 저장
//    req.setAttribute("viewUrl", "/auth/LogInForm.jsp");
//  }
//
//  @Override
//  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//    ServletContext sc = this.getServletContext();
//
//    try {
//      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
//      Member member = memberDao.exist(req.getParameter("email"), req.getParameter("password"));
//      if (member != null) {
//        req.setAttribute("member", member);
//        // 로그인 정보 세션에 저장
//        HttpSession session = req.getSession();
//        session.setAttribute("member", member);
//        // list 로 리다이렉트 요청 저장
//        req.setAttribute("viewUrl", "redirect:/member/list.do");
//      } else {
//        // 로그인 실패로 URL 저장
//        req.setAttribute("viewUrl", "/auth/LogInFail.jsp");
//      }
//    } catch (Exception e) {
//      throw new ServletException(e);
//    }
//  }
//
//}
