package transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import provider.ServiceProvider;
import registry.ServiceRegistry;

import java.net.InetSocketAddress;

/**
 * @author ming
 * @data 2021/6/15 16:40
 */

public abstract class AbstractRpcServer implements RpcServer {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected String hostname;

    protected Integer port;

    //服务提供和注册
    protected ServiceRegistry serviceRegistry;

    protected ServiceProvider serviceProvider;

    public void scanServices() {



    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        //注册服务
        serviceRegistry.register(serviceName,new InetSocketAddress(hostname,port));

        //服务添加到服务列表中
        serviceProvider.addServiceProvider(service,serviceName);
    }
}
