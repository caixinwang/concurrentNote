package C4_AbstractExecutorService;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Code01_InvokeAny {
    static class Callable1 implements Callable<Integer>{
        @Override
        public Integer call() throws Exception {
            System.out.println(1);
            return 1;
        }
    }

    static class Callable2 implements Callable<Integer>{
        @Override
        public Integer call() throws Exception {
//            System.out.println(2);
            try {
                System.out.println(2);
                Thread.sleep(1000);
            }catch (InterruptedException e){//这个异常是sleep被打断产生的中断异常
                e.printStackTrace();
            }
            return 2;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ArrayList<Callable<Integer>> callables=new ArrayList<>();
        callables.add(new Callable1());
        callables.add(new Callable2());
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Integer integer = threadPool.invokeAny(callables);
        System.out.println("result is : "+integer);

    }
}
