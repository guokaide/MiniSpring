# MiniSpring

## 一. 原始 IoC 容器

IoC: Inverse of Control, 控制反转

- 控制: 管理 Bean 的生命周期和依赖关系
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

Spring 支持 3 种属性注入的方式：

- Constructor 注入：在构造器中传入参数进行注入
- Setter 注入：调用 setXXX() 方法注入
- Field 注入：给 Bean 的某个变量赋值

注入操作的本质，就是给 Bean 的各个属性进行赋值。 我们先实现前 2 种注入。

此时，Bean 的创建流程如下：

- Bean 的创建：将 Bean 的定义读取到内存中，通过反射创建实例
  - XML Reader 读取 Bean 配置到到内存
  - 加载 Bean Class
  - 通过反射实例化 Bean (构造器注入构造器参数)
  - 设置 Bean 的属性

在注入属性的时候，如果是这个属性是一个对象怎么办？Spring 是通过引入 ref 属性解决的。

当 Bean 互相依赖的时候，可能会出现循环依赖的问题，比如说， A 依赖 B, B 依赖 C, C 依赖 A。

Spring 通过三级缓存的方式，解决循环依赖问题。

核心思想是，我们先将 A、B、C 三个 Bean 的毛坯创建出来（已经实例化，但是属性未设置完全），然后创建 A 的时候，
就可以注入依赖 B，创建 B 的时候，就可以注入依赖 C，创建 C 的时候，A 可能还是毛坯，但是 A 已经实例化，可以
将其注入到 C 中。

此时，Bean 的创建流程如下：

- Bean 的创建：将 Bean 的定义读取到内存中，通过反射创建实例
  - XML Reader 读取 Bean 配置到到内存
  - 加载 Bean Class
  - 通过反射实例化 Bean，将 Bean 放在毛坯 Bean 容器中
  - 设置 Bean 的属性
    - 如果依赖其他 Bean, 停止设置属性，先创建依赖的 Bean
    - 等依赖的 Bean 创建好之后，再继续设置属性

我们会发现，如果构造器注入的参数包含其他 Bean, 我们无法通过这种方式解决循环依赖的问题。
