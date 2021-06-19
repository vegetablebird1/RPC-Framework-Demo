package transport.netty.client;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import com.ming.factory.SingletonFactory;
import com.ming.serializer.AbstractSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import loadbalancer.LoadBalancer;
import loadbalancer.impl.RoundRobinBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceDiscovery;
import registry.impl.NacosServiceDiscovery;
import transport.RpcClient;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * 客户端，默认使用RoundRobin算法负载均衡
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

    private final AbstractSerializer serializer;

    private final ServiceDiscovery serviceDiscovery;

    private final UnprocessedRequest unprocessedRequest;


    public NettyClient(){
        this(DEFAULT_SERIALIZER,new RoundRobinBalancer());
    }

    public NettyClient(int serializer){
        this(serializer,new RoundRobinBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER,loadBalancer);
    }

    public NettyClient(int serializer, LoadBalancer loadBalancer){
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = AbstractSerializer.getSerializerByCode(serializer);
        this.unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            LOGGER.error("客户端未设置序列化器");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }

        CompletableFuture<RpcResponse> future = new CompletableFuture<>();

        try {
            //寻找服务
            InetSocketAddress address = serviceDiscovery.findService(rpcRequest.getInterfaceName());

            //获得通道
            Channel channel = ChannelProvider.getChannel(address,serializer);

            if (!channel.isActive()) {
                executors.shutdownGracefully();
                return null;
            }

            //放入任务,    **writeAndFlush发送request给服务端**    会进行处理
            unprocessedRequest.put(rpcRequest.getRequestId(),future);
            channel.writeAndFlush(rpcRequest).addListener( (ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    LOGGER.info("客户端发生请求 {} 给服务端",rpcRequest);
                } else {
                    future1.channel().close();


                    future.completeExceptionally(future1.cause());


                    LOGGER.error("客户端发生消息时发生错误:",future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequest.remove(rpcRequest.getRequestId());
            LOGGER.error("连接被打断:",e);
            Thread.currentThread().interrupted();
        }


        //?有什么含义
        return future;
    }
}
