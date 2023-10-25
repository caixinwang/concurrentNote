package C1_Thread.Code03_Interrupt;

import C1_Thread.Util.SleepHelper;

import java.util.concurrent.locks.ReentrantLock;


public class Code06 {//能否打断ReentrantLock抢锁的过程
    private static ReentrantLock lock=new ReentrantLock();
    public static void main(String[] args) {
        Thread t1=new Thread(()->{
            lock.lock();//抢锁
            try {
                System.out.println("t1 is working");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Thread t1 is interrupted!");
                System.out.println(Thread.currentThread().isInterrupted());
            } finally {
                lock.unlock();//释放锁
            }
        });
        t1.start();
        SleepHelper.sleep(1);//t1 remain 4s
        Thread t2=new Thread(()->{
            lock.lock();
            try {
                System.out.println("t2 is working");
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("Thread t2 is interrupted!");
                System.out.println(Thread.currentThread().isInterrupted());
            } finally {
                lock.unlock();//释放锁
            }
            System.out.println("t2 is finished");
        });
        t2.start();
        SleepHelper.sleep(1);//t1 remain 3s
        t2.interrupt();//不会打断抢锁的动作，但是中断位确实被设置为true了,3s后刚一抢到里面进入异常处理了
        SleepHelper.sleep(1);//t1 remain 2s
        t1.interrupt();//t1抢到锁，是可以被中断的，中断之后立马释放资源，t2立马抢到资源开始工作，t2提前两秒
        //这里t2线程开始工作....
    }

}
