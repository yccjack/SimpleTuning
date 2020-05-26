import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

/**
 * @author MysticalYcc2
 * @date 2020/5/25
 * 设置参数 有一点需要注意的是，如果-Xms和-Xmx没有被设定成相同的值，而且-Xmn被使用了，当调整Java堆的大小的时候，
 * 不会调整young代的空间大小，young代的空间大小会保持恒定。因此，-Xmn应该在-Xms和-Xmx设定成相同的时候才指定。
 * -XX:+PrintGCDetails
 * -XX:-UseAdaptiveSizePolicy禁用动态调整
 * <p>
 * -XX:+UseG1GC -XX:+UseConcMarkSweepGC -XX:+UseParallelGC -XX:+UseSerialGC  除了使用G1算法外，其他的算法实际返回用户可视化的可用空间都将少一个Survivor区的大小的空间
 * <p>
 * -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime 获取完整的安全点日志
 * -Djava.util.concurrent.ForkJoinPool.common.parallelism=核数*2    IO操作时会有很多CPU处在闲置，使用默认线程池个数(机器核数)这样可能会丢失7%的性能，此参数修改ForkJoin的线程池个数，
 *
 * vm_1 : 默认：-Xms:默认为物理内存的1/64 -Xmx:默认为物理内存的1/4或者1G
 * vm_2 : -Xms750m -Xms2048m -Xmx2048m
 * vm_3 : -Xms1024m -Xms2048m -Xmx2048m
 * vm_4 : -Xms1024m -Xms3096m -Xmx3096m
 * vm_5 : -Xms250m -Xms750m -Xmx750m
 */
public class JvmDifVmRes {
    public static void main(String[] args) throws IOException {
        //获取当前系统的CPU，内存等参数来适当调整模拟的每个线程的大小
        MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
        System.out.println("堆内存信息: " + memorymbean.getHeapMemoryUsage());
        System.out.println("方法区内存信息: " + memorymbean.getNonHeapMemoryUsage());
        MemoryUsage heapMemoryUsage = memorymbean.getHeapMemoryUsage();
        long max = heapMemoryUsage.getMax();
        if (max / 1024 < 1024) {
            //JvmRunnable.MB_8 = 3*1024*1024;
        }

        //实际使用的内存最大值
        System.out.println("Runtime.getRuntime().maxMemory()=" + Runtime.getRuntime().maxMemory());

        List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println("\n#####################运行时设置的JVM参数#######################");
        System.out.println(inputArgs);
        Simulation simulation = new Simulation();
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseClass(JvmRunnable.class).toPrintable());
        simulation.writeFile();
        simulation.scene();
    }
}
