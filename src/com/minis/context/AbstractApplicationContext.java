package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.core.env.Environment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext implements ApplicationContext {

    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private long startupDate;
    private Environment environment;
    private ApplicationEventPublisher applicationEventPublisher;

    /****************************** 核心方法 ***********************************/
    @Override
    public void refresh() throws BeansException, IllegalStateException {
        registerBeanPostProcessors();
        initApplicationEventPublisher();
        registerListeners();
        onRefresh();
        finishRefresh();
    }

    public abstract void registerBeanPostProcessors();

    public abstract void initApplicationEventPublisher();

    public abstract void registerListeners();

    public abstract void onRefresh();

    public abstract void finishRefresh();

    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    @Override
    public String getApplicationName() {
        return "";
    }

    @Override
    public long getStartupDate() {
        return this.startupDate;
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    @Override
    public void registerBean(String name, Object obj) {
        getBeanFactory().registerBean(name, obj);
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return getBeanFactory().isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) {
        return getBeanFactory().isPrototype(name);
    }

    @Override
    public Class<?> getType(String name) {
        return getBeanFactory().getType(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return getBeanFactory().containsBeanDefinition(name);
    }

    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return getBeanFactory().getBeanNamesForType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        getBeanFactory().addBeanPostProcessor(beanPostProcessor);
    }

    @Override
    public int getBeanPostProcessorCount() {
        return getBeanFactory().getBeanPostProcessorCount();
    }

    @Override
    public void registerDependentBean(String name, String dependentBeanName) {
        getBeanFactory().registerDependentBean(name, dependentBeanName);
    }

    @Override
    public String[] getDependenciesForBean(String name) {
        return getBeanFactory().getDependenciesForBean(name);
    }

    @Override
    public void registerSingleton(String name, Object singletonObject) {
        getBeanFactory().registerSingleton(name, singletonObject);
    }

    @Override
    public Object getSingleton(String name) {
        return getBeanFactory().getSingleton(name);
    }

    @Override
    public boolean containsSingleton(String name) {
        return getBeanFactory().containsSingleton(name);
    }

    @Override
    public String[] getSingletonNames() {
        return getBeanFactory().getSingletonNames();
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void addEventListener(ApplicationEventListener listener) {
        this.applicationEventPublisher.addEventListener(listener);
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
