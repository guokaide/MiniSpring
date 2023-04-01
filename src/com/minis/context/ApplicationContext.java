package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.ListableBeanFactory;
import com.minis.beans.factory.config.ConfigurableBeanFactory;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.core.env.Environment;
import com.minis.core.env.EnvironmentCapable;

/**
 * ApplicationContext: 应用上下文
 * 1. 支持上下文环境
 * 2. 支持事件的发布和监听
 * 3. 支持 BeanFactory
 */
public interface ApplicationContext extends EnvironmentCapable,
        ListableBeanFactory, ConfigurableBeanFactory, ApplicationEventPublisher {

    String getApplicationName();

    long getStartupDate();

    Environment getEnvironment();

    void setEnvironment(Environment environment);

    void close();

    boolean isActive();

    void refresh() throws BeansException, IllegalStateException;

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
