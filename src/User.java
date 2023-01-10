import java.io.PrintWriter;
import java.util.Objects;

public class User {
    private String nickname;
    private PrintWriter pw;

    public User(String nickname, PrintWriter pw) {
        this.nickname = nickname;
        this.pw = pw;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public String toString() {
        return nickname;
    }
}