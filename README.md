# Java Web Development Repository
MVC 아키텍처, 마이바티스 학습 저장소
</br>
# 목차
01. [웹 애플리케이션의 이해](https://github.com/LeeSM0518/MVC/tree/master/understandingwebapplications)
02. [웹 프로그래밍 기초 다지기](https://github.com/LeeSM0518/MVC/tree/master/webprogrammingbasic)
03. [서블릿 프로그래밍](https://github.com/LeeSM0518/MVC/tree/master/servletprogramming)
04. [서블릿과 JDBC](https://github.com/LeeSM0518/MVC/tree/master/servletandjdbc)
05. [MVC 아키텍처](https://github.com/LeeSM0518/MVC/tree/master/mvcarchitecture)
06. [미니 MVC 프레임워크 만들기](https://github.com/LeeSM0518/MVC/tree/master/miniproject)
07. [퍼시스턴스 프레임워크의 도입](https://github.com/LeeSM0518/MVC/tree/master/introductionpersistenceframework)
</br>
# 최종 코드 분석

* **src**
  * **annotation**
    * [Component (@interface)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#component-interface)
  * **bind**
    * [DataBinding (interface)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#databinding-interface)
    * [ServletRequestDataBinder (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#servletrequestdatabinder-class)
  * **context**
    * [ApplicationContext (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#applicationcontext-class)
  * **controls**
    * **project**
      * [ProjectAddController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#projectaddcontroller-class)
      * [ProjectDeleteController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#projectdeletecontroller-class)
      * [ProjectListController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#projectlistcontroller-class)
      * [ProjectUpdateController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#projectupdatecontroller-class)
    * [Controller (interface)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#controller-interface)
    * [LogInController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#logincontroller-class)
    * [LogOutController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#logoutcontroller-class)
    * [MemberAddController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#memberaddcontroller-class)
    * [MemberDeleteController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#memberdeletecontroller-class)
    * [MemberListController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#memberlistcontroller-class)
    * [MemberUpdateController (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#memberupdatecontroller-class)
  * **dao**
    * [db.properties (properties)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#dbproperties)
    * [MemberDao (interface)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#memberdao-interface)
    * [mybatis-config (XML)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#mybatis-config-xml)
    * [PostgresSqlMemberDao (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#postgressqlmemberdao-class)
    * [PostgresSqlMemberDao (XML)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#postgressqlmemberdao-xml)
    * [PostgresSqlProjectDao (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#postgressqlprojectdao-class)
    * [PostgresSqlProjectDao (XML)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#postgressqlprojectdao-xml)
    * [ProjectDao (interface)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#projectdao-interface)
  * **listeners**
    * [ContextLoaderListener (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#contextloaderlistener-class)
  * **servlets**
    * [DispatcherServlet (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#dispatcherservlet-class)
  * **vo**
    * [Member (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#member-class)
    * [Project (class)](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#project-class)
  * [log4j.properties](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#log4jproperties)

<br>

* **web**
  * **auth**
    * [LogInFail.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#loginfailjsp)
    * [LogInForm.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#loginformjsp)
  * **member**
    * [MemberAdd.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#memberaddjsp)
    * [MemberList.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#memberlistjsp)
    * [MemberUpdate.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#memberupdatejsp)
  * **project**
    * [ProjectForm.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#projectformjsp)
    * [ProjectList.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#projectlistjsp)
    * [ProjectUpdateForm.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#projectupdateformjsp)
  * **WEB-INF**
    * [web.xml](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#webxml)
  * [Error.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#errorjsp)
  * [Header.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#headerjsp)
  * [Tail.jsp](https://github.com/LeeSM0518/MVC/blob/master/CodeSummary.md#tailjsp)
