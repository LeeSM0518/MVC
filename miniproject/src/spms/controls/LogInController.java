package spms.controls;

import spms.bind.DataBinding;
import spms.dao.PostgresSqlMemberDao;
import spms.vo.Member;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class LogInController implements Controller, DataBinding {
  PostgresSqlMemberDao memberDao;

  public LogInController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Member member = (Member) model.get("member");
    if (member.getEmail() == null) {
      return "/auth/LogInForm.jsp";
    } else {
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

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "member", spms.vo.Member.class
    };
  }
}
