package C2_Feature.Code02_Sequnce;

import java.util.concurrent.CountDownLatch;

public class Code01_Disorder {
    private static int a=0,b=0;
    private static int x=0,y=0;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < Long.MAX_VALUE; i++) {
            x=0;
            y=0;
            a=0;
            b=0;

            CountDownLatch latch=new CountDownLatch(2);
            Thread one=new Thread(()->{
                a=1;
                x=b;
                latch.countDown();
            });
            Thread other=new Thread(()->{
                b=1;
                y=a;
                latch.countDown();
            });
            one.start();
            other.start();
            latch.await();
            if (x==0&&y==0){
                System.err.println("第"+i+"次x=0、y=0");
                break;
            }

        }

    }
}
