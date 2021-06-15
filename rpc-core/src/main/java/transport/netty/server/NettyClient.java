package transport.netty.server;

import com.ming.entity.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.RpcClient;

/**
 * @author ming
 * @data 2021/6/15 16:39
 */

public class NettyClient implements RpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private static final EventLoopGroup executors;

    private static final Bootstrap bootstrap;

    static {
        executors = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(executors)
                .channel(NioSocketChannel.class);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        return null;
    }
}
