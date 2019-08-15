package web_programming_basic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class SimpeHttpClient {

  public static void main(String[] args) throws Exception {
    // 소켓 및 입출력 스트림 준비
    Socket socket = new Socket("www.kostat.go.kr", 80);
    BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    PrintStream out = new PrintStream(
        socket.getOutputStream());

    // 요청라인 출력
    out.println("GET / HTTP/1.1");

    // 헤더정보 출력
    out.println("Host: www.kostat.go.kr");
    out.println("User-Agent: Mocintosh; Intel Mac OS X 10_14_6)"
    + " AppleWebKit/537.36 (KHTML, like Gecko)"
    + " Chrome/75.0.3770.142 Safari/537.36");

    // 공백라인 출력
    out.println();

    // 응답 내용 출력
    String line = null;
    while ((line = in.readLine()) != null) {
      System.out.println(line);
    }

    in.close();
    out.close();
    socket.close();
  }

}
