import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class RoomChattingServer implements IRoomCommand {
    public static List<User> userList = new ArrayList<>();
    public static Map<String, Room> roomList = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);
            roomList.put(LIVING_ROOM, new Room(LIVING_ROOM, new ArrayList<>()));
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
        User user = null;
        try {
            InetAddress inetAddress = sock.getInetAddress();
            System.out.println(inetAddress.getHostAddress() + "로부터 접속했습니다.");
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            pw = new PrintWriter(new OutputStreamWriter(out));
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            boolean flag = true;
            while (flag) {
                line = br.readLine();
                if (line == null) {
                    System.out.println("line==null");
                    break;
                }
                System.out.println("line: " + line);
                if (findRoomByNickname(line) == null && line != null && line.trim().length() > 0 && line.trim().length() < 11 && !line.trim().equals("")) {
                    user = new User(line, pw);
                    userList.add(user);             // 유저 목록에 추가
                    roomList.get(LIVING_ROOM).addUser(user);  // 대기실에 추가
                    pw.println("true " + line);
                    pw.flush();
                    flag = false;

                    System.out.println(line + "님이 접속하셨습니다.");
                    pw.println("\n-------------------------------------------------\n대기실에 입장하였습니다.\n-------------------------------------------------\n" + line + "님 환영합니다.");
                    pw.flush();
                    serverStatus();
                } else {
                    System.out.println("닉네임 중복");
                    pw.println("false");
                    pw.flush();
                }
            }

            while ((line = br.readLine()) != null) {
                if (line.startsWith(EXIT)) { // 나가기
                    break;
                } else if (line.startsWith(USER_LIST)) { // 유저 목록
                    pw.println("현재 접속중인 유저 목록입니다.");
                    pw.flush();
                    pw.println(getUserList());
                    pw.flush();
                } else if (line.startsWith(MESSAGE)) { // 개인 채팅
                    System.out.println("개인 채팅: " + line);
                    line = line.replace(MESSAGE, "");
                    String[] arr = line.split(" ");
                    String toNickname = arr[0];
                    String message = line.replace(toNickname + " ", "");
                    User toUser = getUser(toNickname);
                    if (toUser != null) {
                        int index = userList.indexOf(toUser);
                        toUser = userList.get(index);
                        toUser.getPw().println(user.getNickname() + "님의 귓속말: " + message);
                        toUser.getPw().flush();
                        pw.println(toUser.getNickname() + "님에게 귓속말 보내기: " + message);
                        pw.flush();
                    } else {
                        pw.println("해당 유저가 접속중이지 않습니다.");
                        pw.flush();
                    }
                } else if (line.startsWith(ROOM_LIST)) {
                    String str = "";
                    for (Room room : roomList.values()) {
                        if (!room.getRoomName().equals(LIVING_ROOM)) str += room.getRoomName() + "\n";
                    }
                    pw.println(str);
                    pw.flush();
                } else if (line.startsWith(ROOM_CREATE)) {
                    line = line.replace(ROOM_CREATE, "");
                    String[] arr = line.split(" ");
                    String roomName = arr[0];
                    if (roomList.containsKey(roomName)) {
                        pw.println("이미 존재하는 방제목입니다.");
                        pw.flush();
                    } else {
                        findRoomByNickname(user.getNickname()).removeUser(user);
                        Room room = new Room(roomName, new ArrayList<>());
                        roomList.put(roomName, room);
                        pw.println(roomName + "방이 생성되었습니다.");
                        pw.flush();
                        room.addUser(user);
                    }
                } else if (line.startsWith(ROOM_JOIN)) {
                    line = line.replace(ROOM_JOIN, "");
                    String[] arr = line.split(" ");
                    String roomName = arr[0];
                    if (roomList.containsKey(roomName)) {
                        findRoomByNickname(user.getNickname()).removeUser(user);
                        Room room = roomList.get(roomName);
                        room.addUser(user);
                    } else {
                        pw.println("존재하지 않는 방입니다.");
                        pw.flush();
                    }
                } else if (line.startsWith(ROOM_EXIT)) {
                    Room room = findRoomByNickname(user.getNickname());
                    if (room.getRoomName().equals(LIVING_ROOM)) {
                        pw.println("대기실에서 나갈 수 없습니다.");
                        pw.flush();
                    } else {
                        room.removeUser(user);
                        roomList.get(LIVING_ROOM).addUser(user);
                        pw.println("-------------------------------------------------\n대기실로 이동하셨습니다.\n-------------------------------------------------");
                        pw.flush();
                    }
                } else if (line.startsWith(ROOM_USER_LIST)) {
                    Room room = findRoomByNickname(user.getNickname());
                    if (room != null) {
                        List<User> roomUserList = room.getUserList();
                        String str = "";
                        for (User u : roomUserList) {
                            str += u.getNickname() + "\n";
                        }
                        pw.println(str);
                        pw.flush();
                    }
                } else {
                    for (User temp : findRoomByNickname(user.getNickname()).getUserList()) {
                        temp.getPw().println(user.getNickname() + ": " + line);
                        temp.getPw().flush();
                    }
                }
            }
        } catch (SocketException e) { // finally에서 close()를 하기 때문에 여기서는 close()를 하지 않는다.
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (user != null) {
                    System.out.println(user.getNickname() + "님의 클라이언트와 연결이 종료되었습니다.");
                    exit(user);
                }
                pw.close();
                br.close();
                sock.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void exit(User user) {
        findRoomByNickname(user.getNickname()).removeUser(user);
        userList.remove(user);
        System.out.println(user.getNickname() + "님이 접속을 종료했습니다.");
        serverStatus();
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

    public static Room findRoomByNickname(String nickname) {
        for (Room room : roomList.values()) {
            for (User user : room.getUserList()) {
                if (user.getNickname().equals(nickname)) return room;
            }
        }
        return null; // 여기에 도착한다면 문제가 있음. 적어도 0이 return되어야 한다.
    }

    public static Room findRoomByRoomName(String roomName) { // 안쓸지도?
        for (Room room : roomList.values()) {
            if (room.getRoomName().equals(roomName)) return room;
        }
        return null;
    }

    public static void serverStatus() {
        System.out.println("현재 접속자 수: " + userList.size());
        System.out.println("현재 방 수: " + roomList.size());
        for (Room room : roomList.values()) {
            System.out.println(room.getRoomName() + " 방의 인원 수: " + room.getUserList().size());
        }
    }
}