package com.winter.ioc.extensions;

import com.winter.ioc.ClassUtils;
import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.bean.PropertyValue;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public void postProcessPropertyValues(Object bean, String beanName, Collection<PropertyValue> propertyValues) {

        Map<String, Object> propertyValueMap = Collections.emptyMap();
        if (CollectionUtils.isNotEmpty(propertyValues)) {
            propertyValueMap = propertyValues.stream().collect(Collectors.toMap(PropertyValue::getName, PropertyValue::getValue));
        }

        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            try {
                Object fieldValue;
                if (ClassUtils.existAnnotation(field, Autowired.class)) {
                    fieldValue = autowireCapableBeanFactory.doGetBean(field.getType());
                } else {
                    fieldValue = propertyValueMap.get(field.getName());
                }
                if (Objects.nonNull(fieldValue)) {
                    field.setAccessible(true);
                    field.set(bean, fieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
