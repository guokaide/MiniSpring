package com.minis.beans;

import com.minis.core.Resource;
import org.dom4j.Element;

/**
 * Resource 中读取 Bean 的定义，将 Bean 的定义注册到 IoC 容器 BeanFactory 中
 * e.g. XML -> Resource -> BeanDefinition -> BeanFactory
 */
public class XMLBeanDefinitionReader {
    BeanFactory beanFactory;

    public XMLBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanId = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);
            this.beanFactory.registerBeanDefinition(beanDefinition);
        }
    }
}