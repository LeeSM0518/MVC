package spms.controls;

import spms.dao.MemberDao;
import spms.vo.Member;

import java.util.Map;

public class MemberDeleteController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    MemberDao memberDao = (MemberDao) model.get("memberDao");
    memberDao.delete(Integer.parseInt(String.valueOf(model.get("no"))));
    return "redirect:list.do";
  }

}
