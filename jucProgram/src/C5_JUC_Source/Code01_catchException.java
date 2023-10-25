package C5_JUC_Source;

public class Code01_catchException {

    static class MyThreadPoolExecutor extends ThreadPoolExecutor{
        public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            t.printStackTrace();
        }
    }

    static class MyThreadFactory implements ThreadFactory{
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    System.out.println("异常");
                    e.printStackTrace();
                    System.out.println(t.getId());
                }
            });
            return thread;
        }
    }
    static class MyRunnable implements Runnable{
        @Override
        public void run() {
            System.out.println(1/0);
        }
    }
    public static void main(String[] args) {
        MyThreadPoolExecutor myThreadPoolExecutor = new MyThreadPoolExecutor(10, 10, 100, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10));
//        myThreadPoolExecutor.execute(new MyRunnable());
        ThreadPoolExecutor myThreadPoolExecutor2 = new ThreadPoolExecutor(10,
                10,
                100,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10),
                new MyThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        myThreadPoolExecutor2.execute(()->{
            System.out.println(123);
        });
        myThreadPoolExecutor2.execute(new MyRunnable());
        myThreadPoolExecutor2.execute(new MyRunnable());

    }
}
