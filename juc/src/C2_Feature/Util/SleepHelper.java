package C2_Feature.Util;

public class SleepHelper {
    public static void sleep(int second){
        try {
            second*=1000;
            Thread.sleep(second);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
