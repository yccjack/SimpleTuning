package jvm;

import java.nio.ByteBuffer;

/**
 * @author ycc
 * @time 21:32
 * 虚引用： 回收内存引用时发起通知，将引用放入队列中，通知手动回收。(类似钩子函数)。
 * 虚引用管理堆外内存(直接内存).
 * ThreadLocal的get是获取线程自己的ThreadLocal.ThreadLocalMap threadLocals 来存储数据，此Map的key是弱引用，再使用完后自己定义的threadLocal强引用需要调用remove删除，且线程池
 * 中最好不适用ThreadLocal对象。
 */
public class TestDirectByteBuffer {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        ThreadLocal<Integer> testThread = ThreadLocal.withInitial(() -> 0);
}
