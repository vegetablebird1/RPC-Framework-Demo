package com.ming.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.enumeration.SerializerCode;
import com.ming.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author ming
 * @data 2021/6/15 11:37
 */

public class KryoSerializer extends AbstractSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);//支持对象循环引用（否则会栈溢出）,默认值就为true
        kryo.setRegistrationRequired(false);//不强制要求注册类，默认就是false
        kryo.register(RpcRequest.class);//注不注册无所谓
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Output output = new Output(outputStream)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            kryo.writeObject(output,object);
            return output.toBytes();
        } catch (IOException e) {
            LOGGER.error("kryo序列化出错!",e);
            throw new SerializeException("kryo序列化出错!");
        }
    }

    @Override
    public Object deserialize(byte[] bytes,Class<?> clazz) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(inputStream)){
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            Object obj = kryo.readObject(input,clazz);
            KRYO_THREAD_LOCAL.remove();
            return obj;
        } catch (IOException e) {
            LOGGER.error("kryo反序列化出错!",e);
            throw new SerializeException("kryo反序列化出错!");
        }
    }

    @Override
    public int getSerializerCode() {
        return SerializerCode.KRYO.getCode();
    }
}
