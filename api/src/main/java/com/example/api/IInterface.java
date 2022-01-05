package com.example.api;

/**
 *
 * @author dan.tang
 * 注解处理器的类，都需要实现该接口
 *
 */
public interface IInterface<T> {
    void implMethod(T target);
}
