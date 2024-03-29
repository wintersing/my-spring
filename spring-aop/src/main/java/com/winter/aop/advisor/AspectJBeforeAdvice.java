package com.winter.aop.advisor;

import com.winter.aop.proxy.MethodInterceptor;
import com.winter.aop.proxy.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJBeforeAdvice extends AbstractAspectJAdvice implements MethodInterceptor {


    public AspectJBeforeAdvice(Method aspectJAdviceMethod, Object aspectJObject) {
        super(aspectJAdviceMethod, aspectJObject);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        invokeAdviceMethod(invocation);
        return invocation.proceed();
    }

}
