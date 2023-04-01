package com.minis.beans.factory.config;

/**
 * 单例 Bean 注册中心：负责管理单例 Bean，包括单例的注册、获取、是否存在以及获取所有的单例 Bean 的名称。
 */
public interface SingletonBeanRegistry {
    void registerSingleton(String name, Object singletonObject);

    Object getSingleton(String name);

    boolean containsSingleton(String name);

    String[] getSingletonNames();
}
