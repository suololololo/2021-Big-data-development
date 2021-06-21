package com.jiajun;

/**
 * @Author: jiajun
 * @Date: 2021-06-19 11:12
 */
public class Bean {
    private String destination;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Bean(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "destination='" + destination + '\'' +
                '}';
    }
}
