package com.ming.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 调用hello方法参数
 * @author ming
 * @data 2021/6/14 21:32
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;

    private String message;

}
