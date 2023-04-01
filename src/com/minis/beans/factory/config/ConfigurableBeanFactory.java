package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;

/**
 * ConfigurableBeanFactory: 将 Bean 的处理器、依赖作为可配置项进行管理
 * 对外提供管理 Bean 的处理器和依赖关系的相关操作
 * 1. 增加 BeanPostProcessor
 * 2. 查询 BeanPostProcessor 的个数
 * 3. 注册 Bean 的依赖
 * 4. 获取 Bean 的依赖
 */
public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    int getBeanPostProcessorCount();

    void registerDependentBean(String name, String dependentBeanName);

    String[] getDependenciesForBean(String name);
}
