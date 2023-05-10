package C1_Thread.Code03_Interrupt;

import C1_Thread.Util.SleepHelper;

public class Code02 {
    public static void main(String[] args) {
        Thread t=new Thread(()->{
            int i=0;
            while (true){
                if (i==0){
                    System.out.println("Thread is "+(Thread.interrupted()?"":"not")+" interrupted!");
                    i++;
                }
                if (Thread.interrupted()){//这里if判断的时候查询的时候是true，但是进入if之后已经重新设置为false了
                    System.out.println("Thread is "+(Thread.interrupted()?"":"not")+" interrupted!");
                    break;
                }
            }
        });
        t.start();//上来肯定还没有被设置过
        SleepHelper.sleep(2);//两秒
        t.interrupt();//打断它
        System.out.println("Main:"+t.interrupted());//尽管这里是t，由于这个方法是静态的，永远都是访问的当前线程，此时也就是main
    }
}
