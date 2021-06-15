package codec;

import com.ming.entity.RpcRequest;
import com.ming.enumeration.PackageType;
import com.ming.serializer.AbstractSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author ming
 * @data 2021/6/15 20:27
 */

public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xAAAAAAAA;

    private final AbstractSerializer serializer;

    public CommonEncoder(AbstractSerializer serializer) {
        this.serializer = serializer;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);

        if (msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_TYPE.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_TYPE.getCode());
        }

        out.writeInt(serializer.getSerializerCode());

        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);

        out.writeBytes(bytes);
    }
}
