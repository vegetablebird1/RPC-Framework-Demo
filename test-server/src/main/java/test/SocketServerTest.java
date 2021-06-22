package test;

import annotain.ServiceScan;
import transport.RpcServer;
import transport.socket.server.SocketServer;

import java.net.InetSocketAddress;

/**
 * @author ming
 * @data 2021/6/21 13:26
 */

@ServiceScan
public class SocketServerTest {

    public static final String hostname = "127.0.0.1";

    private static final int port = 6666;

    public static void main(String[] args) {

        SocketServer socketServer = new SocketServer(new InetSocketAddress(hostname, port), RpcServer.DEFAULT_SERIALIZER);

        // socketServer.publishService(new HelloServiceImpl(), HelloService.class.getName());
        //
        // socketServer.publishService(new ByeServiceImpl(), ByeService.class.getName());

        socketServer.start();

    }

}
