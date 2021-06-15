package com.ming.api.impl;

import com.ming.api.HelloObject;
import com.ming.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ming
 * @data 2021/6/14 21:34
 */

public class HelloServiceImpl implements HelloService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject helloObject) {
        LOGGER.info("远程调用了HelloServiceImpl的hello()方法,请求参数为{}",helloObject.getMessage());
        return "HelloServiceImpl返回结果成功";
    }
}
