package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author MysticalYcc
 * @date 9:51
 * update： 2020年9月17日 22点23分
 * 描述： 在使用异步channel时， bind端口会继续执行，直到main方法结束，这里可以使用 f.channel().closeFuture().syncUninterruptibly()来异步转同步阻塞main方法
 */
public class NettyServer {
    ServerBootstrap b = new ServerBootstrap();
    NioEventLoopGroup boss = new NioEventLoopGroup();
    NioEventLoopGroup work = new NioEventLoopGroup();
    public static void main(String[] args) throws InterruptedException {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();

    }

    public void start() throws InterruptedException {

        b.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitHandler())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f = b.bind(8888).sync();
        System.out.println("服务启动成功，端口8888");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> f.channel().close()));
        //此处阻塞，main线程会卡住，
        f.channel().closeFuture().syncUninterruptibly();
    }
}
