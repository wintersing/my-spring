package com.winter.ioc.bean;

import java.util.List;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(BeanDefinition beanDefinition);

    void registerBeanDefinition(String beanFullName);

    List<BeanDefinition> getBeanDefinitions();

}
