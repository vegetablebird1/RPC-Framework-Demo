package transport.socket.server;

import com.ming.factory.ThreadPoolFactory;
import com.ming.serializer.AbstractSerializer;
import handler.RequestHandler;
import hook.ShutdownHook;
import provider.impl.ServiceProviderImpl;
import registry.impl.NacosServiceRegistry;
import transport.AbstractRpcServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author ming
 * @data 2021/6/21 11:21
 */

public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;
    private final AbstractSerializer serializer;
    private final RequestHandler requestHandler;

    public SocketServer(InetSocketAddress address) {
        this(address.getHostName(),address.getPort());
    }

    public SocketServer(InetSocketAddress address,int serializer) {
        this(address.getHostName(),address.getPort(),serializer);
    }

    public SocketServer(String hostname,int port) {
        this(hostname,port,DEFAULT_SERIALIZER);
    }

    public SocketServer(String hostname,int port,int serializer) {
        this.hostname = hostname;
        this.port = port;
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = AbstractSerializer.getSerializerByCode(serializer);
        this.requestHandler = new RequestHandler();
    }

    @Override
    public void start() {
        start0();
    }

    private void start0() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(hostname,port));
            LOGGER.info("[Socket]服务端正在启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket = null;

            while ((socket = serverSocket.accept()) != null) {
                LOGGER.info("[Socket]服务端连接到 {} : {} 的客户端",socket.getInetAddress().getHostAddress(),socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket,requestHandler,serializer));
            }
        } catch (IOException e) {
            LOGGER.error("[Socket]服务端启动出现错误!",e);
        }
    }

}
