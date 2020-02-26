package spms.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import spms.annotation.Component;
import spms.vo.Project;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@Component("projectDao")
public class PostgresSqlProjectDao implements ProjectDao {

  private SqlSessionFactory sqlSessionFactory;

  public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionFactory = sqlSessionFactory;
  }

  @Override
  public List<Project> selectList(HashMap<String, Object> paramMap) throws Exception {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      return sqlSession.selectList("spms.dao.ProjectDao.selectList", paramMap);
    }
  }

  @Override
  public int insert(Project project) throws Exception {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      int count = sqlSession.insert("spms.dao.ProjectDao.insert", project);
      return count;
    }
  }

  @Override
  public Project selectOne(int no) throws Exception {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      return sqlSession.selectOne("spms.dao.ProjectDao.selectOne", no);
    }
  }

  @Override
  public int update(Project project) throws Exception {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      // 프로젝트 정보를 가져온다.
      Project original = sqlSession.selectOne("spms.dao.ProjectDao.selectOne",
          project.getNo());

      // update 문에 전달할 map 객체 준비
      Hashtable<String, Object> paramMap = new Hashtable<>();

      // 기존 객체 정보와 매개변수로 받은 객체가
      // 변경된 값들이 있는지 확인
      if (!project.getTitle().equals(original.getTitle())) {
        paramMap.put("title", project.getTitle());
      }
      if (!project.getContent().equals(original.getContent())) {
        paramMap.put("content", project.getContent());
      }
      if (project.getStartDate().compareTo(original.getStartDate()) != 0) {
        paramMap.put("startDate", project.getStartDate());
      }
      if (project.getEndDate().compareTo(original.getEndDate()) != 0) {
        paramMap.put("endDate", project.getEndDate());
      }
      if (project.getState() != original.getState()) {
        paramMap.put("state", project.getState());
      }
      if (!project.getTags().equals(original.getTags())) {
        paramMap.put("tags", project.getTags());
      }

      // Map 객체에 저장된 값이 있다면 UPDATE 실행
      if (paramMap.size() > 0) {
        paramMap.put("no", project.getNo());
        int count = sqlSession.update("spms.dao.ProjectDao.update", paramMap);

        sqlSession.commit();
        return count;
      } else {
        return 0;
      }
    }
  }

  @Override
  public int delete(int no) throws Exception {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      int count = sqlSession.delete("spms.dao.ProjectDao.delete", no);
      sqlSession.commit();
      return count;
    }
  }

}
