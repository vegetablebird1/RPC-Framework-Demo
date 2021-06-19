package registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import com.ming.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceRegistry;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author ming
 * @data 2021/6/15 22:20
 */

public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            NacosUtil.registerInstance(serviceName,address);
            LOGGER.info("注册 {} 服务成功",serviceName);
        } catch (NacosException e) {
            LOGGER.error("注册 {} 服务出错!",serviceName,e);
            throw new RpcException(RpcErrorCode.REGISTER_SERVICE_FAILED);
        }
    }
}
