package transport.netty.client;

import com.ming.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放未处理的请求,请求会异步执行
 * @author ming
 * @data 2021/6/17 18:53
 */

public class UnprocessedRequest {

    //任务完成结果为RpcResponse,存入map中
    private static final ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId,CompletableFuture<RpcResponse> future) {
        unprocessedResponseFutures.put(requestId,future);
    }

    public void remove(String requestId){
        unprocessedResponseFutures.remove(requestId);
    }

    //完成某項任務
    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getResponseId());
        if (future != null) {
            future.complete(rpcResponse);//get方法调用时，任务未完成返回rpcResponse
        } else {
            throw new IllegalStateException();
        }


    }

}
