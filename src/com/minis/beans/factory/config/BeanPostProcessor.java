package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;

/**
 * Bean 处理器：负责在 Bean 实例创建之后，做一些后置处理，比如说通过注解依赖注入成员变量
 */
public interface BeanPostProcessor {
    // Bean 初始化之前，对 Bean 做处理之后，返回处理之后的 Bean
    Object postProcessBeforeInitialization(Object bean, String name) throws BeansException;

    // Bean 初始化之后
    Object postProcessAfterInitialization(Object bean, String name) throws BeansException;

    void setBeanFactory(BeanFactory beanFactory);
}
