package com.minis.context;

import com.minis.beans.*;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;

/**
 * Context: 负责整合 IoC 容器的启动过程
 * 1. 读取 Bean 的定义：通过 Resource 解析 XML 文件
 * 2. 构建 BeanDefinition: 通过 XMLBeanDefinitionReader 构建 BeanDefinition
 * 3. 实例化 Bean: 读取 BeanDefinition，并且将其注入到 BeanFactory 中
 */
public class ClassPathXmlApplicationContext implements BeanFactory {

    BeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String fileName) {
        BeanFactory beanFactory = new SimpleBeanFactory();
        XMLBeanDefinitionReader reader = new XMLBeanDefinitionReader(beanFactory);

        Resource resource = new ClassPathXmlResource(fileName);
        reader.loadBeanDefinitions(resource);

        this.beanFactory = beanFactory;
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanFactory.registerBeanDefinition(beanDefinition);
    }
}
