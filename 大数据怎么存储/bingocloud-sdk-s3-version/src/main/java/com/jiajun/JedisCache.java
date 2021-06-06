package com.jiajun;

import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * @Author: jiajun
 * @Date: 2021-06-03 9:35
 */
public class JedisCache {
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    static {
        jedis.auth("295747");

    }


    public static void setCache(String keyName, Cache cache) {
        jedis.set(keyName.getBytes(), Utils.serialize(cache));
    }

    public static Cache getCache(String keyName) {

        if (jedis.exists(keyName.getBytes())) {
            byte[] bytes = jedis.get(keyName.getBytes());
            return (Cache) Utils.deserialize(bytes);
        }
        return null;


    }

    public static boolean removeCache(String keyName) {
        if (jedis.exists(keyName.getBytes())) {
            jedis.del(keyName.getBytes());
            return true;
        } else {
            return false;
        }

    }



}
