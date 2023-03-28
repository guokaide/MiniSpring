package com.minis.beans;

/**
 * BeanDefinition 注册中心
 * 主要负责管理 BeanDefinition, 包括 BeanDefinition 的注册、获取、删除以及判断是否存在
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String name, BeanDefinition beanDefinition);

    BeanDefinition getBeanDefinition(String name);

    void removeBeanDefinition(String name);

    boolean containsBeanDefinition(String name);
}
