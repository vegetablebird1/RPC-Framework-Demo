import annotain.ServiceScan;
import com.ming.api.ByeService;
import com.ming.api.HelloObject;
import com.ming.api.HelloService;
import transport.RpcClient;
import transport.RpcClientProxy;
import transport.RpcClientProxyCglib;
import transport.socket.client.SocketClient;

/**
 * @author ming
 * @data 2021/6/21 13:26
 */

public class SocketClientTest {

    public static void main(String[] args) {

        SocketClient socketClient = new SocketClient(RpcClient.DEFAULT_SERIALIZER);

        // RpcClientProxy proxy = new RpcClientProxy(socketClient);
        //
        // HelloService helloService = proxy.getProxyInstance(HelloService.class);

        RpcClientProxyCglib proxy = new RpcClientProxyCglib(socketClient);
        HelloService helloService = proxy.getProxyInstance(HelloService.class);

        HelloObject helloObject = new HelloObject(777,"hello,world!");
        String hello = helloService.hello(helloObject);
        System.out.println(hello);

        ByeService byeService = proxy.getProxyInstance(ByeService.class);
        String bye = byeService.bye("socket");
        System.out.println(bye);
    }
}
