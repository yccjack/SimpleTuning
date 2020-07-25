package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author MysticalYcc
 * @date 10:05
 */
public class RoutServerHandler extends ChannelInboundHandlerAdapter {

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("处理数据");
        ByteBuf readBytes = (ByteBuf) msg;
        byte[] body = new byte[readBytes.readableBytes()];
        executorService.execute(() -> {
            System.out.println("处理数据转发。。。。。");
            ByteBuf resMsg = allocator.heapBuffer(body.length);
            resMsg.writeBytes(body);
            try {
                ctx.writeAndFlush(resMsg).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        ReferenceCountUtil.release(readBytes);
    }
}
