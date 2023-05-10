package C2_Feature.Code01_Visibility;

import C2_Feature.Util.SleepHelper;

public class Code04 {//可见性
    private static class A{
        boolean running;
        void m(){
            while (running){
            }
        }

        public A(boolean running) {
            this.running = running;
        }
    }

    private static volatile A a=new A(true);
    public static void main(String[] args) {
        new Thread(a::m,"t1").start();
        SleepHelper.sleep(1);
        System.out.println("过了1s");
        a.running=false;
        a=new A(false);
    }
}
