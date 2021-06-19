package transport.netty.server;

import codec.CommonDecoder;
import codec.CommonEncoder;
import com.ming.serializer.AbstractSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author ming
 * @data 2021/6/17 13:40
 */

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * decode需要的序列化器种类
     */
    private final AbstractSerializer serializer;

    public NettyServerInitializer(AbstractSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //心跳机制处理器
        pipeline.addLast(new IdleStateHandler(10,0,0, TimeUnit.SECONDS));

        pipeline.addLast(new CommonEncoder(serializer));

        pipeline.addLast(new CommonDecoder());

        pipeline.addLast(new NettyServerHandler());
    }
}
