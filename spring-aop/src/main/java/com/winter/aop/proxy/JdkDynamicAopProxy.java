package com.winter.aop.proxy;

import com.winter.aop.advice.Advice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class JdkDynamicAopProxy implements InvocationHandler {

    private List<Advice> advice;

    public JdkDynamicAopProxy(List<Advice> advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(proxy, method, args, this.advice);

        return methodInvocation.proceed();
    }


}
