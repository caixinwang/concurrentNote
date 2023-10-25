package C4_AbstractExecutorService;

import java.util.concurrent.*;

public class Code03_CallRun {
    static class Runnable1 implements Runnable{
        @Override
        public void run() {
            try {
//                System.out.println("working...");
                Thread.sleep(5000);//模拟耗时操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    static class ThreadFactory implements java.util.concurrent.ThreadFactory{
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);//不设置为守护进程了
            return thread;
        }
    }

    static class RejectPolicy implements RejectedExecutionHandler{
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
                System.out.println("别放了，线程池坚持不住了");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService=new ThreadPoolExecutor(10,
                10,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                new ThreadFactory(),
                new RejectPolicy());

        int n=100;
        for (int i = 0; i < n; i++) {
            executorService.submit(new Runnable1());
        }
        //不设置守护进程这里就可以不用await
    }
}
