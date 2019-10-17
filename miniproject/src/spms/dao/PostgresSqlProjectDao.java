package spms.dao;

import spms.annotation.Component;
import spms.vo.Project;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component("projectDao")
public class PostgresSqlProjectDao implements ProjectDao {

  private DataSource ds;

  public void setDataSource(DataSource ds) {
    this.ds = ds;
  }

  @Override
  public List<Project> selectList() throws Exception {
    String query = "select pno, pname, sta_date, end_date, state" +
        " from projects" +
        " order by pno desc";
    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
      ArrayList<Project> projects = new ArrayList<>();

      while (rs.next()) {
        projects.add(new Project()
            .setNo(rs.getInt("pno"))
            .setTitle(rs.getString("pname"))
            .setStartDate(rs.getDate("sta_date"))
            .setEndDate(rs.getDate("end_date"))
            .setState(rs.getInt("state")));
      }
      return projects;
    }
  }

  @Override
  public int insert(Project project) throws Exception {
    String query = "insert into projects"
        + " (pname, content, sta_date, end_date, state, cre_date, tags)"
        + " values (?, ?, ?, ?, 0, now(), ?)";

    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, project.getTitle());
      ps.setString(2, project.getContent());
      ps.setDate(3, new java.sql.Date(project.getStartDate().getTime()));
      ps.setDate(4, new java.sql.Date(project.getEndDate().getTime()));
      ps.setString(5, project.getTags());

      return ps.executeUpdate();
    }
  }

  @Override
  public Project selectOne(int no) throws Exception {
    String query = "select pno, pname, content, sta_date, end_date, state, cre_date, tags"
        + " from projects where pno=" + no;
    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
      if (rs.next()) {
        return new Project()
            .setNo(rs.getInt("pno"))
            .setTitle(rs.getString("pname"))
            .setContent(rs.getString("content"))
            .setStartDate(rs.getDate("sta_date"))
            .setEndDate(rs.getDate("end_date"))
            .setCreatedDate(rs.getDate("cre_date"))
            .setTags(rs.getString("tags"));
      } else {
        throw new Exception("해당 번호의 프로젝트를 찾을 수 없습니다.");
      }
    }
  }

  @Override
  public int update(Project project) throws Exception {
    String query = "update projects set " +
        " pname=?," +
        " content=?," +
        " sta_date=?," +
        " end_date=?," +
        " state=?," +
        " tags=?" +
        " where pno=?";

    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, project.getTitle());
      ps.setString(2, project.getContent());
      ps.setDate(3, new java.sql.Date(project.getStartDate().getTime()));
      ps.setDate(4, new java.sql.Date(project.getEndDate().getTime()));
      ps.setInt(5, project.getState());
      ps.setString(6, project.getTags());
      ps.setInt(7, project.getNo());

      return ps.executeUpdate();
    }
  }

  @Override
  public int delete(int no) throws Exception {
    String query = "delete from projects where pno=" + no;
    try (Connection conn = ds.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      return ps.executeUpdate();
    }
  }

}
