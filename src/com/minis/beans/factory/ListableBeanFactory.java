package com.minis.beans.factory;

import com.minis.beans.BeansException;

import java.util.Map;

/**
 * ListableBeanFactory: 将 BeanFactory 内部管理的 Bean、BeanDefinition 作为一个集合进行管理
 * 对外提供 Bean、BeanDefinition 集合相关的查询操作
 * 1. 是否包含某个 Bean 的定义
 * 2. 获取 Bean 的数量
 * 3. 获取所有 BeanDefinition 的名称
 * 4. 根据 Bean 的类型获取 Bean 的名称列表或者实例等
 */
public interface ListableBeanFactory extends BeanFactory {
    boolean containsBeanDefinition(String name);

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    String[] getBeanNamesForType(Class<?> type);

    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

}
