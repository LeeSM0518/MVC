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
    HashMap<String, Object> paramMap = new HashMap<>();
    paramMap.put("orderCond", model.get("orderCond"));
    model.put("projects", projectDao.selectList(paramMap));
    return "/project/ProjectList.jsp";
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "orderCond", String.class
    };
  }
}