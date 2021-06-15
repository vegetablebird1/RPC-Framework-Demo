package com.ming.serializer;

/**
 * 序列化器
 * @author ming
 * @data 2021/6/14 23:58
 */

public abstract class AbstractSerializer {

    public static final int KRYO_SERIALIZER = 0;

    public static final int JSON_SERIALIZER = 1;

    public static final int HESSIAN_SERIALIZER = 2;

    public static final int PROTOBUF_SERIALIZER = 3;

    public static final int  DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    public static AbstractSerializer getSerializerByCode(int code) {
        switch (code) {
            case KRYO_SERIALIZER :
                return new KryoSerializer();
            case JSON_SERIALIZER:
                return new JsonSerializer();
            case HESSIAN_SERIALIZER:
                return new HessianSerializer();
            case PROTOBUF_SERIALIZER:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }

    public abstract byte[] serialize(Object object);

    public abstract Object deserialize(byte[] bytes,Class<?> clazz);

    public abstract int getSerializerCode();

}
