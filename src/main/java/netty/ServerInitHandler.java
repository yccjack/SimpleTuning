package netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author MysticalYcc
 * @date 2020/6/1
 */
public class ServerInitHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        System.out.println("有连接进入");
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline
                .addLast(new RoutServerHandler());
    }

}
