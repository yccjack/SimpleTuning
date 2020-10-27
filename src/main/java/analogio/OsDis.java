package analogio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author ycc
 * @time 20:10
 */
public class OsDis {
    public static class IOPro {
        //文件描述符
        protected SimulationFd fd;

        SimulationFd getFd() {
            if (fd != null) {
                return fd;
            }
            fd = Kernel.getFd();;
            return fd;
        }
    }

    //内核
    private static class Kernel {
        //线程切换保存现场
        private KernelTCB tcb;
        //计算类
        private Calculation calculation;
        //文件描述符
        protected SimulationFd fd;

        public static SimulationFd getFd() {
            return new SimulationFd();
        }

    }

    public static SimulationFd socket() {

        IOPro ioPro = new IOPro();
        return ioPro.getFd();
    }

    public static SimulationFd socket(boolean non_bloking) {

        IOPro ioPro = new IOPro();
        SimulationFd fd = ioPro.getFd();
        fd.bloking = non_bloking;
        return fd;
    }
}

/**
 * 文件描述符模拟类，
 */
class SimulationFd {
    private static final Logger log = LoggerFactory.getLogger(SimulationFd.class);
    //pid
    protected int numbering;
    protected boolean bloking = true;
    //每个pid的缓存数组
    protected volatile byte[] buffer = new byte[8 * 1024];
    /**
     * 非普通IO共享容器
     */
    protected static Queue<byte[]> buf = new ArrayBlockingQueue<>(10);
    /**
     * pid最小值
     */
    static int socketIdMin = 43334;
    /**
     * pid最大值
     */
    static int socketIdMax = 65535;
    /**
     * pid初始集合
     */
    static ArrayBlockingQueue<Integer> usedId = new ArrayBlockingQueue<>(socketIdMax - socketIdMin + 1);
    /**
     * 读取的比特数， -2为初始值，在第一次开始读取的时候变成0。
     */
    int read = -2;

    static {
        for (int i = socketIdMin; i < socketIdMax; i++) {
            usedId.add(i);
        }
    }

    {

        try {
            numbering = usedId.poll(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (numbering >= socketIdMax || numbering == 0) {
            throw new RuntimeException("无可用端口使用");
        }
    }

    int read(byte[] buffer) {

        if (bloking) {
            log.info("陷入内核处理fd，数据未准备好，read调用阻塞，等待DMA数据加载到内核缓存！");
            read = DMAManger.read(this.buffer);
            log.info("内核缓存数据准备好，准备拷贝到用户缓存！");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.arraycopy(this.buffer, 0, buffer, 0, read);
            return read;
        }
        if (this.read == -2) {
            this.read = 0;
            log.info("陷入内核处理fd，数据未准备好，阻塞模式为Non_bloking,直接返回，请再次尝试");
            new Thread(() -> read = DMAManger.read(buffer), "DMA-IO").start();
        } else {
            if (this.read == 0) {
                return 0;
            }
            log.info("内核缓存数据准备好，阻塞模式为Non_bloking,数据已准备好，需要阻塞等待数据拷贝完成。-----准备拷贝到用户缓存！");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] poll = buf.poll();

            System.arraycopy(poll, 0, buffer, 0, read);
            read = -1;
        }
        return read;
    }


    protected static Map<Integer,SimulationFd> fdMap = new ConcurrentHashMap<>();

    private static volatile boolean forkIsReady = false;
    public static boolean select() {
        if(!forkIsReady){
            new Thread(SimulationFd::forkThread, "select-IO").start();
            forkIsReady = true;
        }
        boolean canReturn=false;
        while (!canReturn) {
            canReturn =  hasOneReady();
        }
        return true;
    }
    public static void eventDriven(SimulationFd fd){
        fdMap.put(fd.numbering,fd);
    }
    private static boolean hasOneReady() {
        for (SimulationFd innerFd : fdMap.values()) {
            if (innerFd.read != -2) {
                return true;
            }
        }
        return false;
    }

    public static void forkThread() {
        for (SimulationFd innerFd : fdMap.values()) {
            innerFd.read = DMAManger.read(innerFd.buffer);
        }
    }

    public static int read(int fd,byte[] buffer) {
        log.info("内核缓存数据准备好，准备拷贝到用户缓存！");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SimulationFd simulationFd = fdMap.get(fd);
        if(simulationFd.read==-2){
            return 0;
        }
        System.arraycopy(simulationFd.buffer, 0, buffer, 0, simulationFd.read);
        fdMap.remove(fd);
        return simulationFd.read;
    }

    /**
     * DMA控制器
     */
    static class DMAManger {
        private static final Logger log = LoggerFactory.getLogger(DMAManger.class);
        private static String testDate = "正在读取第一行数据，\n" +
                "正在读取第二行数据\n";

        static int read(byte[] buffer) {
            log.info("DMA执行数据拷贝，此过程不占用CPU。");
            byte[] bytes = testDate.getBytes();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.arraycopy(bytes, 0, buffer, 0, bytes.length);
            buf.offer(buffer);
            log.info("DMA拷贝数据到内核缓存成功，调用IO中断。");
            return bytes.length;
        }
    }

}

class KernelTCB {

}

class Calculation {

}





