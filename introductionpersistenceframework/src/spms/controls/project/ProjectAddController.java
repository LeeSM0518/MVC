package spms.controls.project;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.controls.Controller;
import spms.dao.PostgresSqlProjectDao;
import spms.vo.Project;

import java.util.Map;

@Component("/project/add.do")
public class ProjectAddController implements Controller, DataBinding {

  // DAO 객체
  PostgresSqlProjectDao projectDao;

  // 의존성 주입을 통해 DAO 가 주입된다.
  public ProjectAddController setProjectDao(PostgresSqlProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  // 클라이언트가 보낸 매개변수 값을 인스턴스에 담아서
  //  Map 객체에 저장을 요청하는 메서드이다.
  // 프런트 컨트롤러 쪽에서 Map 객체를 저장하고 excute를 호출할 때
  //  매개변수로 넘길 것이다.
  // project : 데이터 이름
  // Project.class : 데이터 타입
  @Override
  public Object[] getDataBinders() {
    return new Object[]{
        "project", Project.class
    };
  }

  // 프런트 컨트롤러가 페이지 컨트롤러에게 일을
  //  시키기 위해 호출하는 메서드
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
