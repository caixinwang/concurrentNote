package C1_Thread.Code03_Interrupt;

import C1_Thread.Util.SleepHelper;

/**
 * 看interrupt和sleep的联合使用
 */
public class Code03 {
    public static void main(String[] args) {
        Thread t=new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Thread is interrupted!");
                System.out.println(Thread.currentThread().isInterrupted());
            }
        });
        t.start();
        SleepHelper.sleep(1);
        t.interrupt();
    }

}
