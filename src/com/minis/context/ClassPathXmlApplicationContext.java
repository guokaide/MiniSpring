package com.minis.context;

import com.minis.beans.BeanFactory;
import com.minis.beans.BeansException;
import com.minis.beans.SimpleBeanFactory;
import com.minis.beans.XMLBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;

/**
 * Context: 负责整合 IoC 容器的启动过程
 * 1. 读取 Bean 的定义：通过 Resource 解析 XML 文件
 * 2. 构建 BeanDefinition: 通过 XMLBeanDefinitionReader 构建 BeanDefinition
 * 3. 实例化 Bean: 读取 BeanDefinition，并且将其注入到 SimpleBeanFactory 中
 */
public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {

    SimpleBeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }

    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        XMLBeanDefinitionReader reader = new XMLBeanDefinitionReader(beanFactory);

        Resource resource = new ClassPathXmlResource(fileName);
        reader.loadBeanDefinitions(resource);

        this.beanFactory = beanFactory;

        // 一次性将所有 Bean 装配好
        if (isRefresh) {
            this.beanFactory.refresh();
        }
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return this.beanFactory.getBean(name);
    }

    @Override
    public void registerBean(String name, Object obj) {
        this.beanFactory.registerBean(name, obj);
    }

    @Override
    public boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {

    }
}
