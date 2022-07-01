package com.winter.aop.proxy;

import com.winter.aop.advice.Advisor;
import com.winter.ioc.ClassUtils;
import com.winter.ioc.annotation.Autowired;
import com.winter.ioc.bean.BeanDefinition;
import com.winter.ioc.bean.DefaultListableBeanFactory;
import com.winter.ioc.extensions.BeanPostProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationAwareAspectJAutoProxyCreator implements BeanPostProcessor {

    private boolean proxyTargetClass = false;

    private boolean exposeProxy = false;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public boolean isExposeProxy() {
        return exposeProxy;
    }

    @Autowired
    private DefaultListableBeanFactory beanDefinitionRegistry;

    private List<Advisor> allAdvisor;

    private static final List<Class<? extends Annotation>> aspectJAnnotation = Arrays.asList(Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }


    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {

        return null;
    }

    protected List<Advisor> findCandidateAdvisors() throws NoSuchFieldException, IllegalAccessException {
        if (CollectionUtils.isEmpty(this.allAdvisor)) {
            List<Advisor> allAdvisor = new ArrayList<>();
            List<BeanDefinition> beanDefinitions = beanDefinitionRegistry.getBeanDefinitions();

            List<Method> pointcuts = new ArrayList<>();
            for (BeanDefinition beanDefinition : beanDefinitions) {
                Class<?> beanClass = beanDefinition.getBeanClass();
                if (ClassUtils.existAnnotation(beanClass, Aspect.class)) {
                    Object aspectObject = beanDefinitionRegistry.doGetBean(beanClass);

                    Method[] methods = beanClass.getMethods();
                    for (Method method : methods) {

                        //获取通知
                        for (Class<? extends Annotation> aClass : aspectJAnnotation) {
                            Annotation annotation = method.getAnnotation(aClass);
                            if (Objects.nonNull(annotation)) {
                                allAdvisor.add(new Advisor(annotation, method, null, aspectObject));
                            }
                        }

                        //获取切点
                        Pointcut pointcut = method.getAnnotation(Pointcut.class);
                        if (Objects.nonNull(pointcut)) {
                            pointcuts.add(method);
                        }

                    }
                }
            }

            Map<String, Method> pointcutMap = pointcuts.stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
            for (Advisor advisor : allAdvisor) {
                Field field = advisor.getAnnotation().annotationType().getField("value");
                String pointcut = (String) field.get(advisor.getAnnotation());
                Method pointcutMethod = pointcutMap.get(pointcut);

                if (Objects.nonNull(pointcutMethod)) {
                    advisor.setPointCut(pointcutMethod);
                    this.allAdvisor.add(advisor);
                }
            }

        }
        return allAdvisor;
    }

}
