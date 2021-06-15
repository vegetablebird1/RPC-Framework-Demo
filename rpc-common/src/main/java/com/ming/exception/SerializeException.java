package com.ming.exception;

/**
 * @author ming
 * @data 2021/6/14 22:24
 */

public class SerializeException extends RuntimeException{

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
