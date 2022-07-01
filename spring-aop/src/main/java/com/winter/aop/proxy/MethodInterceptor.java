package com.winter.aop.proxy;

public interface MethodInterceptor {

    Object invoke(MethodInvocation invocation) throws Throwable;

}
