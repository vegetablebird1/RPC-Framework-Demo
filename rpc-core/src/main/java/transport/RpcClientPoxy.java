package transport;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author ming
 * @data 2021/6/15 16:49
 */

public class RpcClientPoxy implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientPoxy.class);

    private final RpcClient rpcClient;

    public RpcClientPoxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public <T> T getProxyInstance(Class<?> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOGGER.info("调用了{}的{}方法",method.getDeclaringClass().getName(),method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(),
                method.getName(),
                args,
                method.getParameterTypes(),
                false);
        RpcResponse rpcResponse = null;
        if (rpcClient instanceof RpcClient) {

        }

        return null;
    }
}
