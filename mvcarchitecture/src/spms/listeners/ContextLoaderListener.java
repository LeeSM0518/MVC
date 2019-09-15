package spms.listeners;

import spms.dao.MemberDao;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;

@WebListener
public class ContextLoaderListener implements ServletContextListener {

  Connection conn;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      ServletContext sc = sce.getServletContext();
      sc.setRequestCharacterEncoding("UTF-8");

      Class.forName(sc.getInitParameter("driver"));
      conn = DriverManager.getConnection(
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));

      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(conn);

      sc.setAttribute("memberDao", memberDao);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    try {
      conn.close();
    } catch (Exception e) {}
  }

}
