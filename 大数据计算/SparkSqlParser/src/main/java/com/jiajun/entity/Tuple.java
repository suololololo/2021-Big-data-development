package com.jiajun.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 14:25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tuple {
    private String name;
    private List<String> value;

}
