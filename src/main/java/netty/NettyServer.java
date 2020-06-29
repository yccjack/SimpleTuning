package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author MysticalYcc
 * @date 9:51
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        b.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitHandler())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f = b.bind(8888).sync();
        System.out.println("服务启动成功，端口8888");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> f.channel().close()));
        f.channel().closeFuture().syncUninterruptibly();

    }
}
