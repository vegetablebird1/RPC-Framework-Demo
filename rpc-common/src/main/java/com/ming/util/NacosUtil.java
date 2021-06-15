package com.ming.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ming
 * @data 2021/6/15 19:37
 */

public class NacosUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;

    private static final String SERVICE_ADDRESS = "127.0.0.1:8848";

    private static InetSocketAddress address;

    private static final Set<String> serviceSet = new HashSet<>();

    static {
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVICE_ADDRESS);
        } catch (NacosException e) {
            LOGGER.error("连接nacos出现错误:",e);
            throw new RpcException(RpcErrorCode.FAILED_TO_CONNECTION_TO_SERVICE_REGISTRY);
        }
    }


    public static void registerInstance(String serviceName,InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName,address.getHostName(),address.getPort());
        NacosUtil.address = address;
        serviceSet.add(serviceName);
    }

    /**
     * 获得使用同一服务的所有实例
     * @param serviceName
     * @return
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry(){
        if (!serviceSet.isEmpty() && address != null) {
            String hostname = address.getHostName();
            int port = address.getPort();
            serviceSet.stream().forEach(service -> {
                try {
                    namingService.deregisterInstance(service,hostname,port);
                } catch (NacosException e) {
                    LOGGER.error("注销服务 {} 失败",service,e);
                }
            });
        }
    }


}
