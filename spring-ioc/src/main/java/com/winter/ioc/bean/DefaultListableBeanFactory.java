package com.winter.ioc.bean;

import com.winter.ioc.ClassUtils;
import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.annotation.Bean;
import com.winter.ioc.annotation.Configuration;
import com.winter.ioc.extensions.AutowireCapableBeanFactory;
import com.winter.ioc.extensions.AutowiredAnnotationBeanPostProcessor;
import com.winter.ioc.extensions.BeanPostProcessor;
import com.winter.ioc.extensions.InstantiationAwareBeanPostProcessor;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class DefaultListableBeanFactory implements ApplicationContext, AutowireCapableBeanFactory {

    private final List<BeanDefinition> beanDefinitions = new ArrayList<>(64);

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(16);

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(64);

    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    private final Map<Class<?>, String[]> singletonBeanNamesByType = new ConcurrentHashMap<>(64);

    private DefaultListableBeanFactory() {
    }

    public DefaultListableBeanFactory(String... basePackages) {
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
        //添加后置处理器
        beanPostProcessors.add(new AutowiredAnnotationBeanPostProcessor(this));

        //创建bean
        for (BeanDefinition beanDefinition : beanDefinitions) {
            doGetBean(beanDefinition.getaClass());
        }
    }

    @Override
    public Object doCreateBean(String beanName, Class<?> aClass) {
        System.out.println(beanName + " creating...");
            try {
                Object instance = postProcessBeforeInstantiation(aClass, beanName);
                //判断不是自定义的bean则创建
                if (Objects.isNull(instance)) {
                    instance = aClass.newInstance();
                    earlySingletonObjects.put(beanName, instance);

                    if (ClassUtils.existAnnotation(aClass, Configuration.class)) {
                        //配置类
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
                }
                populateBean(instance, beanName);
                return instance;
            } catch (Exception e) {
                throw new RuntimeException("创建bean:"+beanName+"失败", e);
            }

    }

    private Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        List<BeanPostProcessor> beanPostProcessor = getBeanPostProcessor();
        for (BeanPostProcessor postProcessor : beanPostProcessor) {
            if (postProcessor instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor = (InstantiationAwareBeanPostProcessor) postProcessor;
                Object bean = instantiationAwareBeanPostProcessor.postProcessBeforeInstantiation(beanClass, beanName);
                if (Objects.nonNull(bean)) {
                    return bean;
                }
            }
        }
        return null;
    }


    private List<BeanPostProcessor> getBeanPostProcessor() {
        return beanPostProcessors;
    }

    /**
     * 初始化容器中的bean 的属性字段
     */
    private void populateBean(Object bean, String beanName) {

        boolean continueWithPropertyPopulation = true;

        List<BeanPostProcessor> beanPostProcessor = getBeanPostProcessor();
        for (BeanPostProcessor postProcessor : beanPostProcessor) {
            if (postProcessor instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor = (InstantiationAwareBeanPostProcessor) postProcessor;
                if (!instantiationAwareBeanPostProcessor.postProcessAfterInstantiation(bean, beanName)) {
                    //判断需不需要注入属性
                    continueWithPropertyPopulation = false;
                }
            }
        }

        if (continueWithPropertyPopulation) {

            for (BeanPostProcessor postProcessor : beanPostProcessor) {
                if (postProcessor instanceof InstantiationAwareBeanPostProcessor) {
                    InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor = (InstantiationAwareBeanPostProcessor) postProcessor;
                    instantiationAwareBeanPostProcessor.postProcessPropertyValues(bean, beanName);
                }
            }
        }

    }

    public Object resolveDependency(Class<?> tClass) {
        return doGetBean(tClass);
    }

    @Override
    public <T> T doGetBean(Class<T> tClass) {
        String beanName = BeanNameGenerator.generateBeanName(tClass);
        Object bean = getBean(beanName);
        if (Objects.isNull(bean)) {
            if (!singletonObjects.containsKey(beanName) && !earlySingletonObjects.containsKey(beanName)) {
                Object instance = doCreateBean(beanName, tClass);

                earlySingletonObjects.remove(beanName);
                singletonBeanNamesByType.put(tClass, new String[]{beanName});
                singletonObjects.put(beanName, instance);
            }
        }
        return getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName) {
        Object obj = earlySingletonObjects.get(beanName);
        if (Objects.isNull(obj)) {
            obj = singletonObjects.get(beanName);
        }
        return (T) obj;
    }

    @Override
    public <T> T getBean(Class<T> tClass) {
        String[] beanName = singletonBeanNamesByType.get(tClass);
        if (Objects.nonNull(beanName) && beanName.length > 0) {
            return getBean(beanName[0]);
        }
        return null;
    }

}
