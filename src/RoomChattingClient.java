import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class RoomChattingClient implements IRoomCommand {
    public static void main(String[] args) {
        Socket sock = null;
        PrintWriter pw = null;
        BufferedReader br = null;
        try {
            sock = new Socket("127.0.0.1", PORT);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            pw = new PrintWriter(new OutputStreamWriter(out));
            br = new BufferedReader(new InputStreamReader(in));
            BufferedReader finalBr = br;

            String nicknameMsg = "닉네임을 입력해주세요: ";
            System.out.print(nicknameMsg);

            String line = null;
            final String[] read = {null};
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        read[0] = finalBr.readLine();
                    } catch (IOException e) {
                        System.out.println("서버가 종료되었습니다.");
                    }
                }
            };
            new Thread(runnable).start();
            while ((line = keyboard.readLine()) != null) {
                if (line.trim().equals("") && line.trim().length() > 0 && line.trim().length() < 11) {
                    System.out.println("다시 입력해주세요.");
                    System.out.print(nicknameMsg);
                } else {
                    pw.println(line);
                    pw.flush();
                    int count = 0;
                    while (read[0] == null && count < 1000000) {
                        count++;
                    }
                    if (read[0] != null) {
                        if (read[0].startsWith("true")) {
                            break;
                        } else if (read[0].startsWith("false")) {
                            read(read, runnable);
                        }
                    } else {
                        read(read, runnable);
                    }
                }
            }

            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        String echo = null;
                        try {
                            echo = finalBr.readLine();
                            System.out.println(echo);
                        } catch (SocketException e) {
                            System.out.println("서버와의 연결이 끊어졌습니다. 실행을 종료합니다.");
                            new RuntimeException(e);
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                            new RuntimeException(e);
                            break;
                        }
                    }
                }
            };
            thread.start();

            avaliableCommand(); // 사용 가능한 명령어 출력

            while ((line = keyboard.readLine()) != null) {
                if (line.startsWith(EXIT)) {
                    pw.println(line);
                    pw.flush();
                    thread.interrupt();
                    break;
                } else if (line.startsWith(COMMAND)) {
                    avaliableCommand();
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

    private static void read(String[] read, Runnable runnable) {
        System.out.println("이미 존재하는 닉네임입니다. 다른 닉네임을 입력해주세요.");
        new Thread(runnable).start();
        read[0] = null;
    }

    private static void avaliableCommand() {
        System.out.println("사용 가능한 명령어");
        System.out.println(COMMAND);
        System.out.println(EXIT);
        System.out.println(USER_LIST);
        System.out.println(MESSAGE + "<-공백까지");
        System.out.println(MESSAGE_EX);
        System.out.println(ROOM_LIST);
        System.out.println(ROOM_CREATE + "<-공백까지");
        System.out.println(ROOM_CREATE_EX);
        System.out.println(ROOM_JOIN + "<-공백까지");
        System.out.println(ROOM_JOIN_EX);
        System.out.println(ROOM_EXIT);
    }
}