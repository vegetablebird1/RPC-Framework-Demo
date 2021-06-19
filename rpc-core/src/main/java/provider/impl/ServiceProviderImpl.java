package provider.impl;

import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import provider.ServiceProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务注册表
 * @author ming
 * @data 2021/6/15 22:10
 */

public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();

    private static final Set<String> registerServices = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (registerServices.contains(serviceName)) return;
        registerServices.add(serviceName);
        serviceMap.put(serviceName,service);
        LOGGER.info("向接口: {},注册服务: {}",service.getClass().getInterfaces(),serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) throw new RpcException(RpcErrorCode.SERVICE_NOT_FOUND);
        return service;
    }
}
