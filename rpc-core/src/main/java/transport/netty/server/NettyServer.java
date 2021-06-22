package transport.netty.server;

import com.ming.serializer.AbstractSerializer;
import hook.ShutdownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import provider.impl.ServiceProviderImpl;
import registry.impl.NacosServiceRegistry;
import transport.AbstractRpcServer;

import java.net.InetSocketAddress;

/**
 * @author ming
 * @data 2021/6/15 16:18
 */

public class NettyServer extends AbstractRpcServer {

    private final AbstractSerializer serializer;

    public NettyServer(InetSocketAddress address) {
        this(address.getHostName(),address.getPort(),DEFAULT_SERIALIZER);
    }

    public NettyServer(InetSocketAddress address,int serializer) {
        this(address.getHostName(),address.getPort(),serializer);
    }

    public NettyServer(String hostname,int port) {
        this(hostname,port,DEFAULT_SERIALIZER);
    }

    public NettyServer(String hostname,int port,int serializer) {
        this.hostname = hostname;
        this.port = port;
        this.serializer = AbstractSerializer.getSerializerByCode(serializer);

        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();

        scanServices();
    }



    @Override
    public void start() {
        start0(hostname,port);
    }

    private void start0(String hostname,int port){
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))//日志处理器
                    .option(ChannelOption.SO_BACKLOG,256)//设置线程队列最大等待连接个数
                    .option(ChannelOption.SO_KEEPALIVE,true)//开启TCP底层心跳检测机制
                    .option(ChannelOption.TCP_NODELAY,true)//禁止使用Nagle算法
                    .childHandler(new NettyServerInitializer(serializer));

            ChannelFuture channelFuture = serverBootstrap.bind(hostname, port).sync();
            channelFuture.channel().closeFuture().sync();//监听关闭事件

        } catch (InterruptedException e) {
            LOGGER.error("启动服务端出现错误:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
