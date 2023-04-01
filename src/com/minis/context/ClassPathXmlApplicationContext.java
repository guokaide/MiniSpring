package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.AutowireCapableBeanFactory;
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
public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {

    AutowireCapableBeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }

    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        AutowireCapableBeanFactory beanFactory = new AutowireCapableBeanFactory();
        XMLBeanDefinitionReader reader = new XMLBeanDefinitionReader(beanFactory);

        Resource resource = new ClassPathXmlResource(fileName);
        reader.loadBeanDefinitions(resource);

        this.beanFactory = beanFactory;

        if (isRefresh) {
            refresh();
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

    private void refresh() {
        // 注册拦截 Bean 创建的 BeanPostProcessor
        registerBeanPostProcessors(this.beanFactory);
        // 初始化所有 Bean
        onRefresh();
    }

    private void onRefresh() {
        this.beanFactory.refresh();
    }

    private void registerBeanPostProcessors(AutowireCapableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }
}
