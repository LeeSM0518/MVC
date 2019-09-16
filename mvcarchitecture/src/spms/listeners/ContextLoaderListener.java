package spms.listeners;

import org.apache.commons.dbcp2.BasicDataSource;
import spms.dao.MemberDao;
import spms.util.DBConnectionPool;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.SQLException;

@WebListener
public class ContextLoaderListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      ServletContext sc = sce.getServletContext();
      sc.setRequestCharacterEncoding("UTF-8");

      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource)initialContext.lookup("java:comp/env/jdbc/postgresql");

//      ds = new BasicDataSource();
//      ds.setDriverClassName(sc.getInitParameter("driver"));
//      ds.setUrl(sc.getInitParameter("url"));
//      ds.setUsername(sc.getInitParameter("username"));
//      ds.setPassword(sc.getInitParameter("password"));

      MemberDao memberDao = new MemberDao();
      memberDao.setDataSource(ds);

      sc.setAttribute("memberDao", memberDao);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

}
