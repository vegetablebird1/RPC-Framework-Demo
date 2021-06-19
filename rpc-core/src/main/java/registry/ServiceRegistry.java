package registry;

import java.net.InetSocketAddress;

/**
 * 服务注册
 * @author ming
 * @data 2021/6/15 22:17
 */

public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress address);

}
