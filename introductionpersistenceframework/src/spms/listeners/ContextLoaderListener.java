package spms.listeners;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import spms.context.ApplicationContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.InputStream;

@WebListener // 리스너 배치
public class ContextLoaderListener implements ServletContextListener {

  static ApplicationContext applicationContext;

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      applicationContext = new ApplicationContext();

      // mybatis 설정 파일 경로 저장
      String resource = "spms/dao/mybatis-config.xml";
      // getResourceAsStream 을 통해 설정 파일의 입력 스트림을 가져온다.
      InputStream inputStream = Resources.getResourceAsStream(resource);
      // SqlSessionFactory 를 Builder의 bind를 통해 가져온다.
      SqlSessionFactory sqlSessionFactory =
          new SqlSessionFactoryBuilder().build(inputStream);

      // ApplicationContext에 factory 객체를 등록
      applicationContext.addBean("sqlSessionFactory", sqlSessionFactory);

      ServletContext sc = sce.getServletContext();
      // request의 인코딩 방식을 지정
      sc.setRequestCharacterEncoding("UTF-8");

      // 프로퍼티 파일을 경로를 가져온다
      String propertiesPath = sc.getRealPath(
          sc.getInitParameter("contextConfigLocation"));
      // 프로퍼티 파일 대로 객체 생성
      applicationContext.prepareObjectsByProperties(propertiesPath);
      // 애노테이션 설정 대로 객체 생성
      applicationContext.prepareObjectsByAnnotation("");
      // 의존 객체 주입
      applicationContext.injectDependency();

    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }

}
