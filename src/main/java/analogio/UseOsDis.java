package analogio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author ycc
 * @time 15:17
 */
public class UseOsDis {
    private static final Logger log = LoggerFactory.getLogger(UseOsDis.class);
    public static void main(String[] args) {
        //testBlockIo();
        //testNio();
        testSelectOrPoll();
    }

    private static void testNio() {
        log.info("调用NIO");
        SimulationFd socket = OsDis.socket(false);
        System.out.println(socket.numbering);
        byte[] buffer_2 = new byte[8 * 1024];
        int read1 = socket.read(buffer_2);
        while (read1!=-1){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("用户线程轮询检查数据是否准备好");
            read1 = socket.read(buffer_2);
        }

        log.info(new String(buffer_2));
    }


    public static void testBlockIo(){
        log.info("阻塞IO调用");
        SimulationFd simulationFd = OsDis.socket();
        System.out.println(simulationFd.numbering);
        byte[] buffer_1 = new byte[8 * 1024];
        log.info("调用普通IO");
        int read = simulationFd.read(buffer_1);
        log.info(new String(buffer_1));
    }

    public static void testSelectOrPoll(){
        //ET
        log.info("select调用");
        SimulationFd simulationFd1 = OsDis.socket();
        SimulationFd simulationFd2 = OsDis.socket();

        SimulationFd.eventDriven(simulationFd1);
        SimulationFd.eventDriven(simulationFd2);
        while (SimulationFd.select()){
            for (int fdId :SimulationFd.fdMap.keySet()) {
                byte[] buffer = new byte[8 * 1024];
                int read = SimulationFd.read(fdId, buffer);
                System.out.println(new String(buffer));
            }
        }


    }
}
