import java.util.List;

public class Room implements IRoomCommand {
    private String roomName;
    private List<User> userList;

    public Room(String roomName, List<User> userList) {
        this.roomName = roomName;
        this.userList = userList;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void addUser(User user) {
        if (!userList.contains(user)) {
            userList.add(user);
            if (!roomName.equals(LIVING_ROOM)) {
                user.getPw().println("-------------------------------------------------\n[" + roomName + "]방에 입장하셨습니다.\n-------------------------------------------------");
                for (User temp : userList) {
                    temp.getPw().println(user.getNickname() + "님이 입장하셨습니다.");
                    temp.getPw().flush();
                }
            }
        }
    }

    public void removeUser(User user) {
        if (userList.contains(user)) {
            userList.remove(user);
            if (!roomName.equals(LIVING_ROOM)) {
                for (User temp : userList) {
                    temp.getPw().println(user.getNickname() + "님이 퇴장하셨습니다.");
                    temp.getPw().flush();
                }
            }
        }
        if (userList.size() == 0 && !roomName.equals(LIVING_ROOM)) RoomChattingServer.roomList.remove(roomName);
    }
}
