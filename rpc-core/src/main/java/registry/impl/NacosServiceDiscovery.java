package registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import com.ming.util.NacosUtil;
import loadbalancer.LoadBalancer;
import loadbalancer.impl.RoundRobinBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceDiscovery;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 负载均衡策略默认为轮询
 * @author ming
 * @data 2021/6/15 22:19
 */

public class NacosServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(){
        this(new RoundRobinBalancer());
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress findService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if (instances.isEmpty()) {
                LOGGER.error("找不到 {} 服务,请检查调用服务名!",serviceName);
                throw new RpcException(RpcErrorCode.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            LOGGER.info("成功找到 {} 服务",serviceName);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            LOGGER.error("寻找 {} 服务中出现未知错误!",serviceName,e);
            throw new RpcException(RpcErrorCode.UNKNOWN_ERROR);
        }
    }
}
