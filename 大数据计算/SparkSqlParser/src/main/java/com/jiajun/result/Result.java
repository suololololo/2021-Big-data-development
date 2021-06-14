package com.jiajun.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 15:36
 */
@Getter
@Setter
@AllArgsConstructor
public class Result<T>{
    private int code;
    private T data;

}
