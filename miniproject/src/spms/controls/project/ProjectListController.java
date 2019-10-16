package spms.controls.project;

import spms.annotation.Component;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.dao.ProjectDao;

import java.util.Map;

@Component("/project/list.do")
public class ProjectListController implements Controller {

  PostgresSqlProjectDao projectDao;

  public ProjectListController setMemberDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    model.put("projects", projectDao.selectList());
    return "/project/ProjectList.jsp";
  }

}