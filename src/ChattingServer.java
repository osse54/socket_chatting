import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChattingServer {
    public static List<Socket> list = new ArrayList<>();
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(10001);
            int count = 1;
            while (true) {
                System.out.println("접속을 기다립니다: " + count);
                Socket sock = server.accept();
                list.add(sock);
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
            while((line = br.readLine()) != null) {
                sendAll(line);
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
        for (Socket sock : list) {
            try {
                OutputStream out = sock.getOutputStream();
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                pw.println(msg);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
