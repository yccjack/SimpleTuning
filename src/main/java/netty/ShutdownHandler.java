package netty;

import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ShutdownHandler implements SignalHandler {
    private Logger log = LoggerFactory.getLogger(ShutdownHandler.class);
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    public ShutdownHandler(EventLoopGroup boss, EventLoopGroup worker) {
        this.boss = boss;
        this.worker = worker;
    }

    @Override
    public void handle(Signal signal) {
        try {
            worker.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("服务关闭");
    }
}
