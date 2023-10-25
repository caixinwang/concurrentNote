package C1_Thread.Code03_Interrupt;

import C1_Thread.Util.SleepHelper;


public class Code05 {//能否打断synchronized抢锁的过程----不可能
    private static Object o=new Object();
    public static void main(String[] args) {
        Thread t1=new Thread(()->{
           synchronized (o){
               System.out.println("t1 is working");
               try {
                   Thread.sleep(5000);
               } catch (InterruptedException e) {
                   System.out.println("Thread is interrupted!");
                   System.out.println(Thread.currentThread().isInterrupted());
               }
           }
        });
        t1.start();
        SleepHelper.sleep(1);//t1 remain 4s
        Thread t2=new Thread(()->{
            synchronized (o){
                System.out.println("t2 is working");
                System.out.println(Thread.currentThread().isInterrupted());
            }
        });
        t2.start();
        SleepHelper.sleep(1);//t1 remain 3s
        t2.interrupt();//不会打断抢锁的动作，但是中断位确实被设置为true了
        SleepHelper.sleep(1);//t1 remain 2s
        t1.interrupt();//t1抢到锁，是可以被中断的，中断之后立马释放资源，t2立马抢到资源开始工作
        //这里t2线程开始工作....
    }

}
