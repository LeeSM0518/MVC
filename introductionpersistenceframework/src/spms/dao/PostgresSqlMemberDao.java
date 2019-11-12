package spms.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import spms.annotation.Component;
import spms.vo.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@Component("memberDao")
public class PostgresSqlMemberDao implements MemberDao {

  SqlSessionFactory sqlSessionFactory;

  public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionFactory = sqlSessionFactory;
  }

  public List<Member> selectList(HashMap<String, Object> paramMap) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      return sqlSession.selectList("spms.dao.MemberDao.selectList", paramMap);
    }
  }

  public int insert(Member member) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      int count = sqlSession.insert("spms.dao.MemberDao.insert", member);
      sqlSession.commit();
      return count;
    }
  }

  public Member selectOne(int no) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      return sqlSession.selectOne("spms.dao.MemberDao.selectOne", no);
    }
  }

  public int update(Member member) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Member original = sqlSession.selectOne("spms.dao.MemberDao.selectOne", member.getNo());

      Hashtable<String, Object> paramMap = new Hashtable<>();
      if (!member.getName().equals(original.getName()))
        paramMap.put("name", member.getName());
      if (!member.getEmail().equals(original.getEmail())) {
        paramMap.put("email", member.getEmail());
      }
      if (paramMap.size() > 0) {
        paramMap.put("no", member.getNo());
        int count = sqlSession.update("spms.dao.MemberDao.update", paramMap);

        sqlSession.commit();
        return count;
      } else {
        return 0;
      }
    }
  }

  public int delete(int no) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
      int count = sqlSession.delete("spms.dao.MemberDao.delete", no);
      sqlSession.commit();
      return count;
    }
  }

  public Member exist(String email, String password) {
    HashMap<String, String> paramMap = new HashMap<>();
    paramMap.put("email", email);
    paramMap.put("password", password);

    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      return sqlSession.selectOne("spms.dao.MemberDao.exist", paramMap);
    }
  }

}
