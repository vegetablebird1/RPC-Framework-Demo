import com.ming.api.HelloObject;
import com.ming.api.HelloService;
import com.ming.serializer.AbstractSerializer;
import transport.RpcClient;
import transport.RpcClientProxy;
import transport.netty.client.NettyClient;

/**
 * @author ming
 * @data 2021/6/18 23:37
 */

public class NettyClientTest {

    public static void main(String[] args) {

        RpcClient client = new NettyClient(AbstractSerializer.JSON_SERIALIZER);

        RpcClientProxy proxy = new RpcClientProxy(client);

        HelloService service = proxy.getProxyInstance(HelloService.class);

        HelloObject object = new HelloObject(777,"Hello,world");

        String res = service.hello(object);

        System.out.println(res);


    }

}
