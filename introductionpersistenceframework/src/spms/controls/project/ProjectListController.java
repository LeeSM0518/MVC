package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.dao.ProjectDao;

import java.util.HashMap;
import java.util.Map;

@Component("/project/list.do")
public class ProjectListController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectListController setMemberDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    // DAO 에게 정렬 조건 전달
    HashMap<String, Object> paramMap = new HashMap<>();
    paramMap.put("orderCond", model.get("orderCond"));
    model.put("projects", projectDao.selectList(paramMap));
    return "/project/ProjectList.jsp";
  }

  // 클라이언트가 보낸 'orderCond' 값을 받기위함
  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "orderCond", String.class
    };
  }
}