import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PersonalChattingServer implements IPersonalCommand {
    public static List<User> userList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(10001);
            int count = 1;
            while (true) {
                System.out.println("접속을 기다립니다: " + count);
                Socket sock = server.accept();
                new Thread() {
                    @Override
                    public void run() {
                        chatting(sock);
                    }
                }.start();
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void chatting(Socket sock) {
        PrintWriter pw = null;
        BufferedReader br = null;
        try {
            InetAddress inetAddress = sock.getInetAddress();
            System.out.println(inetAddress.getHostAddress() + "로부터 접속했습니다.");
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            pw = new PrintWriter(new OutputStreamWriter(out));
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            String nickname = null;
            if ((line = br.readLine()) != null) {
                userList.add(new User(line, pw));
                System.out.println(line + "님이 접속하셨습니다.");
                pw.println(line + "님 환영합니다.");
                pw.flush();
                nickname = line;
            }
            while ((line = br.readLine()) != null) {
                if (line.startsWith(EXIT)) { // 나가기
                    System.out.println(line + "님이 나가셨습니다.");
                    userList.remove(new User(line, null));
                    break;
                } else if (line.startsWith(USER_LIST)) { // 유저 목록
                    pw.println("현재 접속중인 유저 목록입니다.");
                    pw.flush();
                    pw.println(getUserList());
                    pw.flush();
                } else if (line.startsWith(MESSAGE)) { // 개인 채팅
                    System.out.println(line);
                    line = line.replace(MESSAGE, "");
                    String[] arr = line.split(" ");
                    String toNickname = arr[1];
                    String message = arr[2];
                    User toUser = getUser(toNickname);
                    if (toUser != null) {
                        int index = userList.indexOf(toUser);
                        toUser = userList.get(index);
                        toUser.getPw().println(nickname + ": " + message);
                        toUser.getPw().flush();
                    } else {
                        pw.println("해당 유저가 접속중이지 않습니다.");
                        pw.flush();
                    }
                } else {
                    sendAll(line);
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
                throw new RuntimeException(e);
            }
        }
    }

    public static void sendAll(String msg) {
        for (User user : userList) {
            user.getPw().println(msg);
            user.getPw().flush();
        }
    }

    public static String getUserList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userList.size(); i++) {
            sb.append(userList.get(i).getNickname());
            if (i % 5 == 4) {
                sb.append("\n");
            } else {
                if (i != userList.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    public static User getUser(String nickname) {
        System.out.println("찾는 닉네임: " + nickname);
        for (User user : userList) {
            System.out.println("비교 닉네임: " + user.getNickname());
            if (user.getNickname().equals(nickname)) {
                return user;
            }
        }
        return null;
    }
}