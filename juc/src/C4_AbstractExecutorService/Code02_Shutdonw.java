package C4_AbstractExecutorService;

import java.util.ArrayList;
import java.util.concurrent.*;

public class Code02_Shutdonw {

    static class Runnable1 implements Runnable{
        @Override
        public void run() {
            System.out.println(1);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("exit");
        }
    }

    static class ThreadFactory implements java.util.concurrent.ThreadFactory{
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService=new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory());
        executorService.submit(new Runnable1());
        executorService.shutdown();
//        executorService.shutdownNow();
//        executorService.awaitTermination(2, TimeUnit.MINUTES);
    }
}
