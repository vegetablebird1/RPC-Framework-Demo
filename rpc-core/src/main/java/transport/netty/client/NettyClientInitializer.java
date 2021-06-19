package transport.netty.client;

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
 * @data 2021/6/17 15:06
 */

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final AbstractSerializer serializer;

    public NettyClientInitializer(AbstractSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new CommonEncoder(serializer));

        pipeline.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));

        pipeline.addLast(new CommonDecoder());

        pipeline.addLast(new NettyClientHandler());

    }

}
