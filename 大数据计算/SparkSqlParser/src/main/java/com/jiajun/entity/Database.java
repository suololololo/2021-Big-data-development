package com.jiajun.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 14:23
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Database {
    private String name;
    private List<Table> tableList;
}
