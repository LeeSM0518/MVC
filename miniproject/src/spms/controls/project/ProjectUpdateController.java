package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.vo.Project;

import java.util.Map;

@Component("/project/update.do")
public class ProjectUpdateController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectUpdateController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "no", Integer.class,
        "project", spms.vo.Project.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Project project = (Project)model.get("project");

    if (project.getTitle() == null) {
      Integer no = (Integer)model.get("no");
      Project detailInfo = projectDao.selectOne(no);
      model.put("project", detailInfo);
      return "/project/ProjectUpdateForm.jsp";
    } else {
      projectDao.update(project);
      return "redirect:list.do";
    }
  }
}
