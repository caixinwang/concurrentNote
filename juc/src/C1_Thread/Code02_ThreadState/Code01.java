package C1_Thread.Code02_ThreadState;

import C1_Thread.Util.SleepHelper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class Code01 {//线程的状态
    public static void main(String[] args) throws Exception {

        Thread t1=new Thread(()->{
            System.out.println("2:"+Thread.currentThread().getState());//START了才会执行里面的代码
            System.out.println("t1 is working ...");
            SleepHelper.sleep(1);//try-catch麻烦自己写一个
        });
        System.out.println("1:"+t1.getState());
        t1.start();//将t1线程开启，start之前都是new状态，start之后要么是READY要么是RUNNING，也就是RUNNABLE
        t1.join();//等待t1线程结束
        System.out.println("3:"+t1.getState());//t1线程结束

        System.out.println("=====================================");

        Thread t2=new Thread(()->{
            LockSupport.park();//等着被别人叫醒---waiting
            System.out.println("t2 is working");
            SleepHelper.sleep(3);
        });
        t2.start();
        SleepHelper.sleep(1);
        System.out.println("4:"+t2.getState());//因为park了，所以此时是WAITING
        LockSupport.unpark(t2);//叫醒t2
        SleepHelper.sleep(1);//睡一秒确定t2已经被叫醒了
        System.out.println("5:"+t2.getState());//t2往下执行到sleep---TIMED_WAITING

        System.out.println("=====================================");

        final Object o=new Object();//资源
        Thread t3=new Thread(()->{
            synchronized (o){
                System.out.println("t3 得到了锁 o");
            }
        });

        new Thread(()->{//定义为t3之后直接启动一个线程先去把资源o抢了
            synchronized (o){
                System.out.println("另一个线程 得到了锁 o");
                SleepHelper.sleep(3);
            }
        }).start();

        SleepHelper.sleep(1);
        t3.start();//另外一线线程拿着资源要占3s，此时t3被阻塞了，BLOCKED
        SleepHelper.sleep(1);
        System.out.println("6:"+t3.getState());//此时另外一个线程还没释放资源，还是阻塞
        SleepHelper.sleep(2);//主线程睡这两秒的过程中，另外一个线程释放资源了，t3得到了资源，开始working

        System.out.println("=====================================");

        final Lock lock = new ReentrantLock();//用的JUC的锁---cas实现---是一种忙等待，不是BLOCKED
        Thread t4=new Thread(()->{
            lock.lock();//申请获得这把锁
            System.out.println("t4 得到了锁");
            System.out.println("t4 is working");
            SleepHelper.sleep(1);
            lock.unlock();//释放锁
        });

        new Thread(()->{//t4定义完成之后直接启动一个线程先去把锁抢了
            lock.lock();//申请获得这把锁
            System.out.println("另外一个线程 得到了锁");
            SleepHelper.sleep(3);//拿着锁3秒
            lock.unlock();//释放锁
        }).start();

        SleepHelper.sleep(1);//此时另外那个线程差不多还有2s才释放锁
        t4.start();//另外一个线程还有2s释放锁，所以t4还有2s左右才能开始working
        SleepHelper.sleep(1);//还1s释放锁
        System.out.println("7:"+t4.getState());//还没抢到锁，忙等
        SleepHelper.sleep(3);//睡完肯定抢到了并且工作结束了

        System.out.println("=====================================");

        Thread t5=new Thread(()->{
            LockSupport.park();//等着被叫醒
            System.out.println("t5 is working");
            SleepHelper.sleep(1);
        });
        t5.start();
        SleepHelper.sleep(1);
        System.out.println("8:"+t5.getState());//还没被叫醒---WAITING
        LockSupport.unpark(t5);//叫醒了
        SleepHelper.sleep(2);

    }
}
