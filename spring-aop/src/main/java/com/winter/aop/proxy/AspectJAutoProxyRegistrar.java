package com.winter.aop.proxy;

import com.winter.aop.annotation.EnableAspectJAutoProxy;
import com.winter.ioc.ClassUtils;
import com.winter.ioc.bean.BeanDefinition;
import com.winter.ioc.bean.BeanDefinitionRegistry;
import com.winter.ioc.extensions.ImportBeanDefinitionRegistrar;

public class AspectJAutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(Class<?> targetBeanClass, BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition = new BeanDefinition(AnnotationAwareAspectJAutoProxyCreator.class);
        EnableAspectJAutoProxy enableAspectJAutoProxy = ClassUtils.findAnnotation(targetBeanClass, EnableAspectJAutoProxy.class);
        beanDefinition.addPropertyValue("proxyTargetClass", enableAspectJAutoProxy.proxyTargetClass());
        beanDefinition.addPropertyValue("exposeProxy", enableAspectJAutoProxy.exposeProxy());
        registry.registerBeanDefinition(beanDefinition);
    }

}
