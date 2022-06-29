package com.winter.ioc.extensions;

import java.lang.reflect.Constructor;

public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

    Class<?> predictBeanType(Class<?> beanClass, String beanName);

    Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName);

    Object getEarlyBeanReference(Object bean, String beanName);

}
