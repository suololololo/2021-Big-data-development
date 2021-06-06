package com.jiajun;

import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: jiajun
 * @Date: 2021-06-02 15:38
 */
public class RedisManager {
    private static final String logFile = "D:\\»Ìπ§—ßœ∞\\log.txt";
    private static File file = new File(logFile);
    private static Map<String, Cache> map = new ConcurrentHashMap<>();

    public synchronized static void writeObject() {
        FileOutputStream fileOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(map);

        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {

        }
    }

    public static Map<String, Cache> readObject() {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Map<String, Cache> map = new HashMap<>();
            map = (Map<String, Cache>) objectInputStream.readObject();
            return map;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } catch (ClassNotFoundException e) {

        }
        return null;
    }

    public static void set(String fileName, Cache cache) {
        map.put(fileName,cache);
    }

    public static void remove(String fileName) {
        map.remove(fileName);
    }


}
