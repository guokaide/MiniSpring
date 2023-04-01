package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 由于 BeanFactory 会有多个实现，这里我们对 BeanFactory 的通用代码进行抽象，
 * 抽象出一个 AbstractBeanFactory 类，目的是为了代码复用、解耦。
 * 设计模式：interface-abstract class-class 接口抽象类
 * 1. 继承 DefaultSingletonBeanRegistry，由其对 Bean 进行管理，确保创建的 Bean 都是单例的 Bean
 * 2. 实现 BeanDefinitionRegistry，管理 BeanDefinition
 * 因此，AbstractBeanFactory 既是 Bean 的工厂，同时也是 BeanDefinition 的仓库。
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {

    protected final List<String> beanDefinitionNames = new ArrayList<>();
    protected final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>(256);
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    public AbstractBeanFactory() {
    }

    // 包装方法：将容器中的所有 Bean 一次性创建出来
    public void refresh() {
        for (String name : this.beanDefinitionNames) {
            try {
                getBean(name);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /******************************* BeanFactory 实现 ***************************************/

    /**
     * 核心方法
     * 1. 获取 Bean 的定义
     * 2. 创建 Bean 的实例
     * 3. 对 Bean 进行后置处理以及初始化
     */
    @Override
    public Object getBean(String name) throws BeansException {
        // 1. 从容器中尝试获取 Bean
        Object singleton = this.getSingleton(name);
        // 2. 获取失败，创建 Bean
        if (singleton == null) {
            // 3. 创建前，尝试从毛坯 Bean 中获取 Bean
            singleton = this.earlySingletonObjects.get(name);
            if (singleton == null) {
                // 4. 判断是否存在 Bean 的定义
                System.out.println("get bean null -------------- " + name);
                BeanDefinition beanDefinition = this.beanDefinitions.get(name);
                if (beanDefinition == null) {
                    throw new BeansException("Bean definition of " + name + " is not exist");
                }
                // 5. 如果存在 Bean 的定义，实例化 Bean
                try {
                    singleton = createBean(beanDefinition);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // 6. 注册 Bean 实例
                this.registerSingleton(name, singleton);
                // 7. BeanPostProcessor 处理
                // s1: postProcessBeforeInitialization
                applyBeanPostProcessorBeforeInitialization(singleton, name);
                // s2: init-method
                if (beanDefinition.getInitMethodName() != null) {
                    invokeInitMethod(beanDefinition, singleton);
                }
                // s3: postProcessAfterInitialization
                applyBeanPostProcessorAfterInitialization(singleton, name);
            }
        }
        return singleton;
    }

    private void invokeInitMethod(BeanDefinition beanDefinition, Object singleton) {
        // 1. 获取 singleton 的 Class 对象
        Class<?> clz = singleton.getClass();
        Method method;
        try {
            // 2. 通过 singleton 的 Class 对象获取 init-method
            method = clz.getMethod(beanDefinition.getInitMethodName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            // 3. 执行 init-method
            method.invoke(singleton);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
        this.beanDefinitionNames.add(name);
        // 如果 Bean 不是懒加载，读取定义的时候就直接加载
        if (!beanDefinition.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitions.get(name);
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitions.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitions.containsKey(name);
    }

    /******************************* Bean 的创建和依赖注入 ***************************************/
    private Object createBean(BeanDefinition beanDefinition) throws Exception {
        // 1. 创建毛坯 Bean 实例
        Object object = doCreateBean(beanDefinition);
        System.out.println(beanDefinition.getId() + " bean created. " + beanDefinition.getClassName() + " : " + object.toString());
        // 2. 将毛坯 Bean 放到缓存中
        this.earlySingletonObjects.put(beanDefinition.getId(), object);
        // 3. 完善毛坯 Bean, 主要是设置 Bean 的属性
        populateBean(beanDefinition, object);
        return object;
    }

    // 创建毛坯 Bean，仅实例化 Bean, 但是不设置属性
    private Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        // 1. 获取 Bean 的 Class 对象，通过 Class 对象获取 Class 相关信息，比如说 constructor, method, field 等
        Class<?> clz = Class.forName(beanDefinition.getClassName());

        // 2. 获取配置的构造器参数
        ConstructArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();

        // 3. 如果构造器参数没有设置，则直接调用无参构造函数实例化 Bean
        if (argumentValues.isEmpty()) return clz.newInstance();

        // 4. 如果构造器参数设置了，获取构造器参数的类型和值
        Class<?>[] parameterTypes = new Class<?>[argumentValues.size()];
        Object[] parameterValues = new Object[argumentValues.size()];
        for (int i = 0; i < argumentValues.size(); i++) {
            ConstructorArgumentValue argumentValue = argumentValues.get(i);
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
        // 5. 通过构造器参数类型获取有参构造器
        Constructor<?> constructor = clz.getConstructor(parameterTypes);
        // 6. 通过有参构造器创建 Bean 的实例
        return constructor.newInstance(parameterValues);
    }

    // 完善毛坯 Bean, 为 Bean 设置属性
    private void populateBean(BeanDefinition beanDefinition, Object object) throws Exception {
        handleProperties(beanDefinition, object);
    }

    // 为 Bean 注入属性
    private void handleProperties(BeanDefinition beanDefinition, Object object) throws Exception {
        Class<?>[] parameterTypes;
        Object[] parameterValues;
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

    /************************************* 子类实现 ***************************************/
    public abstract Object applyBeanPostProcessorBeforeInitialization(Object singleton, String name) throws BeansException;

    public abstract Object applyBeanPostProcessorAfterInitialization(Object singleton, String name) throws BeansException;
}
