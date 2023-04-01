package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.support.AbstractBeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * AutowireCapableBeanFactory 支持 Bean 实例在调用 init-method 之前通过 @Autowired 注解注入属性
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory {

    private final List<AutowiredAnnotationBeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public void addBeanPostProcessor(AutowiredAnnotationBeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public Object applyBeanPostProcessorBeforeInitialization(Object singleton, String name) throws BeansException {
        Object result = singleton;
        for (AutowiredAnnotationBeanPostProcessor beanPostProcessor : beanPostProcessors) {
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
        for (AutowiredAnnotationBeanPostProcessor beanPostProcessor : beanPostProcessors) {
            beanPostProcessor.setBeanFactory(this);
            result = beanPostProcessor.postProcessAfterInitialization(singleton, name);
            if (result == null) return null;
        }
        return result;
    }
}
