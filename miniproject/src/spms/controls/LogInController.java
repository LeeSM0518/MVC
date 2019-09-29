package spms.controls;

import spms.dao.MemberDao;
import spms.vo.Member;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class LogInController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    if (model.get("member") == null) {
      return "/auth/LogInForm.jsp";
    } else {
      MemberDao memberDao = (MemberDao) model.get("memberDao");
      Member member = (Member) model.get("member");
      Member existMember = memberDao.exist(member.getEmail(), member.getPassword());
      if (existMember != null) {
        ((HttpSession) model.get("session"))
            .setAttribute("member", existMember);
        return "redirect:/member/list.do";
      } else {
        return "/auth/LogInFail.jsp";
      }
    }
  }

}
