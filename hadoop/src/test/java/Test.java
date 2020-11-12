import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @Author: wangsen
 * @Date: 2020/11/12 11:11
 * @Description:
 **/
public class Test {
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now(Clock.system(ZoneId.of("Asia/Shanghai")));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        System.out.println(now);
        System.out.println(now.format(dtf));
    }
}
