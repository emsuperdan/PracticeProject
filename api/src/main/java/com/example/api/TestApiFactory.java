package com.example.api;

import android.util.LruCache;

import com.example.annotation.annotation.TestClassAnnotation;
import com.example.annotation.annotation.bean.DiffMeta;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * {类的功能描述}
 *
 * @author dan.tang
 * @date 2021-5-1
 */
public class TestApiFactory {
    private final LruCache<String, Object> mCache = new LruCache<>(20);
    private static final String SEPARATOR = "$$";

    public <T> T create(Class<T> tClass, HashMap<Class, DiffMeta> diffMetas) {
        DiffMeta diffMeta = diffMetas.get(tClass);

        Object newProxyInstance = Proxy.newProxyInstance(tClass.getClassLoader(),
                new Class[]{tClass},
                (proxy, method, args) -> method.invoke(getInstance(tClass, method, diffMetas),
                        args));

        return (T) newProxyInstance;
    }

    private <T> T getInstance(Class<T> tClass, Method method, HashMap<Class, DiffMeta> diffMetas) {
        String key = tClass.getCanonicalName() + SEPARATOR + method.getName();
        if (mCache.get(key) != null){
            return (T) mCache.get(key);
        }

//        DiffMeta diffMeta = diffMetas.get(key);
//        if (diffMeta !=null){
//            Class selectClass = null;
//            for (Class implClass : diffMeta.getSubClass()){
//                TestClassAnnotation testClassAnnotation = null;
//
//            }
//        }


        Class selectClass = null;
        for (Class implClass : diffMetas.get(key).getSubClass()){
            selectClass = implClass;
        }
        T diff = null;
        try {
            diff = (T) selectClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        mCache.put(key, diff);
        return diff;
    }
}
