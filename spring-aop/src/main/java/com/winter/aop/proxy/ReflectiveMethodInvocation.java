package com.winter.aop.proxy;

import com.winter.aop.advisor.Advice;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation implements MethodInvocation {

    protected final Object target;

    protected final Method method;

    protected Object[] arguments;

    private int currentInterceptorIndex = -1;

    protected List<Advice> interceptorsAndDynamicMethodMatchers;

    public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments, List<Advice> interceptorsAndDynamicMethodMatchers) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object invokeJoinPoint() throws Throwable {
        return this.method.invoke(this.target, this.arguments);
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {


        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            //若所有方法已调用，则调用连接点方法
            return invokeJoinPoint();
        }

        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);


        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this, args);
    }

}
