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

  // SqlSession 을 생성할 객체
  SqlSessionFactory sqlSessionFactory;

  // 의존 주입을 통해 객체가 주입됨
  public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionFactory = sqlSessionFactory;
  }

  public List<Member> selectList(HashMap<String, Object> paramMap) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      // 맵퍼 파일을 통해 selectList 호출
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
      // Member 정보를 가져옴
      Member original = sqlSession.selectOne("spms.dao.MemberDao.selectOne", member.getNo());

      // update 문을 전달할 Map 객체 준비
      Hashtable<String, Object> paramMap = new Hashtable<>();
      if (!member.getName().equals(original.getName()))
        paramMap.put("name", member.getName());
      if (!member.getEmail().equals(original.getEmail())) {
        paramMap.put("email", member.getEmail());
      }
      // Map 객체에 저장된 값이 있다면 UPDATE 문을 실행 없으면 0 반환
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
