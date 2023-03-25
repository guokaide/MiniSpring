package com.minis.beans;

import java.util.*;

public class SimpleBeanFactory implements BeanFactory {

    private final List<String> beanNames = new ArrayList<>();
    private final List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private final Map<String, Object> singletons = new HashMap<>();

    public SimpleBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        Object singleton = singletons.get(beanName);
        if (singleton == null) {
            // 1. 判断是否存在 Bean 的定义
            int index = beanNames.indexOf(beanName);
            if (index == -1) {
                throw new BeansException("Bean definition of " + beanName + " is not exist");
            }
            // 2. 如果存在，实例化 Bean
            BeanDefinition beanDefinition = beanDefinitions.get(index);
            try {
                singleton = Class.forName(beanDefinition.getClassName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            // 3. 存储 Bean 实例
            singletons.put(beanName, singleton);
        }
        return singleton;
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanNames.add(beanDefinition.getId());
        this.beanDefinitions.add(beanDefinition);
    }
}
