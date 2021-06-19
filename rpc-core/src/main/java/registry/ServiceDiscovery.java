package registry;

import java.net.InetSocketAddress;

/**
 * 服务发现
 * @author ming
 * @data 2021/6/15 22:18
 */

public interface ServiceDiscovery {

    InetSocketAddress findService(String serviceName);

}
