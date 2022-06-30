package com.winter.ioc.bean;

public class BeanDefinition {
    private String beanName;
    private String className;
    private Class<?> beanClass;

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
}
