package com.ming.exception;

import com.ming.enumeration.RpcErrorCode;

/**
 * @author ming
 * @data 2021/6/14 22:10
 */

public class RpcException extends RuntimeException {

    public RpcException(RpcErrorCode code) {
        super(code.getMessage());
    }

    public RpcException(RpcErrorCode code,String detail) {
        super(code.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
