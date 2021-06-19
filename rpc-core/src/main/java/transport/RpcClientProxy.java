package transport;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.util.RpcResultChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.netty.client.NettyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 代理客户端，向服务端发送请求
 * @author ming
 * @data 2021/6/15 16:49
 */

public class RpcClientProxy implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientProxy.class);

    //被代理对象
    private final RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxyInstance(Class<?> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
        LOGGER.info("调用了{}的{}方法",method.getDeclaringClass().getName(),method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(),
                method.getName(),
                args,
                method.getParameterTypes(),
                false);

        //发生调用请求
        RpcResponse rpcResponse = null;
        if (rpcClient instanceof NettyClient) {
            try {
                CompletableFuture<RpcResponse> future = (CompletableFuture<RpcResponse>) rpcClient.sendRequest(rpcRequest);
                rpcResponse = future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("调用远程方法失败", e);
                return null;
            }
        }

        RpcResultChecker.checkResult(rpcRequest,rpcResponse);
        return rpcResponse.getData();
    }
}
