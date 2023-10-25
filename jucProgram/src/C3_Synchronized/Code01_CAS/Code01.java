package C3_Synchronized.Code01_CAS;

import java.util.concurrent.atomic.AtomicInteger;

public class Code01 {//用乐观锁的方式玩一次n++，用AtomicInteger这个类
    AtomicInteger n=new AtomicInteger();

    void m(){
        for (int i = 0; i < 10000; i++) {
            n.incrementAndGet();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Code01 t=new Code01();
        Thread[] threads=new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i]=new Thread(t::m,"thread-"+i);
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {//让主线程等着100个线程全部执行完
            threads[i].join();
        }
        System.out.println(t.n);
    }
}
