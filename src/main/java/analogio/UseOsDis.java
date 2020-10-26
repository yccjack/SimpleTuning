package analogio;

/**
 * @author ycc
 * @time 15:17
 */
public class UseOsDis {

    public static void main(String[] args) {
        SimulationFd simulationFd = OsDis.socket();
        byte[] buffer_1 = new byte[8 * 1024];
        System.out.println("调用普通IO");
        int read = simulationFd.read(buffer_1);
        System.out.println(new String(buffer_1));
        System.out.println("调用NIO");
        SimulationFd socket = OsDis.socket(false);
        byte[] buffer_2 = new byte[8 * 1024];
        int read1 = socket.read(buffer_2);
        while (read1!=-1){
            System.out.println("用户线程轮询检查数据是否准备好");
            read1 = socket.read(buffer_2);
        }

        System.out.println(new String(buffer_2));
    }
}
