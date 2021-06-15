package com.ming.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ming
 * @data 2021/6/14 23:23
 */

@AllArgsConstructor
@Getter
public enum RpcErrorCode {

    UNKNOWN_ERROR("出现未知错误"),

    UNKNOWN_PROTOCOL("未知协议"),

    UNKNOWN_SERIALIZER("未知序列化器"),

    UNKNOWN_PACKAGE_TYPE("未知协议数据包类型"),

    SERIALIZER_NOT_FOUND("找不到序列化器"),

    SERVICE_NOT_FOUND("找不到对应服务"),

    SERVICE_INVOCATION_FAILED("远程服务调用失败"),

    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("服务未实现任何接口,无法调用"),

    RESPONSE_NOT_MATCH("响应与请求号不匹配"),

    REGISTER_SERVICE_FAILED("注册服务失败"),

    FAILED_TO_CONNECTION_TO_SERVICE_REGISTRY("连接注册中心失败"),

    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败");

    private final String message;

}
