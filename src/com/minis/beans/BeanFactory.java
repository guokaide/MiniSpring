package com.minis.beans;

/**
 * IoC 容器
 */
public interface BeanFactory {
    // 获取一个 Bean，IoC 容器的核心方法
    Object getBean(String beanName) throws BeansException;
    // 注册一个 BeanDefinition
    void registerBeanDefinition(BeanDefinition beanDefinition);
}
