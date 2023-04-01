package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.AbstractAutowireCapableBeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.*;

/**
 * IoC 引擎: 具有以下能力
 * 1. BeanFactory: 注册、获取 Bean
 * 2. ListableBeanFactory: 查询 Bean
 * 3. ConfigurableBeanFactory: 为 Bean 注入 BeanPostProcessor 和依赖
 * 4. AutowireCapableBeanFactory: 对 Bean 进行后置处理，支持 @Autowired 注解
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory {
    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitions.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return (String[]) this.beanDefinitionNames.toArray();
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        List<String> result = new ArrayList<>();
        for (String name : this.beanNames) {
            BeanDefinition beanDefinition = this.beanDefinitions.get(name);
            Class<?> beanClass = beanDefinition.getBeanClass();
            if (type.isAssignableFrom(beanClass)) {
                result.add(name);
            }
        }
        return (String[]) result.toArray();
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> result = new HashMap<>();
        String[] names = getBeanNamesForType(type);
        for (String name : names) {
            Object bean = getBean(name);
            result.put(name, (T) bean);
        }
        return result;
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        super.addBeanPostProcessor(beanPostProcessor);
    }

    @Override
    public int getBeanPostProcessorCount() {
        return super.beanPostProcessors.size();
    }

    @Override
    public void registerDependentBean(String name, String dependentBeanName) {
        BeanDefinition beanDefinition = this.beanDefinitions.get(name);
        Set<String> depends = new HashSet<>(Set.of(beanDefinition.getDependsOn()));
        depends.add(dependentBeanName);
        beanDefinition.setDependsOn(depends.toArray(new String[0]));
    }

    @Override
    public String[] getDependenciesForBean(String name) {
        BeanDefinition beanDefinition = this.beanDefinitions.get(name);
        return beanDefinition.getDependsOn();
    }
}
