package transport;

import com.ming.entity.RpcRequest;
import com.ming.serializer.AbstractSerializer;

/**
 * @author ming
 * @data 2021/6/15 16:23
 */

public interface RpcClient {

    int DEFAULT_SERIALIZER = AbstractSerializer.DEFAULT_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
