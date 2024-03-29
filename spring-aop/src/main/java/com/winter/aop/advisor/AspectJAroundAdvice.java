package com.winter.aop.advisor;

import com.winter.aop.proxy.MethodInterceptor;
import com.winter.aop.proxy.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJAroundAdvice extends AbstractAspectJAdvice implements MethodInterceptor {


    public AspectJAroundAdvice(Method aspectJAdviceMethod, Object aspectJObject) {
        super(aspectJAdviceMethod, aspectJObject);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invokeAdviceMethod(invocation);
//        return invocation.proceed();
    }



}
