package com.ming;

import com.ming.entity.RpcRequest;
import com.ming.serializer.AbstractSerializer;
import com.ming.serializer.KryoSerializer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * @author ming
 * @data 2021/6/15 14:55
 */

public class Test {

    public static void main(String[] args) {
        AbstractSerializer serializer = new KryoSerializer();

        RpcRequest rpcRequest = new RpcRequest("1","2","3",new Object[]{1},new Class[]{Integer.class},false);

        byte[] bytes = serializer.serialize(rpcRequest);
        System.out.println(new String(bytes,StandardCharsets.UTF_8));

        Object o = serializer.deserialize(bytes, RpcRequest.class);

        System.out.println(o);

    //     CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    //         System.out.println("开始...");
    //         try {
    //             TimeUnit.SECONDS.sleep(2);
    //             System.out.println("结束");
    //             return 2;
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //         }
    //         return 3;
    //     });
    //
    //     try {
    //         TimeUnit.SECONDS.sleep(1);
    //     } catch (InterruptedException e) {
    //         e.printStackTrace();
    //     }
    //     future.completeExceptionally(new RuntimeException("gg"));
    //
    //     try {
    //         System.out.println(future.get());
    //     } catch (InterruptedException | ExecutionException e) {
    //         e.printStackTrace();
    //     }


    }

}
