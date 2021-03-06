# 01. 웹 애플리케이션의 이해

</br>

# 1.1. 데스크톱 애플리케이션

## 1.1.1. 데스크톱 애플리케이션 실습

**예제) 계산기 애플리케이션**

<img src="../capture/스크린샷 2019-08-13 오전 12.33.22.png" width=500>

* **CaculatorFrame.java의 일부 소스 코드의 주요 부분**

  ```java
  public class CalculatorFrame extends JFrame implements ActionListener {
  ```

  > CalculatorFrame 클래스는 윈도우 기능을 갖기 위해 JFrame 클래스를 상속받는다. 또한 '=' 버튼과 'Clear' 버튼의 클릭 이벤트를 처리하는 역할(리스너)도 수행하기 위해 ActionListner 인터페이스를 구현한다.
  >
  > 버튼을 클릭하게 되면 actionPerformed()가 호출된다.

  </br>

  ```java
  JButton equal = new JButton("=");
  JButton clear = new JButton("Clear");
  ...
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == euqal) {
      compute();
    } else {
      clearForm();
    }
  }
  ```

  > 먼저 어떤 버튼이 클릭 됐는지 구분하기 위해 event 객체의 getSource() 반환값을 검사한다.
  >
  > 그 후, 알맞는 버튼의 기능의 메소드를 호출한다.

</br>

## 1.1.2. 데스크톱 애플리케이션의 문제점

* **배포가 번거롭다.** 
  * 기능을 추가하거나 변경할 때마다 다시 배포해야 한다.
* **보안에 취약하다.**

</br>

**해결 방안** : 배포가 번거러운 문제는 자동 갱신을 통하여 해결할 수 있다. 하지만 보안이 취약한 문제는 여전히 남아있다.

</br>

# 1.2. 클라이언트 ・ 서버 애플리케이션

* **계산기 애플리케이션의 클라이언트, 서버 구조**

<img src="../capture/스크린샷 2019-08-13 오전 12.49.07.png" width=600>

* **특징** : 애플리케이션의 기능을 클라이언트와 서버로 분리한다.
  * 업무 변화에 대응하기 쉽다.
  * 서버 쪽에서 데이터베이스에 접속 => 보안이 강화된다.

</br>

## 1.2.1. 계산기 서버 실행

1. **CalculaterServer.java 실행**

* **실행 결과**

  ```
  CalculatorServer startup:
  waiting client...
  ```

* **main()**

  ```java
  public static void main(String[] args) throws Exception {
    // 통신 포트 번호 '8888' 설정 및 서비스 실행
    CalculatorServer app = new CalculatorServer(8888);
    app.service();
  }
  ```

* **service()**

  ```java
  public void service() throws Exception {
    // new ServerSocket(port) : main()에서 설정한 포트번호로 서버 소켓 생성
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("CalculatorServer startup:");
  
    Socket socket = null;
  
    while (true) {
      try {
        System.out.println("waiting client...");
  
        // serverSocket.accept() : 클라이언트의 연결을 기다리다가 연결이 이루어지면
        //  클라이언트의 요청을 처리한다. (블록상태)
        socket = serverSocket.accept();
        System.out.println("connected to client.");
  
        processRequest(socket);
        System.out.println("closed client.");
  
      } catch (Throwable e) {
        System.out.println("connection error!");
      }
    }
  }
  ```

* **processRequest()**

  ```java
  private void processRequest(Socket socket) throws Exception {
    Scanner in = new Scanner(socket.getInputStream());
    PrintStream out = new PrintStream(socket.getOutputStream());
  
    String operator = null;
    double a, b, r;
  
    while (true) {
      try {
        operator = in.nextLine();
  
        if (operator.equals("goodbye")) {
          out.println("goodbye");
          break;
  
        } else {
          a = Double.parseDouble(in.nextLine());
          b = Double.parseDouble(in.nextLine());
          r = 0;
  
          switch (operator) {
            case "+":
              r = a + b;
              break;
            case "-":
              r = a - b;
              break;
            case "*":
              r = a * b;
              break;
            case "/":
              if (b == 0) throw new Exception("0 으로 나눌 수 없습니다!");
              r = a / b;
              break;
            default:
              throw new Exception("해당 연산을 지원하지 않습니다!");
          }
          out.println("success");
          out.println(r);
        }
  
      } catch (Exception err) {
        out.println("failure");
        out.println(err.getMessage());
      }
    }
  
    try {
      out.close();
    } catch (Exception e) {
    }
    try {
      in.close();
    } catch (Exception e) {
    }
    try {
      socket.close();
    } catch (Exception e) {
    }
  }
  ```

  > 클라이언트 소켓으로부터 입출력을 위한 스트림 객체를 준비한다. 무한 반복하면서 클라이언트가 보낸 연산자와 값을 읽어 계산을 수행하고 그 결과를 응답한다.

</br>

## 1.2.2. 계산기 클라이언트 실행

2. **CalculatorFrame.java 실행**

<img src="../capture/스크린샷 2019-08-13 오전 1.05.39.png" width=500>

* **버튼 클릭 이벤트 처리 - actionPerformed(), compute()**

  ```java
  // 이벤트 객체를 조사하여 이벤트가 발생한 버튼이 무엇인지 알아낸다.
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == equal) {
      compute();
    } else {
      clearForm();
    }
  }
  
  // compute()는 CalculatorAgent를 사용하여 연산 수행
  private void compute() {
    double a = Double.parseDouble(operand1.getText());
    double b = Double.parseDouble(operand2.getText());
    double r = 0;
  
    // 0으로 나눈다거나 잘못된 연산자를 입력할 경우의 예외 처리
    try {
      r = calcAgent.compute(operator.getText(), a, b);
      result.setText(Double.toString(r));
  
    } catch (Exception err) {
      JOptionPane.showMessageDialog(
        null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  ```

* **서버 측과의 통신을 담당하는 객체 - CalculatorAgent 클래스**

  ```java
  public CalculatorAgent(String ip, int port) throws Exception {
    socket = new Socket(ip, port);
    out = new PrintStream(socket.getOutputStream());
    in = new Scanner(socket.getInputStream());
  }
  
  // 서버로 부터 계산 결과를 받으면 먼저 사
  public double compute(String operator, double a, double b) throws Exception {
    try {
  		// 사용자가 입력한 연산자와 두 개의 입력값을 서버에 전달한다.
      out.println(operator);
      out.println(a);
      out.println(b);
      out.flush();
  
      // 서버로 부터 계산 결과를 받으면 먼저 상태를 조사한다.
      String state = in.nextLine();
      if (state.equals("success")) {
        // 다음 라인을 읽어서 서버가 보낸 값을 리턴
        return Double.parseDouble(in.nextLine());
      } else {
        // 오류 발생시 오류 메시지를 포함한 예외를 던진다.
        throw new Exception(in.nextLine());
      }
    } catch (Exception e) {
      throw e;
    }
  }
  ```

</br>

## 1.2.3. 클라이언트 ・ 서버 구조의 장점

데스크톱 애플리케이션의 기능 일부를 서버에 이관하는 구조로 만들면 **기능 변경이나 추가에 대해 보다 유연하게 대처할 수 있다.**

</br>

## 1.2.4. 문제점과 개선방안

**문제점** : CalculatorServer의 문제는 한 번에 하나의 클라이언트 하고만 연결된다는 것이다. 현재 연결된 클라이언트와의 연결이 끊어질 때까지 다른 클라이언트는 기다려야 한다.

* **동시 작업이 불가한 이유**

  CalculatorServer는 일단 어떤 클라이언트와 연결되면 해당 클라이언트가 연결을 끊을 때까지 다른 클라이언트의 연결 요청을 승인하지 않기 때문이다.

  * **CalculatorServer의 service() 코드**

    ```java
    public void service() throws Exception {
      ...
      while(ture) {
        ...
        // accpet() : 연결을 승인하고 해당 클라이언트와의 대화를 위해 소켓을 리턴한다.
        socket = serverSocket.accpet();
       	...
        // processRequst() : 클라이언트가 연결을 끊을때까지 리턴하지 않게 되어있다.
        processRequest(socket);
        ...
      }
    }
    ```

  * 이 문제를 해결하기 위해 대부분의 서버 프로그램은 **멀티 프로세스 또는 멀티 스레드와 같은 병행처리 방식을 도입한다.**

</br>

**멀티 프로세스(Multi-process)와 멀티 스레드(Multi-thread)**

* **멀티 프로세스** 
  * 클라이언트가 연결 요청을 하면 서버 프로그램은 **자신을 복제하여 클라이언트에 대응하게** 하고, 자신은 다른 클라이언트의 요청을 기다린다.
  * 이 방식은 메모리를 모두 복제하기 때문에 **자원 낭비가 심하다.**
* **멀티 스레드**
  * 클라이언트 요청을 처리하는 **일부 코드만 별도로 분리하여 실행**
  * 메모리 낭비가 적다

</br>

# 1.3. 다중 클라이언트의 요청 처리

스레드를 이용하여 다중 클라이언트의 요청을 처리하는 계산기 서버 만들기

* **다중 클라이언트의 요청의 특징**
  * 클라이언트의 요청 처리 부분을 별도의 작업으로 분리한다.
  * 분리된 작업은 스레드에 정의한다.
  * 다중 클라이언트의 요청이 동시에 병행 처리된다.

</br>

## 1.3.1. 멀티 스레드 적용 실습

<img src="../capture/스크린샷 2019-08-13 오전 1.36.45.png">

</br>

**계산 작업을 스레드로 위임**

* **개선된 CalculatorServer 소스코드**

  ```java
  public void service() throws Exception {
    ...
    while(true) {
      try {
        socket = serverSocket.accept();
        System.out.println("connected to client.");
        
        new CalculatorWorker(socket).start();
        ...
  ```

  > 클라이언트와 연결이 이루어지는 즉시 CalculatorWorker 스레드를 생성하여 작업을 위임한다. 그리고 다시, 대기열에 있는 다른 클라이언트와의 연결을 승인한다.

</br>

**CalculatorWorker의 등장**

* **CalculatorWorker 클래스의 일부 코드**

  ```java
  // 스레드로서 기능을 수행하기 위해 Thread를 상속받는다.
  public class CalculatorWorker extends Thread {
    static int count;
    Socket socket;
    Scanner in;
    PrintStream out;
    int workerId;
    
    public CalculatorWorker(Socket socket) throws Exception {
      // 스레드를 구분하기 위해 클래스 변수 count를 증가시켜 스레드 고유 번호로 할당
      workerId = ++count;
      this.socket = socket;
      // 클라이언트와 입출력을 하기 위한 스트림 객체를 준비한다.
      in = new Scanner(socket.getInputStream());
      out = new PrintStream(socket.getOutputStream());
    }
  }
  ```

</br>

**스레드의 실행과 run()**

* **CalculatorWorker의 run() 메소드**

  스레드를 시작시키면 부모 스레드의 실행 흐름으로부터 분리되어 별개의 흐름으로 run() 코드가 실행된다. 즉, <u>부모 스레드와 자식 스레드가 병행으로 작업을 수행한다.</u>

  ```java
  public void run() {
    System.out.println("[thread-" + workerId + "] processing the client request.");
  
    String operator = null;
    double a, b, r;
  
    while (true) {
      try {
        operator = in.nextLine();
  
        // 클라이언트에서 'goodbye'를 보낼 때까지 무한 반복하며 계산 요청을 처리해준다.
        if (operator.equals("goodbye")) {
          out.println("goodbye");
          break;
  
        } else {
          a = Double.parseDouble(in.nextLine());
          b = Double.parseDouble(in.nextLine());
          r = 0;
  
          switch (operator) {
            case "+":
              r = a + b;
              break;
            case "-":
              r = a - b;
              break;
            case "*":
              r = a * b;
              break;
            case "/":
              if (b == 0) throw new Exception("0 으로 나눌 수 없습니다!");
              r = a / b;
              break;
            default:
              throw new Exception("해당 연산을 지원하지 않습니다!");
          }
          out.println("success");
          out.println(r);
        }
  
      } catch (Exception err) {
        out.println("failure");
        out.println(err.getMessage());
      }
    }
  
    try { out.close(); } catch (Exception e) {}
    try { in.close(); } catch (Exception e) {}
    try { socket.close(); } catch (Exception e) {}
  
    System.out.println("[thread-" + workerId + "] closed client.");
  }
  ```

</br>

## 1.3.2. 문제점과 개선방안

* **문제점**
  * C/S 환경에서의 프로그래밍은 데스크톱 애플리케이션보다 더 복잡하다.
* **개선방안**
  * 네트워크 프로그래밍, 스레드 프로그래밍, 애플리케이션 자원을 관리하기 위한 프로그래밍들을 자동화하면 프로그래밍이 훨씬 간결해지고 쉬워진다.

</br>

# 1.4. 클라이언트 ・ 서버 아키텍처의 진화

지금의 웹 애플리케이션 시대에서의 서버는 비즈니스 로직을 수행하는 애플리케이션 서버를 포함하는 말이다.

</br>

## 1.4.1. 전통적인 클라이언트 ・ 서버 아키텍처

클라이언트와 서버로 역할을 나누어 실행

<img src="../capture/스크린샷 2019-08-13 오전 1.54.11.png" width=600>

> **서버** : 데이터 처리
>
> **클라이언트** : UI와 비즈니스 처리

* **단점**
  * 프로그램이 변경되면 다시 설치해야 된다.
  * 클라이언트가 DBMS로 바로 접속하기 때문에 보안에 취약하다.

</br>

## 1.4.2. 개선된 클라이언트 ・ 서버 아키텍처

클라이언트의 업무 처리 부분은 서버로 이관하고, 클라이언트는 오로지 사용자와의 사용자와의 상호작용을 처리하는 UI 만을 담당

<img src="../capture/스크린샷 2019-08-13 오전 1.59.56.png">

> **클라이언트** : 데이터를 입력받을 화면을 사용자에게 제공 및 입력 데이터 형식 검사, 서버가 원하는 형식으로 변환
>
> **애플리케이션 서버** : DBMS 서버를 이용하여 데이터를 처리하고 클라이언트의 접근을 제어하며 처리해야 할 작업들을 하나의 트랜잭션으로 묶어서 관리
>
> **서버** : 데이터베이스

* **특징**
  * DB 접속정보가 노출되는 사고를 막을 수 있다.
  * 서버에서 기능 변경을 하더라도 바로 클라이언트에 적용이 가능하다.

</br>

# 1.5. 웹 애플리케이션 아키텍처의 특징

<img src="../capture/스크린샷 2019-08-13 오전 2.10.44.png">

 **웹 애플리케이션 서버 구조**

* 클라이언트와의 통신은 웹 서버가 전담 => 네트워크 및 멀티 스레드 프로그래밍으로부터 탈출
* 애플리케이션 서버는 애플리케이션 실행 및 관리에 집중

</br>

## 1.5.1. 웹 애플리케이션 실습

**출처** : [https://whitepaek.tistory.com/12](https://whitepaek.tistory.com/12)

* **Tomcat 다운받는 방법(MAC)**

  <img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile1.uf.tistory.com%2Fimage%2F998379385BD40D521BB880">

* **IntelliJ 에서 아파치 톰캣 설정 및 JSP 실행 방법**

  1. 새로운 프로젝트를 만든다.

  <img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile9.uf.tistory.com%2Fimage%2F99D5713D5C25055A06FF78" width=600>

  2. Configuration 설정

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile5.uf.tistory.com%2Fimage%2F99A17F415C2506CD19C4BE">

* 좌측에 Project 목록에서 "index.jsp" 파일을 클릭하고, "\<body> \</body>" 태그 안에 자신이 출력하고 싶은 결과를 간략하게 작성한다.
* 우측 상단에 "Add Configuration…" 을 클릭한다.

3. "+" 클릭

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile24.uf.tistory.com%2Fimage%2F99D6FD3D5C25076C0F0B03">

4. "Add New Configuration" 목록이 나오면 하단의 "35 items more" 클릭

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile21.uf.tistory.com%2Fimage%2F99550C335C2507BB0EEBDB">

5. Tomcat Server 클릭 => Local 클릭

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile7.uf.tistory.com%2Fimage%2F9921F54B5C2508430506B8">

6. Tomcat Server의 Configuration 클릭

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile29.uf.tistory.com%2Fimage%2F9930E14F5C2508B91EA992">

7. Apache Tomcat 디렉토리에 있는 톰캣 라이브러리 선택

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile6.uf.tistory.com%2Fimage%2F9971D14B5BD531B92CFA61">

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile7.uf.tistory.com%2Fimage%2F99B7AB455C2509BB25CA1B">

8. Application server에 자신이 선택한 앱이 설정되었으면 "Fix" 클릭

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile21.uf.tistory.com%2Fimage%2F998F214F5C250A71333FEC">

9. "Application context"를 "/"로 수정 후 "OK" 클릭 (브라우저 기본 URL을 "localhost:8080" 으로 설정해주기 위함)

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile2.uf.tistory.com%2Fimage%2F9909CC4F5C250B1B206503">

10. 실행

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile23.uf.tistory.com%2Fimage%2F9901DD405C250C4F0198BA">

11. 프로젝트 수정 후 톰갯을 재시작하지 않고 브라우저 새로고침으로 수정된 결과 반영

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=http%3A%2F%2Fcfile29.uf.tistory.com%2Fimage%2F99FA65375C250CE8019938">

</br>

* **웹 애플리케이션 실행**

<img src="../capture/스크린샷 2019-08-13 오후 2.04.36.png" width=500>

<img src="../capture/스크린샷 2019-08-13 오후 2.05.29.png" width=500>

</br>

## 1.5.2. 웹 애플리케이션의 특징

웹 애플리케이션 방식이 기존의 C/S 환경과 비교해서 무엇이 다른지 살펴보자.

</br>

* **배치**

  * 기존 C/S 환경

    <img src="../capture/스크린샷 2019-08-13 오후 2.09.25.png" width=500>

    > 비즈니스 처리 부분을 서버에 배치하고 UI 처리 부분을 클라이언트에 배치하였기 때문에, UI가 변경 되면 클라이언트 프로그램을 다시 설치해야 한다.

  * 웹 환경

    <img src="../capture/스크린샷 2019-08-13 오후 2.11.50.png" width=700>

    > 비즈니스 로직과 UI 로직을 모두 서버에 배치하여 기능이 추가되거나 변경되더라도 서버쪽만 바꾸면 된다. 하지만 애플리케이션을 실행할 때마다 UI 로직을 내려받아야 하기 때문에 네트워크 오버헤드가 발생한다.

</br>

* **실행**

  웹 브라우저가 설치되어 있고 인터넷에 연결되어 있다면 어디에서라도 애플리케이션을 실행할 수 있다.

  ```sequence
  클라이언트->서버: 1. 사용자 입력폼을 요청한다
  서버->서버: 2. 입력폼을 만든다.
  서버-->클라이언트: 3. 입력폼을 전달한다.
  클라이언트->클라이언트: 4. 입력폼을 화면에 출력한다.
  클라이언트->서버: 5. 사용자가 입력한 값을 보낸다.
  서버->서버: 6. 계산 후 결과 화면을 만든다.
  서버-->클라이언트: 7. 결과 화면을 출력한다.
  ```

</br>

* **개발**

  * 기존 C/S 환경에서의 개발

  <img src="../capture/스크린샷 2019-08-13 오후 11.02.00.png" width=700>

  > 네트워크 프로그래밍과 멀티 스레드 프로그래밍이 필요하다

  * 웝 브라우저와 웹 서버의 도입

  <img src="../capture/스크린샷 2019-08-13 오후 11.06.25.png">

  > 웹 브라우저와 웹 서버가 네트워크, 멀티 스레드 프로그래밍을 대신 처리해줘서 개발자는 단지 어떤 업무를 처리하고 무엇을 출력할 것인가에 대해서만 개발하면 된다.

</br>

* **클라이언트 소스 분석**

  calculator.html

  ```html
  <!DOCTYPE html>
  <html>
  <head>
      <meta charset="UTF-8">
      <title>Insert title here</title>
  </head>
  <body>
  <h1>계산기</h1>
  <form action="calc" method="post">
      <input type="text" name="v1" style="width: 50px;">
      <select name="op">
          <option value="+">+</option>
          <option value="-">-</option>
          <option value="*">*</option>
          <option value="/">/</option>
      </select>
      <input type="text" name="v2" style="width: 50px;">
      <input type="submit" value="=">
  </form>
  </body>
  </html>
  ```

  * **\<h1>계산기\</h1>** : h1 태그는 화면에 제목을 표시할 때 사용한다.
  * **\<form>…\</form>** : form 태그는 입력폼을 만든다.
  * **\<input type="입력유형">** : input 태그는 유형에 따라 입력상자(text)나 체크 박스(checkbox), 라디오 버튼(radio), 암호 입력상자(password), 데이터 전송 버튼(submit), 입력상자 초기화 버튼(reset), 파일 업로드 버튼(file) 등을 만들 때 사용
  * **\<select>…\</select>** : select 태그는 여러 항목 중에서 하나를 선택하는 콤보 상자를 만든다.

</br>

* **서버 소스 분석**

  CalculatorServlet.java

  ```java
  package understanding_web_applications;
  
  import java.io.IOException;
  import java.io.PrintWriter;
  
  import javax.servlet.GenericServlet;
  import javax.servlet.ServletException;
  import javax.servlet.ServletRequest;
  import javax.servlet.ServletResponse;
  import javax.servlet.annotation.WebServlet;
  
  @WebServlet("/calc")
  @SuppressWarnings("serial")
  public class CalculatorServlet extends GenericServlet {
  
    @Override
    public void service(
        ServletRequest request, ServletResponse response)
        throws ServletException, IOException {
      String operator = request.getParameter("op");
      int v1 = Integer.parseInt(request.getParameter("v1"));
      int v2 = Integer.parseInt(request.getParameter("v2"));
      int result = 0;
  
      response.setContentType("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();
  
      switch (operator) {
        case "+":
          result = v1 + v2;
          break;
        case "-":
          result = v1 - v2;
          break;
        case "*":
          result = v1 * v2;
          break;
        case "/":
          if (v2 == 0) {
            out.println("0 으로 나눌 수 없습니다!");
            return;
          }
  
          result = v1 / v2;
          break;
      }
  
      out.println(v1 + " " + operator + " " + v2 + " = " + result);
    }
  
  }
  ```

  > 클라이언트가 보낸 데이터를 받아서 연산을 수행하고 그 결과를 출력하는 클래스이다.

</br>

* **웹 애플리케이션의 등장 이유**

  예전보다 더 자주 더 많이 시스템이 변경되기 때문에, 기존의 C/S 환경은 매번 클라이언트 프로그램을 재설치해야 하므로 이러한 문제를 처리할 방안으로 나온것이 웹 애플리케이션이다.

</br>

## 1.5.3. 문제점과 개선방안

* **문제점**
  * 매번 사용자가 출력 화면을 서버로부터 내려받아야 한다는점
  * 이로 인해, 서버 및 네트워크 자원에 대한 오버헤드 발생
* **개선 방안**
  * **AJAX(Asynchronous JavaScrpit and XML)** : 같은 화면에서 데이터만 바뀔 때는, 서버에서 UI 전체를 받아오기 보다는 데이터만 받아오는 것이 효율적이다. 즉, 화면은 그대로 두고 데이터만 받아오는 기술이다.
  * **변화에 유연한 대응이 가능한 애플리케이션 아키텍처** : 비즈니스의 변화에 유연하게 대응할 수 있는 구조로 애플리케이션을 설계해야 한다. (Ex: MVC 아키텍처, ...)

</br>

# 1.6. 정리

* **'데스크톱 애플리케이션, 개인용 애플리케이션에서는 최고의 아키텍처'**
* **"C/S 애플리케이션, 동시 작업을 요구하는 기업용 애플리케이션에 적합!"**
* **"웹 애플리케이션, 매우 유연한 사용 환경을 제공, 플랫폼 간에 매끈한 연결 지원"**