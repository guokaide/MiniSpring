package com.minis.beans.factory;

import com.minis.beans.BeansException;

/**
 * IoC 容器
 */
public interface BeanFactory {
    // 获取一个 Bean，IoC 容器的核心方法
    Object getBean(String name) throws BeansException;

    void registerBean(String name, Object obj);

    boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    Class<?> getType(String name);

}
