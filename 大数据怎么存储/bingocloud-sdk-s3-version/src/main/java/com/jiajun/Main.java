package com.jiajun;

import redis.clients.jedis.Jedis;

import java.io.File;
import java.util.Date;
import java.util.Scanner;

/**
 * @Author: jiajun
 * @Date: 2021-06-01 20:14
 */
public class Main {
    public static void main(String[] args) {
//        final String accessKey = "261EB1C4FC64A6CC7C9D";
//        final String secrectKey =   "W0YyQjczMTVCNkQ3NEI4RkE0REU5QjE3QjcyQUYw";
//        final String dir = "D:\\»Ìπ§—ßœ∞\\js\\";
//        final String bucketName = "chenjiajun";
        Scanner input = new Scanner(System.in);
        System.out.println("enter the accessKey");
        String accessKey = input.nextLine();
        System.out.println("enter the secrectKey");
        String secrectKey = input.nextLine();
        System.out.println("enter the dir");
        String dir = input.nextLine();
        System.out.println("enter the bucketName");
        String bucketName = input.nextLine();

        FileSychronizer fileSychronizer = new FileSychronizer(dir, bucketName, accessKey, secrectKey);
        fileSychronizer.start();
        fileSychronizer.listen();


}


}
