package jvm;

import java.util.concurrent.CountDownLatch;

/**
 * @author ycc
 * @time 14:43
 */
public class CacheLinePadding {
    private static final long count = 100000000;


    /**
     * 测试缓存行是否存在，
     * 这里使用InnerNo作为测试缓存行的类，缓存行默认使用64字节，这里一个long是8字节，当注释f1......后，会出现伪共享，证明缓存行的存在。 把注释打开后 耗时与不适用volatile差不多，消除了缓存行的问题，
     * 进而消除了伪共享问题。
     */
    private static class InnerNo{
       // private long f1,f2,f3,f4,f5,f6,f7;
        private volatile long s =1;
        //private long f8,f9,f10,f11,f12,f13,f14;
    }
    static  InnerNo[] arr;
    static  {
        arr  =new InnerNo[2];
        arr[0]=new InnerNo();
        arr[1]=new InnerNo();
    }
    public static void main(String[] args) throws InterruptedException {
         CountDownLatch downLatch = new CountDownLatch(2);

        Thread t1 = new Thread(()->{
            for (int i = 0; i <count ; i++) {
                arr[0].s = i;
            }
            downLatch.countDown();
        });
        Thread t2= new Thread(()->{
            for (int i = 0; i <count ; i++) {
                arr[1].s = i;
            }
            downLatch.countDown();
        });
        long start = System.nanoTime();
        t1.start();
        t2.start();
        downLatch.await();
        System.out.println("耗时："+(System.nanoTime()-start)/1000000+"s");
    }
}
