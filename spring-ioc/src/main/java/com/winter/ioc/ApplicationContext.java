package com.winter.ioc;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Bean;
import com.winter.ioc.annotation.Component;
import com.winter.ioc.annotation.Configuration;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ApplicationContext {

    private final Map<String, Object> beanMap = new ConcurrentHashMap<>(64);

    private ApplicationContext() {
    }

    public ApplicationContext(String... basePackages) {
        try {
            List<Class<?>> allClass = Arrays.stream(basePackages).flatMap(packageName ->
                    {
                        try {
                            return ClassUtils.getAllClassByPakcage(packageName).stream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            ).collect(Collectors.toList());
            initBean(allClass);
            initBeanFields();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Bean，将需要的bean保存到容器beanMap中
     *
     * @param classList
     */
    private void initBean(List<Class<?>> classList) {
        classList.stream().filter(aClass -> existAnnotation(aClass, Component.class))
                .forEach(aClass -> {
                    Class<?>[] interfaces = aClass.getInterfaces();
                    String beanName = interfaces.length == 0 ? toLowerCaseFirstWord(aClass.getSimpleName()) : toLowerCaseFirstWord(interfaces[0].getSimpleName());
                    if (beanMap.containsKey(beanName)) {
                        throw new RuntimeException("bean already existed");
                    }
                    try {
                        Object instance = aClass.newInstance();
                        if (existAnnotation(aClass, Configuration.class)) {

                            Method[] declaredMethods = aClass.getDeclaredMethods();
                            for (Method declaredMethod : declaredMethods) {
                                Bean annotation = declaredMethod.getAnnotation(Bean.class);
                                if (Objects.nonNull(annotation)) {
                                    String name = declaredMethod.getName();
                                    Object invoke = declaredMethod.invoke(instance);
                                    beanMap.put(name, invoke);
                                }
                            }
                        }
                        beanMap.put(beanName, instance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 初始化容器中的bean 的属性字段
     */
    private void initBeanFields() throws IllegalAccessException {
        for (Object bean : beanMap.values()) {
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (existAutowired(field)) {
                    String fieldName = toLowerCaseFirstWord(field.getName());

                    if (!beanMap.containsKey(fieldName)) {
                        throw new RuntimeException("class not found");
                    }

                    Object beanObject = beanMap.getOrDefault(fieldName, null);
                    // 允许访问私有属性
                    field.setAccessible(true);
                    // 给bean 的field属性赋值为beanObject
                    field.set(bean, beanObject);
                }
            }
        }
    }

    /**
     * 是否存在Component 注解
     *
     * @param aClass
     * @return
     */
    private boolean existAnnotation(Class<?> aClass, Class<? extends Annotation> targetAnnotation) {
        if (aClass.isAnnotation() || aClass.getAnnotations().length == 0) {
            return false;
        }

        Annotation declaredAnnotation = aClass.getDeclaredAnnotation(targetAnnotation);
        if (Objects.nonNull(declaredAnnotation)) {
            return true;
        }

        Annotation[] annotations = aClass.getAnnotations();

        for (Annotation annotation : annotations) {
            return existAnnotation(annotation, targetAnnotation);
        }
        return false;
    }


    private boolean existAnnotation(Annotation annotation, Class<? extends Annotation> targetAnnotation) {
        Annotation[] annotations = annotation.annotationType().getAnnotations();

        for (Annotation childAnnotation : annotations) {
            Class<?>[] interfaces = childAnnotation.getClass().getInterfaces();
            if (interfaces.length <= 0
                    || interfaces[0].getName().equals(Retention.class.getName())
                    || interfaces[0].getName().equals(Documented.class.getName())
                    || interfaces[0].getName().equals(Target.class.getName())) {
                continue;
            }
            if (interfaces[0].getName().equals(targetAnnotation.getName())) {
                return true;
            } else {
                return existAnnotation(childAnnotation, targetAnnotation);
            }
        }
        return false;
    }

    /**
     * 是否存在Autowired 注解
     *
     * @param field
     * @return
     */
    private boolean existAutowired(Field field) {
        return field.getAnnotation(Autowired.class) != null;
    }


    /**
     * 获取bean
     *
     * @param beanName
     * @param <T>
     * @return
     */
    public <T> T getBean(String beanName) {
        if (!beanMap.containsKey(beanName)) {
            return null;
        }
        return (T) beanMap.get(beanName);
    }

    /**
     * 首字母转小写
     *
     * @param s
     * @return
     */
    private String toLowerCaseFirstWord(String s) {
        return Character.isLowerCase(s.charAt(0)) ? s
                : (new StringBuilder().append(Character.toLowerCase(s.charAt(0)))).append(s.substring(1)).toString();
    }
}
