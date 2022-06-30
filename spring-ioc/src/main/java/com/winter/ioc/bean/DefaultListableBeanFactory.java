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

        List<BeanDefinition> beanDefinitions = getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = beanDefinition.getBeanClass();

            if (ClassUtils.existAnnotation(beanClass, Configuration.class)) {
                //配置类
                Object instance = doGetBean(beanClass);
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

                Import annotation = beanClass.getAnnotation(Import.class);
                if (Objects.nonNull(annotation)) {
                    Class<?>[] importClassArr = annotation.value();
                    for (Class<?> importClass : importClassArr) {
                        Object importInstance = doGetBean(importClass);
                        singletonObjects.put(BeanNameGenerator.generateBeanName(importClass), importInstance);
                    }
                }
                if (instance instanceof BeanPostProcessor) {
                    beanPostProcessors.add((BeanPostProcessor) instance);
                }
            } else if (beanClass.isAssignableFrom(ImportBeanDefinitionRegistrar.class)) {
                Object importInstance = doGetBean(beanClass);
                ImportBeanDefinitionRegistrar importBeanDefinitionRegistrar = (ImportBeanDefinitionRegistrar) importInstance;
                importBeanDefinitionRegistrar.registerBeanDefinitions(this);
            }

        }
    }

    private void registerBeanPostProcessors() {
        //添加后置处理器
        beanPostProcessors.add(new AutowiredAnnotationBeanPostProcessor(this));

        List<BeanDefinition> beanDefinitions = getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            if (beanClass.isAssignableFrom(BeanPostProcessor.class)) {
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) doGetBean(beanClass);
                beanPostProcessors.add(beanPostProcessor);
            }
        }
    }

    private void finishBeanFactoryInitialization() {

        //创建bean
        List<BeanDefinition> beanDefinitions = getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Object bean = doGetBean(beanDefinition.getBeanClass());
            if (bean instanceof BeanPostProcessor) {
                beanPostProcessors.add((BeanPostProcessor) bean);
            }

            if (bean instanceof ImportBeanDefinitionRegistrar) {
                ImportBeanDefinitionRegistrar definitionRegistrar = (ImportBeanDefinitionRegistrar) bean;
                definitionRegistrar.registerBeanDefinitions(this);
            }

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
            }
            populateBean(instance, beanName);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("创建bean:" + beanName + "失败", e);
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

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.add(beanDefinition);
    }

    @Override
    public void registerBeanDefinition(String beanFullName) {
        try {
            registerBeanDefinition(new BeanDefinition(Class.forName(beanFullName)));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        return this.beanDefinitions;
    }

}
