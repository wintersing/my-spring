package com.winter.ioc;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Bean;
import com.winter.ioc.annotation.Configuration;
import com.winter.ioc.bean.BeanDefinition;
import com.winter.ioc.bean.BeanNameGenerator;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ApplicationContext {

    private final List<BeanDefinition> beanDefinitions = new ArrayList<>(64);

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(64);

    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    private final Map<Class<?>, String[]> singletonBeanNamesByType = new ConcurrentHashMap<Class<?>, String[]>(64);

    private ApplicationContext() {
    }

    public ApplicationContext(String... basePackages) {
        doScan(basePackages);
        refresh();
    }

    private void doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            List<BeanDefinition> list = null;
            try {
                list = ClassUtils.getAllClassByPakcage(basePackage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (CollectionUtils.isNotEmpty(list)) {
                beanDefinitions.addAll(list);
            }
        }
    }

    private void refresh() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            doCreateBean(beanDefinition);
        }
    }


    private void doCreateBean(BeanDefinition beanDefinition) {
        doCreateBean(beanDefinition.getBeanName(), beanDefinition.getaClass());
    }

    private void doCreateBean(String beanName, Class<?> aClass) {

        if (!singletonObjects.containsKey(beanName) && !earlySingletonObjects.containsKey(beanName)) {
            System.out.println(beanName+" creating...");
            try {
                Object instance = aClass.newInstance();
                earlySingletonObjects.put(beanName, instance);
                singletonBeanNamesByType.put(aClass, new String[]{beanName});
                populateBean(instance);
                if (ClassUtils.existAnnotation(aClass, Configuration.class)) {
                    Method[] declaredMethods = aClass.getDeclaredMethods();
                    for (Method declaredMethod : declaredMethods) {
                        Bean annotation = declaredMethod.getAnnotation(Bean.class);
                        if (Objects.nonNull(annotation)) {
                            String name = declaredMethod.getName();
                            Object invoke = declaredMethod.invoke(instance);
                            singletonObjects.put(name, invoke);
                        }
                    }
                }
                earlySingletonObjects.remove(beanName);
                singletonObjects.put(beanName, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化容器中的bean 的属性字段
     */
    private void populateBean(Object bean) throws IllegalAccessException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (ClassUtils.existAnnotation(field, Autowired.class)) {
                Object beanObject = doGetBean(field.getType());
                if (Objects.isNull(beanObject)) {
                    String beanName = BeanNameGenerator.generateBeanName(field.getType());
                    doCreateBean(beanName, field.getType());
                    beanObject = doGetBean(field.getClass());
                }
                // 允许访问私有属性
                field.setAccessible(true);
                // 给bean 的field属性赋值为beanObject
                field.set(bean, beanObject);
            }
        }
    }


    private <T> T doGetBean(Class<T> tClass) {
        String beanName = BeanNameGenerator.generateBeanName(tClass);
        Object bean = getBean(beanName);
        if (Objects.isNull(bean)) {
            doCreateBean(beanName, tClass);
        }
        return getBean(beanName);
    }

    public <T> T getBean(String beanName) {
        Object obj = earlySingletonObjects.get(beanName);
        if (Objects.isNull(obj)) {
            obj = singletonObjects.get(beanName);
        }
        return (T) obj;
    }


    public <T> T getBean(Class<T> tClass) {
        String[] beanName = singletonBeanNamesByType.get(tClass);
        if (Objects.nonNull(beanName) && beanName.length > 0) {
            return getBean(beanName[0]);
        }
        return null;
    }

}
