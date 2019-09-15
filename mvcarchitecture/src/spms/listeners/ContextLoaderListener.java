package spms.listeners;

import spms.dao.MemberDao;
import spms.util.DBConnectionPool;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextLoaderListener implements ServletContextListener {

  private DBConnectionPool connPool;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      ServletContext sc = sce.getServletContext();
      sc.setRequestCharacterEncoding("UTF-8");

      Class.forName(sc.getInitParameter("driver"));
      connPool = new DBConnectionPool(
          sc.getInitParameter("driver"),
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));

      MemberDao memberDao = new MemberDao();
      memberDao.setDbConnectionPool(connPool);

      sc.setAttribute("memberDao", memberDao);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    connPool.closeAll();
  }

}
