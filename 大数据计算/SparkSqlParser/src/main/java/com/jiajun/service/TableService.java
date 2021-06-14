package com.jiajun.service;

import com.jiajun.entity.Table;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 14:36
 */
@Service
public interface TableService {
    List<Table> listTables(String dataBaseName);

}
