package spms.controls;

import spms.annotation.Component;
import spms.dao.PostgresSqlMemberDao;

import java.util.Map;

@Component("/member/list.do")
public class MemberListController implements Controller {

  PostgresSqlMemberDao memberDao;

  public MemberListController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    model.put("members", memberDao.selectList());
    return "/member/MemberList.jsp";
  }

}
