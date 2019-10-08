package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.PostgresSqlMemberDao;
import spms.vo.Member;

import java.util.Map;

@Component("/member/update.do")
public class MemberUpdateController implements Controller, DataBinding {

  PostgresSqlMemberDao memberDao;

  public MemberUpdateController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Member member = (Member) model.get("member");
    if (member.getEmail() == null) {
      member = memberDao.selectOne(Integer.parseInt(String.valueOf(model.get("no"))));
      model.put("member", member);
      return "/member/MemberUpdate.jsp";
    } else {
      memberDao.update(member);
      return "redirect:list.do";
    }
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "no", Integer.class,
        "member", spms.vo.Member.class
    };
  }
}
