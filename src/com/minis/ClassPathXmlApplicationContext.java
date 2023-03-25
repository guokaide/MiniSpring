package com.minis;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 这个类的功能太多了，违背了单一职责原则
public class ClassPathXmlApplicationContext {
    private final List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private final Map<String, Object> singletons = new HashMap<>();

    public ClassPathXmlApplicationContext(String fileName) {
        this.readXml(fileName);
        this.instanceBeans();
    }

    // 配置文件中读取 Bean 的定义，将 Bean 的定义保存在内存中
    private void readXml(String fileName) {
        SAXReader saxReader = new SAXReader();
        URL xmlPath = this.getClass().getClassLoader().getResource(fileName);
        try {
            // 读取 XML 文件
            Document document = saxReader.read(xmlPath);
            // 读取 Root Element
            Element rootElement = document.getRootElement();
            // 读取 Root Element 下的每一个 Element
            for (Element element : (List<Element>) rootElement.elements()) {
                String beanId = element.attributeValue("id");
                String beanClassName = element.attributeValue("class");
                BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);
                beanDefinitions.add(beanDefinition);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    // 实例化 Bean，通过反射创建 Bean 的实例
    private void instanceBeans() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                singletons.put(beanDefinition.getId(),
                        Class.forName(beanDefinition.getClassName()).newInstance());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 外部程序从容器中获取 Bean 实例，会逐步演变成核心方法
    public Object getBean(String beanName) {
        return singletons.get(beanName);
    }
}
