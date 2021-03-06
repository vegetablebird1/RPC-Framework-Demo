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
                    .handler(new LoggingHandler(LogLevel.INFO))//???????????????
                    .option(ChannelOption.SO_BACKLOG,256)//??????????????????????????????????????????
                    .option(ChannelOption.SO_KEEPALIVE,true)//??????TCP????????????????????????
                    .option(ChannelOption.TCP_NODELAY,true)//????????????Nagle??????
                    .childHandler(new NettyServerInitializer(serializer));

            ChannelFuture channelFuture = serverBootstrap.bind(hostname, port).sync();
            channelFuture.channel().closeFuture().sync();//??????????????????

        } catch (InterruptedException e) {
            LOGGER.error("???????????????????????????:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
