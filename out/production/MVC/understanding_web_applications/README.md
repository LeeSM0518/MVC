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

* **계산기 애플리케이션의 클라이언트, 서버 구조 **

  <img src="../capture/스크린샷 2019-08-13 오전 12.49.07.png" width=600>

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

