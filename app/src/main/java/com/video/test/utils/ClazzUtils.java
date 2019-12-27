package com.video.test.utils;

import java.lang.reflect.ParameterizedType;

/**
 * @author Enoch Created on 2018/6/25.
 */
public class ClazzUtils {

    private ClazzUtils() {
        throw new UnsupportedOperationException("UtilClazz cant't initialize");
    }

    /**
     * getGenericSuperclass(): 获得带有泛型的父类
     * ParameterizedType  :参数化类型,即泛型
     * getActualTypeArguments()[position] 获得参数化类型的数组,泛型可以有多个
     */

    public static <T> T getGenericInstance(Object o, int postinon) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass()))
                    .getActualTypeArguments()[postinon])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }
}
