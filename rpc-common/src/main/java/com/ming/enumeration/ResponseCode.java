package com.ming.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码
 * @author ming
 * @data 2021/6/14 21:58
 */

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200,"调用方法成功"),

    FAIL(400,"调用方法失败"),

    METHOD_NOT_FOUND(400,"找不到此方法"),

    CLASS_NOT_FOUND(400,"找不到此类");

    private final Integer code;

    private final String message;

}
