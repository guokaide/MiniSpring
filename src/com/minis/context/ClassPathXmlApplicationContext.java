package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.beans.factory.xml.XMLBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;

/**
 * Context: 负责整合 IoC 容器的启动过程
 * 1. 读取 Bean 的定义：通过 Resource 解析 XML 文件
 * 2. 构建 BeanDefinition: 通过 XMLBeanDefinitionReader 构建 BeanDefinition
 * 3. 读取 BeanDefinition: 将其注入到 AutowireCapableBeanFactory 中
 * 4. 注册 BeanPostProcessor: AutowireCapableBeanFactory 注册 AutowiredAnnotationBeanPostProcessor
 * 5. 创建所有 Bean: 获取所有 Bean 定义，执行 getBean() 创建 Bean 实例
 * 6. Bean 的后置处理：调用 BeanPostProcessor 后置处理并初始化
 * <p>
 * ClassPathXmlApplicationContext 默认会实例化所有 Bean
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    DefaultListableBeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }

    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        Resource resource = new ClassPathXmlResource(fileName);

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XMLBeanDefinitionReader reader = new XMLBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);

        this.beanFactory = beanFactory;

        if (isRefresh) {
            try {
                this.refresh();
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void registerBeanPostProcessors() {
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

    @Override
    public void initApplicationEventPublisher() {
        this.setApplicationEventPublisher(new SimpleApplicationEventPublisher());
    }

    @Override
    public void registerListeners() {
        this.addEventListener(new ApplicationEventListener());
    }

    @Override
    public void onRefresh() {
        this.beanFactory.refresh();
    }


    @Override
    public void finishRefresh() {
        this.publishEvent(new ContextRefreshEvent("Context Refreshed..."));
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }
}
