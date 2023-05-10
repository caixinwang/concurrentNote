package C1_Thread.Code01_Create;

import java.util.concurrent.*;

public class Code01 {//线程的几种创建方法
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Hello MyThread!");
        }
    }

    static class MyRun implements Runnable {
        @Override
        public void run() {
            System.out.println("Hello MyRun!");
        }
    }

    static class MyCall implements Callable<String>{
        @Override
        public String call() throws Exception {
            return "6666";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("========继承=========");
        new MyThread().start();
        Thread.sleep(10);
        System.out.println("========实现Runnable接口=========");
        new Thread(new MyRun()).start();
        Thread.sleep(10);
        //lamda表达式
        System.out.println("========lamda=========");
        new Thread(()->{
            System.out.println("Hello Lambda!");
        }).start();
        Thread.sleep(10);
        //线程池
        System.out.println("=========线程池submit========");
        ExecutorService service= Executors.newCachedThreadPool();
        service.execute(()-> System.out.println("hello!"));
        Thread.sleep(10);
        //有返回值类型
        System.out.println("=========线程池有返回值类型========");
        Future<String> f = service.submit(new MyCall());
        System.out.println(f.get());
        service.shutdown();
        Thread.sleep(10);
        //FutureTask
        System.out.println("=========FutureTask========");
        FutureTask<String> futureTask=new FutureTask<>(new MyCall());
        new Thread(futureTask).start();
        System.out.println(futureTask.get());
        Thread.sleep(10);

    }
}
