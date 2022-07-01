package com.winter.ioc.extensions;

import com.winter.ioc.bean.PropertyValue;

import java.util.Collection;

public interface AutowireCapableBeanFactory {

    <T> T doGetBean(Class<T> tClass);

    <T> T doGetBean(Class<T> tClass, Collection<PropertyValue> propertyValues);

    Object doCreateBean(String beanName, Class<?> aClass, Collection<PropertyValue> propertyValues);

    Object resolveDependency(Class<?> tClass);

}
