package jvm;

import java.nio.ByteBuffer;

/**
 * @author ycc
 * @time 21:32
 * 虚引用： 回收内存引用时发起通知，将引用放入队列中，通知手动回收。(类似钩子函数)。
 * 虚引用管理堆外内存(直接内存)
 */
public class TestDirectByteBuffer {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
}
