package com.winter.ioc.bean;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {
    private String beanName;
    private String className;
    private Class<?> beanClass;
    private List<PropertyValue> propertyValues = new ArrayList<>(0);

    public BeanDefinition(Class<?> beanClass) {
        this.className = beanClass.getName();
        this.beanClass = beanClass;
        this.beanName = BeanNameGenerator.generateBeanName(beanClass);
    }

    public String getBeanName() {
        return beanName;
    }

    public String getClassName() {
        return className;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public void addPropertyValue(String name, Object value) {
        propertyValues.add(new PropertyValue(name, value));
    }

}
