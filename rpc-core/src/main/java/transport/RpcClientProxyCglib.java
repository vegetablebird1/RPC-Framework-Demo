package transport;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.util.RpcResultChecker;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import transport.netty.client.NettyClient;
import transport.socket.client.SocketClient;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 使用Cglib方式实现动态代理
 * @author ming
 * @data 2021/6/25 17:58
 */

@Slf4j
public class RpcClientProxyCglib implements MethodInterceptor {

    private final RpcClient rpcClient;

    public RpcClientProxyCglib(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    //clazz被代理对象
    @SuppressWarnings("unchecked")
    public <T> T getProxyInstance(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        //设置enhancer的父类
        enhancer.setSuperclass(clazz);
        //设置回调函数
        enhancer.setCallback(this);

        return (T) enhancer.create();
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        log.info("调用了{}的{}方法",method.getDeclaringClass().getName(),method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(),
                method.getName(),
                objects,
                method.getParameterTypes(),
                false);



        //发生调用请求
        RpcResponse rpcResponse = null;
        if (rpcClient instanceof NettyClient) {
            try {
                CompletableFuture<RpcResponse> future = (CompletableFuture<RpcResponse>) rpcClient.sendRequest(rpcRequest);
                rpcResponse = future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("调用远程方法失败", e);
                return null;
            }
        } else if (rpcClient instanceof SocketClient) {
            rpcResponse = (RpcResponse) rpcClient.sendRequest(rpcRequest);
        }

        RpcResultChecker.checkResult(rpcRequest,rpcResponse);
        return rpcResponse.getData();
    }
}
