package com.winter.aop.advisor;

import com.winter.aop.proxy.MethodInterceptor;
import com.winter.aop.proxy.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice implements MethodInterceptor {


    public AspectJAfterThrowingAdvice(Method aspectJAdviceMethod, Object aspectJObject) {
        super(aspectJAdviceMethod, aspectJObject);
    }

    @Override
    public Object invoke(MethodInvocation mi, Object[] args) throws Throwable {
        try {
            return mi.proceed(args);
        }
        catch (Throwable ex) {
            invokeAdviceMethod(args);
            throw ex;
        }
    }


}
