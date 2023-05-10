package C2_Feature.Code01_Visibility;

//import jdk.internal.vm.annotation.Contended;

import java.util.concurrent.CountDownLatch;

public class Code06 {//缓存行
    public static long COUNT=10_0000_0000L;

    private static class T{
//        private long x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16;
//        @Contended
        public long x=0L;

//        private long p1,p2,p3,p4,p5,p6,p7,p8,p9,p0,p11,p12,p13,p14,p15,p16;

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
