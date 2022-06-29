package com.winter.ioc.bean;

import com.winter.ioc.annotation.Component;
import com.winter.ioc.annotation.Controller;
import com.winter.ioc.annotation.Service;

import java.lang.annotation.Annotation;
import org.apache.commons.lang3.StringUtils;

public class BeanDefinition {
    private String beanName;
    private String className;
    private Class<?> aClass;

    public BeanDefinition(Class<?> aClass) {
        this.className = aClass.getName();
        this.aClass = aClass;
        this.beanName = BeanNameGenerator.generateBeanName(aClass);
    }




    public String getBeanName() {
        return beanName;
    }

    public String getClassName() {
        return className;
    }

    public Class<?> getaClass() {
        return aClass;
    }
}
