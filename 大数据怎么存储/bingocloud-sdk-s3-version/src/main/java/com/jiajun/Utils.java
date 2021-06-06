package com.jiajun;

import java.io.*;
import java.util.regex.Pattern;

/**
 * @Author: jiajun
 * @Date: 2021-06-02 11:34
 */
public class Utils {
    public static byte[] serialize(Object object) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] bytes = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            objectOutputStream.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return bytes;
    }

    public static Object deserialize(byte[] bytes)  {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (IOException e){

        } catch (ClassNotFoundException e) {

        }
        return null;

    }


}
