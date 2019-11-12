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
      sqlSession.commit();
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
      Project original = sqlSession.selectOne("spms.dao.ProjectDao.selectOne",
          project.getNo());

      Hashtable<String, Object> paramMap = new Hashtable<>();

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
