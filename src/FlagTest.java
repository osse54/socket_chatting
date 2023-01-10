import java.util.HashMap;
import java.util.Map;

public class FlagTest {
    public static void main(String[] args) {
        int flag = 1<<26;

        System.out.println(flag & 1<<25);
        System.out.println(flag & 1<<26);
        System.out.println((flag & 1<<25) != 0);
        System.out.println((flag & 1<<26) != 0);
        System.out.println(false && false);
    }
}
