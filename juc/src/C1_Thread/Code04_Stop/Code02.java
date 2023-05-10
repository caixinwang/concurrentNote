package C1_Thread.Code04_Stop;

import C1_Thread.Util.SleepHelper;

public class Code02 {//suspend  resume

    public static void main(String[] args) {
        Thread t=new Thread(()->{
           while (true){
               System.out.println("working ...");
               SleepHelper.sleep(1);
           }
        });
        t.start();
        SleepHelper.sleep(5);//5s 之后把t暂停
        t.suspend();
        System.out.println("suspend");
        SleepHelper.sleep(3);//3s 之后让t继续
        t.resume();
        SleepHelper.sleep(3);//stop结束掉
        t.stop();
    }
}
