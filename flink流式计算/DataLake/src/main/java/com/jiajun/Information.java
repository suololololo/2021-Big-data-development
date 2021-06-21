package com.jiajun;

import java.io.Serializable;

/**
 * @Author: jiajun
 * @Date: 2021-06-17 11:02
 */
public class Information implements Serializable {
    private String key;
    private String content;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Information(String key, String content) {
        this.key = key;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Information{" +
                "key='" + key + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
