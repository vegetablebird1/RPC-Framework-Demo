package transport.socket.server;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.serializer.AbstractSerializer;
import handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.socket.codec.ObjectDecoder;
import transport.socket.codec.ObjectEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 处理Socket方式RPC的线程
 * @author ming
 * @data 2021/6/21 11:38
 */

public class SocketRequestHandlerThread implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketRequestHandlerThread.class);

    private final Socket socket;

    private final RequestHandler requestHandler;

    private final AbstractSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, AbstractSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()){
            RpcRequest rpcRequest = (RpcRequest) ObjectDecoder.decode(inputStream);
            Object res = requestHandler.handler(rpcRequest);

            RpcResponse<Object> rpcResponse = RpcResponse.success(rpcRequest.getRequestId(),res);
            ObjectEncoder.encode(outputStream,rpcResponse,serializer);
        } catch (IOException e) {
            LOGGER.error("调用或发送出现错误:",e);
        }
    }
}
