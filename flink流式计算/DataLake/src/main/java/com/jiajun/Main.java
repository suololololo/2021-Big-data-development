package com.jiajun;

/**
 * @Author: jiajun
 * @Date: 2021-06-17 11:18
 */
public class Main {
    public static void main(String[] args) throws Exception{
        Producer producer = new Producer();
        while (true) {
            producer.writeToKafka();
            Thread.sleep(500);
        }


    }
}
