import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;

/**
 * @author MysticalYcc
 * @date 2020/5/25
 * 设置参数 有一点需要注意的是，如果-Xms和-Xmx没有被设定成相同的值，而且-Xmn被使用了，当调整Java堆的大小的时候，
 * 不会调整young代的空间大小，young代的空间大小会保持恒定。因此，-Xmn应该在-Xms和-Xmx设定成相同的时候才指定。
 *
 *  vm_1 : 默认：-Xms:默认为物理内存的1/64 -Xmx:默认为物理内存的1/4或者1G
 *  vm_2 : -Xms750m -Xms2048m -Xmx2048m
 *  vm_3 : -Xms1024m -Xms2048m -Xmx2048m
 *  vm_4 : -Xms1024m -Xms3096m -Xmx3096m
 *  vm_5 : -Xms250m -Xms750m -Xmx750m
 */
public class JvmDifVmRes {
    public static void main(String[] args) throws IOException {
        MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
        System.out.println("堆内存信息: " + memorymbean.getHeapMemoryUsage());
        System.out.println("方法区内存信息: " + memorymbean.getNonHeapMemoryUsage());

        List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println("\n#####################运行时设置的JVM参数#######################");
        System.out.println(inputArgs);
        Simulation simulation = new Simulation();
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseClass(JvmRunnable.class).toPrintable());
        simulation.writeFile();
       // simulation.scene();
    }
}
