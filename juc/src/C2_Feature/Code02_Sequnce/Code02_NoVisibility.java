package C2_Feature.Code02_Sequnce;

public class Code02_NoVisibility {
    private static boolean ready=false;
    private static int number;

    private static class ReaderThread extends Thread{
        @Override
        public void run() {
            while(!ready){
                Thread.yield();//变为就绪态，让出时间片
            }
            System.out.println(number);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t=new ReaderThread();
        t.start();
        number=42;
        ready=true;
        t.join();//主线程等待t线程执行结束
    }
}
