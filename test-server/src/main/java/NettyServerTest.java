import com.alibaba.nacos.api.exception.NacosException;
import com.ming.api.ByeService;
import com.ming.api.HelloService;
import com.ming.serializer.AbstractSerializer;
import impl.ByeServiceImpl;
import impl.HelloServiceImpl;
import transport.RpcServer;
import transport.netty.server.NettyServer;

import java.net.InetSocketAddress;

/**
 * @author ming
 * @data 2021/6/18 23:37
 */

public class NettyServerTest {

    private static final String hostname = "127.0.0.1";

    private static final String remote = "47.107.36.140";

    private static final int port = 6666;

    public static void main(String[] args) throws NacosException {

        InetSocketAddress address = new InetSocketAddress(hostname, port);

        RpcServer server = new NettyServer(address, AbstractSerializer.HESSIAN_SERIALIZER);

        /*---------------手动注册服务-----------------------*/
        server.publishService(new HelloServiceImpl(),HelloService.class.getName());

        server.publishService(new ByeServiceImpl(), ByeService.class.getName());
        /*------------------------------------------------*/

        server.start();
    }

}
