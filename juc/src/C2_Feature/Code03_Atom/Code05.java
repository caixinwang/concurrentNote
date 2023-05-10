package C2_Feature.Code03_Atom;

import java.util.concurrent.CountDownLatch;

public class Code05 {//进行上锁，保证是100 0000
    private static long n=0L;

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads=new Thread[100];
        CountDownLatch latch=new CountDownLatch(threads.length);
        for (int i = 0; i < threads.length; i++) {
            threads[i]=new Thread(()->{
                for (int j = 0; j < 10000; j++) {
                    synchronized (Code05.class){
                        n++;
                    }
                }
                latch.countDown();
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        latch.await();
        System.out.println(n);
    }
}
