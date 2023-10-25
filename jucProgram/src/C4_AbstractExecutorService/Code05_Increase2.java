package C4_AbstractExecutorService;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Code05_Increase2 {

    static final Unsafe UNSAFE;
    static final long A_OFFSET;
    static volatile int a=0;
    static Class<Code05_Increase2> c = Code05_Increase2.class;

    static {

        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
            A_OFFSET = UNSAFE.staticFieldOffset(c.getDeclaredField("a"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public static void increase(){
        for (;;){
            int t=a;
            if (UNSAFE.compareAndSwapInt(c,A_OFFSET,t,++t)){
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 100000; i++) {
            executorService.execute(Code05_Increase2::increase);
        }
        executorService.shutdown();
        executorService.awaitTermination(2,TimeUnit.MINUTES);
        System.out.println("==================");
        System.out.println(a);
        System.out.println("==================");

    }
}
