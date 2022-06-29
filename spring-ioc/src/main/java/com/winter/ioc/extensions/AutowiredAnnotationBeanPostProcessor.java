package com.winter.ioc.extensions;

import com.winter.ioc.ClassUtils;
import com.winter.ioc.annotation.Autowired;

import java.lang.reflect.Field;

public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    public AutowiredAnnotationBeanPostProcessor(AutowireCapableBeanFactory autowireCapableBeanFactory) {
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    @Override
    public void postProcessPropertyValues(Object bean, String beanName) {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (ClassUtils.existAnnotation(field, Autowired.class)) {
                try {
                    Object beanObject = autowireCapableBeanFactory.doGetBean(field.getType());
                    field.setAccessible(true);
                    field.set(bean, beanObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

}
