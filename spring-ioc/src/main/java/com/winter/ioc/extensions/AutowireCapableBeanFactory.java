package com.winter.ioc.extensions;

public interface AutowireCapableBeanFactory {

    <T> T doGetBean(Class<T> tClass);

    Object doCreateBean(String beanName, Class<?> aClass);

    Object resolveDependency(Class<?> tClass);

}
