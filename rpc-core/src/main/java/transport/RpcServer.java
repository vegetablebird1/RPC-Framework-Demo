package transport;

import com.ming.serializer.AbstractSerializer;

/**
 * @author ming
 * @data 2021/6/15 16:21
 */

public interface RpcServer {

    int DEFAULT_SERIALIZER = AbstractSerializer.DEFAULT_SERIALIZER;

    void start();

    <T> void publishService(T service,String serviceName);

}
