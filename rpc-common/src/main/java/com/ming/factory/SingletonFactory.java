package com.ming.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ming
 * @data 2021/6/15 17:07
 */

public class SingletonFactory {

    private static final Map<Class<?>,Object> OBJECT_MAP = new HashMap<>();

    public SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> clazz){
        Object instance = OBJECT_MAP.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                    OBJECT_MAP.put(clazz, instance);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return clazz.cast(instance);
    }
}
