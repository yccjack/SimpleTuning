package jdkcode;

import netty.NettyServer;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author MysticalYcc
 * @date 14:29
 */
public class OffsetVo {
    public int a = 0;
    public long b = 0;
    public String c= "123";
    public Object d= null;
    public int e = 100;
    public static int f= 0;
    public static String g= "";
    public Object h= null;
    public boolean i;

    public NettyServer nettyServer = new NettyServer();

    public static void main(String[] args) throws Exception {
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseClass(OffsetVo.class).toPrintable());
        System.out.println("=================");
        Unsafe unsafe = getUnsafeInstance();
        OffsetVo vo = new OffsetVo();
        vo.a=2;
        vo.b=3;
        vo.d=new HashMap<>();
        long aoffset = unsafe.objectFieldOffset(OffsetVo.class.getDeclaredField("a"));
        System.out.println("aoffset="+aoffset);
        // 获取a的值
        int va = unsafe.getInt(vo, aoffset);
        System.out.println("va="+va);
    }

    public static Unsafe getUnsafeInstance() throws Exception {
        // 通过反射获取rt.jar下的Unsafe类
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        // return (Unsafe) theUnsafeInstance.get(null);是等价的
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);
    }
}
