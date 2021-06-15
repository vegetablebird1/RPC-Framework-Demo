package com.ming.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 远程调用请求包
 * @author ming
 * @data 2021/6/14 21:40
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 请求包id
     */
    private String requestId;

    /**
     * 远程方法的所在接口名
     */
    private String interfaceName;

    /**
     * 调用方法名
     */
    private String methodName;

    /**
     * 方法参数
     */
    private Object[] params;

    /**
     * 方法参数类型
     */
    private Class<?>[]paramTypes;

    /**
     * 是否是心跳包
     */
    private Boolean isHeartBeat;

}
