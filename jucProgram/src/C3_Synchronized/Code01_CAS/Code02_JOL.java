package C3_Synchronized.Code01_CAS;

import org.openjdk.jol.info.ClassLayout;

public class Code02_JOL {
    public static void main(String[] args) throws Exception{
        Thread.sleep(5000);
        Object o=new Object();
//        System.out.println(ClassLayout.parseInstance(o).toPrintable());//打印它的内存布局
        synchronized (o){
            System.out.println(ClassLayout.parseInstance(o).toPrintable());
        }
    }
}
