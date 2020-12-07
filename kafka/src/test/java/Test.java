import java.util.Random;

public class Test {
    @org.junit.Test
    public void test(){
        for (int i = 0; i < 20; i++) {
            System.out.println(new Random().nextInt(10));
        }
    }
}
