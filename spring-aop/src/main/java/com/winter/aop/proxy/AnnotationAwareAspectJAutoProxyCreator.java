package com.winter.aop.proxy;

import com.winter.aop.advisor.*;
import com.winter.ioc.ClassUtils;
import com.winter.ioc.bean.BeanDefinition;
import com.winter.ioc.bean.BeanFactory;
import com.winter.ioc.bean.DefaultListableBeanFactory;
import com.winter.ioc.extensions.BeanFactoryAware;
import com.winter.ioc.extensions.BeanPostProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationAwareAspectJAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

    private boolean proxyTargetClass = false;

    private boolean exposeProxy = false;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public boolean isExposeProxy() {
        return exposeProxy;
    }

    private DefaultListableBeanFactory beanFactory;

    private List<Advisor> allAdvisor;

    private static final List<Class<? extends Annotation>> aspectJAnnotation = Arrays.asList(Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return wrapIfNecessary(bean, beanName);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    protected Object wrapIfNecessary(Object bean, String beanName) {
        List<Advisor> eligibleAdvisors = findEligibleAdvisors(bean.getClass(), beanName);
        return createProxy(bean, eligibleAdvisors);
    }

    private Object createProxy(Object object, List<Advisor> eligibleAdvisors) {
        return createAopProxy(object, eligibleAdvisors).getProxy();
    }

    private AopProxy createAopProxy(Object object, List<Advisor> eligibleAdvisors) {
        return new JdkDynamicAopProxy(object, eligibleAdvisors);
    }

    protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);

        if (!eligibleAdvisors.isEmpty()) {
            //调用链在这里排序
            eligibleAdvisors = sortAdvisors(eligibleAdvisors);
        }
        return eligibleAdvisors;
    }

    private List<Advisor> sortAdvisors(List<Advisor> eligibleAdvisors) {
        if (CollectionUtils.isEmpty(eligibleAdvisors)) {
            return Collections.emptyList();
        }
        return eligibleAdvisors.stream().sorted(Comparator.comparing(e -> aspectJAnnotation.indexOf(e.getAnnotation().annotationType()))).collect(Collectors.toList());
    }

    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
        List<Advisor> eligibleAdvisors = new ArrayList<>();
        for (Advisor candidateAdvisor : candidateAdvisors) {
            for (Method method : beanClass.getMethods()) {
                if (candidateAdvisor.matches(method)) {
                    eligibleAdvisors.add(candidateAdvisor);
                    break;
                }
            }
        }
        return eligibleAdvisors;
    }

    protected List<Advisor> findCandidateAdvisors() {
        if (CollectionUtils.isEmpty(this.allAdvisor)) {
            List<Advisor> allAdvisor = new ArrayList<>();
            List<BeanDefinition> beanDefinitions = beanFactory.getBeanDefinitions();

            for (BeanDefinition beanDefinition : beanDefinitions) {
                Class<?> beanClass = beanDefinition.getBeanClass();
                if (ClassUtils.existAnnotation(beanClass, Aspect.class)) {
                    Object aspectObject = beanFactory.doGetBean(beanClass);

                    Method[] methods = beanClass.getMethods();
                    for (Method method : methods) {

                        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
                        Map<Class<? extends Annotation>, Annotation> annotationMap = Arrays.stream(declaredAnnotations).collect(Collectors.toMap(e -> e.annotationType(), e -> e));

                        for (Class<? extends Annotation> annotation : aspectJAnnotation) {
//                            for (Annotation declaredAnnotation : declaredAnnotations) {
                            Annotation declaredAnnotation = annotationMap.get(annotation);
                            if (Objects.nonNull(declaredAnnotation)) {
                                try {
                                    Method value = declaredAnnotation.annotationType().getMethod("value");
                                    String pointcut = (String) value.invoke(declaredAnnotation);
                                    AbstractAspectJAdvice aspectJAdvice = buildAspectJAdvice(declaredAnnotation, method, aspectObject);
                                    if (Objects.nonNull(aspectJAdvice)) {
                                        allAdvisor.add(new Advisor(declaredAnnotation, aspectJAdvice, aspectObject, pointcut));
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
//                            }

                        }

                    }
                }
            }
            this.allAdvisor = allAdvisor;
        }
        return allAdvisor;
    }

    private AbstractAspectJAdvice buildAspectJAdvice(Annotation annotation, Method method, Object aspectObject) {
        if (Around.class.getName().equals(annotation.annotationType().getName())) {
            return new AspectJAroundAdvice(method, aspectObject);
        } else if (Before.class.getName().equals(annotation.annotationType().getName())) {
            return new AspectJBeforeAdvice(method, aspectObject);
        } else if (After.class.getName().equals(annotation.annotationType().getName())) {
            return new AspectJAfterAdvice(method, aspectObject);
        } else if (AfterReturning.class.getName().equals(annotation.annotationType().getName())) {
            return new AspectJAfterReturningAdvice(method, aspectObject);
        } else if (AfterThrowing.class.getName().equals(annotation.annotationType().getName())) {
            return new AspectJAfterThrowingAdvice(method, aspectObject);
        }
        return null;
    }

}
