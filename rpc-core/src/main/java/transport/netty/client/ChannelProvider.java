package transport.netty.client;

import com.ming.serializer.AbstractSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.netty.server.NettyServerInitializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * 从连接中获得通道，当在同一连接中再次尝试获得通道时，
 * 会先在map中拿，没有或不是空闲状态就重新创建
 * @author ming
 * @data 2021/6/17 21:28
 */

public class ChannelProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelProvider.class);

    private static EventLoopGroup eventExecutors;

    private static Bootstrap bootstrap = initializeBootstrap();

    private static Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    /**
     * 获得复用的channel
     * @param address 服务端地址
     * @param serializer 序列化器
     * @return  通道
     */
    public static Channel getChannel(InetSocketAddress address, AbstractSerializer serializer) throws InterruptedException {
        String key = address.toString() + serializer.getSerializerCode();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if (channel != null && channel.isActive()) {
                //通道存在并空闲,返回
                return channel;
            }else {
                //移除正在使用的通道
                channelMap.remove(key);
            }
        }

        //不存在key,创建通道并把channel放入map中
        //给客户端设置handler
        bootstrap.handler(new NettyServerInitializer(serializer));

        Channel channel = null;
        try {
            channel = connect(bootstrap,address);
        } catch (ExecutionException e) {
            LOGGER.error("连接客户端并创建通道失败!",e);
            return null;
        }
        channelMap.put(key,channel);
        return channel;
    }

    /**
     * 连接服务端，获得通道
     * @param bootstrap 客户端
     * @param address 服务端地址
     * @return get()通道
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static Channel connect(Bootstrap bootstrap,InetSocketAddress address) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        //给连接注册一个监听器看是否成功
        bootstrap.connect(address).addListener((ChannelFutureListener) future -> {    //new ChannelFutureListener对象
            if (future.isSuccess()) {
                //监听当前操作是否成功
                LOGGER.info("客户端连接服务端成功");
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException("连接服务端出错!");
            }
        });

        //get()返回为连接通道
        return completableFuture.get();
    }

    /**
     * 初始化客户端Bootstrap
     * @return
     */
    private static Bootstrap initializeBootstrap() {
        eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }


}
