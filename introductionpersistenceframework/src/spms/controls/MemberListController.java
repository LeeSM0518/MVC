package spms.controls;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.MemberDao;
import spms.dao.PostgresSqlMemberDao;

import java.util.HashMap;
import java.util.Map;

@Component("/member/list.do")
public class MemberListController implements Controller, DataBinding {

  MemberDao memberDao;

  public MemberListController setMemberDao(MemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    HashMap<String, Object> paramMap = new HashMap<>();
    paramMap.put("orderCond", model.get("orderCond"));
    model.put("members", memberDao.selectList(paramMap));
    return "/member/MemberList.jsp";
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "orderCond", String.class
    };
  }
}
