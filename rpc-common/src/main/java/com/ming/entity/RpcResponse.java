package com.ming.entity;

import com.ming.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 远程调用响应包
 * @author ming
 * @data 2021/6/14 21:51
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RpcResponse<T> implements Serializable {

    /**
     * 响应id
     */
    private String responseId;

    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 响应补充信息
     */
    private String message;

    /**
     * 响应结果
     */
    private T data;


    /**
     * 返回成功结果
     */
    public static <T> RpcResponse<T> success(String responseId,T data){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setResponseId(responseId)
                .setStatusCode(ResponseCode.SUCCESS.getCode())
                .setMessage(ResponseCode.SUCCESS.getMessage())
                .setData(data);
        return rpcResponse;
    }

    /**
     * 返回错误结果
     */
    public static <T> RpcResponse<T> fail(String responseId,ResponseCode code){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setResponseId(responseId)
                .setStatusCode(code.getCode())
                .setMessage(code.getMessage());
        return rpcResponse;
    }

}
