package codec;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.enumeration.PackageType;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import com.ming.serializer.AbstractSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 通用解码器
 * @author ming
 * @data 2021/6/14 22:45
 */

public class CommonDecoder extends ReplayingDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDecoder.class);

    private static final Integer MAGIC_NUMBER = 0xAAAAAAAA;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //判断协议
        int magicNum = in.readInt();
        if (magicNum != MAGIC_NUMBER) {
            LOGGER.error("协议包不正确，出现未知协议包:{}",magicNum);
            throw new RpcException(RpcErrorCode.UNKNOWN_PROTOCOL);
        }

        //协议包种类
        int packageType = in.readInt();
        Class<?> packageClass = null;
        if (packageType == PackageType.REQUEST_TYPE.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageType == PackageType.RESPONSE_TYPE.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            LOGGER.error("未知的数据包类型:{}",packageType);
            throw new RpcException(RpcErrorCode.UNKNOWN_PACKAGE_TYPE);
        }

        //序列化器
        int serializerType = in.readInt();
        AbstractSerializer serializer = AbstractSerializer.getSerializerByCode(serializerType);
        if (serializer == null) {
            LOGGER.error("找不到对应的序列化:{}",serializerType);
            throw new RpcException(RpcErrorCode.UNKNOWN_SERIALIZER);
        }

        //序列化字节长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object object = serializer.deserialize(bytes,packageClass);
        out.add(object);
    }
}
