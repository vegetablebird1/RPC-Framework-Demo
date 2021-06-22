package transport;

import annotain.Service;
import annotain.ServiceScan;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import com.ming.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import provider.ServiceProvider;
import registry.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.Set;

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
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass = null;//启动类
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                LOGGER.error("启动类缺少 @ServiceScan注解!");
                throw new RpcException(RpcErrorCode.SERVICE_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("出现未知错误!",e);
        }

        //扫描哪个包下的
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)) {
            if (mainClassName.lastIndexOf(".") == -1) {
                basePackage = "";
            } else basePackage = mainClassName.substring(0,mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classes = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object object = null;
                try {
                    object = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error("创建" + clazz + "实例出现错误!",e);
                    continue;
                }
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> aInterface : interfaces) {
                        publishService(object,aInterface.getCanonicalName());
                    }
                } else {
                    publishService(object,serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        //注册服务
        serviceRegistry.register(serviceName,new InetSocketAddress(hostname,port));

        //服务添加到服务列表中
        serviceProvider.addServiceProvider(service,serviceName);
    }
}
