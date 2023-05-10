package C2_Feature.Code01_Visibility;

import C2_Feature.Util.SleepHelper;

public class Code02 {//可见性
//    private static boolean running=true;
    private static volatile boolean running=true;
    private static void m(){
        System.out.println("m start");
        while (running){

        }
        System.out.println("m finished");
    }

    public static void main(String[] args) {
        new Thread(Code02::m,"t1").start();
        SleepHelper.sleep(1);
        running=false;
    }
}
