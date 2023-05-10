package C1_Thread.Code04_Stop;

import C1_Thread.Util.SleepHelper;

public class Code04 {//中断标志位
    public static void main(String[] args) {
        Thread t=new Thread(()->{
            while (!Thread.interrupted()){

            }
            System.out.println("Thread t finished");
        });
        t.start();
        SleepHelper.sleep(3);
        t.interrupt();
    }
}
