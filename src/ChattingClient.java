import java.io.*;
import java.net.Socket;

public class ChattingClient {
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("127.0.0.1", 10001);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        String echo = null;
                        try {
                            echo = br.readLine();
                            System.out.println("서버로부터 전달받은 문자열: " + echo);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
            thread.start();
            String line = null;
            while((line = keyboard.readLine()) != null) {
                if (line.equals("quit")) {
                    thread.interrupt();
                    break;
                }
                pw.println(line);
                pw.flush();
            }
            pw.close();
            br.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
