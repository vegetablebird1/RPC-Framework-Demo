package handler;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import provider.ServiceProvider;
import provider.impl.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RPC处理器
 * @author ming
 * @data 2021/6/15 20:43
 */

public class RequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private static final ServiceProvider serviceProvider = new ServiceProviderImpl();


    public Object handler(RpcRequest rpcRequest) {
        Object service = RequestHandler.serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest,service);
    }

    /**
     * 调用目标方法
     * @param rpcRequest 请求
     * @param service 服务调用传入的服务
     * @return
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) {
        Object result = null;
        try {
            //调用目标方法是什么
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            result = method.invoke(service,rpcRequest.getParams());
            LOGGER.info("成功调用 {} 服务的 {} 方法",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.info("调用服务 {} 的 {} 方法失败!",rpcRequest.getInterfaceName(),rpcRequest.getMethodName(),e);
            return RpcResponse.fail(rpcRequest.getRequestId(),ResponseCode.METHOD_NOT_FOUND);
        }
        return result;
    }

}
