package com.winter.aop.advisor;

import com.winter.aop.proxy.MethodInterceptor;
import com.winter.aop.proxy.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJAfterAdvice extends AbstractAspectJAdvice implements MethodInterceptor {


    public AspectJAfterAdvice(Method aspectJAdviceMethod, Object aspectJObject) {
        super(aspectJAdviceMethod, aspectJObject);
    }

    @Override
    public Object invoke(MethodInvocation invocation, Object[] args) throws Throwable {
        try {
            return invocation.proceed(args);
        }
        finally {
            invokeAdviceMethod(args);
        }
    }

}
