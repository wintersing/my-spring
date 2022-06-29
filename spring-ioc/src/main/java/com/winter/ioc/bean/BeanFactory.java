package com.winter.ioc.bean;

public interface BeanFactory {

    <T> T getBean(String beanName);

    <T> T getBean(Class<T> tClass);

}
