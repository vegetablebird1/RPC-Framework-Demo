package com.ming.util;

import com.ming.entity.RpcRequest;
import com.ming.entity.RpcResponse;
import com.ming.enumeration.ResponseCode;
import com.ming.enumeration.RpcErrorCode;
import com.ming.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查远程调用结果是否正确工具类
 * @author ming
 * @data 2021/6/19 16:31
 */

public class RpcResultChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcResultChecker.class);

    private static final String INTERFACE_NAME = "interfaceName: ";

    public RpcResultChecker() {
    }

    public static void checkResult(RpcRequest rpcRequest,RpcResponse rpcResponse) {
        if (rpcResponse == null) {
            LOGGER.error("远程调用失败,serviceName:{}",rpcRequest.getInterfaceName());
            throw new RpcException(RpcErrorCode.SERVICE_INVOCATION_FAILED,INTERFACE_NAME + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getResponseId())) {
            throw new RpcException(RpcErrorCode.RESPONSE_NOT_MATCH,INTERFACE_NAME + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            LOGGER.error("调用方法失败,serviceName:{},RpcResponse:{}",rpcRequest.getInterfaceName(),rpcResponse);
            throw new RpcException(RpcErrorCode.SERVICE_INVOCATION_FAILED,INTERFACE_NAME + rpcRequest.getInterfaceName());
        }
    }
}
