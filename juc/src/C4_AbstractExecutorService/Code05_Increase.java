package C4_AbstractExecutorService;

import java.util.concurrent.*;

public class Code05_Increase {

    static int a=0;

    public static
    synchronized
    void increase(){
        a++;
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10000; i++) {
            executorService.execute(Code05_Increase::increase);
        }
        executorService.shutdown();
        executorService.awaitTermination(2,TimeUnit.MINUTES);
        System.out.println(a);
    }
}
