package C3_Synchronized.Code01_CAS;

import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Code03 {
    static class A{

    }
    public static void main(String[] args) throws Exception {

        Thread.sleep(5000);
        List<A> listA = new ArrayList<>();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i <100 ; i++) {
                A a = new A();
                synchronized (a){
                    listA.add(a);
                }
            }
            try {
                Thread.sleep(100000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        Thread.sleep(3000);

        out.println("打印list中第1个对象的对象头：");
        out.println((ClassLayout.parseInstance(listA.get(1)).toPrintable()));

        Thread t2 = new Thread(() -> {
            //这里循环了40次。达到了批量撤销的阈值
            for (int i = 0; i < 50; i++) {
                A a =listA.get(i);
                synchronized (a){
                }
            }
            try {
                Thread.sleep(10000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t2.start();

        //———————————分割线，前面代码不再赘述——————————————————————————————————————————
        Thread.sleep(3000);
        out.println("打印list中第11个对象的对象头：");
        out.println((ClassLayout.parseInstance(listA.get(11)).toPrintable()));
        out.println("打印list中第26个对象的对象头：");
        out.println((ClassLayout.parseInstance(listA.get(26)).toPrintable()));
        out.println("打印list中第45个对象的对象头：");
        out.println((ClassLayout.parseInstance(listA.get(45)).toPrintable()));
        out.println("打印list中第90个对象的对象头：");
        out.println((ClassLayout.parseInstance(listA.get(90)).toPrintable()));


        Thread t3 = new Thread(() -> {
            for (int i = 20; i < 80; i++) {
                A a =listA.get(i);
                synchronized (a){
                    if(i==20||i==22||i==35||i==70){
                        out.println("thread3 第"+ i + "次");
                        out.println((ClassLayout.parseInstance(a).toPrintable()));
                    }
                }
            }
        });
        t3.start();

        Thread.sleep(10000);
        out.println("打印list中第90个对象的对象头：");
        out.println((ClassLayout.parseInstance(listA.get(90)).toPrintable()));

        out.println("重新输出新实例A");
        A a = new A();
        synchronized (a){}
        out.println((ClassLayout.parseInstance(a).toPrintable()));
    }
}
