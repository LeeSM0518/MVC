package spms.listeners;

import spms.controls.*;
import spms.dao.PostgresSqlMemberDao;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class ContextLoaderListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      ServletContext sc = sce.getServletContext();
      sc.setRequestCharacterEncoding("UTF-8");

      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/postgresql");

//      MemberDao memberDao = new MemberDao();
      PostgresSqlMemberDao memberDao = new PostgresSqlMemberDao();
      memberDao.setDataSource(ds);

      sc.setAttribute("/auth/login.do",
          new LogInController().setMemberDao(memberDao));
      sc.setAttribute("/auth/logout.do",
          new LogOutController());
      sc.setAttribute("/member/list.do",
          new MemberListController().setMemberDao(memberDao));
      sc.setAttribute("/member/add.do",
          new MemberAddController().setMemberDao(memberDao));
      sc.setAttribute("/member/update.do",
          new MemberUpdateController().setMemberDao(memberDao));
      sc.setAttribute("/member/delete.do",
          new MemberDeleteController().setMemberDao(memberDao));

    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

}
