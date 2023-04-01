# MiniSpring

## 一. 原始 IoC 容器

IoC: Inverse of Control, 控制反转

- 控制：管理 Bean 的生命周期和依赖关系
- 反转：原来由程序员控制，但是现在由框架控制

例如，正常的控制过程是，调用者直接创建 Bean 使用； 而 IoC 则是由框架创建 Bean，然后将 Bean 注入，供调用者使用。

IoC 容器：核心职责是管理 Bean 的生命周期及依赖关系。

一个最简单的 IoC 容器：

- Bean 的配置：.xml 配置文件配置 Bean 的定义（声明）
- Bean 的创建：将 Bean 的定义读取到内存中，通过反射创建实例
  - XML Reader 读取 Bean 配置到到内存
  - 加载 Bean Class
  - 通过反射实例化 Bean
- Bean 的存储：将创建的实例保存在 Map 中
- Bean 的获取：通过 getBean() 获取 Bean 实例

接下来，进一步增强 IoC 容器：

- 增加单例 Bean 的接口定义，所有的 Bean 默认为单例（Spring 框架默认的实现）
- 预留事件监听的接口，方便后续进一步解耦代码逻辑
- 扩展 BeanDefinition，增加属性: constructor 参数、property、scope、lazyInit、dependsOn 等

> 单例（Singleton）是指某个类在整个系统中只有唯一的对象实例。

最终，SimpleBeanFactory 既是 Bean 的工厂，同时也是 BeanDefinition 的仓库。

## 二. 依赖注入

**注入操作的本质，就是给 Bean 的各个属性进行赋值。**

Spring 支持 3 种属性注入的方式：

- Constructor 注入：在构造器中传入参数进行注入
- Setter 注入：调用 setXXX() 方法注入
- Field 注入：给 Bean 的某个变量赋值

我们先实现前 2 种注入。

此时，Bean 的创建流程如下：

- Bean 的创建：将 Bean 的定义读取到内存中，通过反射创建实例
  - XML Reader 读取 Bean 配置到到内存
  - 加载 Bean Class
  - 通过反射实例化 Bean (构造器注入构造器参数)
  - 设置 Bean 的属性

在注入属性的时候，如果是这个属性是一个对象怎么办？Spring 是通过引入 ref 属性解决的。

当 Bean 互相依赖的时候，可能会出现循环依赖的问题，比如说， A 依赖 B, B 依赖 C, C 依赖 A。

我们通过增加一层缓存来解决循环依赖的问题：将早期毛坯 Bean 缓存起来（已经实例化，但是属性未设置完全）。

核心思想是，我们先将 A、B、C 三个 Bean 的毛坯创建出来，然后创建 A 的时候，就可以注入依赖 B，创建 B 的时候，
就可以注入依赖 C，创建 C 的时候，A 可能还是毛坯，但是 A 已经实例化，可以将其注入到 C 中。

此时，Bean 的创建流程如下：

- Bean 的创建：将 Bean 的定义读取到内存中，通过反射创建实例
  - XML Reader 读取 Bean 配置到到内存
  - 加载 Bean Class
  - 通过反射实例化 Bean，将 Bean 放在毛坯 Bean 容器中
  - 设置 Bean 的属性
    - 如果依赖其他 Bean, 停止设置属性，先创建依赖的 Bean
    - 等依赖的 Bean 创建好之后，再继续设置属性

我们会发现，如果构造器注入的参数包含其他 Bean, 我们无法通过这种方式解决循环依赖的问题。

解决了循环依赖的问题之后，我们引入一个核心方法 refresh(), 作为整个容器启动的入口。

> Spring 通过三级缓存的方式，解决循环依赖问题。
> - singletonObjects: 用于存储完全创建好的单例 Bean 实例。
> - earlySingletonObjects: 用于存储早期创建但是未完成初始化的单例 Bean 实例。
> - singletonFactories: 用于存储创建单例 Bean 实例的工厂对象。
>
> 当 Spring 发现两个或更多个 Bean 之间存在循环依赖关系时，它会将其中一个 Bean 创建的过程中尚未完成的实例放入
> earlySingletonObjects 缓存中，
> 然后将创建该 Bean 的工厂对象放入 singletonFactories 缓存中。
>
> 接着，Spring会暂停当前 Bean的 创建过程，去创建它所依赖的 Bean。
> 当依赖的 Bean 创建完成后，Spring 会将其放入 singletonObjects 缓存中，并使用它来完成当前Bean的创建过程。
>
> 在创建当前Bean的过程中，如果发现它还依赖其他的 Bean，Spring 会重复上述过程，直到所有 Bean 的创建过程都完成为止。
>
> 需要注意的是，当使用构造函数注入方式时，循环依赖是无法解决的。
> 因为在创建 Bean 时，必须先创建它所依赖的Bean实例，而构造函数注入方式需要在创建 Bean 实例时就将依赖的 Bean 实例传入构造函数中。
> 如果依赖的 Bean 实例尚未创建完成，就无法将其传入构造函数中，从而导致循环依赖无法解决。
>
> 此时，可以考虑使用 Setter 注入方式来解决循环依赖问题。

## 三. 支持注解

Spring 通过 @Autowired 注解可以在 Bean 中注入对象，例如：

```java
public class Test {
  @Autowired
  private TestAutowired testAutowired;
}
```

好处是，不需要在配置文件中使用 ref 显式配置，在代码中加一个注解，就可以起到依赖注入的效果。

要想支持注解，仅靠引入一个注解是没有办法实现的，我们还需要引入另外一个程序去解释它，否则，注解就成了注释。

那应该在哪一段程序、哪个时机去解释这个注解呢？

既然这个注解是作用在一个实例变量上的，那说明，我们必须先创建好这个 Bean 之后，才能去解释这个注解。

因此，我们在 Bean 实例创建之后，通过 BeanPostProcessor 处理 @Autowired 注解。

此时，Bean 的创建流程如下：

- Bean 的创建：将 Bean 的定义读取到内存中，通过反射创建实例，并且完善属性，处理 @Autowired 注解
  - 读取 Bean 定义：XML Reader 读取配置文件，将 Bean 的定义加载到内存中的 BeanDefinition 中
  - 构建 Bean 实例：根据 BeanDefinition，通过反射实例化 Bean
  - 存储 Bean 缓存：将 Bean 放在毛坯 Bean 容器，解决循环依赖的问题
  - 完善 Bean 设置：设置 Bean 的属性
    - 如果依赖其他 Bean, 停止设置属性，先创建依赖的 Bean
    - 等依赖的 Bean 创建好之后，再继续设置属性
  - Bean 的后置处理：通过 BeanPostProcessor 对 Bean 进行后置处理
    - BeforeInitialization: 通过反射解析 @Autowired 注解，创建 Bean 实例，并注入到 Bean 的属性中
    - init-method: 通过反射执行初始化方法
    - AfterInitialization: 初始化之后，执行其他操作