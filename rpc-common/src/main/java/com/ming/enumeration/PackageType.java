package com.ming.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ming
 * @data 2021/6/14 23:48
 */

@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_TYPE(0),

    RESPONSE_TYPE(1);

    private final int code;

}
