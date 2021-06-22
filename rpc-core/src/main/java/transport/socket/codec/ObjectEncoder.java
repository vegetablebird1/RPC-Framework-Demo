package transport.socket.codec;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.enumeration.PackageType;
import com.ming.serializer.AbstractSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ming
 * @data 2021/6/21 12:46
 */

public class ObjectEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectEncoder.class);

    private static final int MAGIC_NUMBER = 0xAAAAAAAA;

    public static void encode(OutputStream outputStream, Object object, AbstractSerializer serializer) throws IOException {
        outputStream.write(intToBytes(MAGIC_NUMBER));

        if (object instanceof RpcRequest) {
            outputStream.write(intToBytes(PackageType.REQUEST_TYPE.getCode()));
        } else if (object instanceof RpcResponse) {
            outputStream.write(intToBytes(PackageType.RESPONSE_TYPE.getCode()));
        }

        outputStream.write(intToBytes(serializer.getSerializerCode()));

        byte[] bytes = serializer.serialize(object);
        outputStream.write(intToBytes(bytes.length));
        outputStream.write(bytes);

        //清空缓冲区
        outputStream.flush();
    }

    public static byte[] intToBytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((value >> 24) & 0xFF);
        bytes[1] = (byte) ((value >> 16) & 0xFF);
        bytes[2] = (byte) ((value >> 8) & 0xFF);
        bytes[3] = (byte) (value & 0xFF);
        return bytes;
    }

}
