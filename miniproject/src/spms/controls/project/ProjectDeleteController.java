package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.dao.ProjectDao;

import java.util.Map;

@Component("/project/delete.do")
public class ProjectDeleteController implements Controller, DataBinding {

  PostgresSqlProjectDao projectDao;

  public ProjectDeleteController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public Object[] getDataBinders() {
    return new Object[] {
        "no", Integer.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Integer no = (Integer) model.get("no");
    projectDao.delete(no);
    return "redirect:list.do";
  }
}
