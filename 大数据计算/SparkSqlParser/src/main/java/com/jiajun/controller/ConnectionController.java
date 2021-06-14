package com.jiajun.controller;

import com.jiajun.result.Result;
import com.jiajun.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 14:42
 */
@Controller
public class ConnectionController {
    @Autowired
    DatabaseService databaseService;

    @RequestMapping("/index")
    public String getConnect() {
        return "index";
    }

    @RequestMapping("/connect")
    @ResponseBody
    public Result getDatabase(ModelMap modelMap) {
        ResultSet resultSet = databaseService.listDatabase();
        return dataProcess(resultSet);
    }

    @RequestMapping("/tables")
    @ResponseBody
    public Result getTables(@RequestParam("databaseName") String name) {
        ResultSet resultSet = databaseService.listTables(name);
        Result result = dataProcess(resultSet);


        return result;
    }

    @RequestMapping("/data")
    @ResponseBody
    public Result getData(@RequestParam("databaseName") String databaseName, @RequestParam("tableName") String tableName) {
        ResultSet resultSet = databaseService.listData(databaseName, tableName);
        return dataProcess(resultSet);
    }

    @RequestMapping("desc")
    @ResponseBody
    public Result getDesc(@RequestParam("databaseName") String databaseName, @RequestParam("tableName") String tableName) {
        ResultSet resultSet = databaseService.listTupleDesc(databaseName, tableName);
        return dataProcess(resultSet);
    }


    private Result dataProcess(ResultSet resultSet) {
        Result result = null;
        List<Map<String, String>> res = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int column = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= column; i++) {
                    map.put(metaData.getColumnName(i), resultSet.getString(i));
                }
                res.add(map);
            }
        } catch (SQLException e) {
            return new Result(-1, e);
        }
        return new Result(1, res);
    }

    @PostMapping("/sql")
    @ResponseBody
    public Result execute(String databaseName, String sql) {
        ResultSet resultSet = null;
        try {
           resultSet = databaseService.executeSql(databaseName, sql);
        } catch (Exception e) {
            return new Result(-1,null);
        }


        return dataProcess(resultSet);
    }

    @PostMapping("/config")
    @ResponseBody
    public Result connect(String connectionName, String ip, String port, String userName, String password) {
        ResultSet resultSet = null;
        try {
            resultSet = databaseService.conect(connectionName,ip,port,userName,password);
        }
        catch (Exception e) {
//            return "index";

            return new Result(-1,null);
        }
//        return "detail";
        return new Result(1,null);
    }
    @RequestMapping("/detail")
    public String detail() {
        return "detail";
    }
}
