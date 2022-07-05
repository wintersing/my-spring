package com.winter.aop.advisor;

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
     * @param args
     */
    public Object invokeAdviceMethod(Object[] args) throws Throwable {
        return aspectJAdviceMethod.invoke(aspectJObject, args);
    }

}
