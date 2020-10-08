package jvm;

import java.lang.ref.SoftReference;

/**
 * 软引用， 设置堆大小为25m，软引用在内存不够时会被垃圾回收回收掉。
 * @author ycc
 * @time 21:17
 * vm: -Xmx25m
 */
public class SoftReferenceTest {

    public static void main(String[] args) throws InterruptedException {
        SoftReference<byte[]> m = new SoftReference<>(new byte[1024*1024*10]);
        System.out.println(m.get());
        System.gc();
        Thread.sleep(500);
        System.out.println(m.get());
        byte[] b = new byte[1024*1024*15];
        System.out.println(m.get());
    }
}
