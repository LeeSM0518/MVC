package lesson03.servlets;

import javax.servlet.*;
import java.io.IOException;

public class HelloWorld extends GenericServlet {

  ServletConfig config;

  @Override
  public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    System.out.println("service() calls");
  }

}
