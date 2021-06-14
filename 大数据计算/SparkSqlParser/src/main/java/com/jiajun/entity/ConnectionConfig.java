package com.jiajun.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 14:38
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionConfig {
    private String connectionName;
    private String ip;
    private String port;
    private String userName;
    private String password;

}
