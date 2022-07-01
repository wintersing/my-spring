package com.winter.aop.annotation;

import com.winter.aop.proxy.AspectJAutoProxyRegistrar;
import com.winter.ioc.annotation.Component;
import com.winter.ioc.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
@Component
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

    boolean proxyTargetClass() default false;

    boolean exposeProxy() default false;

}