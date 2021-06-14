package com.jiajun.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 14:27
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TDItem {
    private String name;
    private Class<?> clazz;
}
