package com.minis.beans.factory.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 说明：@Autowired 注解的处理类：利用反射读取 Bean 中标记了 @Autowired 的成员变量，
 * 通过 BeanFactory 实例化 Bean 属性，然后将 Bean 属性注入到 Bean 中
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        Class<?> clz = bean.getClass();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            // 找到标记 @Autowired 的成员变量进行处理
            boolean annotationPresent = field.isAnnotationPresent(Annotation.class);
            if (annotationPresent) {
                String fieldName = field.getName();
                Object autowired = beanFactory.getBean(name);
                try {
                    field.setAccessible(true);
                    field.set(bean, autowired);
                    System.out.println("autowire " + fieldName + " for bean " + name);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}
