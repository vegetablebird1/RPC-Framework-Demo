package transport.netty.server;

import com.alibaba.fastjson.JSON;
import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.factory.SingletonFactory;
import com.ming.serializer.JsonSerializer;
import handler.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ming
 * @data 2021/6/17 11:44
 */

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

    private final RequestHandler requestHandler;

    private int loss_connect_time = 0;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    //读请求包
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            if (msg.getIsHeartBeat()) {
                LOGGER.info("接收到客户端 [{}] 的心跳包...",ctx.channel().remoteAddress());
                return;
            }

            /*----------------处理请求-----------------*/
            LOGGER.info("服务端正在处理请求:{}", msg);
            Object result = requestHandler.handler(msg);
            /*----------------------------------------*/

            if (ctx.channel().isActive() && ctx.channel().isWritable()) {

                //到这里肯定，result不为空，因为如果为null，requestHandler已经抛异常了
                //**把结果封装**

                RpcResponse success = RpcResponse.success(msg.getRequestId(), result);
                ctx.writeAndFlush(success);

            } else {
                LOGGER.error("通道不可写!");
            }
        } finally {
            //将计数设置为0，释放ByteBuf,回收,出站处理器就不用写，netty会处理
            ReferenceCountUtil.release(msg);
        }
    }

    //处理心跳包
    //服务端未发生事件:读，写，读和写
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();//状态
            if (state == IdleState.READER_IDLE) {
                loss_connect_time++;
                LOGGER.info("第" + loss_connect_time + "次服务端发生读空闲,10s没有接收心跳包");
                if (loss_connect_time >= 3) {
                    LOGGER.info("关闭与不活跃客户端的连接");
                    ctx.close();
                }
            }
        } else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("服务端处理请求时有错误发生,通道关闭",cause);
        ctx.close();
    }

}
