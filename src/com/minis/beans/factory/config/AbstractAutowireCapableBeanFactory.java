package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.support.AbstractBeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持 Bean 实例在调用 init-method 之前通过 @Autowired 注解注入属性
 * 1. AbstractBeanFactory: 支持 Bean 的注册和创建
 * 2. AbstractAutowireCapableBeanFactory： 支持 @Autowired
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
        implements AutowireCapableBeanFactory {

    protected final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public Object applyBeanPostProcessorBeforeInitialization(Object singleton, String name) throws BeansException {
        Object result = singleton;
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            // 用于创建 Bean
            beanPostProcessor.setBeanFactory(this);
            result = beanPostProcessor.postProcessBeforeInitialization(singleton, name);
            // 如果 result == null, 后续的 PostProcessor 执行就没有意义了
            // 所以，直接打断后续的处理
            if (result == null) return null;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorAfterInitialization(Object singleton, String name) throws BeansException {
        Object result = singleton;
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            beanPostProcessor.setBeanFactory(this);
            result = beanPostProcessor.postProcessAfterInitialization(singleton, name);
            if (result == null) return null;
        }
        return result;
    }
}
