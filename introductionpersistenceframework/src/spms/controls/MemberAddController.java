package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.PostgresSqlMemberDao;
import spms.vo.Member;

import java.util.Map;

@Component("/member/add.do")
// DataBinding 인터페이스 구현 선언
public class MemberAddController implements Controller, DataBinding {

  PostgresSqlMemberDao memberDao;

  public MemberAddController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Member member = (Member)model.get("member");
    if (member.getEmail() == null) {
      return "/member/MemberAdd.jsp";
    } else {
      memberDao.insert(member);

      return "redirect:list.do";
    }
  }

  // 메서드 구현
  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "member", Member.class
    };
  }

}
