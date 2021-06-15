package com.ming.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.ming.enumeration.SerializerCode;
import com.ming.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author ming
 * @data 2021/6/15 11:38
 */

public class HessianSerializer extends AbstractSerializer{

    private static final Logger LOGGER = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(outputStream);
            hessianOutput.writeObject(object);
            return outputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Hessian序列化出错!",e);
            throw new SerializeException("Hessian序列化出错!");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    LOGGER.error("关闭流出错",e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)){
            hessianInput = new HessianInput(inputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
            LOGGER.error("Hessian反序列化出错!",e);
            throw new SerializeException("Hessian反序列化出错!");
        } finally {
            if (hessianInput != null) {
                hessianInput.close();
            }
        }
    }

    @Override
    public int getSerializerCode() {
        return SerializerCode.HESSIAN.getCode();
    }
}
