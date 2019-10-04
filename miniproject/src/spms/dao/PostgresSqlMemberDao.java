package spms.dao;

import spms.vo.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresSqlMemberDao implements MemberDao {

  private DataSource ds;

  public void setDataSource(DataSource ds) {
    this.ds = ds;
  }

  public List<Member> selectList() throws Exception {
    String query = "select mno, mname, email, cre_date" +
        " from members" +
        " order by mno";
    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
      ArrayList<Member> members = new ArrayList<>();

      while (rs.next()) {
        members.add(new Member()
            .setNo(rs.getInt("mno"))
            .setName(rs.getString("mname"))
            .setEmail(rs.getString("email"))
            .setCreateDate(rs.getDate("cre_date")));
      }
      return members;
    }
  }

  public int insert(Member member) throws Exception {
    int success;
    String query = "insert into members (email, pwd, mname, cre_date, mod_date) values" +
        " (?, ?, ?, now(), now())";

    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, member.getEmail());
      ps.setString(2, member.getPassword());
      ps.setString(3, member.getName());
      success = ps.executeUpdate();
    }

    return success;
  }

  public int delete(int no) throws Exception {
    int success;
    String query = "delete from members where mno=?";

    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setInt(1, no);
      success = ps.executeUpdate();
    }

    return success;
  }

  public Member selectOne(int no) throws Exception {
    Member member;
    String query = "select mno, email, mname, cre_date from members" +
        " where mno=" + no;

    try (Connection conn = ds.getConnection();
    PreparedStatement ps = conn.prepareStatement(query);
    ResultSet rs = ps.executeQuery()) {
      rs.next();
      member = new Member()
          .setNo(no)
          .setEmail(rs.getString("email"))
          .setName(rs.getString("mname"))
          .setCreateDate(rs.getDate("cre_date"));
    }
    return member;
  }

  public int update(Member member) throws Exception {
    int success = 0;
    String query = "update members set email=?, mname=?, mod_date=now() where mno=?";

    try (Connection conn = ds.getConnection();
    PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, member.getEmail());
      ps.setString(2, member.getName());
      ps.setInt(3, member.getNo());
      success = ps.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getSQLState());
      System.out.println(e.getMessage());
    }
    return success;
  }

  public Member exist(String email, String password) throws Exception {
    Member member = null;
    String query = "select mno, mname, cre_date, mod_date from members" +
        " where email=? and pwd=?";

    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, email);
      ps.setString(2, password);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        member = new Member()
            .setName(rs.getString("mname"))
            .setEmail(email)
            .setNo(rs.getInt("mno"))
            .setCreateDate(rs.getDate("cre_date"))
            .setModifiedDate(rs.getDate("mod_date"))
            .setPassword(password);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return member;
  }

}
