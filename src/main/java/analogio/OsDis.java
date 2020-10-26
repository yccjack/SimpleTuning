package analogio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author ycc
 * @time 20:10
 */
public class OsDis {
    public static class IOPro {
        //文件描述符
        protected SimulationFd fd;

        {
            fd = new SimulationFd();
        }

    }

    //内核
    private static class kernel {
        //线程切换保存现场
        private KernelTCB tcb;
        //计算类
        private Calculation calculation;

    }

    public static SimulationFd socket() {

        IOPro ioPro = new IOPro();
        return ioPro.fd;
    }

    public static SimulationFd socket(boolean non_bloking) {

        IOPro ioPro = new IOPro();
        SimulationFd fd = ioPro.fd;
        fd.bloking = non_bloking;
        return fd;
    }
}

class SimulationFd {
    //pid
    protected int numbering;
    protected boolean bloking = true;
    //每个pid的缓存数组
    protected byte[] buffer;
    static int socketIdMin = 43334;
    static int socketIdMax = 65535;
    static ArrayBlockingQueue<Integer> usedId = new ArrayBlockingQueue<>(socketIdMax - socketIdMin + 1);
    int read = 0;

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
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("陷入内核处理fd，数据未准备好，read调用阻塞，等待DMA数据加载到内核缓存！");
            this.buffer = new byte[8 * 1024];
            read = DMAManger.read(this.buffer);
            System.out.println("内核缓存数据准备好，准备拷贝到用户缓存！");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.arraycopy(this.buffer, 0, buffer, 0, read);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            return read;
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (this.buffer == null) {
            this.buffer = new byte[8 * 1024];
            System.out.println("陷入内核处理fd，数据未准备好，阻塞模式为Non_bloking,直接返回，请再次尝试");
            new Thread(() -> read = DMAManger.read(buffer)).start();
        } else {
            System.out.println("内核缓存数据准备好，阻塞模式为Non_bloking,数据已准备好，需要阻塞等待数据拷贝完成。-----准备拷贝到用户缓存！");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.arraycopy(this.buffer, 0, buffer, 0, read);
            read = -1;
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        return read;
    }

}

class KernelTCB {

}

class Calculation {

}

class DMAManger {
    private static String testDate = "正在读取第一行数据，\n" +
            "正在读取第二行数据\n";

    static int read(byte[] buffer) {
        System.out.println("--------------------------------------");
        System.out.println("DMA执行数据拷贝，此过程不占用CPU。");
        byte[] bytes = testDate.getBytes();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.arraycopy(bytes, 0, buffer, 0, bytes.length);
        System.out.println("DMA拷贝数据到内核缓存成功，调用IO中断。");
        System.out.println("--------------------------------------");
        return bytes.length;
    }
}



