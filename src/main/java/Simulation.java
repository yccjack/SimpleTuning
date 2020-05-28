
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author MysticalYcc
 * @date 2020/5/25
 */
public class Simulation {
    static File fileRead = new File("file_1.txt");
    static String s = "这是需要写入的数据，，，非常可怕的数据，千万别照着念，不然你会累死！！！！\n";


    /**
     * 场景： 1S内启动100个线程模拟1S内有100个请求，每个线程大小约占8MB，线程存活时间1-3s;
     * vm_1 : 默认：-Xms:默认为物理内存的1/64 -Xmx:默认为物理内存的1/4或者1G
     * vm_2 : -Xms750m -Xms2048m -Xmx2048m
     * vm_3 : -Xms1024m -Xms2048m -Xmx2048m
     * vm_4 : -Xms1024m -Xms3096m -Xmx3096m
     * vm_5 : -Xms250m -Xms1024m -Xmx1024m
     */
    void scene() {
        try {
            //延迟启动，供查看GC情况
            Thread.sleep(10000);
            work();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 1秒内循环100次，共20次这样的循环，模拟瞬间大量的请求；
     *
     * @throws InterruptedException 中断
     */
    private void work() throws InterruptedException {
        int count = 0;
        int returnCount = 0;
        while (true) {
            Thread thread = new Thread(new JvmRunnable());
            thread.start();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (++count >= 100) {
                Thread.sleep(1000);
                count = 0;
                returnCount++;

            }
            if (returnCount >= 20) {
                return;
            }
        }
    }

    void writeFile() throws IOException {
        if (!fileRead.exists()) {
            fileRead.createNewFile();
        }
        FileOutputStream ops = new FileOutputStream(Simulation.fileRead);
        while (true) {
            if (fileRead.length() > JvmRunnable.MB_8) {
                ops.close();
                return;
            }
            ops.write(Simulation.s.getBytes());
        }
    }
}

/**
 * 模拟业务类
 * 我本地的虚拟机环境是64位并且开启了compressed压缩，对象都是8字节对齐，static属性不在Simulation的内存布局中，因为他是属于class类。
 * 因启用了compressed压缩，引用类占用4字节空间，对象头占用12个字节，总共16个字节数据，需要补齐；
 * 启动线程后需要申请8MB的缓冲区，来模拟一次请求处理中所需要申请的内存，业务停留时间1-3S，即对象存活时间最大只有3S；
 */
class JvmRunnable implements Runnable {
    static final int MB_8 = 8 * 1024 * 1024;

    //Random random = new Random();

    @Override
    public void run() {
        try {
            //int sleepRandom = random.nextInt(2) * 1000 + 1000;
            System.out.println("处理业务中，耗时1s");
            //模拟业务数据需要的内存空间大小
            ByteBuffer buffer = ByteBuffer.allocate(MB_8);
            try (FileInputStream ios = new FileInputStream(Simulation.fileRead);
                 FileChannel channelRead = ios.getChannel()) {
                channelRead.read(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //业务耗时

            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
