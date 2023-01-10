import java.io.*;
import java.net.Socket;

public class PersonalChattingClient implements IPersonalCommand {
    public static void main(String[] args) {
        Socket sock = null;
        PrintWriter pw = null;
        BufferedReader br = null;
        try {
            sock = new Socket("127.0.0.1", 10001);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            pw = new PrintWriter(new OutputStreamWriter(out));
            br = new BufferedReader(new InputStreamReader(in));
            BufferedReader finalBr = br;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        String echo = null;
                        try {
                            echo = finalBr.readLine();
                            System.out.println(echo);
                        } catch (IOException e) {
                            e.printStackTrace();
                            new RuntimeException(e);
                        }
                    }
                }
            };
            thread.start();

            String nicknameMsg = "닉네임을 입력해주세요\n0자 이상 10자 이하로 입력해주세요: ";
            String line = null;

            while ((line = keyboard.readLine()) == null && line.trim().equals("") && line.trim().length() > 0 && line.trim().length() < 11) {
                System.out.println("다시 입력해주세요.");
                System.out.print(nicknameMsg);
            }

            line.replaceAll(" ", ""); // 모든 공백 제거
            System.out.println(line);

            // 닉네임 전송
            pw.println(line);
            pw.flush();

            // 명령어 출력
            System.out.println("사용 가능한 명령어");
            System.out.println(COMMAND);
            System.out.println(EXIT);
            System.out.println(USER_LIST);
            System.out.println(MESSAGE + "<-공백까지");
            System.out.println(MESSAGE_EX);

            while ((line = keyboard.readLine()) != null) {
                if (line.startsWith(EXIT)) {
                    pw.println(line);
                    pw.flush();
                    thread.interrupt();
                    break;
                } else if (line.startsWith(USER_LIST)) {
                    pw.println(line);
                    pw.flush();
                } else if (line.startsWith(COMMAND)) {
                    System.out.println("사용 가능한 명령어");
                    System.out.println(COMMAND);
                    System.out.println(EXIT);
                    System.out.println(USER_LIST);
                } else {
                    pw.println(line);
                    pw.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                pw.close();
                br.close();
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}