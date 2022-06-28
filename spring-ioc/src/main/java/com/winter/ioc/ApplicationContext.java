package com.winter.ioc;

import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Bean;
import com.winter.ioc.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ApplicationContext {

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(64);

    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    private final Map<Class<?>, String[]> singletonBeanNamesByType = new ConcurrentHashMap<Class<?>, String[]>(64);

    private ApplicationContext() {
    }

    public ApplicationContext(String... basePackages) {
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

        allClass.forEach(this::getBean);
    }

    private void createBean(Class<?> aClass) {
        Class<?>[] interfaces = aClass.getInterfaces();
        String beanName = interfaces.length == 0 ? toLowerCaseFirstWord(aClass.getSimpleName()) : toLowerCaseFirstWord(interfaces[0].getSimpleName());
        if (!singletonObjects.containsKey(beanName) && !earlySingletonObjects.containsKey(beanName)) {
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
                Object beanObject = getBean(field.getType());
                if (Objects.isNull(beanObject)) {
                    createBean(field.getType());
                    beanObject = getBean(field.getClass());
                }
                // 允许访问私有属性
                field.setAccessible(true);
                // 给bean 的field属性赋值为beanObject
                field.set(bean, beanObject);
            }
        }

    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName) {
        Object obj = earlySingletonObjects.get(beanName);
        if (Objects.isNull(obj)) {
            obj = singletonObjects.get(beanName);
        }
        return (T) obj;
    }


    @SuppressWarnings("unchecked")
    private <T> T getBean(Class<T> tClass) {
        String beanName = toLowerCaseFirstWord(tClass.getSimpleName());
        Object bean = getBean(beanName);
        if (Objects.isNull(bean)) {
            createBean(tClass);
        }
        return getBean(beanName);

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
