public interface IRoomCommand {
    int PORT = 10001;
    String LIVING_ROOM = "대기실";

    String COMMAND = "/cmd";
    String EXIT = "/exit";
    String USER_LIST = "/userlist";
    String MESSAGE = "/to ";

    String ROOM_LIST = "/roomlist";
    String ROOM_CREATE = "/roomcreate ";
    String ROOM_JOIN = "/roomjoin ";
    String ROOM_EXIT = "/roomexit";
    String ROOM_USER_LIST = "/roomuserlist";

    String MESSAGE_EX = "/to <닉네임> <메세지>";
    String ROOM_CREATE_EX = "/roomcreate <방이름>";
    String ROOM_JOIN_EX = "/roomjoin <방이름>";
}
