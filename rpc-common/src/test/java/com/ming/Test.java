package com.ming;

import com.ming.entity.RpcRequest;
import com.ming.serializer.AbstractSerializer;
import com.ming.serializer.KryoSerializer;

import java.nio.charset.StandardCharsets;

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

    }

}
