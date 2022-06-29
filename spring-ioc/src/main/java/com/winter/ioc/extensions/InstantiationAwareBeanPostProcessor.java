package com.winter.ioc.extensions;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName);

    boolean postProcessAfterInstantiation(Object bean, String beanName);

    /**
     * 此接口跟spring的不一样，见org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessPropertyValues
     *
     * @param bean
     * @param beanName
     */
    void postProcessPropertyValues(Object bean, String beanName);

}
