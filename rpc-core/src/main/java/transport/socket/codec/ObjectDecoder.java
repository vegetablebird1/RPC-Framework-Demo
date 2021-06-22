package transport.socket.codec;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.enumeration.PackageType;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import com.ming.serializer.AbstractSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author ming
 * @data 2021/6/21 11:47
 */

public class ObjectDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectDecoder.class);

    private static final int MAGIC_NUMBER = 0xAAAAAAAA;

    public static Object decode(InputStream inputStream) throws IOException {
        byte[] numBytes = new byte[4];
        inputStream.read(numBytes);
        int magicNum = bytesToInt(numBytes);
        if (magicNum != MAGIC_NUMBER) {
            LOGGER.error("协议包不正确，出现未知协议包:{}",magicNum);
            throw new RpcException(RpcErrorCode.UNKNOWN_PROTOCOL);
        }

        inputStream.read(numBytes);
        int packageType = bytesToInt(numBytes);
        Class<?> packageClass = null;
        if (packageType == PackageType.REQUEST_TYPE.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageType == PackageType.RESPONSE_TYPE.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            LOGGER.error("未知的数据包类型: {}",packageType);
            throw new RpcException(RpcErrorCode.UNKNOWN_PACKAGE_TYPE);
        }

        inputStream.read(numBytes);
        int serializerCode = bytesToInt(numBytes);
        AbstractSerializer serializer = AbstractSerializer.getSerializerByCode(serializerCode);
        if (serializer == null) {
            LOGGER.error("找不到序列化器!");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }

        inputStream.read(numBytes);
        int length = bytesToInt(numBytes);
        byte[] bytes = new byte[length];
        inputStream.read(bytes);
        return serializer.deserialize(bytes,packageClass);
    }


    public static int bytesToInt(byte[] bytes) {
        int value;
        value = ((bytes[0] & 0xFF) << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | (bytes[3] & 0xFF);
        return value;
    }

}
