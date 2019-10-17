package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.vo.Project;

import java.util.Map;

@Component("/project/add.do")
public class ProjectAddController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectAddController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "project", Project.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Project project = (Project) model.get("project");
    if (project.getTitle() == null) {
      return "/project/ProjectForm.jsp";
    } else {
      projectDao.insert(project);
      return "redirect:list.do";
    }
  }
}
