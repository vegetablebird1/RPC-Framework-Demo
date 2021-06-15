package com.ming.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ming.entity.RpcRequest;
import com.ming.enumeration.SerializerCode;
import com.ming.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author ming
 * @data 2021/6/15 11:38
 */

public class JsonSerializer extends AbstractSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializer.class);


    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONString(object).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Object obj = JSONObject.parseObject(bytes, clazz);
        if (obj instanceof RpcRequest) {
            return handlerRequest(obj);
        } else {
            LOGGER.error("Json序列化出错!");
            throw new SerializeException("Json序列化出错!");
        }
    }


    /**
     * 判断反序列化后参数类型是否一致
     * @param object
     * @return
     */
    private Object handlerRequest(Object object) {
        RpcRequest rpcRequest = (RpcRequest) object;
        for (int i = 0 ; i < rpcRequest.getParamTypes().length ; i++){
            Class<?> clazz =rpcRequest.getParamTypes()[i];
            if (!clazz.isAssignableFrom(rpcRequest.getParams()[i].getClass())) {
                byte[] bytes = JSON.toJSONBytes(rpcRequest.getParams()[i]);
                rpcRequest.getParams()[i] = JSONObject.parseObject(new String(bytes,StandardCharsets.UTF_8),clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getSerializerCode() {
        return SerializerCode.JSON.getCode();
    }
}
