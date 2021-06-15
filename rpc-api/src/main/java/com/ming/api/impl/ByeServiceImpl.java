package com.ming.api.impl;

import com.ming.api.ByeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ming
 * @data 2021/6/14 21:37
 */

public class ByeServiceImpl implements ByeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ByeServiceImpl.class);

    @Override
    public String bye(String name) {
        LOGGER.info("远程调用了ByeServiceImpl的bye()方法");
        return String.format("bye,[%s]",name);
    }
}
