package C1_Thread.Code03_Interrupt;

import C1_Thread.Util.SleepHelper;

public class Code01 {
    public static void main(String[] args) {
        Thread t=new Thread(()->{
            int i=0;
            while (true){
                if (i==0){
                    System.out.println("Thread is "+(Thread.currentThread().isInterrupted()?"":"not")+" interrupted!");
                    i++;
                }
                if (Thread.currentThread().isInterrupted()){
                    System.out.println("Thread is "+(Thread.currentThread().isInterrupted()?"":"not")+" interrupted!");
                    break;//只要有人设置过标志位了，那么就结束线程，这也是比较优雅的结束线程的方法。
                }
            }
        });
        t.start();//上来肯定还没有被设置过
        SleepHelper.sleep(2);//两秒
        t.interrupt();//打断它
    }
}
