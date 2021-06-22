package transport.socket.client;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.enumeration.ResponseCode;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import com.ming.serializer.AbstractSerializer;
import com.ming.util.RpcResultChecker;
import loadbalancer.LoadBalancer;
import loadbalancer.impl.RoundRobinBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceDiscovery;
import registry.impl.NacosServiceDiscovery;
import transport.RpcClient;
import transport.socket.codec.ObjectDecoder;
import transport.socket.codec.ObjectEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author ming
 * @data 2021/6/21 13:03
 */

public class SocketClient implements RpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);

    private final ServiceDiscovery serviceDiscovery;

    private final AbstractSerializer serializer;

    private static final String INTERFACE_NAME = "interfaceName: ";

    public SocketClient() {
        this(DEFAULT_SERIALIZER);
    }

    public SocketClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER,loadBalancer);
    }

    public SocketClient(int serializer) {
        this(serializer,new RoundRobinBalancer());
    }

    public SocketClient(int serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = AbstractSerializer.getSerializerByCode(serializer);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            LOGGER.error("未设置序列化器!");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress address = serviceDiscovery.findService(rpcRequest.getInterfaceName());

        try (Socket socket = new Socket()){
            socket.connect(address);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ObjectEncoder.encode(outputStream,rpcRequest,serializer);
            Object object = ObjectDecoder.decode(inputStream);
            RpcResponse rpcResponse = (RpcResponse) object;

            if (rpcResponse == null) {
                LOGGER.error("服务调用失败!,service: {}",rpcRequest.getInterfaceName());
                throw new RpcException(RpcErrorCode.SERVICE_INVOCATION_FAILED,INTERFACE_NAME + rpcRequest.getInterfaceName());
            }

            if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
                LOGGER.error("服务调用失败!,service: {} ,response: {}",rpcRequest.getInterfaceName(),rpcResponse);
                throw new RpcException(RpcErrorCode.SERVICE_INVOCATION_FAILED,INTERFACE_NAME + rpcRequest.getInterfaceName());
            }

            RpcResultChecker.checkResult(rpcRequest,rpcResponse);
            return rpcResponse;

        } catch (IOException e) {
            LOGGER.error("调用时发生错误:",e);
        }
        return null;
    }

}
