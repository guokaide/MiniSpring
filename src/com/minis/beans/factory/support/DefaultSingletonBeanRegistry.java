package com.minis.beans.factory.support;

import com.minis.beans.factory.config.SingletonBeanRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供 SingletonBeanRegistry 的默认实现，可以被其它实现替换
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    // 存放容器中所有 Bean 的名称列表
    protected final List<String> beanNames = new ArrayList<>();
    // 存放容器中所有的 Bean: <BeanName, Bean>
    protected final Map<String, Object> singletons = new ConcurrentHashMap<>(256);

    @Override
    public void registerSingleton(String name, Object singletonObject) {
        synchronized (this.singletons) {
            this.beanNames.add(name);
            this.singletons.put(name, singletonObject);
        }
    }

    @Override
    public Object getSingleton(String name) {
        return this.singletons.get(name);
    }

    @Override
    public boolean containsSingleton(String name) {
        return this.beanNames.contains(name);
    }

    @Override
    public String[] getSingletonNames() {
        return this.beanNames.toArray(new String[0]);
    }

    public void removeSingleton(String beanName) {
        synchronized (this.singletons) {
            this.beanNames.remove(beanName);
            this.singletons.remove(beanName);
        }
    }
}
