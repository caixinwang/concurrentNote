package C2_Feature.Code03_Atom;

import C2_Feature.Util.SleepHelper;

public class Code03 {//上锁的本质
    private static Object o=new Object();//资源对象，当成锁就行

    public static void main(String[] args) {
        Runnable r=()->{
            System.out.println(Thread.currentThread().getName()+"start!");
            SleepHelper.sleep(2);
            System.out.println(Thread.currentThread().getName()+"end!");
        };
        for (int i = 0; i < 3; i++) {
            new Thread(r).start();
        }
    }
}
