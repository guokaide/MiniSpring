package com.minis.beans;

/**
 * Bean 的定义
 */
public class BeanDefinition {
    private static final String SCOPE_SINGLETON = "singleton";
    private static final String SCOPE_PROTOTYPE = "prototype";
    // Bean 的别名
    private String id;
    // Bean 的全名
    private String className;
    // 构造器参数
    private ArgumentValues constructorArgumentValues;
    // 属性
    private PropertyValues propertyValues;
    // Bean 的作用域
    private String scope = SCOPE_SINGLETON;
    // Bean 要在加载定义的时候创建，还是第一次获取 Bean 的时候创建
    private boolean lazyInit = false;
    // 初始化方法
    private String initMethodName;
    // Bean 的依赖
    private String[] dependsOn;
    // Bean 的 Class<?>
    private volatile Object beanClass;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArgumentValues getConstructorArgumentValues() {
        return constructorArgumentValues;
    }

    public void setConstructorArgumentValues(ArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues =
                constructorArgumentValues != null ? constructorArgumentValues : new ArgumentValues();
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope);
    }

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String[] getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    public Class<?> getBeanClass() {
        return (Class<?>) beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

}
