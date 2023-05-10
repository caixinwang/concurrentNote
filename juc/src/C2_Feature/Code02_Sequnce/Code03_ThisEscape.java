package C2_Feature.Code02_Sequnce;

import java.io.IOException;

public class Code03_ThisEscape {
    private int num=888;

    public Code03_ThisEscape() {//构造器
        new Thread(()->{
//            System.out.println(num);
            System.out.println(this.num);
        }).start();
    }

    public static void main(String[] args) throws IOException {
        new Code03_ThisEscape();
        System.in.read();//让主线程阻塞一下，确保输出完毕
    }

}
