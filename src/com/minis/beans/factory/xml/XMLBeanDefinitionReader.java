package com.minis.beans.factory.xml;

import com.minis.beans.factory.config.*;
import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource 中读取 Bean 的定义，将 Bean 的定义注册到 IoC 容器 SimpleBeanFactory 中
 * e.g. XML -> Resource -> BeanDefinition -> BeanFactory
 */
public class XMLBeanDefinitionReader {
    AbstractBeanFactory beanFactory;

    public XMLBeanDefinitionReader(AbstractBeanFactory abstractBeanFactory) {
        this.beanFactory = abstractBeanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            // 处理 Bean id
            String beanId = element.attributeValue("id");
            // 处理 Bean class
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);
            // 处理构造器参数
            ConstructArgumentValues argumentValues = new ConstructArgumentValues();
            List<Element> argumentElements = element.elements("constructor-arg");
            for (Element e : argumentElements) {
                String aType = e.attributeValue("type");
                String aName = e.attributeValue("name");
                String aValue = e.attributeValue("value");
                argumentValues.addArgumentValue(new ConstructorArgumentValue(aType, aName, aValue));
            }
            beanDefinition.setConstructorArgumentValues(argumentValues);
            // 处理属性
            PropertyValues propertyValues = new PropertyValues();
            List<Element> propertyElements = element.elements("property");
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                String pRef = e.attributeValue("ref");
                String pV = "";
                boolean isRef = false;
                if (pValue != null && !pValue.equals("")) {
                    pV = pValue;
                } else if (pRef != null && !pRef.equals("")) {
                    pV = pRef;
                    isRef = true;
                    refs.add(pRef);
                }
                propertyValues.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(propertyValues);
            beanDefinition.setDependsOn(refs.toArray(new String[0]));
            this.beanFactory.registerBeanDefinition(beanId, beanDefinition);
        }
    }
}