package C1_Thread.Code04_Stop;

import C1_Thread.Util.SleepHelper;

public class Code01 {//stop

    public static void main(String[] args) {
        Thread t=new Thread(()->{
           while (true){
               System.out.println("working ...");
               SleepHelper.sleep(1);
           }
        });
        t.start();
        SleepHelper.sleep(5);
        t.stop();
    }
}
