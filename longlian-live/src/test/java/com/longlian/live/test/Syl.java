package com.longlian.live.test;
//
//import sun.misc.BASE64Decoder;

import java.net.URLEncoder;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 2017/5/28.
 */
public class Syl {

    public static void main(String[] args) {
     /*   ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
        concurrentLinkedQueue.add("a");
        concurrentLinkedQueue.add("b");
        concurrentLinkedQueue.offer("c");
        System.out.println(concurrentLinkedQueue.size());
        Object s =  concurrentLinkedQueue.peek();
        System.out.println(s + "--->" + concurrentLinkedQueue.size());

        Object m =  concurrentLinkedQueue.peek();
        System.out.println(m+ "--->" + concurrentLinkedQueue.size());*/

    /*    A a = new A();
        a.setA(5);

        a.set(a);;
        System.out.println(a.getA());*/
        String s= "BINLOG '\n" +
                "bIUvWRMBAAAAVgAAAN89sQIAAHoAAAAAAAEADWxvbmdsaWFuLWxpdmUACmxsX2FjY291bnQACgj2\n" +
                "9vYIEhIPDw8OFAIUAhQCAAAsAZYADwD+A0JIto8=\n" +
                "bIUvWR8BAAAAjgAAAG0+sQIAAHoAAAAAAAEAAgAK/////8D9DAAAAAAAAACAAAAAAAAA8wCAAAAA\n" +
                "AAADZQCAAAAAAAACcgAoAAAAAAAAAJmcevH1ATDA/QwAAAAAAAAAgAAAAAAAAPcAgAAAAAAAA2kA\n" +
                "gAAAAAAAAnIAKAAAAAAAAACZnHrx9QEwkzo53w==\n" +
                "'/*!*/;";

//        BASE64Decoder decoder = new BASE64Decoder();
//        try {
//            byte[] b = decoder.decodeBuffer(s);
//            System.out.println( new String(b));
//        }catch (Exception e){}

    }


}
class A{
    public  final  String aa = "2";
private int a= 0;

    public void set(A  i ){
        //i = new A();
        i.setA(10);
    }
    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}
