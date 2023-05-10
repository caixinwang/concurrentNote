package C4_AbstractExecutorService;

import java.util.concurrent.*;

public class Code04_New {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(()->{
            System.out.println("hello!");
        });
        executorService.shutdown();
        executorService.awaitTermination(1,TimeUnit.MINUTES);
    }
}
