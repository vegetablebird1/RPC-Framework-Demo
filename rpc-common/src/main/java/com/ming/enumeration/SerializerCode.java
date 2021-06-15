package com.ming.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化方式
 * @author ming
 * @data 2021/6/14 22:26
 */

@AllArgsConstructor
@Getter
public enum SerializerCode {

    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;

}
