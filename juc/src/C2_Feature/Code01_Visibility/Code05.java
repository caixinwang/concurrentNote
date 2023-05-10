package C2_Feature.Code01_Visibility;

import C2_Feature.Util.SleepHelper;

import java.util.concurrent.CountDownLatch;

public class Code05 {//缓存行
    public static long COUNT=10_0000_0000L;

    private static class T{
        public long x=0L;
    }

    public static T[] arr=new T[2];

    static {
        arr[0]=new T();
        arr[1]=new T();
    }

    public static void main(String[] args) throws InterruptedException {//357
        CountDownLatch latch=new CountDownLatch(2);

        Thread t1=new Thread(()->{//不断修改arr[0]
            for (int i = 0; i < COUNT; i++) {
                arr[0].x=i;
            }
            latch.countDown();
        });

        Thread t2=new Thread(()->{
            for (int i = 0; i < COUNT; i++) {//不断修改arr[1]
                arr[1].x=i;
            }
            latch.countDown();
        });

        final long start=System.nanoTime();
        t1.start();
        t2.start();
        latch.await();
        System.out.println((System.nanoTime()-start)/100_0000);

    }
}
