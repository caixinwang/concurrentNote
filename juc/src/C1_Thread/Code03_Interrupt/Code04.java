package C1_Thread.Code03_Interrupt;

import C1_Thread.Util.SleepHelper;


public class Code04 {//wait
    private static Object o=new Object();
    public static void main(String[] args) {
        Thread t=new Thread(()->{
           synchronized (o){
               try {
                   o.wait();
               } catch (InterruptedException e) {
                   System.out.println("Thread is interrupted!");
                   System.out.println(Thread.currentThread().isInterrupted());
               }
           }
        });
        t.start();
        SleepHelper.sleep(1);
        t.interrupt();
    }

}
