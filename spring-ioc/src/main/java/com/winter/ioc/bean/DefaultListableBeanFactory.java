package com.winter.ioc.bean;

import com.winter.ioc.ClassUtils;
import com.winter.ioc.annotation.Bean;
import com.winter.ioc.annotation.Configuration;
import com.winter.ioc.annotation.Import;
import com.winter.ioc.extensions.*;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class DefaultListableBeanFactory implements ApplicationContext, AutowireCapableBeanFactory, BeanDefinitionRegistry {

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(16);


    private final List<BeanDefinition> beanDefinitions = new ArrayList<>(64);


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
                for (BeanDefinition beanDefinition : list) {
                    registerBeanDefinition(beanDefinition);
                }
            }
        }
    }

    private void refresh() {

        prepareBeanFactory();

        invokeBeanFactoryPostProcessors();

        registerBeanPostProcessors();

        finishBeanFactoryInitialization();

    }

    private void prepareBeanFactory() {
        beanFactoryPostProcessors.add(new ConfigurationClassPostProcessor());
    }

    private void invokeBeanFactoryPostProcessors() {

        /*for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            beanFactoryPostProcessor.postProcessBeanFactory();
        }*/

        //todo 使用ConfigurationClassPostProcessor处理配置类
        List<BeanDefinition> beanDefinitions = getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            List<PropertyValue> propertyValues = beanDefinition.getPropertyValues();
            processImports(beanClass, beanClass, propertyValues);
        }
    }

    private void processImports(Class<?> beanClass, Class<?> targetBeanClass, List<PropertyValue> propertyValues) {

        Import importAnnotation = ClassUtils.findAnnotation(beanClass, Import.class);
        if (Objects.nonNull(importAnnotation)) {
            doGetBean(beanClass, propertyValues);
            Class<?>[] importClassArr = importAnnotation.value();
            for (Class<?> importClass : importClassArr) {
                processImports(importClass, targetBeanClass, Collections.emptyList());
            }
        } else if (ImportBeanDefinitionRegistrar.class.isAssignableFrom(beanClass)) {
            Object instance = doGetBean(beanClass, propertyValues);

            ImportBeanDefinitionRegistrar importBeanDefinitionRegistrar = (ImportBeanDefinitionRegistrar) instance;
            importBeanDefinitionRegistrar.registerBeanDefinitions(targetBeanClass, this);
        } else if (ClassUtils.existAnnotation(beanClass, Configuration.class)) {
            Object instance = doGetBean(beanClass, propertyValues);

            Method[] declaredMethods = beanClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                Bean annotation = declaredMethod.getAnnotation(Bean.class);
                if (Objects.nonNull(annotation)) {
                    String name = declaredMethod.getName();
                    Object invoke = null;
                    try {
                        invoke = declaredMethod.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    singletonObjects.put(name, invoke);
                }
            }
        }

    }

    private void registerBeanPostProcessors() {
        //添加后置处理器
        beanPostProcessors.add(new AutowiredAnnotationBeanPostProcessor(this));

        List<BeanDefinition> beanDefinitions = getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            List<PropertyValue> propertyValues = beanDefinition.getPropertyValues();
            if (BeanPostProcessor.class.isAssignableFrom(beanClass)) {
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) doGetBean(beanClass, propertyValues);
                beanPostProcessors.add(beanPostProcessor);
            }
        }
    }

    private void finishBeanFactoryInitialization() {

        //创建所有bean
        List<BeanDefinition> beanDefinitions = getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            List<PropertyValue> propertyValues = beanDefinition.getPropertyValues();
            doGetBean(beanDefinition.getBeanClass(), propertyValues);
        }
    }

    @Override
    public Object doCreateBean(String beanName, Class<?> aClass, Collection<PropertyValue> propertyValues) {
        try {
            Object instance = postProcessBeforeInstantiation(aClass, beanName);
            //判断不是自定义的bean则创建
            if (Objects.isNull(instance)) {
                instance = aClass.newInstance();
                earlySingletonObjects.put(beanName, instance);
            }
            populateBean(instance, beanName, propertyValues);
            instance = initializeBean(instance, beanName);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("创建bean:" + beanName + "失败", e);
        }

    }

    private Object initializeBean(Object bean, String beanName) {

        bean = postProcessBeforeInitialization(bean, beanName);

        //调用初始化方法
        invokeInitMethods(bean);

        bean = postProcessAfterInitialization(bean, beanName);
        return bean;
    }

    private void invokeInitMethods(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
        }
    }

    private Object postProcessAfterInitialization(Object bean, String beanName) {
        List<BeanPostProcessor> beanPostProcessor = getBeanPostProcessor();

        //初始化后
        Object result = bean;
        for (BeanPostProcessor postProcessor : beanPostProcessor) {
            result = postProcessor.postProcessAfterInitialization(bean, beanName);
            if (Objects.isNull(result)) {
                return result;
            }
        }
        return result;
    }


    private Object postProcessBeforeInitialization(Object bean, String beanName) {
        List<BeanPostProcessor> beanPostProcessor = getBeanPostProcessor();

        //初始化前
        Object result = bean;
        for (BeanPostProcessor postProcessor : beanPostProcessor) {
            result = postProcessor.postProcessBeforeInitialization(bean, beanName);
            if (Objects.isNull(result)) {
                return result;
            }
        }
        return result;
    }

    private Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        //实例化前
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
    private void populateBean(Object bean, String beanName, Collection<PropertyValue> propertyValues) {

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
            //实例化后
            for (BeanPostProcessor postProcessor : beanPostProcessor) {
                if (postProcessor instanceof InstantiationAwareBeanPostProcessor) {
                    InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor = (InstantiationAwareBeanPostProcessor) postProcessor;
                    instantiationAwareBeanPostProcessor.postProcessPropertyValues(bean, beanName, propertyValues);
                }
            }
        }

    }

    public Object resolveDependency(Class<?> tClass) {
        return doGetBean(tClass);
    }

    @Override
    public <T> T doGetBean(Class<T> tClass) {
        return doGetBean(tClass, Collections.emptyList());
    }

    @Override
    public <T> T doGetBean(Class<T> tClass, Collection<PropertyValue> propertyValues) {
        if (tClass.isInterface()) {
            List<BeanDefinition> subclass = new ArrayList<>(1);
            for (BeanDefinition beanDefinition : getBeanDefinitions()) {
                if (tClass.isAssignableFrom(beanDefinition.getBeanClass())) {
                    subclass.add(beanDefinition);
                }
            }
            if (subclass.size() == 0) {
                return null;
            }
            if (subclass.size() > 1) {
                throw new RuntimeException("找到多个Bean " + tClass.getName());
            }
            tClass = (Class<T>) subclass.get(0).getBeanClass();
        }
        String beanName = BeanNameGenerator.generateBeanName(tClass);
        Object bean = getBean(tClass);
        if (Objects.isNull(bean)) {
            if (!singletonObjects.containsKey(beanName) && !earlySingletonObjects.containsKey(beanName)) {
                Object instance = doCreateBean(beanName, tClass, propertyValues);

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
        List<Object> objects = new ArrayList<>(1);
        for (Object value : earlySingletonObjects.values()) {
            if (tClass.isAssignableFrom(value.getClass())) {
                objects.add(value);
            }
        }
        for (Object value : singletonObjects.values()) {
            if (tClass.isAssignableFrom(value.getClass())) {
                objects.add(value);
            }
        }
        if (objects.size() == 0) {
            return null;
        }
        if (objects.size() > 1) {
            throw new RuntimeException("找到多个Bean " + tClass.getName());
        }

        return (T) objects.get(0);
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.add(beanDefinition);
    }

    @Override
    public void registerBeanDefinition(Class<?> beanClass) {
        registerBeanDefinition(new BeanDefinition(beanClass));
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        //new一个新的List，是为保护该属性
        return new ArrayList<>(this.beanDefinitions);
    }

}
