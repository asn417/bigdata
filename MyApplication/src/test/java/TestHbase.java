import java.util.Random;

public class TestHbase {

    public static void main(String[] args) {
        Random r=new Random();
        System.out.println("=====================");
        for (int i = 0; i < 500; i++) {
            int a=r.nextInt(22);
            if (a==21){
                System.out.println(a);
            }
        }
    }
}
