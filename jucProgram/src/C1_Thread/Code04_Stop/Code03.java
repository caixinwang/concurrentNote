package C1_Thread.Code04_Stop;

import C1_Thread.Util.SleepHelper;

public class Code03 {//volatile
    private static volatile boolean running =true;
    public static void main(String[] args) {
        Thread t=new Thread(()->{
            long i=0L;
            while (running) {
                i++;
            }
            System.out.println("Thread t finished and i = "+i);
        });
        t.start();
        SleepHelper.sleep(1);
        running=false;
    }
}
