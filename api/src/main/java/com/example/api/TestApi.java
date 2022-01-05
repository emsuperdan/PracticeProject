package com.example.api;

import com.example.annotation.annotation.bean.DiffMeta;

import java.util.HashMap;

/**
 *
 * @author dan.tang
 * @date 2021-5-1
 */
public class TestApi {
    private volatile static TestApi singleton;
    private static final HashMap<Class, DiffMeta> mCountryDiffs = new HashMap<>();
    private static TestApiFactory mCountryDiffFactory;

    private TestApi() {
        mCountryDiffFactory = new TestApiFactory();
    }

    public static TestApi getInstance() {
        if (singleton == null) {
            synchronized (TestApi.class) {
                if (singleton == null) {
                    singleton = new TestApi();
                }
            }
        }
        return singleton;
    }

    public <T> T get(Class<T> tClass) {
        return mCountryDiffFactory.create(tClass, mCountryDiffs);
    }
}
