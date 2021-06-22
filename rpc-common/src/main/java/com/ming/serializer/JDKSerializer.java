package com.ming.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author ming
 * @data 2021/6/19 20:52
 */

public class JDKSerializer extends AbstractSerializer{

    private static final Logger LOGGER = LoggerFactory.getLogger(JDKSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        return null;
    }

    @Override
    public int getSerializerCode() {
        return 0;
    }
}
