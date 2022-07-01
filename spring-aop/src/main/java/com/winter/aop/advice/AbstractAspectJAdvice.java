package com.winter.aop.advice;

import java.lang.reflect.Method;

public abstract class AbstractAspectJAdvice implements Advice {

    protected final Method aspectJAdviceMethod;

    protected final Object aspectJObject;

    public AbstractAspectJAdvice(Method aspectJAdviceMethod, Object aspectJObject) {
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.aspectJObject = aspectJObject;
    }

    /**
     * 统一调用通知方法
     *
     * @return
     * @throws Throwable
     */
    public Object invokeAdviceMethod() throws Throwable {
        return aspectJAdviceMethod.invoke(aspectJObject);
    }

}
