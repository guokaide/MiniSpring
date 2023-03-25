# MiniSpring

一. 原始 IoC 容器

IoC: Inverse of Control, 控制反转

- 控制: 管理 Bean 的生命周期和依赖关系
- 反转：原来由程序员控制，但是现在由框架控制

IoC 容器：核心职责是管理 Bean 的生命周期及依赖关系。

一个最简单的 IoC 容器：

- Bean 的配置：.xml 配置文件配置 Bean 的定义（声明）
- Bean 的创建：将 Bean 的定义读取到内存中，通过反射创建实例
  - XML Reader 读取 Bean 配置到到内存
  - 加载 Bean Class
  - 通过反射实例化 Bean
- Bean 的存储：将创建的实例保存在 Map 中
- Bean 的获取：通过 getBean() 获取 Bean 实例
