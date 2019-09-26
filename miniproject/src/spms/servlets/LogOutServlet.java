package spms.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth/logout")
public class LogOutServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // 회원 정보 삭제를 위한 세션 초기화
    HttpSession session = req.getSession();
    session.invalidate();

    // 로그인 url로 리다이렉트 요청 저장
    req.setAttribute("viewUrl", "redirect:login.do");
  }
}
