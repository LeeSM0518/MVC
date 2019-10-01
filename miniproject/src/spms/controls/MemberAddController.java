package spms.controls;

import spms.dao.MemberDao;
import spms.vo.Member;

import java.util.Map;

public class MemberAddController implements Controller {

  MemberDao memberDao;

  public MemberAddController setMemberDao(MemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    if (model.get("member") == null) {
      return "/member/MemberAdd.jsp";
    } else {
      MemberDao memberDao = (MemberDao) model.get("memberDao");

      Member member = (Member) model.get("member");
      memberDao.insert(member);

      return "redirect:list.do";
    }
  }
}
