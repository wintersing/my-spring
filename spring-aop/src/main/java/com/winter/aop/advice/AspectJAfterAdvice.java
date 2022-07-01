package com.winter.aop.advice;

import com.winter.aop.proxy.MethodInterceptor;
import com.winter.aop.proxy.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJAfterAdvice extends AbstractAspectJAdvice implements MethodInterceptor {


    public AspectJAfterAdvice(Method aspectJAdviceMethod, Object aspectJObject) {
        super(aspectJAdviceMethod, aspectJObject);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        }
        finally {
            invokeAdviceMethod();
        }
    }

}
