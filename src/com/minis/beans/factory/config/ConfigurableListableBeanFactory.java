package com.minis.beans.factory.config;

import com.minis.beans.factory.ListableBeanFactory;

/**
 * ConfigurableListableBeanFactory 组合了 4 类 BeanFactory
 * 1. BeanFactory: 注册、获取 Bean
 * 2. ListableBeanFactory: 查询 Bean
 * 3. ConfigurableBeanFactory: 为 Bean 注入 BeanPostProcessor 和依赖
 * 4. AutowireCapableBeanFactory: 对 Bean 进行后置处理，支持 @Autowired 注解
 */
public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory, ConfigurableBeanFactory, AutowireCapableBeanFactory {
}
