package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.PostgresSqlMemberDao;

import java.util.Map;

@Component("/member/delete.do")
public class MemberDeleteController implements Controller, DataBinding {

  PostgresSqlMemberDao memberDao;

  public MemberDeleteController setMemberDao(PostgresSqlMemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    memberDao.delete(Integer.parseInt(String.valueOf(model.get("no"))));
    return "redirect:list.do";
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "no", Integer.class
    };
  }

}
