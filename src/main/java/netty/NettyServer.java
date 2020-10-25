package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

/**
 * @author MysticalYcc
 * @date 9:51
 * update： 2020年9月17日 22点23分
 * 描述： 在使用异步channel时， bind端口会继续执行，直到main方法结束，这里可以使用 f.channel().closeFuture().syncUninterruptibly()来异步转同步阻塞main方法
 */
public class NettyServer {
    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Logger log = LoggerFactory.getLogger(NettyServer.class);
    private int port=8888;
    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup();
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        NettyServer nettyServer = new NettyServer();
        nettyServer.serverStart(b);

    }

//    public void start() throws InterruptedException {
//
//        b.group(boss,worker)
//                .channel(NioServerSocketChannel.class)
//                .childHandler(new ServerInitHandler())
//                .option(ChannelOption.SO_BACKLOG, 128)
//                .childOption(ChannelOption.SO_KEEPALIVE, true);
//        ChannelFuture f = b.bind(8888).sync();
//        System.out.println("服务启动成功，端口8888");
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> f.channel().close()));
//        //此处阻塞，main线程会卡住，
//        f.channel().closeFuture().syncUninterruptibly();
//    }

    private void serverStart(ServerBootstrap b) throws InterruptedException {
        Signal signal = new Signal(getOOSignalType());
        Signal.handle(signal,new ShutdownHandler(boss,worker));
        log.info("服务启动");
        ChannelFuture f = b.bind(port).sync();
        f.addListener(new ServerBoundListener());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> f.channel().close()));
    }

    private String getOOSignalType() {
        return System.getProperties().getProperty("os.name").toLowerCase().startsWith("win")?"INT":"TERM";

    }


}
