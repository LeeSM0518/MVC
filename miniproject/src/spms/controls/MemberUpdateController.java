package spms.controls;

import spms.dao.MemberDao;
import spms.vo.Member;

import java.util.Map;

public class MemberUpdateController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    MemberDao memberDao = (MemberDao) model.get("memberDao");
    Member member;
    if (model.get("member") == null) {
      member = memberDao.selectOne(Integer.parseInt(String.valueOf(model.get("no"))));
      model.put("member", member);
      return "/member/MemberUpdate.jsp";
    } else {
      member = (Member) model.get("member");
      memberDao.update(member);

      return "redirect:list.do";
    }
  }

}
