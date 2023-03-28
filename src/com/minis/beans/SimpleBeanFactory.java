package com.minis.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供 BeanFactory 的默认实现
 * 继承 DefaultSingletonBeanRegistry，由其对 Bean 进行管理，确保创建的 Bean 都是单例的 Bean
 * 实现 BeanDefinitionRegistry，管理 BeanDefinition
 * 因此，SimpleBeanFactory 既是 Bean 的工厂，同时也是 BeanDefinition 的仓库。
 */
public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>(256);
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    public SimpleBeanFactory() {
    }

    // 包装方法：将容器中的所有 Bean 一次性创建出来
    public void refresh() {
        for (String name : this.beanNames) {
            try {
                getBean(name);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /******************************* BeanFactory 实现 ***************************************/
    @Override
    public Object getBean(String name) throws BeansException {
        // 1. 从容器中尝试获取 Bean
        Object singleton = this.getSingleton(name);
        // 获取失败，创建 Bean
        if (singleton == null) {
            // 2. 从毛坯 Bean 中获取 Bean
            singleton = this.earlySingletonObjects.get(name);
            if (singleton == null) {
                // 3. 判断是否存在 Bean 的定义
                BeanDefinition beanDefinition = this.beanDefinitions.get(name);
                if (beanDefinition == null) {
                    throw new BeansException("Bean definition of " + name + " is not exist");
                }
                // 4. 如果存在 Bean 的定义，实例化 Bean
                try {
                    singleton = createBean(beanDefinition);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            // 5. 注册 Bean 实例
            this.registerSingleton(name, singleton);
            // 6. 预留 BeanPostProcessor 位置
        }
        return singleton;
    }

    @Override
    public void registerBean(String name, Object obj) {
        this.registerSingleton(name, obj);
    }

    @Override
    public boolean containsBean(String name) {
        return this.containsSingleton(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitions.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitions.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitions.get(name).getBeanClass();
    }

    /******************************* BeanDefinitionRegistry 实现 ***************************************/
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitions.put(name, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitions.get(name);
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitions.remove(name);
        this.removeSingleton(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitions.containsKey(name);
    }

    /******************************* Bean 的创建和依赖注入 ***************************************/
    private Object createBean(BeanDefinition beanDefinition) throws Exception {
        // 1. 创建毛坯 Bean
        Object object = doCreateBean(beanDefinition);
        this.earlySingletonObjects.put(beanDefinition.getId(), object);
        // 2. 设置 Bean 的属性
        handleProperties(beanDefinition, object);
        return object;
    }

    // 创建毛坯 Bean，仅实例化 Bean, 但是不设置属性
    private Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        // 1. 获取 Bean 的 Class 对象
        Class<?> clz = Class.forName(beanDefinition.getClassName());
        // 2. 获取 Bean 的构造器对象
        // 2.1 获取构造器参数类型和值
        ArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();
        // 如果构造器参数没有设置，则直接调用无参构造函数实例化 Bean
        if (argumentValues.isEmpty()) return clz.newInstance();

        // 如果构造器参数设置了，则调用有参构造函数实例化 Bean
        Class<?>[] parameterTypes = new Class<?>[argumentValues.size()];
        Object[] parameterValues = new Object[argumentValues.size()];
        for (int i = 0; i < argumentValues.size(); i++) {
            ArgumentValue argumentValue = argumentValues.get(i);
            String aType = argumentValue.getType();
            Object aValue = argumentValue.getValue();
            // 目前，仅支持 String、Integer、int 三种类型
            if ("String".equals(aType) || "java.lang.String".equals(aType)) {
                parameterTypes[i] = String.class;
                parameterValues[i] = aValue;
            } else if ("Integer".equals(aType) || "java.lang.Integer".equals(aType)) {
                parameterTypes[i] = Integer.class;
                parameterValues[i] = Integer.valueOf((String) aValue);
            } else if ("int".equals(aType)) {
                parameterTypes[i] = int.class;
                parameterValues[i] = Integer.valueOf((String) aValue);
            } else {
                parameterTypes[i] = String.class;
                parameterValues[i] = aValue;
            }
        }
        // 2.2 获取 Bean 的构造器对象
        Constructor<?> constructor = clz.getConstructor(parameterTypes);
        // 3. 创建 Bean 的实例
        return constructor.newInstance(parameterValues);
    }

    // 给 Bean object 注入属性
    private void handleProperties(BeanDefinition beanDefinition, Object object) throws Exception {
        Object[] parameterValues;
        Class<?>[] parameterTypes;
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (int i = 0; i < propertyValues.size(); i++) {
            PropertyValue propertyValue = propertyValues.get(i);
            String pType = propertyValue.getType();
            String pName = propertyValue.getName();
            Object pValue = propertyValue.getValue();
            boolean isRef = propertyValue.isRef();
            parameterTypes = new Class<?>[1];
            parameterValues = new Object[1];
            if (!isRef) {
                // 目前，仅支持 String、Integer、int 三种类型
                if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                    parameterTypes[0] = String.class;
                    parameterValues[0] = pValue;
                } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                    parameterTypes[0] = Integer.class;
                    parameterValues[0] = Integer.valueOf((String) pValue);
                } else if ("int".equals(pType)) {
                    parameterTypes[0] = int.class;
                    parameterValues[0] = Integer.valueOf((String) pValue);
                } else {
                    parameterTypes[0] = String.class;
                    parameterValues[0] = pValue;
                }
            } else {
                // 如果依赖是对象，创建这个对象
                parameterTypes[0] = Class.forName(pType);
                parameterValues[0] = getBean((String) pValue);
            }
            // 4.1 根据 setXxx() 规范，查找 Bean 的 setter 方法
            String methodName = "set" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
            // 获取 Bean 的 Class 对象
            Class<?> clz = Class.forName(beanDefinition.getClassName());
            // 获取 Bean 的 setter 方法
            Method method = clz.getMethod(methodName, parameterTypes);

            // 4.2 调用 setter 方法设置属性
            method.invoke(object, parameterValues);
        }
    }
}
