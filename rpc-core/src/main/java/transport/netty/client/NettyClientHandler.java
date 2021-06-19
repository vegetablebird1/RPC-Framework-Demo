package transport.netty.client;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.factory.SingletonFactory;
import com.ming.serializer.AbstractSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 处理服务端返回的响应
 * @author ming
 * @data 2021/6/17 15:05
 */

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    private final UnprocessedRequest unprocessedRequest;

    public NettyClientHandler() {
        this.unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
    }

    //读服务端返回信息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            LOGGER.info("客户端收到服务端返回消息为: {}",msg);

            //把结果返回到future中
            unprocessedRequest.complete(msg);
        } finally {
            //释放
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {

                //与客户端相连的 **服务端** 地址
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();

                //长时间没有写
                LOGGER.info("向服务端 [{}] 发送心跳包", address);

                //获得channel
                Channel channel = ChannelProvider.getChannel(address, AbstractSerializer.getSerializerByCode(AbstractSerializer.DEFAULT_SERIALIZER));

                //发送心跳包
                RpcRequest heartbeatPackage = new RpcRequest();
                heartbeatPackage.setIsHeartBeat(true);
                //发送操作不成功CLOSE_ON_FAILURE,关闭通道channel
                channel.writeAndFlush(heartbeatPackage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("远程调用过程出现异常:",cause);
        ctx.close();
    }
}
